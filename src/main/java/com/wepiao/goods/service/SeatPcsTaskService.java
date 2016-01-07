package com.wepiao.goods.service;

import com.wepiao.goods.vo.SeatPcsTaskParam;

/**
 *  Seat Purchase Task Service
 */
public interface SeatPcsTaskService {

  /**
   * 添加座位购买任务。
   * @param inputParam
   * @return Int 插入成功个数。
   */
  int addTask(SeatPcsTaskParam inputParam);

  /**
   * 定时锁座。
   */
  void cronLockAndDraw();

  /**
   * 定时出票。
   */
//  void draw();

}
