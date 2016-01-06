package com.wepiao.goods.service;

import com.wepiao.goods.common.entity.GoodsException;
import java.util.Set;

/**
 * 可售座位服务
 */
public interface SeatAvailableService {

  /**
   * 查询单个排期的可售座位
   * @param scheduleId
   * @param cinemaNo
   * @param channelId
   * @return
   */
  public String getSalableSeats(Integer scheduleId, Integer cinemaNo,
      Integer channelId) throws GoodsException;
}
