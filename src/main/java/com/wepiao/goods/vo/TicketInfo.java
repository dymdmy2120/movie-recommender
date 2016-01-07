package com.wepiao.goods.vo;

public class TicketInfo {

  /**
   * 订单中的票号
   */
  private String ticketCode;
  /**
   * 座位号
   */
  private String seatCode;
  /**
   * 影院交互的第三方的订单好
   */
  private String tpOrderId;
  /**
   * 订单中票的状态
   */
  private Integer orderStatus;
  /**
   * 票状态是否完全一致
   */
  private boolean tsFlag;

  public TicketInfo() {

  }

  public String getTicketCode() {
    return ticketCode;
  }

  public void setTicketCode(String ticketCode) {
    this.ticketCode = ticketCode;
  }

  public String getSeatCode() {
    return seatCode;
  }

  public void setSeatCode(String seatCode) {
    this.seatCode = seatCode;
  }

  public String getTpOrderId() {
    return tpOrderId;
  }

  public void setTpOrderId(String tpOrderId) {
    this.tpOrderId = tpOrderId;
  }

  public Integer getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(Integer orderStatus) {
    this.orderStatus = orderStatus;
  }

  public boolean isTsFlag() {
    return tsFlag;
  }

  public void setTsFlag(boolean tsFlag) {
    this.tsFlag = tsFlag;
  }

//  @Override public String toString() {
//    return "TicketInfo{" +
//        "ticketCode='" + ticketCode + '\'' +
//        ", seatcode='" + seatCode + '\'' +
//        ", tpOrderId='" + tpOrderId + '\'' +
//        ", orderStatus=" + orderStatus +
//        ", tsFlag=" + tsFlag +
//        '}';
//  }
}
