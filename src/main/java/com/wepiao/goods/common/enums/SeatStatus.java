package com.wepiao.goods.common.enums;

/**
 * 座位状态 Created by qilei on 15/12/24.
 */
public enum SeatStatus {
  /**
   * 未锁座
   */
  UNLOCKED(0),
  /**
   * 已锁座
   */
  LOCKED(1),
  /**
   * 已出票
   */
  DRAWED(2),
  /**
   * 已取票（核销）
   */
  VERIFIED(3),
  /**
   * 已解锁
   */
  UNLOCKDONE(4),
  /**
   * 已退票
   */
  REFUNDED(5),
  /**
   * 退票失败
   */
  REFUND_FAILED(6);

  private byte val;

  SeatStatus(int val) {
    this.val = (byte)val;
  }

  public Byte getVal() {
    return val;
  }

}
