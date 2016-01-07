package com.wepiao.goods.vo;

import javax.validation.constraints.NotNull;

/**
 * Seat Purchase Task request parameters.
 */
public class SeatPcsTaskParam extends BaseSeatParam {

//  @NotNull(message = "scheduleId is null")
//  private Integer scheduleId;
//
//  @NotNull(message = "cinemaNo is null")
//  private Integer baseCinemaNo;

  @NotNull(message = "hallId is null")
  private String hallId;

  @NotNull(message = "hallName is null")
  private String hallName;

  @NotNull(message = "sectionNo is null")
  private String sectionNo;

  // 座位号, 以逗号隔开
//  @NotNull(message = "seats is null")
//  private String seats;

  @NotNull(message = "showTime is null")
  private String showTime;

  @NotNull(message = "source is null")
  private byte source;

//  public Integer getScheduleId() {
//    return scheduleId;
//  }
//
//  public void setScheduleId(Integer scheduleId) {
//    this.scheduleId = scheduleId;
//  }

  public String getHallId() {
    return hallId;
  }

  public void setHallId(String hallId) {
    this.hallId = hallId;
  }

  public String getHallName() {
    return hallName;
  }

  public void setHallName(String hallName) {
    this.hallName = hallName;
  }

  public String getSectionNo() {
    return sectionNo;
  }

  public void setSectionNo(String sectionNo) {
    this.sectionNo = sectionNo;
  }

//  public String getSeats() {
//    return seats;
//  }
//
//  public void setSeats(String seats) {
//    this.seats = seats;
//  }

  public String getShowTime() {
    return showTime;
  }

  public void setShowTime(String showTime) {
    this.showTime = showTime;
  }

  public byte getSource() {
    return source;
  }

  public void setSource(byte source) {
    this.source = source;
  }
}
