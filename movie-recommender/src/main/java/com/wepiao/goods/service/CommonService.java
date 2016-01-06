package com.wepiao.goods.service;

import java.util.List;

import com.wepiao.goods.model.goods.LocalOrderInfo;
import java.util.Set;

/**
 * 公用服务 Created by qilei on 15/7/31.
 */
public interface CommonService {

  /**
   * 座位按照是否为本地库存分组
   */
  void groupSeat(Integer scheduleId, List<String> seatList, List<LocalOrderInfo> localSeats,
      List<String> cinemaSeats);

  /**
   * 获取影厅的静态座位图
   * @param bisServerId
   * @param bisCinemaNo
   * @param hallNo
   * @return
   */
  Set<String> getStaticSeat(String bisServerId, String bisCinemaNo,String hallNo);
}
