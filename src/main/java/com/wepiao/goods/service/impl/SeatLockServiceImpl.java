package com.wepiao.goods.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.wepiao.common.utils.DateUtil;
import com.wepiao.common.utils.JsonMapper;
import com.wepiao.goods.common.entity.CommonResponse;
import com.wepiao.goods.common.entity.GoodsException;
import com.wepiao.goods.common.entity.ResponseMessage;
import com.wepiao.goods.common.enums.SeatStatus;
import com.wepiao.goods.common.utils.Bis;
import com.wepiao.goods.common.utils.Constants;
import com.wepiao.goods.mapper.goods.BisOrderInfoMapper;
import com.wepiao.goods.mapper.goods.LocalOrderInfoMapper;
import com.wepiao.goods.model.goods.BisOrderInfo;
import com.wepiao.goods.model.goods.LocalOrderInfo;
import com.wepiao.goods.service.CommonService;
import com.wepiao.goods.service.ScheduleService;
import com.wepiao.goods.service.SeatLockService;
import com.wepiao.goods.vo.Schedule;
import com.wepiao.goods.vo.SeatLockParam;
import com.wepiao.goods.vo.TpOrderInfo;
import com.wxmovie.bis.ticketplatform.result.IBisResult;

@Service
public class SeatLockServiceImpl implements SeatLockService {

  @Autowired
  LocalOrderInfoMapper localOrderInfoMapper;
  @Autowired
  CommonService commonService;
  @Autowired
  private ScheduleService scheduleService;
  @Autowired
  private BisOrderInfoMapper bisOrderInfoMapper;

  private static final Logger logger = LoggerFactory.getLogger(SeatLockServiceImpl.class);

  @Override
  public CommonResponse<?> lock(SeatLockParam seatLockParam) {
    List<TpOrderInfo> tpOrderInfos = Lists.newArrayList();
    try {
      List<String> seatList = Splitter.on(",").splitToList(seatLockParam.getSeats());

      List<LocalOrderInfo> localOrderInfos = Lists.newArrayList();
      List<String> cinemaSeats = Lists.newArrayList();
      commonService.groupSeat(seatLockParam.getScheduleId(), seatList, localOrderInfos, cinemaSeats);

      if (CollectionUtils.isNotEmpty(cinemaSeats)) {
        TpOrderInfo tpOrderInfo = lockCinemaSeats(cinemaSeats, seatLockParam);
        tpOrderInfo.setSeat(Joiner.on(",").join(cinemaSeats));
        tpOrderInfos.add(tpOrderInfo);
      }
      if (CollectionUtils.isNotEmpty(localOrderInfos)) {
        List<TpOrderInfo> list = lockLocalSeats(localOrderInfos, seatLockParam);
        tpOrderInfos.addAll(list);
      }
    } catch (GoodsException e) {
      logger.error("lock seat error: ", e);
      return e.getCommonResponse();
    } catch (Exception e) {
      logger.error("lock seat error: ", e);
      return CommonResponse.fail();
    }
    return CommonResponse.success(tpOrderInfos);
  }

  @Override
  public Boolean unlock(String orderId) {
    List<LocalOrderInfo> localOrderInfos = localOrderInfoMapper.getByOrderId(orderId);
    BisOrderInfo bisOrderInfo = bisOrderInfoMapper.getByOrderId(orderId);
    if (bisOrderInfo == null  && localOrderInfos.size() == 0) {
      throw new GoodsException(ResponseMessage.SE70008);
    }
    Integer scheduleId = null;
    Integer tpId = null;
    Integer cinemaNo = null;
    if (CollectionUtils.isNotEmpty(localOrderInfos)) {
      LocalOrderInfo localOrderInfo = localOrderInfos.get(0);
      scheduleId = localOrderInfo.getScheduleId();
      tpId = localOrderInfo.getChannelId();
      cinemaNo = localOrderInfo.getCinemaNo();
    } else {
      scheduleId = bisOrderInfo.getScheduleId();
      tpId = bisOrderInfo.getChannelId();
      cinemaNo = bisOrderInfo.getCinemaNo();
    }

    Schedule schedule =
        scheduleService.getSchedule(scheduleId, cinemaNo, tpId);
    boolean unlockSeatFlag = true;
    // 解锁黄金坐席
    if (localOrderInfos != null && localOrderInfos.size() > 0) {
      int unlockCount = unlockLocalSeats(localOrderInfos);
      if (unlockCount != localOrderInfos.size()) {
        unlockSeatFlag = false;
        throw new GoodsException(ResponseMessage.SE70007);
      }
    }
    // 向影院发起解锁请求
    if (bisOrderInfo != null) {
      if (!SeatStatus.LOCKED.getVal().equals(bisOrderInfo.getSeatStatus())) {
        throw new GoodsException(ResponseMessage.SE70008);
      }
      // 向第三方影院请求解锁
      boolean unlockResult = unlockCinemaSeat(bisOrderInfo, schedule);

    }
    return unlockSeatFlag;
  }

  /**
   * 定时解锁超时订单。
   */
  @Override
  public void cronUnlock() {
    List<String> overtimeOrderIds = localOrderInfoMapper.getOvertimeOrders();
    for (String orderId : overtimeOrderIds) {
      localOrderInfoMapper.unlockLocalSeat(orderId);
    }
  }

  private boolean unlockCinemaSeat(BisOrderInfo bisOrderInfo, Schedule schedule
     ) {
    //List<String> seatList = Splitter.on("|").splitToList(bisOrderInfo.getSeat());
    boolean flag=doUnlockSeat(schedule, bisOrderInfo);
    int seatStatus = bisOrderInfo.getSeatStatus();
    int lockTicket = SeatStatus.LOCKED.getVal();
    int initTicket = SeatStatus.UNLOCKDONE.getVal();

    if (seatStatus == lockTicket) {
      // begin to unlock cinemaSeat
      bisOrderInfo.setSeatStatus((byte)initTicket);
      int i = bisOrderInfoMapper.updateSelective(bisOrderInfo);
    } else {
      throw new GoodsException(ResponseMessage.SE70008);
    }

    return flag;
  }

  /**
   * 解锁自有库存
   */
  private int unlockLocalSeats(List<LocalOrderInfo> LocalOrderInfos)  {
    int unlockCount = 0;
    for (LocalOrderInfo localOrderInfo : LocalOrderInfos) {
      // 判断当前座位状态类型
      int seatStatus = localOrderInfo.getLocalSeatStatus();
      int lockTicket = SeatStatus.LOCKED.getVal();
      int initTicket = SeatStatus.UNLOCKED.getVal();
      // 如果当前座位为锁定状态
      if (seatStatus == lockTicket) {
        // begin to unlock localHotseat
        localOrderInfo.setLocalSeatStatus((byte)initTicket);
        localOrderInfo.setOrderId("");
        localOrderInfo.setLockTime(null);
        int count = localOrderInfoMapper.updateSelective(localOrderInfo);
        unlockCount = unlockCount + count;
      } else {
        throw new GoodsException(ResponseMessage.SE70007);
      }
    }
    return unlockCount;
  }

  private boolean doUnlockSeat(Schedule schedule,BisOrderInfo seatBean) {
    com.wxmovie.bis.ticketplatform.entity.BisOrderInfo bisOrderInfo =
        new com.wxmovie.bis.ticketplatform.entity.BisOrderInfo();
    bisOrderInfo.setSeqNo(schedule.getSeqNo());// 第三方排期编号
    bisOrderInfo.setShowSeqNo(schedule.getShowSeqNo());// 排期内部编号
    bisOrderInfo.setCinemaNo(schedule.getBisCinemaNo());// 第三方影院编号
    bisOrderInfo.setCinemaLinkId(schedule.getCinemaLinkId());// 第三方影院LinkId
    bisOrderInfo.setHallNo(schedule.getHallNo());// 第三方影厅编号 //orderInfo.getBisRoomNo()
    bisOrderInfo.setLocNo(schedule.getSectionNo());// 场区编号
    bisOrderInfo.setFilmNo(schedule.getBisMovieNo());
    bisOrderInfo.setShowDate(schedule.getShowDate());
    bisOrderInfo.setShowTime(schedule.getShowTime());
    // bisOrderInfo.setAllMoney();//orderInfo.getPayTotal().doubleValue()
    bisOrderInfo.setSinglePrice(schedule.getActualSellPrice().doubleValue());// 实际售卖价
    bisOrderInfo.setSettlementPrice(schedule.getSettlementPrice().doubleValue());// 结算价

    bisOrderInfo.setOrderNo(seatBean.getTpOrderId());
    bisOrderInfo.setSelfOrderNo(seatBean.getFixOrderId());
    bisOrderInfo.setMobile("13800138000");
    bisOrderInfo.setSeats(seatBean.getSeat());// 座位 区_行_列|区_行_列

    IBisResult result = Bis.unLockSeat(bisOrderInfo, schedule.getBisServerId());

    if (result != null) {
      if (Constants.SUCC.equals(result.getErrorMsg())) {
        return true;
      } else {
        throw new GoodsException(
            ResponseMessage.TP_ERROR.withSub(result.getErrorCode()).withMsg(result.getErrorMsg()));
      }
    } else {
      throw new GoodsException(ResponseMessage.SE70008);
    }
  }

  private TpOrderInfo lockCinemaSeats(List<String> cinemaSeats, SeatLockParam seatLockParam) {
    Schedule schedule = scheduleService.getSchedule(seatLockParam.getScheduleId(),
        seatLockParam.getBaseCinemaNo(), seatLockParam.getTpId());

    String seats = Joiner.on("|").join(cinemaSeats);
    TpOrderInfo tpOrderInfo =
        doLockSeatAndRecord(schedule, seats, seatLockParam.getMobile(), seatLockParam.getOrderId(), cinemaSeats.size());
    return tpOrderInfo;
  }

  /**
   * 锁坐并记录
   */
  private TpOrderInfo doLockSeatAndRecord(Schedule schedule, String seat, String mobileNo,
      String orderId, int num) {
    TpOrderInfo result = doLockSeat(schedule, seat, mobileNo, orderId);

    logger.debug("doLockSeatAndRecord return TpOrderInfo: " + JsonMapper.getInstance().toJson(result));

    // 插入库存表
    BisOrderInfo bisOrderInfo = new BisOrderInfo();
    bisOrderInfo.setFixOrderId(orderId);
    bisOrderInfo.setScheduleId(schedule.getBaseScheduleId());
    bisOrderInfo.setCinemaNo(schedule.getCinemaNo());
    bisOrderInfo.setChannelId(Integer.parseInt(schedule.getTpId()));
    bisOrderInfo.setSeat(seat);
    bisOrderInfo.setNum((byte)num);
    bisOrderInfo.setSeatStatus(SeatStatus.LOCKED.getVal());
    bisOrderInfo.setTpOrderId(result.getTpOrderId());
    bisOrderInfo.setTpExtOrderId(result.getTpExtOrderId());
    bisOrderInfo.setTicketCode(result.getTicketCode());
    bisOrderInfo.setPriceRecordId(schedule.getRecordId());
    String showTime = schedule.getShowTime();
    Date lockTime = new Date();
    Date showTimeDate = DateUtil.string2date(showTime, DateUtil.FORMAT_DATETIME);
     bisOrderInfo.setShowTime(showTimeDate);
     bisOrderInfo.setModifiedTime(lockTime);
     bisOrderInfo.setCreatedTime(lockTime);

     bisOrderInfoMapper.insert(bisOrderInfo);
    logger.info("insert BisOrderInfo succeed, orderId:{}", orderId);
    return result;
  }

  private List<TpOrderInfo> lockLocalSeats(List<LocalOrderInfo> localOrderInfos,
      SeatLockParam seatLockParam) {
    List<TpOrderInfo> list = Lists.newArrayList();
    Set<Integer> ids =
        FluentIterable.from(localOrderInfos).transform(new Function<LocalOrderInfo, Integer>() {
          @Override
          public Integer apply(LocalOrderInfo LocalOrderInfo) {
            return LocalOrderInfo.getId();
          }
        }).toSet();

    Integer lockCount = null;
    int lockedCount = localOrderInfoMapper.lockSeats(ids, seatLockParam.getOrderId());
    if (ids.size() == lockedCount) {
      for (LocalOrderInfo item : localOrderInfos) {
        TpOrderInfo tpOrderInfo = new TpOrderInfo();
        tpOrderInfo.setTpOrderId(item.getTpOrderId());
        tpOrderInfo.setTpExtOrderId(item.getTpExtOrderId());
        tpOrderInfo.setSeat(item.getSeat());
        list.add(tpOrderInfo);
      }
      return list;
    } else {
      throw new GoodsException("锁坐失败");
    }
  }

  private TpOrderInfo doLockSeat(Schedule schedule, String seat, String mobileNo, String orderId) {
    com.wxmovie.bis.ticketplatform.entity.BisOrderInfo bisOrderInfo =
        new com.wxmovie.bis.ticketplatform.entity.BisOrderInfo();
    bisOrderInfo.setSeqNo(schedule.getSeqNo());// 第三方排期编号
    bisOrderInfo.setShowSeqNo(schedule.getShowSeqNo());// 排期内部编号
    bisOrderInfo.setCinemaNo(schedule.getBisCinemaNo());// 第三方影院编号
    bisOrderInfo.setCinemaLinkId(schedule.getCinemaLinkId());// 第三方影院LinkId
    bisOrderInfo.setHallNo(schedule.getHallNo());// 第三方影厅编号 //orderInfo.getBisRoomNo()
    bisOrderInfo.setLocNo(schedule.getSectionNo());// 场区编号
    bisOrderInfo.setFilmNo(schedule.getBisMovieNo());
    bisOrderInfo.setShowDate(schedule.getShowDate());
    bisOrderInfo.setShowTime(schedule.getShowTime());

    int seatNum = seat.split(Constants.SYMBOL_VERTICAL_BAR_ESCAPE).length;
    bisOrderInfo.setAllMoney(schedule.getActualSellPrice().doubleValue() * seatNum);
    bisOrderInfo.setSinglePrice(schedule.getActualSellPrice().doubleValue());// 实际售卖价
    bisOrderInfo.setSettlementPrice(schedule.getSettlementPrice().doubleValue());// 结算价

    bisOrderInfo.setSelfOrderNo(orderId);
    bisOrderInfo.setMobile(mobileNo);
    bisOrderInfo.setSeats(seat);// 座位 区_行_列|区_行_列

    IBisResult result = Bis.lockSeat(bisOrderInfo, schedule.getBisServerId());
    logger.debug("Bis.lockSeat result: {}", JsonMapper.getInstance().toJson(result));
    if (result != null) {
      if (Constants.SUCC.equals(result.getErrorMsg())) {
        TpOrderInfo tpOrderInfo = new TpOrderInfo();
         tpOrderInfo.setTpOrderId(result.getOrder().getOrderNo());
        String exOrderNo = result.getOrder().getExOrderNo();
        tpOrderInfo.setTpExtOrderId(Strings.isNullOrEmpty(exOrderNo) ? "" : exOrderNo);
        return tpOrderInfo;
      } else {
        throw new GoodsException(
            ResponseMessage.TP_ERROR.withSub(result.getErrorCode()).withMsg(result.getErrorMsg()));
      }
    } else {
      throw new GoodsException(ResponseMessage.SE80002);
    }
  }
}
