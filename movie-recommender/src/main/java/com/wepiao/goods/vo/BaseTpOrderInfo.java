package com.wepiao.goods.vo;

import java.io.Serializable;

/**
 * 接入商订单信息基类 锁坐返回值
 * Created by qilei on 15/12/25.
 */
public class BaseTpOrderInfo implements Serializable {

  private static final long serialVersionUID = -6154964903514207374L;

  /**
   * 影院订单号（第三方订单号）
   */
  private String tpOrderId;

  /**
   *  接入商订单号
   */
  private String tpExtOrderId;

  /** 锁坐超时时间(s) 默认10分钟 **/
  private Integer lockSeatTimeout = 10;

  public String getTpOrderId() {
    return tpOrderId;
  }

  public void setTpOrderId(String tpOrderId) {
    this.tpOrderId = tpOrderId;
  }

  public String getTpExtOrderId() {
    return tpExtOrderId;
  }

  public void setTpExtOrderId(String tpExtOrderId) {
    this.tpExtOrderId = tpExtOrderId;
  }

  public Integer getLockSeatTimeout() {
    return lockSeatTimeout;
  }

  public void setLockSeatTimeout(Integer lockSeatTimeout) {
    this.lockSeatTimeout = lockSeatTimeout;
  }
}
