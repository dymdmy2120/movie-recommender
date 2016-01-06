package com.wepiao.goods.mq;

import com.wepiao.goods.common.enums.SeatTaskStatus;
import com.wepiao.goods.common.utils.OrderUtil;
import com.wepiao.goods.model.goods.SeatPurchaseTask;
import com.wepiao.goods.vo.SeatLockParam;

public class SeatPcsTaskMessage {

  private SeatPurchaseTask seatPurchaseTask;
  private SeatLockParam seatLockParam;
  private SeatTaskStatus seatStatus = SeatTaskStatus.INIT;
  private int retryTimes = 0;
  // TODO: 改成随机生成手机号for大地
  private final static String DEFAULT_PHONE_NUM = "13800138000";

  public SeatPcsTaskMessage() {
  }

  public SeatPcsTaskMessage(SeatPurchaseTask seatPurchaseTask) {
    this.seatPurchaseTask = seatPurchaseTask;
    seatLockParam = new SeatLockParam();
    seatLockParam.setBaseCinemaNo(seatPurchaseTask.getCinemaNo());
    seatLockParam.setScheduleId(seatPurchaseTask.getScheduleId());
    seatLockParam.setOrderId(OrderUtil.createOrderId());
    seatLockParam.setMobile(DEFAULT_PHONE_NUM);
    seatLockParam.setTpId(0);  // 0 - 微信电影票
    seatLockParam.setSeats(seatPurchaseTask.getSeat());
  }

  public int getRetryTimes() {
    return retryTimes;
  }

  public void setRetryTimes(int retryTimes) {
    this.retryTimes = retryTimes;
  }

  public SeatPurchaseTask getSeatPurchaseTask() {
    return seatPurchaseTask;
  }

  public void setSeatPurchaseTask(SeatPurchaseTask seatPurchaseTask) {
    this.seatPurchaseTask = seatPurchaseTask;
  }

  public SeatLockParam getSeatLockParam() {
    return seatLockParam;
  }

  public void setSeatLockParam(SeatLockParam seatLockParam) {
    this.seatLockParam = seatLockParam;
  }

  public SeatTaskStatus getSeatStatus() {
    return seatStatus;
  }

  public void setSeatStatus(SeatTaskStatus seatStatus) {
    this.seatStatus = seatStatus;
  }
}
