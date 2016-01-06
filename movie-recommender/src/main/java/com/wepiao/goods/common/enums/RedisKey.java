package com.wepiao.goods.common.enums;

/**
 * ClassName: RedisKey <br> Function: Redis键枚举类. <br> date: 2015年7月22日 下午2:09:26 <br>
 *
 * @author zhiyong.fan
 */
public enum RedisKey {
  // 对象类型：对象ID：对象属性
  STATIC_SEAT_KEY_PREFIX("GoodsCenter:StaticSeat:"), REFUND_FAIL_REQUEST("GoodsCenter:RefundFail");

  private String keyPrefix;

  private RedisKey(String keyPrefix) {
    this.keyPrefix = keyPrefix;
  }

  public String getVal() {
    return this.keyPrefix;
  }

  @Override
  public String toString() {
    return this.name() + "_" + this.keyPrefix;
  }
}
