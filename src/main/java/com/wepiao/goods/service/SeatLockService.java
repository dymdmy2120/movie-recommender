package com.wepiao.goods.service;

import com.wepiao.goods.common.entity.CommonResponse;
import com.wepiao.goods.common.entity.GoodsException;
import com.wepiao.goods.vo.SeatLockParam;

/**
 * 座位锁定服务 Created by qilei on 15/7/31.
 */
public interface SeatLockService {
  /**
   * 锁坐
   *
   * @param lockSeatParam
   * @return List 接入商订单信息
   * @throws GoodsException
   */
  public CommonResponse<?> lock(SeatLockParam seatLockParam);

  /**
   * 解锁
   */
  public Boolean unlock(String orderId);

  /**
   * 定时解锁
   */
  public void cronUnlock();
}
