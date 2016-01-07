package com.wepiao.goods.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.wepiao.goods.common.entity.GoodsException;
import com.wepiao.goods.common.utils.Bis;
import com.wepiao.goods.model.goods.BisOrderInfo;
import com.wepiao.goods.model.goods.LocalOrderInfo;
import com.wepiao.goods.model.goods.TicketRefundInfoBean;
import com.wxmovie.bis.ticketplatform.entity.ReturnTicketInfo;
import com.wxmovie.bis.ticketplatform.result.IBisResult;

/**
 * Created by zhangliang on 2015/12/30.
 * Function:退票服务层实现类
 */
@Service("ticketRefundServiceImpl")
public class TicketRefundServiceImpl extends BaseSeatRefundService{
    /**
    * 当前时间距离上映时间的时间间隔
    * */
    @Value("${wp-goods-center.refund.interval}")
    private long interval;
    @Override
    public boolean refundTicket(String orderId) throws GoodsException {
        //退票成功标志
        boolean flag = false;
        try {
            //1.查询自有库存中用户订单所对应的座位信息
            List<LocalOrderInfo> localOrderInfos = localOrderInfoMapper.selectLocalSeatsByOrderId(orderId);

            /**
             * 非自有库存中用户订单所对应的座位信息
             * 注:这里的orderId相当于非自有库存中的FixOrderId
             * */
            BisOrderInfo bisOrderInfo = bisOrderInfoMapper.getByOrderId(orderId);

            //创建所有的退票请求的对象
            ArrayList<TicketRefundInfoBean> ticketRefundInfoList = Lists.newArrayList();
            int counter = 0;

            //自有库存处理逻辑
            if(localOrderInfos != null && localOrderInfos.size()>0){
                Iterator<LocalOrderInfo> localOrderInfoIterator = localOrderInfos.iterator();
                while(localOrderInfoIterator.hasNext()){
                    //将退票请求直接加入到退票集合中
                    LocalOrderInfo localOrderInfo = localOrderInfoIterator.next();
                    List<Object> returnTicketParams = getReturnTicketParams(localOrderInfo.getScheduleId(), localOrderInfo.getCinemaNo(),
                            localOrderInfo.getChannelId(),
                            localOrderInfo.getTpOrderId(), 1);
                    TicketRefundInfoBean ticketRefundInfoBean = new TicketRefundInfoBean();
                    ticketRefundInfoBean.setReturnTicketInfo((ReturnTicketInfo) returnTicketParams.get(0));
                    ticketRefundInfoBean.setBisServerId(String.valueOf(returnTicketParams.get(1)));
                    ticketRefundInfoBean.setRetryTimes(0);
                    ticketRefundInfoBean.setSource(2);
                    ticketRefundInfoBean.setChannelId(localOrderInfo.getChannelId());
                    ticketRefundInfoBean.setLocalOrderInfo(localOrderInfo);
                    //将退票实体添加到退票集合中
                    ticketRefundInfoList.add(ticketRefundInfoBean);
                }
                logger.info("自有库存中退票成功数量为：{}", counter);
            }


            //非自有库存处理逻辑
            if(bisOrderInfo != null){
                List<Object> returnTicketParams = getReturnTicketParams(bisOrderInfo.getScheduleId(), bisOrderInfo.getCinemaNo(),
                        bisOrderInfo.getChannelId(),
                        bisOrderInfo.getTpOrderId(), 1);
                TicketRefundInfoBean ticketRefundInfoBean = new TicketRefundInfoBean();
                ticketRefundInfoBean.setReturnTicketInfo((ReturnTicketInfo)returnTicketParams.get(0));
                ticketRefundInfoBean.setBisServerId(String.valueOf(returnTicketParams.get(1)));
                ticketRefundInfoBean.setRetryTimes(0);
                ticketRefundInfoBean.setSource(1);
                ticketRefundInfoBean.setChannelId(bisOrderInfo.getChannelId());
                ticketRefundInfoBean.setBisOrderInfo(bisOrderInfo);
                //将退票实体添加到退票集合中
                ticketRefundInfoList.add(ticketRefundInfoBean);
            }

            //退票
            for (TicketRefundInfoBean ticketRefundInfoBean : ticketRefundInfoList) {
                IBisResult result = Bis.returnTicket(ticketRefundInfoBean.getReturnTicketInfo(), ticketRefundInfoBean.getBisServerId());
                if(result != null && "succ".equals(result.getErrorMsg())){
                    flag = true;
                    refundTicketOk(ticketRefundInfoBean);
                }else{
                    refundTicketFail(ticketRefundInfoBean);
                }
            }
        } catch (Exception e) {
            logger.error("RunningException",e.getMessage());
            e.printStackTrace();
        }
        //只要有一个座位退票成功    就返回true
        return flag;
    }
}
