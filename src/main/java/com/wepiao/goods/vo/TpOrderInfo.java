package com.wepiao.goods.vo;

/**
 * 出票、查询票 等接口返回值
 * Created by qilei on 15/9/9.
 */
public class TpOrderInfo extends BaseTpOrderInfo {

  private static final long serialVersionUID = 2372255968742252828L;

  /** 一次锁坐的座位 **/
  private String seat;

  /**
   *  出票码(双码用"|"分割)
   */
  private String ticketCode;

  public String getSeat() {
    return seat;
  }

  public void setSeat(String seat) {
    this.seat = seat;
  }

  public String getTicketCode() {
    return ticketCode;
  }

  public void setTicketCode(String ticketCode) {
    this.ticketCode = ticketCode;
  }
}
