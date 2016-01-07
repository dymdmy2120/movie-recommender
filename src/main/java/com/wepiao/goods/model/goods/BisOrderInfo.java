package com.wepiao.goods.model.goods;

import java.util.Date;

public class BisOrderInfo {
    /**
     *
     */
    private Integer id;

    /**
     *  与接入商交互的订单号
     */
    private String fixOrderId;

    /**
     *  排期id
     */
    private Integer scheduleId;

    /**
     *  影院编号
     */
    private Integer cinemaNo;

    /**
     *  渠道ID(例如豆瓣、京东)
     */
    private Integer channelId;

    /**
     *  座位
     */
    private String seat;

    /**
     *  座位数量
     */
    private Byte num;

    /**
     *  座位状态。1:已锁座 2:已出票 3:已取票 4:已解锁 5:已退票 6:退票失败
     */
    private Byte seatStatus;

    /**
     *  第三方订单号（影院订单号）
     */
    private String tpOrderId;

    /**
     *  接入商订单号
     */
    private String tpExtOrderId;

    /**
     *  出票码(双码用"|"分割)
     */
    private String ticketCode;

    /**
     *  价格流水id
     */
    private Integer priceRecordId;

    /**
     *  1.订单中心 2.自有库存
     */
    private Byte source = 1;

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

    public String getFixOrderId() {
        return fixOrderId;
    }

    public void setFixOrderId(String fixOrderId) {
        this.fixOrderId = fixOrderId == null ? null : fixOrderId.trim();
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Integer getCinemaNo() {
        return cinemaNo;
    }

    public void setCinemaNo(Integer cinemaNo) {
        this.cinemaNo = cinemaNo;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat == null ? null : seat.trim();
    }

    public Byte getNum() {
        return num;
    }

    public void setNum(Byte num) {
        this.num = num;
    }

    public Byte getSeatStatus() {
        return seatStatus;
    }

    public void setSeatStatus(Byte seatStatus) {
        this.seatStatus = seatStatus;
    }

    public String getTpOrderId() {
        return tpOrderId;
    }

    public void setTpOrderId(String tpOrderId) {
        this.tpOrderId = tpOrderId == null ? null : tpOrderId.trim();
    }

    public String getTpExtOrderId() {
        return tpExtOrderId;
    }

    public void setTpExtOrderId(String tpExtOrderId) {
        this.tpExtOrderId = tpExtOrderId == null ? null : tpExtOrderId.trim();
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        //锁座时没ticketCode, 插入空值
        this.ticketCode = ticketCode == null ? "" : ticketCode.trim();
    }

    public Integer getPriceRecordId() {
        return priceRecordId;
    }

    public void setPriceRecordId(Integer priceRecordId) {
        this.priceRecordId = priceRecordId;
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