package com.wepiao.goods.service.impl;

import com.google.common.collect.Sets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;
import com.wepiao.goods.common.entity.CommonResponse;
import com.wepiao.goods.common.entity.GoodsException;
import com.wepiao.goods.common.entity.ResponseMessage;
import com.wepiao.goods.common.enums.PaymentType;
import com.wepiao.goods.common.enums.SeatStatus;
import com.wepiao.goods.common.utils.Bis;
import com.wepiao.goods.mapper.goods.BisOrderInfoMapper;
import com.wepiao.goods.mapper.goods.LocalOrderInfoMapper;
import com.wepiao.goods.model.goods.BisOrderInfo;
import com.wepiao.goods.model.goods.LocalOrderInfo;
import com.wepiao.goods.service.ScheduleService;
import com.wepiao.goods.service.TicketDrawService;
import com.wepiao.goods.vo.DrawTicketResult;
import com.wepiao.goods.vo.Schedule;
import com.wepiao.goods.vo.TpOrderInfo;
import com.wxmovie.bis.ticketplatform.entity.BisTicketInfo;
import com.wxmovie.bis.ticketplatform.result.IBisResult;

@Service
public class TicketDrawServiceImpl implements TicketDrawService {

  @Autowired
  private BisOrderInfoMapper bisOrderInfoMapper;

  @Autowired
  private LocalOrderInfoMapper localOrderInfoMapper;

  @Autowired
  private ScheduleService scheduleService;
  private Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);

  @Override
  public Set<TpOrderInfo> drawTicket(String orderId, String phoneNum, String requestId,
      Integer paymentType, String paymentId, Double realPayment) throws GoodsException {
    Set<TpOrderInfo> sets = Sets.newHashSet();
    List<LocalOrderInfo> localOrderInfos = localOrderInfoMapper.getByOrderId(orderId);
    BisOrderInfo bisOrderInfo = bisOrderInfoMapper.getByOrderId(orderId);

    if (bisOrderInfo == null && (localOrderInfos == null || localOrderInfos.size() == 0)) {
      logger.error("库存中不存在要查询的订单 orderId:{}", orderId);
      throw new GoodsException(ResponseMessage.SE90002);
    }
    Integer scheduleId = null;
    Integer cinemaNo = null;
    Integer channelId = null;
    if (CollectionUtils.isNotEmpty(localOrderInfos)) {
      LocalOrderInfo localOrderInfo = localOrderInfos.get(0);
      scheduleId = localOrderInfo.getScheduleId();
      cinemaNo = localOrderInfo.getCinemaNo();
      channelId = localOrderInfo.getChannelId();
    } else {
      scheduleId = bisOrderInfo.getScheduleId();
      cinemaNo = bisOrderInfo.getCinemaNo();
      channelId = bisOrderInfo.getChannelId();
    }
    // scheduleService.getSchedule(scheduleId, cinemaNo, String.valueOf(channelId));
    Schedule schedule = scheduleService.getSchedule(scheduleId, cinemaNo, channelId);
    if (bisOrderInfo != null) {
      DrawTicketResult drawTicketResult =
          drawTicketAndRecord(schedule, bisOrderInfo, phoneNum, paymentType, paymentId, realPayment);
      TpOrderInfo tpOrderInfo = new TpOrderInfo();
      tpOrderInfo.setSeat(bisOrderInfo.getSeat());
      tpOrderInfo.setTicketCode(drawTicketResult.getTicketCode());
      tpOrderInfo.setTpOrderId(drawTicketResult.getTpOrderId());
      String tpOrderId = null;
      if (!Strings.isNullOrEmpty(drawTicketResult.getTpOrderId())) {
        tpOrderId = drawTicketResult.getTpOrderId();
      } else {
        if (!Strings.isNullOrEmpty(bisOrderInfo.getTpOrderId())) {
          tpOrderId = bisOrderInfo.getTpOrderId();
        }
      }
      tpOrderInfo.setTpOrderId(tpOrderId);
      sets.add(tpOrderInfo);
    }
    if (CollectionUtils.isNotEmpty(localOrderInfos)) {
      Set<TpOrderInfo> drawLocalResult =
          drawLocalTicket(schedule, localOrderInfos, orderId, phoneNum);
      sets.addAll(drawLocalResult);
  }
    return sets;
  }

  private Set<TpOrderInfo> drawLocalTicket(Schedule schedule,
      List<LocalOrderInfo> localOrderInfos, String orderId, String mobileNo) throws GoodsException {
    Set<TpOrderInfo> sets = Sets.newHashSet();
    Set<Integer> ids =
        FluentIterable.from(localOrderInfos).transform(new Function<LocalOrderInfo, Integer>() {
          @Override
          public Integer apply(LocalOrderInfo localOrderInfo) {
            return localOrderInfo.getId();
          }
        }).toSet();
    Integer drawCount = localOrderInfoMapper.drawTickets(ids);
    if (drawCount != null && drawCount.equals(ids.size())) {
      for (LocalOrderInfo localOrderInfo : localOrderInfos) {
        TpOrderInfo tpOrderInfo = new TpOrderInfo();
        tpOrderInfo.setSeat(localOrderInfo.getSeat());
        tpOrderInfo.setTicketCode(localOrderInfo.getTicketCode());
        tpOrderInfo.setTpOrderId(localOrderInfo.getTpOrderId());
        // todo fixOrderId
        sets.add(tpOrderInfo);
      }
      return sets;
    } else {
      logger.error("The order was out of the ticket.");
      throw new GoodsException(ResponseMessage.SE70006);// 出票失败
    }
  }
  @Override
  public CommonResponse<?> drawOneTicketByBis(String orderId, String phoneNum, String requestId) {
    try {
      BisOrderInfo bisOrderInfo = bisOrderInfoMapper.getByOrderId(orderId);
      // Preconditions.checkState(bisOrderInfos.size() > 0, "此接口，订单只包含一个座位");
      Integer scheduleId = bisOrderInfo.getScheduleId();
      Integer channelId = bisOrderInfo.getChannelId();
      Integer cinemaNo = bisOrderInfo.getCinemaNo();

      Schedule schedule = scheduleService.getSchedule(scheduleId, cinemaNo, channelId);
      DrawTicketResult result =
          drawTicketAndRecord(schedule, bisOrderInfo, phoneNum, null, null, null);

      if (result != null) {
        // 添加自有库存记录。
        LocalOrderInfo localOrderInfo = new LocalOrderInfo();
        Date createTime = new Date();
        localOrderInfo.setChannelId(scheduleId);
        localOrderInfo.setCinemaNo(cinemaNo);
        localOrderInfo.setCreatedTime(createTime);
        localOrderInfo.setFixOrderId(orderId);
        localOrderInfo.setLocalSeatStatus(SeatStatus.UNLOCKED.getVal());
        localOrderInfo.setModifiedTime(createTime);
        localOrderInfo.setShowTime(bisOrderInfo.getShowTime());
        localOrderInfo.setPriceRecordId(schedule.getRecordId());
        localOrderInfo.setSeat(bisOrderInfo.getSeat());
        localOrderInfo.setSource(bisOrderInfo.getSource());
        localOrderInfo.setTicketCode(result.getTicketCode());
        localOrderInfo.setTpExtOrderId(bisOrderInfo.getTpExtOrderId());
        localOrderInfo.setTpOrderId(bisOrderInfo.getTpOrderId());
        localOrderInfo.setScheduleId(schedule.getBaseScheduleId());
        localOrderInfo.setLockTimeout(10);
        localOrderInfoMapper.insert(localOrderInfo);
      }
    } catch (GoodsException e) {
      logger.error("drawOneTicketByBis error: ", e);
      return e.getCommonResponse();
    } catch (Exception e) {
      logger.error("drawOneTicketByBis error: ", e);
      return CommonResponse.fail();
    }
    return CommonResponse.success();
  }

  /**
   * 出票并记录到库存表
   */
  private DrawTicketResult drawTicketAndRecord(Schedule schedule, BisOrderInfo bisOrderInfo,
      String mobile, Integer paymentType, String paymentId, Double realPayment) {
    DrawTicketResult result =
        drawOneTicket(schedule, bisOrderInfo, mobile, paymentType, paymentId, realPayment);
    bisOrderInfo.setTicketCode(result.getTicketCode());
    if (!Strings.isNullOrEmpty(result.getTpOrderId())) {
      bisOrderInfo.setTpOrderId(result.getTpOrderId());
    }

    bisOrderInfo.setSeatStatus(SeatStatus.DRAWED.getVal());
    bisOrderInfoMapper.updateSelective(bisOrderInfo);
    return result;
  }

  private DrawTicketResult drawOneTicket(Schedule schedule, BisOrderInfo bisOrderInfo,
      String mobileNo, Integer paymentType, String paymentId, Double realPayment) {

    DrawTicketResult drawTicketResult =
        doDrawTicket(schedule, bisOrderInfo.getFixOrderId(), bisOrderInfo.getTpOrderId(),
            bisOrderInfo.getSeat(), mobileNo, paymentType, paymentId, realPayment);
    return drawTicketResult;
  }

  private DrawTicketResult doDrawTicket(Schedule schedule, String orderId, String tpOrderId,
      String seat, String mobileNo, Integer paymentType, String paymentId, Double realPayment) {
    com.wxmovie.bis.ticketplatform.entity.BisOrderInfo orderInfo =
        new com.wxmovie.bis.ticketplatform.entity.BisOrderInfo();
    orderInfo.setMobile(mobileNo);
    orderInfo.setSelfOrderNo(orderId);
    orderInfo.setOrderNo(tpOrderId);
    orderInfo.setSeats(seat);
    // bo.setExOrderNo(exOrderNo);//(bizOrderInfoTmp.getOrderDetail().get(0).getBisExtCode());
    // bo.setTpExtOrderId(tpExtOrderId);

    orderInfo.setCinemaNo(schedule.getBisCinemaNo());
    orderInfo.setCinemaLinkId(schedule.getCinemaLinkId());
    orderInfo.setHallNo(schedule.getHallNo());
    orderInfo.setLocNo(schedule.getSectionNo());
    orderInfo.setFilmNo(schedule.getBisMovieNo());
    orderInfo.setShowSeqNo(schedule.getShowSeqNo());
    orderInfo.setSeqNo(schedule.getSeqNo());
    orderInfo.setShowDate(schedule.getShowDate());
    orderInfo.setShowTime(schedule.getShowTime());

    int seatNum = seat.split(",").length;
    double totalCash = schedule.getActualSellPrice().doubleValue() * seatNum;
    orderInfo.setAllMoney(totalCash);
    orderInfo.setCash(totalCash);// 这个是实际支付金额
    orderInfo.setSinglePrice(schedule.getActualSellPrice().doubleValue());
    orderInfo.setSettlementPrice(schedule.getSettlementPrice().doubleValue());

    if (null != paymentType && PaymentType.MEMBER.getVal().equals(paymentType)) {
      orderInfo.setPayChannelType(String.valueOf(paymentType));
      orderInfo.setBankOrderId(paymentId);
      orderInfo.setSettlementPrice(realPayment);
    }

    IBisResult result = Bis.drawTicket(orderInfo, schedule.getBisServerId());
    if (result != null && "succ".equalsIgnoreCase(result.getErrorMsg())) {
      BisTicketInfo ticketInfo = result.getTicket();
      String ticketNo = ticketInfo.getVoucherId();// 单码
      if (Strings.isNullOrEmpty(ticketNo)) {
        ticketNo = ticketInfo.getExtOrderNo() + "|" + ticketInfo.getTicketNo();// 双码
      }
      return new DrawTicketResult(ticketNo, ticketInfo.getOrderNo());
    } else {
      throw new GoodsException(ResponseMessage.TP_ERROR.withMsg(result.getErrorMsg()).withSub(
          result.getErrorCode()));
    }
  }

}
