package com.wepiao.goods.service.impl;

import com.google.common.collect.Lists;
import com.wepiao.goods.common.enums.SeatStatus;
import com.wepiao.goods.mapper.goods.BisOrderInfoMapper;
import com.wepiao.goods.mapper.goods.LocalOrderInfoMapper;
import com.wepiao.goods.model.goods.BisOrderInfo;
import com.wepiao.goods.model.goods.LocalOrderInfo;
import com.wepiao.goods.model.goods.TicketRefundInfoBean;
import com.wepiao.goods.service.ScheduleService;
import com.wepiao.goods.service.TicketRefundService;
import com.wepiao.goods.vo.Schedule;
import com.wxmovie.bis.ticketplatform.entity.ReturnTicketInfo;
import com.wxmovie.bis.ticketplatform.enumerate.TicketStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.Date;
import java.util.List;

/**
 * Created by zhangliang on 2015/12/29.
 * Function:退票的基类
 * Reason:  1. 这样做使代码分离，方便后期维护
            2. 代码可读性比较高，看接口就知道该类想实现什么功能
            3. 代码逻辑分离，这样写的代码一层层分下去最后在控制层只要调用。高内聚低耦合的原则。
 */
public abstract class BaseSeatRefundService implements TicketRefundService{

    protected static Logger logger = LoggerFactory.getLogger(BaseSeatRefundService.class);

    @Autowired
    protected LocalOrderInfoMapper localOrderInfoMapper;

    @Autowired
    protected BisOrderInfoMapper bisOrderInfoMapper;

    @Autowired
    protected ScheduleService scheduleService;

    public List<Object> getReturnTicketParams(Integer scheduleId, Integer cinemaNo, Integer channelId,
                                              String tpOrderId, int ticketCount){
      List<Object> refundTicketParams = Lists.newArrayListWithCapacity(2);
      //获得订单所对应的排期信息
      Schedule schedule = scheduleService.getSchedule(scheduleId, cinemaNo, channelId);
      if(schedule == null){
          return refundTicketParams;
      }
      //封装退票参数
      ReturnTicketInfo ticketInfo = new ReturnTicketInfo();
      ticketInfo.setFilmNo(schedule.getBisMovieNo());// 第三方影片编号
      ticketInfo.setBackTicketReason("Personal Reason");
      ticketInfo.setCinemaLinkId(schedule.getCinemaLinkId());
      ticketInfo.setCinemaNo(schedule.getBisCinemaNo());// 第三方影院编号
      ticketInfo.setHallNo(schedule.getHallNo());// 第三方影厅编号
      ticketInfo.setLocNo(schedule.getSectionNo());
      ticketInfo.setOrderNo(tpOrderId);// 第三方订单号
      ticketInfo.setShowDate(schedule.getShowDate());
      ticketInfo.setShowSeqNo(schedule.getShowSeqNo());
      ticketInfo.setShowTime(schedule.getShowTime());
      ticketInfo.setTicketCount(ticketCount);
      refundTicketParams.add(0, ticketInfo);
      refundTicketParams.add(1, schedule.getBisServerId());
      return refundTicketParams;
    }

    /**
     * refundTicketOk: 退票成功
     * 自有库存退票成功:
     *              1.删除自有库存订单表(local_order_info)中订单号所对应的座位信息
     *              2.将该条退票成功的记录插入到 接入商订单表(bis_order_info)中
     * 非自有库从退票成功:
     *              1.将座位状态修改为退票成功状态(status=5)
     * */
    public void refundTicketOk(TicketRefundInfoBean ticketRefundInfoBean){
      int result = 0;
      if(ticketRefundInfoBean.getSource() == 2){
        // 自由库存退票成功 删除local_order_info表中的对应记录
          LocalOrderInfo localOrderInfo = ticketRefundInfoBean.getLocalOrderInfo();
          int delOkCount = localOrderInfoMapper.deleteLocalRefundOkTickets(localOrderInfo);
          logger.info("自有库存中订单号{}对应座位成功删除{}条记录", localOrderInfo.getOrderId(), delOkCount);
          // 将该记录插入到bis_order_info表中
          BisOrderInfo bisOrderInfo =
                  assembleBisOrderInfo(ticketRefundInfoBean, SeatStatus.REFUNDED.getVal());
          result = bisOrderInfoMapper.insert(bisOrderInfo);
          logger.info("自有库存中订单号{}对应座位退票成功, 插入订单表{}条记录", localOrderInfo.getScheduleId(), result);
      } else {
          // 非自有库存退票成功 更新bis_order_info表中数座位状态为退票成功
          BisOrderInfo bisOrderInfo = ticketRefundInfoBean.getBisOrderInfo();
          result =
                  bisOrderInfoMapper.updateRefundCommonSeatByFixOrderId(bisOrderInfo.getFixOrderId(),//这里fixOrderId相当于用户的订单号
                          SeatStatus.REFUNDED.getVal());
          logger.info("非自有库存中订单号{}对应座位退票成功, 更新订单表{}条记录", bisOrderInfo.getFixOrderId(), result);
      }
    }

    /**
     * refundTicketFail:退票失败
     * 自有库从退票失败：
     *          1.将bis_order_info表中座位状态为退票失败
     *          2.将退票失败的座位信息添加到bis_order_info
     * 非自有库存退票失败:
     *          1.将座位状态修改为退票失败(status=6)
     */
    public void refundTicketFail(TicketRefundInfoBean ticketRefundInfoBean) {
        int result = 0;
        if (ticketRefundInfoBean.getSource() == 2) {
            // 自有库存退票失败 将退票信息插入bis_order_info表中座位状态为退票失败
            BisOrderInfo bisOrderInfo =
                    assembleBisOrderInfo(ticketRefundInfoBean, SeatStatus.REFUND_FAILED.getVal());
            result = bisOrderInfoMapper.insert(bisOrderInfo);
            logger.info("自有库存中订单号{}对应座位退票失败, 插入订单表{}条记录", bisOrderInfo.getFixOrderId(), result);//TODO
        } else {
            // 非自有库存
            BisOrderInfo bisOrderInfo = ticketRefundInfoBean.getBisOrderInfo();
            result =
                    bisOrderInfoMapper.updateRefundCommonSeatByFixOrderId(bisOrderInfo.getFixOrderId(),//TODO  传用户的Id
                            SeatStatus.REFUND_FAILED.getVal());
            logger.info("非自有库存中订单号{}对应座位退票失败, 更新订单表{}条记录", bisOrderInfo.getFixOrderId(), result);//TODO
        }
    }


    public BisOrderInfo assembleBisOrderInfo(TicketRefundInfoBean ticketRefundInfoBean,
                                         Byte seatStatus) {
        LocalOrderInfo localOrderInfo = ticketRefundInfoBean.getLocalOrderInfo();
        BisOrderInfo bisOrderInfo = new BisOrderInfo();
        bisOrderInfo.setFixOrderId(localOrderInfo.getFixOrderId());
        bisOrderInfo.setCinemaNo(localOrderInfo.getCinemaNo());
        bisOrderInfo.setTpOrderId(localOrderInfo.getTpOrderId());
        bisOrderInfo.setSeat(localOrderInfo.getSeat());
        bisOrderInfo.setSeatStatus(seatStatus);
        bisOrderInfo.setScheduleId(localOrderInfo.getScheduleId());
        bisOrderInfo.setShowTime(localOrderInfo.getShowTime());
        bisOrderInfo.setChannelId(localOrderInfo.getChannelId());
        bisOrderInfo.setTicketCode(localOrderInfo.getTicketCode());
        bisOrderInfo.setCreatedTime(localOrderInfo.getCreatedTime());
        bisOrderInfo.setChannelId(localOrderInfo.getChannelId());
        bisOrderInfo.setSource(localOrderInfo.getSource());
        bisOrderInfo.setTpExtOrderId(localOrderInfo.getTpExtOrderId());
        return bisOrderInfo;
    }
}
