/**
 * Project Name:wp-goods-center
 * File Name:TicketService.java
 * Package Name:com.wepiao.goods.service
 * Date:2015年12月25日下午4:22:27
 *
*/

package com.wepiao.goods.service;

import java.util.List;

import com.wepiao.goods.common.entity.GoodsException;
import com.wepiao.goods.vo.TicketInfo;
import com.wepiao.goods.vo.VoucherCode;


/**
 * 查询票的状态，查询票的二维码，票的核销
 */
public interface TicketService {
  /**
   * 从接入商查询出票的状态
   */
  List<TicketInfo> queryTicketStatus(String orderId) throws GoodsException;

  /**
   * 从开放平台(自由库存，非自由库存)查询出订单的状态
   */
  List<TicketInfo> queryOrderStatus(String orderId) throws GoodsException;

  /**
   * 查询出票的二维码
   */
  List<VoucherCode> queryCode(String orderId) throws GoodsException;

  /**
   * 票的核销
   */
  boolean verfiyTicket(String orderId) throws GoodsException;


}
