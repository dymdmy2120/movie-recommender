/**
 * Project Name:wp-local-stock <br> File Name:SecKillSeatService.java <br>
 * PackageName:com.wepiao.products.stock.seat.service <br> Date:2015年7月17日下午11:38:56
 */

package com.wepiao.goods.service;

import java.util.List;
import java.util.Map;

import com.wepiao.goods.common.entity.GoodsException;
import com.wepiao.goods.model.goods.LocalOrderInfo;

/**
 * ClassName:SecKillSeatService <br/> Function: 秒杀座位服务层接口. <br/> Reason: TODO ADD REASON. <br/>
 * Date: 2015年7月17日 下午11:38:56 <br/>
 *
 * @author zhiyong.fan
 * @see
 */
public interface SecKillSeatService {

  /**
   * getSecKillSeats:获取指定数量的秒杀座位. <br/>
   *
   * @param secKillCount 秒杀座位数
   */
  public List<LocalOrderInfo> getSecKillSeats(int secKillCount) throws GoodsException;

  /**
   * changeFailedSecKillSeatStatus:更新秒杀失败的座位状态. <br/>
   *
   * @param secKillFailSeats 秒杀失败的座位状态 Key:排期ID value:该排期对应的座位
   * @param status 座位状态
   */
  public int changeFailedSecKillSeatStatus(List<Map<String, List<String>>> secKillFailSeats);
}
