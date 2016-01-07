/**
 * Project Name:wp-local-stock <br> File Name:ForbidSeat.java <br> Package
 * Name:com.wepiao.products.stock.common.model <br> Date:2015年8月6日上午10:56:39
 */

package com.wepiao.goods.model.open;

import java.io.Serializable;
import java.util.Date;

/**
 * ClassName:ForbidSeat <br> Function: 禁售座位POJO类. <br> Reason: TODO ADD REASON. <br> Date: 2015年8月6日
 * 上午10:56:39 <br>
 *
 * @author zhiyong.fan
 * @see
 */
public class ForbidSeat implements Serializable {

  /**
   * serialVersionUID:TODO
   */
  private static final long serialVersionUID = 1L;
  /**
   * id
   */
  private String id;
  /**
   * 影院编号
   */
  private String baseCinemaNo;
  /**
   * 影院ID
   */
  private String baseCinemaId;
  /**
   * 影院名称
   */
  private String baseCinemaName;
  /**
   * 禁用类型 0:影厅 1：座位 2：影院
   */
  private int forbidType;
  /**
   * 规则有效开始时间
   */
  private Date beginTime;
  /**
   * 规则失效结束时间
   */
  private Date endTime;
  /**
   * 影厅编号
   */
  private String hallNo;
  /**
   * 影厅名称
   */
  private String hallName;
  /**
   * 禁售座位 格式：01:1:1,2,3,4,5,6,7,8,9,10
   */
  private String seat;
  /**
   * 规则状态 0：不可用 1：可用
   */
  private int status;
  /**
   * 创建者ID
   */
  private String createId;
  /**
   * 创建时间
   */
  private Date creatTime;
  /**
   * 编辑者ID
   */
  private String editerId;
  /**
   * 编辑时间
   */
  private Date editTime;
  /**
   * 渠道ID
   */
  private String channelId;
  /**
   * 渠道名称
   */
  private String channelName;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getBaseCinemaNo() {
    return baseCinemaNo;
  }

  public void setBaseCinemaNo(String baseCinemaNo) {
    this.baseCinemaNo = baseCinemaNo;
  }

  public String getBaseCinemaId() {
    return baseCinemaId;
  }

  public void setBaseCinemaId(String baseCinemaId) {
    this.baseCinemaId = baseCinemaId;
  }

  public String getBaseCinemaName() {
    return baseCinemaName;
  }

  public void setBaseCinemaName(String baseCinemaName) {
    this.baseCinemaName = baseCinemaName;
  }

  public int getForbidType() {
    return forbidType;
  }

  public void setForbidType(int forbidType) {
    this.forbidType = forbidType;
  }

  public Date getBeginTime() {
    return beginTime;
  }

  public void setBeginTime(Date beginTime) {
    this.beginTime = beginTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public String getHallNo() {
    return hallNo;
  }

  public void setHallNo(String hallNo) {
    this.hallNo = hallNo;
  }

  public String getHallName() {
    return hallName;
  }

  public void setHallName(String hallName) {
    this.hallName = hallName;
  }

  public String getSeat() {
    return seat;
  }

  public void setSeat(String seat) {
    this.seat = seat;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getCreateId() {
    return createId;
  }

  public void setCreateId(String createId) {
    this.createId = createId;
  }

  public Date getCreatTime() {
    return creatTime;
  }

  public void setCreatTime(Date creatTime) {
    this.creatTime = creatTime;
  }

  public String getEditerId() {
    return editerId;
  }

  public void setEditerId(String editerId) {
    this.editerId = editerId;
  }

  public Date getEditTime() {
    return editTime;
  }

  public void setEditTime(Date editTime) {
    this.editTime = editTime;
  }

  public String getChannelId() {
    return channelId;
  }

  public void setChannelId(String channelId) {
    this.channelId = channelId;
  }

  public String getChannelName() {
    return channelName;
  }

  public void setChannelName(String channelName) {
    this.channelName = channelName;
  }
}
