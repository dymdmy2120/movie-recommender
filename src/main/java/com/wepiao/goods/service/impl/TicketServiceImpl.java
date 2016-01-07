package com.wepiao.goods.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.wepiao.goods.common.constant.Contants;
import com.wepiao.goods.common.entity.CommonResponse;
import com.wepiao.goods.common.entity.GoodsException;
import com.wepiao.goods.common.entity.ResponseMessage;
import com.wepiao.goods.common.enums.SeatStatus;
import com.wepiao.goods.mapper.goods.BisOrderInfoMapper;
import com.wepiao.goods.mapper.goods.LocalOrderInfoMapper;
import com.wepiao.goods.model.goods.BisOrderInfo;
import com.wepiao.goods.model.goods.LocalOrderInfo;
import com.wepiao.goods.service.ScheduleService;
import com.wepiao.goods.service.TicketService;
import com.wepiao.goods.vo.Schedule;
import com.wepiao.goods.vo.TicketInfo;
import com.wepiao.goods.vo.VoucherCode;
import com.wxmovie.bis.ticketplatform.BisGateFacade;
import com.wxmovie.bis.ticketplatform.entity.BisTicketInfo;
import com.wxmovie.bis.ticketplatform.result.IBisResult;

/**
 * ClassName:TicketServiceImpl 关于票业务的查询和核销操作
 * @author dynamo
 */
@Service
public class TicketServiceImpl implements TicketService {
  @Autowired
  private ScheduleService scheduleService;
  @Autowired
  private BisOrderInfoMapper bisOrderInfoMapper;
  @Autowired
  private LocalOrderInfoMapper localOrderInfoMapper;
  private Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);

  @Override
  public List<TicketInfo> queryTicketStatus(String orderId) throws GoodsException {
    List<TicketInfo> ticketInfos = Lists.newArrayList();
    BisOrderInfo bisOrderInfo = bisOrderInfoMapper.getByOrderId(orderId);
    List<LocalOrderInfo> localOrderInfos = localOrderInfoMapper.getByOrderId(orderId);
    Schedule schedule = getSchedule(orderId, bisOrderInfo, localOrderInfos);
    if (bisOrderInfo != null) { // 该订单是直接到接入商进行购买的，并没有购买自由库存的座位
      TicketInfo ticketInfo = new TicketInfo();
      BisTicketInfo bisTicketInfo =
          this.getBisTicketInfo(bisOrderInfo.getCinemaNo(), bisOrderInfo.getTicketCode(),
              bisOrderInfo.getSeat(), bisOrderInfo.getTpOrderId(), schedule);
      ticketInfo.setTpOrderId(bisOrderInfo.getTpOrderId());
      ticketInfo.setSeatCode(bisOrderInfo.getSeat());
      String ticketCode = bisOrderInfo.getTicketCode();
      if (ticketCode != null) {
        ticketInfo.setTicketCode(ticketCode);
      }
      IBisResult bisResult =
          this.getBisResult(bisTicketInfo, null, schedule, 0, ResponseMessage.SE70001);
      if (null != bisResult && Contants.SUCC.equals(bisResult.getErrorMsg())) {
          ticketInfo.setOrderStatus(bisResult.getTicket().getTicketStatus().getValue());
          ticketInfo.setTsFlag(true);
      }
      ticketInfos.add(ticketInfo);
    }
    if (localOrderInfos != null && localOrderInfos.size() > 0) {// 订购的时自由库存的座位
      List<TicketInfo> tickets = this.getLocalOrderInfos(localOrderInfos);
      ticketInfos.addAll(tickets);
    }
    return ticketInfos;
  }

  @Override
  public List<TicketInfo> queryOrderStatus(String orderId) throws GoodsException {
    List<TicketInfo> ticketInfos = Lists.newArrayList();
    BisOrderInfo bisOrderInfo = bisOrderInfoMapper.getByOrderId(orderId);
    if (bisOrderInfo == null) {
      logger.error("库存中不存在要查询的订单 orderId:{}", orderId);
      throw new GoodsException(ResponseMessage.SE90002);
    }
    List<LocalOrderInfo> localOrderInfos = localOrderInfoMapper.getByOrderId(orderId);
    List<TicketInfo> ticketInfoForLocal = this.getLocalOrderInfos(localOrderInfos);
    TicketInfo ticketInfoForBisOrderInfo = this.queryTicketForBisOrderInfo(orderId);
    ticketInfos.addAll(ticketInfoForLocal);
    ticketInfos.add(ticketInfoForBisOrderInfo);
    return ticketInfos;
  }

  @Override
  public List<VoucherCode> queryCode(String orderId) throws GoodsException {
    List<VoucherCode> codes = Lists.newArrayList();
    BisOrderInfo bisOrderInfo = bisOrderInfoMapper.getByOrderId(orderId);
    List<LocalOrderInfo> localOrderInfos = localOrderInfoMapper.getByOrderId(orderId);
    Schedule schedule = getSchedule(orderId, bisOrderInfo, localOrderInfos);
    if (bisOrderInfo != null) { //订了非自由库存的座
      BisTicketInfo bisTicketInfo =
          this.getBisTicketInfo(bisOrderInfo.getCinemaNo(), bisOrderInfo.getTicketCode(),
              bisOrderInfo.getSeat(), bisOrderInfo.getTpOrderId(), schedule);
      IBisResult bisResult =
          this.getBisResult(bisTicketInfo, null, schedule, 2, ResponseMessage.SE70003);
      if (bisResult != null && Contants.SUCC.equals(bisResult.getErrorMsg())) {
        VoucherCode voucherCode =
            new VoucherCode(bisOrderInfo.getTpOrderId(), bisResult.getTicket().getVoucherNo());
          // 返回的是以某个符号 |分割开来
        /*
         * List<String> voucherNo =
         * Splitter.on("|").trimResults().splitToList(bisResult.getTicket().getVoucherNo());
         */
        codes.add(voucherCode);
      }
    }
    if(localOrderInfos != null && localOrderInfos.size() > 0){ //订了自由库存的座
      for(LocalOrderInfo localOrderInfo: localOrderInfos){
        BisTicketInfo bisTicketInfo =
            this.getBisTicketInfo(localOrderInfo.getCinemaNo(), localOrderInfo.getTicketCode(),
                localOrderInfo.getSeat(), localOrderInfo.getTpOrderId(), schedule);
        try {
        IBisResult bisResult =
              this.getBisResult(bisTicketInfo, null, schedule, 2, ResponseMessage.SE70003);
          if (bisResult != null && Contants.SUCC.equals(bisResult.getErrorMsg())) {
            VoucherCode voucherCode =
                new VoucherCode(localOrderInfo.getTpOrderId(), bisResult.getTicket().getVoucherNo());
            codes.add(voucherCode);
          }
        } catch (Exception e) {// 进行下次循环
          continue;
        }
      }
    }
    return codes;
  }

  @Override
  public boolean verfiyTicket(String orderId) throws GoodsException {
    BisOrderInfo bisOrderInfo = bisOrderInfoMapper.getByOrderId(orderId);
    List<LocalOrderInfo> localOrderInfos = localOrderInfoMapper.getByOrderId(orderId);
    boolean flag = true;
    Schedule schedule = getSchedule(orderId, bisOrderInfo, localOrderInfos);
    //TODO 当非自由库存中的票进行核销时失败了或更新状态失败是否继续往下走
    // 检查自由库存或非自由库存票的状态，若已经是核销则不往下走
    int verfiyLocalVal = checkVerifyValLocalOrderInfo(localOrderInfos);
    int verfiyBisOrdVal = checkVerifyValBisOrderInfo(bisOrderInfo);
    if (verfiyLocalVal == 1 || verfiyBisOrdVal == 1) {
      if (verfiyLocalVal == 1) {// 自由库存的核销
        this.verfiyTicketFromLocOrInfo(localOrderInfos, orderId);
        // 进行再一次确认是否核销成功
        if (checkVerifyValLocalOrderInfo(localOrderInfoMapper.getByOrderId(orderId)) != 0) {
          logger.error("verify ticket from LocalOrderInfo fail");
          flag = false;
        }
      }
      if (verfiyBisOrdVal == 1) {// 非自由库存
        this.verfiyTicketFromBisOrderInfo(bisOrderInfo, orderId);
        if (checkVerifyValLocalOrderInfo(localOrderInfoMapper.getByOrderId(orderId)) != 0) {
          logger.error("verify ticket from BisOrderInfo fail");
          flag = false;
        }
      }
      return flag;
    } else {
      logger.error("verfiy ticket error:{}", "Tickets already be Verfied");
      return true;
    }
  }

  private Schedule getSchedule(String orderId, BisOrderInfo bisOrderInfo,
      List<LocalOrderInfo> localOrderInfos) {
    if(bisOrderInfo == null && (localOrderInfos == null || localOrderInfos.size() == 0)){
      logger.error("库存中不存在要查询的订单 orderId:{}", orderId);
      throw new GoodsException(ResponseMessage.SE90002);
    }
    Integer scheduleId = null;
    Integer cinemaNo = null;
    Integer channelId = null;
    if(CollectionUtils.isNotEmpty(localOrderInfos)){
      LocalOrderInfo localOrderInfo  =  localOrderInfos.get(0);
      scheduleId = localOrderInfo.getScheduleId();
      cinemaNo = localOrderInfo.getCinemaNo();
      channelId = localOrderInfo.getChannelId();
    } else {
      scheduleId = bisOrderInfo.getScheduleId();
      cinemaNo = bisOrderInfo.getCinemaNo();
      channelId = bisOrderInfo.getChannelId();
    }
    // scheduleService.getSchedule(scheduleId, cinemaNo, String.valueOf(channelId));
    return scheduleService.getSchedule(scheduleId, cinemaNo, channelId);
  }
/**
 * 根据orderId得到自由库存的订座信息，然后封装成TicketInfo
 */
  private List<TicketInfo> getLocalOrderInfos(List<LocalOrderInfo> localOrderInfos) {
    List<TicketInfo> ticketInfoList = Lists.newArrayList();
    if (localOrderInfos != null && localOrderInfos.size() > 0) {
      for (int i = 0; i < localOrderInfos.size(); i++) {
        LocalOrderInfo localOrderInfo = localOrderInfos.get(i);
        String ticketCode = localOrderInfo.getTicketCode();
        String seatcode = localOrderInfo.getSeat();
        String tpOrderId = localOrderInfo.getTpOrderId();
        int ticketStatus = localOrderInfo.getLocalSeatStatus();
        TicketInfo ticketInfo = new TicketInfo();
        ticketInfo.setSeatCode(seatcode);
        ticketInfo.setTpOrderId(tpOrderId);
        ticketInfo.setOrderStatus(ticketStatus);
        ticketInfo.setTicketCode(ticketCode);
        ticketInfoList.add(ticketInfo);
      }
    }
    return ticketInfoList;
  }

  /**
   * 根据orderId得到自由库存的订座信息，然后封装成TicketInfo
   */
  private TicketInfo queryTicketForBisOrderInfo(String orderId){
    BisOrderInfo bisOrder = bisOrderInfoMapper.getByOrderId(orderId);
    Integer scheduleId = bisOrder.getScheduleId();
    Integer cinemaNo = bisOrder.getCinemaNo();
    Integer channelId = bisOrder.getChannelId();
    Schedule schedule =
            scheduleService.getSchedule(scheduleId, cinemaNo, channelId);

    TicketInfo ticketInfo = doQueryTicket(schedule, bisOrder.getSeat(), orderId, bisOrder.getTpOrderId());
  //查询出票信息，记录出票码
    if (bisOrder.getSeatStatus().equals(SeatStatus.LOCKED.getVal())){
      bisOrder.setTicketCode(ticketInfo.getTicketCode());
      if (!Strings.isNullOrEmpty(ticketInfo.getTpOrderId())){
        bisOrder.setTpOrderId(ticketInfo.getTpOrderId());
      }
      bisOrder.setSeatStatus(SeatStatus.DRAWED.getVal());
      bisOrderInfoMapper.updateSelective(bisOrder);
    }
    return ticketInfo;
  }

  /**
   * 调用ticketplatform查询出票信息
   *
   * @throws StockBizException
   */
  private TicketInfo doQueryTicket(Schedule schedule, String seat,
                                   String orderId, String tpOrderId) throws GoodsException {
    com.wxmovie.bis.ticketplatform.entity.BisOrderInfo bisOrderInfo = new com.wxmovie.bis.ticketplatform.entity.BisOrderInfo();
    //todo mobile
    //bisOrderInfo.setMobile("13800138000");
    bisOrderInfo.setOrderNo(tpOrderId);
    int seatNum = seat.split(Contants.SYMBOL_VERTICAL_BAR_ESCAPE).length;
    double totalCash = schedule.getActualSellPrice().doubleValue() * seatNum;
    bisOrderInfo.setAllMoney(totalCash);
    //bisOrderInfo.setAllMoney(schedule.getSettlementPrice().doubleValue());
    bisOrderInfo.setBaseCinemaNo(String.valueOf(schedule.getCinemaNo()));
    bisOrderInfo.setSelfOrderNo(orderId);
    bisOrderInfo.setSeats(seat);
    bisOrderInfo.setCinemaNo(schedule.getBisCinemaNo());
    bisOrderInfo.setCinemaLinkId(schedule.getCinemaLinkId());
    bisOrderInfo.setHallNo(schedule.getHallNo());
    bisOrderInfo.setShowSeqNo(schedule.getSeqNo());
    bisOrderInfo.setShowDate(schedule.getShowDate());
    bisOrderInfo.setShowTime(schedule.getShowTime());
    bisOrderInfo.setFilmNo(schedule.getBisMovieNo());
    bisOrderInfo.setSettlementPrice(schedule.getSettlementPrice().doubleValue());
    bisOrderInfo.setSinglePrice(schedule.getSettlementPrice().doubleValue());
    IBisResult bisResult =
        this.getBisResult(null, bisOrderInfo, schedule, 1, ResponseMessage.SE70002);
    TicketInfo ticketInfo = new TicketInfo();
    if (bisResult != null && Contants.SUCC.equals(bisResult.getErrorMsg())) {
        BisTicketInfo bisTicketInfo = bisResult.getTicket();
        String ticketCode = bisTicketInfo.getVoucherId();//单码
        if (Strings.isNullOrEmpty(ticketCode)) {
          ticketCode = bisTicketInfo.getExtOrderNo() + "|" + bisTicketInfo.getTicketNo();// 双码
        }
        ticketInfo.setSeatCode(seat);
        ticketInfo.setTpOrderId(tpOrderId);
        ticketInfo.setTicketCode(ticketCode);
        if (bisTicketInfo.getTicketStatus() != null) {
          ticketInfo.setOrderStatus(bisTicketInfo.getTicketStatus().getValue());
      }
        ticketInfo.setTsFlag(true);
    } else {
      logger.error("return bisResult is null or fali");
      throw new GoodsException(ResponseMessage.SE70002);
    }
    return ticketInfo;
  }


  /**
   * 封装与接入商交互的BisTicketInfo信息
   */
  private BisTicketInfo getBisTicketInfo(Integer cinemaNo, String ticketCode, String seat,
      String tpOrderId, Schedule schedule) {
    BisTicketInfo bisTicketInfo = new BisTicketInfo();
    String voucherId = "";
    // 获取票（双码1）
    String extOrderNo = "";
    // 获取票（双码2）
    String ticketNO = "";
    if (ticketCode != null) {
      /*
       * List<String> codes = Splitter.on("|").trimResults().splitToList(ticketCode);
       * bisTicketInfo.setExtOrderNo(codes.get(0)); if (codes.size() > 1) {
       * bisTicketInfo.setTicketNo(codes.get(1));// 双码
       */
      // 获取票（单码）
      String[] ticketCodeArray = ticketCode.split(Contants.SYMBOL_VERTICAL_BAR_ESCAPE);
      // 证明是双码
      if (ticketCodeArray.length == 2) {
        extOrderNo = ticketCodeArray[0].trim();
        ticketNO = ticketCodeArray[1].trim();
        voucherId = extOrderNo;
      } else {
        voucherId = ticketCode;
      }
    }
    bisTicketInfo.setVoucherId(voucherId);
    bisTicketInfo.setTicketNo(ticketNO);
    bisTicketInfo.setOrderNo(tpOrderId);
    bisTicketInfo.setExtOrderNo(extOrderNo);
    bisTicketInfo.setCinemaNo(cinemaNo.toString());
    bisTicketInfo.setHallNo(schedule.getHallNo());
    bisTicketInfo.setSeats(seat);
    bisTicketInfo.setCinemaLinkId(schedule.getCinemaLinkId());
    bisTicketInfo.setOrderNo(tpOrderId);
    return bisTicketInfo;
  }
  /**
   * 调用第三方接入商接口操作的统一方法 getBisResult:(这里用一句话描述这个方法的作用). <br/>
   *
   * @param bisTicketInfo 传入订座系统的参数信息
   * @param operate 表示与接入商要进行的那种操作(查询二维码，票状态，订单状态，核销)
   * @return
   */
  private IBisResult getBisResult(BisTicketInfo bisTicketInfo,
      com.wxmovie.bis.ticketplatform.entity.BisOrderInfo bisOrderInfo, Schedule schedule,
      int operate, ResponseMessage responseMessage) {
    IBisResult bisResult = null;
    String bisServerId = schedule.getBisServerId();
    try {
      switch (operate) {
        case 0:
          bisResult = BisGateFacade.queryTicketStatus(bisTicketInfo, bisServerId);
          break;
        case 1:
          bisResult = BisGateFacade.queryTicket(bisOrderInfo, bisServerId);
          break;
        case 2:
          bisResult = BisGateFacade.qryCode(bisTicketInfo, bisServerId);
          break;
        case 3:
          bisResult = BisGateFacade.printTicket(bisTicketInfo, bisServerId);
          break;
        default:
          logger.error("参数 operate非法");
          break;
      }

    } catch (Exception e) {
      logger.error("getBisResult error", e);
      throw new GoodsException(responseMessage);
    }
    if (bisResult == null) {
      logger.error("bisResult is null");
      throw new GoodsException(responseMessage);
    }
    return bisResult;
  }

  /**
   * checkVerifyValLocalOrderInfo:(判断是否调用第三方核销接口). <br/>
   * TODO(0：全部为核销状态 1：存在未核销状态 -1：数据为空).<br/>
   */
  public int checkVerifyValLocalOrderInfo(List<LocalOrderInfo> localOrderInfos) {
    int checkCount = 0;
    int verifyVal = SeatStatus.VERIFIED.getVal();
    if (localOrderInfos != null && localOrderInfos.size() > 0) {
      for (int i = 0; i < localOrderInfos.size(); i++) {
        LocalOrderInfo localOrderInfo = localOrderInfos.get(i);
        int seatStatus = localOrderInfo.getLocalSeatStatus();
        // 如果票的状态存在不为核销状态的时候
        if (seatStatus != verifyVal) {
          return 1;
        }
      }
    } else {
      checkCount = -1;
    }
    return checkCount;
  }

  /**
   * (0：全部为核销状态 1：存在未核销状态 -1：数据为空).
   */
  public int checkVerifyValBisOrderInfo(BisOrderInfo bisOrderInfo) {
    int checkCount = 0;
    int verifyVal = SeatStatus.VERIFIED.getVal();
    if (bisOrderInfo != null) {
      int seatStatus = bisOrderInfo.getSeatStatus();
      // 如果票的状态存在不为核销状态的时候
      if (seatStatus != verifyVal) {
        return 1;
      }
    } else {
      checkCount = -1;
    }
    return checkCount;
  }

  private int verfiyTicketFromLocOrInfo(List<LocalOrderInfo> localOrderInfos, String orderId) {
    Schedule schedule = this.getSchedule(orderId, null, localOrderInfos);
    int verifyCount = 0;
    if (schedule == null) {
      logger.error("verfiyTicketFromLocalOrderInfo error schedul is null ");
      return 0;
    }
    // 获取bisServerId、第三方订单号
    String bisServerId = schedule.getBisServerId();
    String tpOrderId = localOrderInfos.get(0).getTpOrderId();
    List<LocalOrderInfo> localHotseatStoreInfoList = new ArrayList<LocalOrderInfo>();
    if (StringUtils.isNotBlank(bisServerId) && StringUtils.isNotBlank(tpOrderId)) {
      Byte verifyVal = SeatStatus.VERIFIED.getVal();
      for (int i = 0; i < localOrderInfos.size(); i++) {
        LocalOrderInfo localHotseatBean = localOrderInfos.get(i);
        String ticketCode = localHotseatBean.getTicketCode();
        // 只要请求该接口的一律把票进行核销，
        localHotseatBean.setLocalSeatStatus(verifyVal);
        localHotseatStoreInfoList.add(localHotseatBean);
        if (StringUtils.isBlank(ticketCode)) {
          logger.error("verfiyTicketFromLocOrInfo get ticketCode by orderId = {} is null", orderId);
          continue;
        }
        BisTicketInfo bisTicketInfo =
            this.getBisTicketInfo(localHotseatBean.getCinemaNo(), localHotseatBean.getTicketCode(),
                localHotseatBean.getSeat(), localHotseatBean.getTpOrderId(), schedule);

        // TODO 开始调用第三方接口打印票操作
        IBisResult ibResult = null;
        try {
          ibResult = this.getBisResult(bisTicketInfo, null, schedule, 3, ResponseMessage.SE70004);
        } catch (Exception e) {
          logger.error("调用影院接口核销（打票）出现异常：{}", e.getMessage(), e);
        }
        if (ibResult == null) {
          logger.error("调用影院接口核销（打票）获取IBisResult为空！");
          continue;
        }
        // 判断是否核销成功
        String errorMessage = ibResult.getErrorMsg();
        if (Contants.SUCC.equals(errorMessage)) {
          verifyCount = verifyCount + 1;
        } else {
          logger.error("调用影院接口核销（打印票）出现异常error_message={} orderId={}", errorMessage, orderId);
          continue;
        }
      }
    }
    if (localHotseatStoreInfoList.size() > 0) {
      int verifyLocal = localOrderInfoMapper.updateLocalOrderInfoBatch(localHotseatStoreInfoList);
      if (logger.isDebugEnabled()) {
        logger.info("thicketplatform verify ticket count={},localhotseat count={}", verifyCount,
            verifyLocal);
      }
      return verifyCount;
    }
    return 0;
  }

  private int verfiyTicketFromBisOrderInfo(BisOrderInfo bisOrderInfo, String orderId) {
    int verifyCount = 0;
    // 查看票的排期
    Schedule schedule = this.getSchedule(orderId, bisOrderInfo, null);
    if (schedule == null) {
      logger.info("schedule is null orderId{}", orderId);
      throw new GoodsException(ResponseMessage.SE70004);
    }
    String bisServerId = schedule.getBisServerId();
    if (StringUtils.isNotBlank(bisServerId)) {
      // 只要请求该接口的一律把票进行核销
      // FIXME 当通过接入商核销成功后再来更新BisOrderInfo表
      Byte verifyVal = SeatStatus.VERIFIED.getVal();
      BisOrderInfo bisOrderInfoTmp = new BisOrderInfo();
      bisOrderInfoTmp.setId(bisOrderInfo.getId());
      bisOrderInfoTmp.setSeatStatus(verifyVal);
      bisOrderInfoMapper.updateSelective(bisOrderInfoTmp);
      if (StringUtils.isBlank(bisOrderInfo.getTicketCode())) {
        logger
            .error("verfiyTicketFromBisOrderInfo get ticketCode by orderId = {} is null", orderId);
        throw new GoodsException(ResponseMessage.SE70004);
      }
      BisTicketInfo bisTicketInfo =
          this.getBisTicketInfo(bisOrderInfo.getCinemaNo(), bisOrderInfo.getTicketCode(),
              bisOrderInfo.getSeat(), bisOrderInfo.getTpOrderId(), schedule);
      // TODO 开始调用第三方接口打印票操作
      IBisResult result =
          this.getBisResult(bisTicketInfo, null, schedule, 3, ResponseMessage.SE70004);
      if (result != null) {
        if (Contants.SUCC.equals(result.getErrorMsg())) {
          return verifyCount;
        } else {
          throw new GoodsException(new CommonResponse(result.getErrorCode(), result.getErrorCode(),
              "verify fail"));
        }
      } else {
        throw new GoodsException(ResponseMessage.SE70004);
      }
    }
    return verifyCount;
}
}