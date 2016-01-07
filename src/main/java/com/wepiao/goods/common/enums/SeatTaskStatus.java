package com.wepiao.goods.common.enums;

/**
 * Seat purchase task status.
 */
public enum SeatTaskStatus {
  /**
   * 未锁座
   */
  INIT(0),
  /**
   * 已出票
   */
  DRAWED(1),
  /**
   * 锁座出票失败
   */
  FAILED(2),
  /**
   * 临时状态， 不计入数据库。
   */
  LOCKED(3);

  private byte val;

  SeatTaskStatus(int val) {
    this.val = (byte)val;
  }

  public Byte getVal() {
    return val;
  }

}
