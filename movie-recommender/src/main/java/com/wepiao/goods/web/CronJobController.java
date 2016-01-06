package com.wepiao.goods.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wepiao.goods.common.entity.CommonResponse;
import com.wepiao.goods.service.LocalRefundService;
import com.wepiao.goods.service.SeatLockService;
import com.wepiao.goods.service.SeatPcsTaskService;
import com.wepiao.goods.service.StaticSeatsMapService;

@Controller
@RequestMapping("/jobs")
public class CronJobController {

  @Autowired
  private SeatLockService seatLockService;

  @Autowired
  private SeatPcsTaskService seatPcsTaskService;

  @Autowired
  private StaticSeatsMapService staticSeatsMapService;

  @Autowired
  private LocalRefundService localTicketRefundService;

  /**
   *  定时查询seat_purchase_task表， 把任务放入Queue - SeatLockAndDrawJobQ
   * @return
   */
  @RequestMapping(value = "/lockAndDraw", method = RequestMethod.POST)
  @ResponseBody
  public CommonResponse<?> lockAndDraw() {
    seatPcsTaskService.cronLockAndDraw();
    return CommonResponse.success();
  }

  /**
   * 定时查询local_order_info表， 把超时未解锁的座位解锁。
   * @return
   */
  @RequestMapping(value = "/local/unlock", method = RequestMethod.POST)
  @ResponseBody
  public CommonResponse<?> unlock() {
    seatLockService.cronUnlock();
    return CommonResponse.success();
  }

  /**
   * 定时查询local_order_info表，自动退上映前2小时未售票
   * @return
   * */
  @RequestMapping(value = "/local/refund", method = RequestMethod.POST)
  @ResponseBody
  public CommonResponse<?> refund() {
    localTicketRefundService.cronLocalRefund();
    return CommonResponse.success();
  }

  /**
   * 更新静态座位图
   * @return
   */
  @RequestMapping(value = "/staticSeatsMap/update", method = RequestMethod.POST)
  @ResponseBody
  public CommonResponse<?> updateStaticSeatsMap() {
    staticSeatsMapService.updateCache();
    return CommonResponse.success();
  }

}
