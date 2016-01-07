package com.wepiao.goods.service;

import com.wepiao.goods.vo.Schedule;

/**
 * 排期服务
 * Created by qilei on 15/11/5.
 */
public interface ScheduleService {

  /**
   * 查询单条排期
   * @param scheduleId 排期id
   * @param cinemalNo 影院编号（微票）
   * @param channelId 渠道id
   * @return
   */
  Schedule getSchedule(Integer scheduleId, Integer cinemalNo, Integer channelId);

  /**
   * 根据价格流水查询排期
   * @param priceRecordId
   * @return
   */
  Schedule getScheduleByPriceRecordId(Integer priceRecordId);
}
