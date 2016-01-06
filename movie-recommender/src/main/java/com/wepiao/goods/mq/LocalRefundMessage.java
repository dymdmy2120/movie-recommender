package com.wepiao.goods.mq;

import com.wepiao.goods.model.goods.LocalOrderInfo;

public class LocalRefundMessage {

  private LocalOrderInfo localOrderInfo;
  private int retryTimes = 0;

  public LocalRefundMessage() {
  }

  public LocalRefundMessage(LocalOrderInfo localOrderInfo) {
    this.localOrderInfo = localOrderInfo;
  }

  public int getRetryTimes() {
    return retryTimes;
  }

  public void setRetryTimes(int retryTimes) {
    this.retryTimes = retryTimes;
  }

  public LocalOrderInfo getLocalOrderInfo() {
    return localOrderInfo;
  }

  public void setLocalOrderInfo(LocalOrderInfo localOrderInfo) {
    this.localOrderInfo = localOrderInfo;
  }

}
