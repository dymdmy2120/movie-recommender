package com.wepiao.goods.common.enums;

/**
 * 支付方式
 */
public enum PaymentType {

  /**
   * 微信支付
   */
  WECHAT(1),
  /**
   * 支付宝
   */
  ALIPAY(2),
  /**
   * 红包、预售卷
   */
  PRESELL(3),
  /**
   * 会员卡
   */
  MEMBER(4);

  private Integer val;

  PaymentType(Integer i) {
    this.val = i;
  }

  public Integer getVal() {
    return val;
  }
}
