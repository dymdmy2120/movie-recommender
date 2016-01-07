/**
 * Project Name:wp-local-stock <br> File Name:TicketRefundInfoBean.java <br> Package
 * Name:com.wepiao.products.stock.ticket.bean <br> Date:2015年8月10日上午11:43:34
 */

package com.wepiao.goods.model.goods;

import java.io.Serializable;

import com.wxmovie.bis.ticketplatform.entity.ReturnTicketInfo;

/**
 * ClassName:TicketRefundInfoBean
 * Function: 退票信息对象.
 * @author zhangliang
 */
public class TicketRefundInfoBean implements Serializable {

  /**
   * 退票参数对象
   */
  private ReturnTicketInfo returnTicketInfo;
  /**
   * 服务器ID
   */
  private String bisServerId;
  /**
   * 重试次数
   */
  private int retryTimes = 0;
  /**
   * 座位来源 0：自有库存 1：订单中心(相当于非自有库从)
   */
  private int source;
  /**
   * 渠道ID
   */
  private Integer channelId;
  /**
   * 自有库存实体类
   */
  private LocalOrderInfo localOrderInfo;
  /**
   * 非自有库存实体类
   */
  private BisOrderInfo bisOrderInfo;
  /**
   * 退票开始时间绝对秒数
   */
  private long beginTime;
  /**
   * 退票结束绝对秒数
   */
  private long endTime;

  public ReturnTicketInfo getReturnTicketInfo() {
    return returnTicketInfo;
  }

  public void setReturnTicketInfo(ReturnTicketInfo returnTicketInfo) {
    this.returnTicketInfo = returnTicketInfo;
  }

  public String getBisServerId() {
    return bisServerId;
  }

  public void setBisServerId(String bisServerId) {
    this.bisServerId = bisServerId;
  }

  public int getRetryTimes() {
    return retryTimes;
  }

  public void setRetryTimes(int retryTimes) {
    this.retryTimes = retryTimes;
  }

  public int getSource() {
    return source;
  }

  public void setSource(int source) {
    this.source = source;
  }

  public Integer getChannelId() {
    return channelId;
  }

  public void setChannelId(Integer channelId) {
    this.channelId = channelId;
  }

  public LocalOrderInfo getLocalOrderInfo() {
    return localOrderInfo;
  }

  public void setLocalOrderInfo(LocalOrderInfo localOrderInfo) {
    this.localOrderInfo = localOrderInfo;
  }

  public BisOrderInfo getBisOrderInfo() {
    return bisOrderInfo;
  }

  public void setBisOrderInfo(BisOrderInfo bisOrderInfo) {
    this.bisOrderInfo = bisOrderInfo;
  }

//  public long getBeginTime() {
//    return beginTime;
//  }
//
//  public void setBeginTime(long beginTime) {
//    this.beginTime = beginTime;
//  }
//
//  public long getEndTime() {
//    return endTime;
//  }
//
//  public void setEndTime(long endTime) {
//    this.endTime = endTime;
//  }
}
