package com.wepiao.goods.vo;

public class DrawTicketResult {
  private String ticketCode;// 出票码
  private String tpOrderId;// 接入商订单号

  public DrawTicketResult(String ticketCode, String tpOrderId) {
    this.ticketCode = ticketCode;
    this.tpOrderId = tpOrderId;
  }

  public String getTicketCode() {
    return ticketCode;
  }

  public void setTicketCode(String ticketCode) {
    this.ticketCode = ticketCode;
  }

  public String getTpOrderId() {
    return tpOrderId;
  }

  public void setTpOrderId(String tpOrderId) {
    this.tpOrderId = tpOrderId;
  }
}
