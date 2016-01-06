/**
 * Project Name:wp-local-stock File Name:TicketOperationService.java Package
 * Name:com.wepiao.products.stock.seat.service Date:2015年7月18日上午3:40:24
 */

package com.wepiao.goods.service;

import com.wepiao.goods.common.entity.GoodsException;

/**
 * 退票服务
 */
public interface TicketRefundService {

  /**
   * 退票
   *
   * @param orderId 订单号
   * @return 退票状态 true:退票成功 false:退票失败
   */
  public boolean refundTicket(String orderId) throws GoodsException;
}
