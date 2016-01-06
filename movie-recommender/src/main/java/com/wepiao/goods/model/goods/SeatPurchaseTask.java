package com.wepiao.goods.model.goods;

import java.util.Date;

public class SeatPurchaseTask {
    /**
     *  
     */
    private Integer id;

    /**
     *  排期Id
     */
    private Integer scheduleId;

    /**
     *  座位
     */
    private String seat;

    /**
     *  座位状态。0：待购票；1：已出票；2：出票失败
     */
    private Byte seatStatus;

    /**
     *  影院编号
     */
    private Integer cinemaNo;

    /**
     *  影厅编号
     */
    private String hallId;

    /**
     *  影厅名称
     */
    private String hallName;

    /**
     *  座位来源0：黄金座位；1：虚拟选座；2：其他
     */
    private Byte source;

    /**
     *  放映时间
     */
    private Date showTime;

    /**
     *  新建时间
     */
    private Date createdTime;

    /**
     *  更新时间
     */
    private Date modifiedTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat == null ? null : seat.trim();
    }

    public Byte getSeatStatus() {
        return seatStatus;
    }

    public void setSeatStatus(Byte seatStatus) {
        this.seatStatus = seatStatus;
    }

    public Integer getCinemaNo() {
        return cinemaNo;
    }

    public void setCinemaNo(Integer cinemaNo) {
        this.cinemaNo = cinemaNo;
    }

    public String getHallId() {
        return hallId;
    }

    public void setHallId(String hallId) {
        this.hallId = hallId == null ? null : hallId.trim();
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName == null ? null : hallName.trim();
    }

    public Byte getSource() {
        return source;
    }

    public void setSource(Byte source) {
        this.source = source;
    }

    public Date getShowTime() {
        return showTime;
    }

    public void setShowTime(Date showTime) {
        this.showTime = showTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}