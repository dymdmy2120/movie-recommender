package com.wepiao.goods.vo;

import javax.validation.constraints.NotNull;

public class BaseSeatParam {

  /** 排期id **/
  @NotNull(message = "scheduleId is null")
  private Integer scheduleId;

  /** 影院编号 **/
  @NotNull(message = "cinemaNo is null")
  private Integer baseCinemaNo;

  /** 座位列表 **/
  @NotNull(message = "seats is null")
  private String seats;

  public Integer getScheduleId() {
    return scheduleId;
  }

  public void setScheduleId(Integer scheduleId) {
    this.scheduleId = scheduleId;
  }

  public Integer getBaseCinemaNo() {
    return baseCinemaNo;
  }

  public void setBaseCinemaNo(Integer baseCinemaNo) {
    this.baseCinemaNo = baseCinemaNo;
  }

  public String getSeats() {
    return seats;
  }

  public void setSeats(String seats) {
    this.seats = seats;
  }
}
