package com.wepiao.goods.vo;

import javax.validation.constraints.NotNull;

/**
 *  锁座请求参数
 */
public class SeatLockParam extends BaseSeatParam  {

  /** 手机号 **/
  @NotNull(message = "mobile is null")
  private String mobile;

  /** 订单id **/
  @NotNull(message = "orderId is null")
  private String orderId;

  /** 渠道id **/
  @NotNull(message = "tpId is null")
  private Integer tpId;

  /** 请求id **/
  private String requestId;

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public Integer getTpId() {
    return tpId;
  }

  public void setTpId(Integer tpId) {
    this.tpId = tpId;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }
}
