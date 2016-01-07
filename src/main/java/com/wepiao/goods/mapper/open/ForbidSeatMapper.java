/**
 * Project Name:wp-local-stock <br> File Name:ForbidSeatMapper.java <br> Package
 * Name:com.wepiao.products.stock.common.mapper <br> Date:2015年8月6日上午11:43:55
 */

package com.wepiao.goods.mapper.open;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

public interface ForbidSeatMapper {

  /**
   * selectForbidSeat:获取所有禁售座位. <br>
   *
   * @param params 查询条件参数
   * @return 禁售座位集合
   */
  public List<String> getForbidSeat(Map<String, Object> params);
}
