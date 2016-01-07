/**
 * Project Name:wp-local-stock File Name:OrderUtil.java Package Name:com.wepiao.products.stock.common.utils
 * Date:2015年7月28日上午11:35:26
 */

package com.wepiao.goods.common.utils;

import com.wxmovie.bis.ticketplatform.util.StringUtility;
import java.util.Date;
import java.util.Random;

/**
 * ClassName:OrderUtil <br/> Function: TODO ADD FUNCTION. <br/> Reason:	 TODO ADD REASON. <br/>
 * Date:     2015年7月28日 上午11:35:26 <br/>
 *
 * @author Zeng
 * @see
 */
public class OrderUtil {
  // 黄金坐席购买座位时生成用户订单号
  public static String createOrderId() {
    Random r = new Random();
    int rondom = r.nextInt(900000) + 100000;
    return StringUtility.getDateToString(new Date(), "yyMMddHHmmss") + rondom;
  }
}
