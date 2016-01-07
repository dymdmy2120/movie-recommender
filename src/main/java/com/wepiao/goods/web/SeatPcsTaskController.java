package com.wepiao.goods.web;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wepiao.common.utils.DateUtil;
import com.wepiao.goods.common.entity.CommonResponse;
import com.wepiao.goods.common.entity.ResponseMessage;
import com.wepiao.goods.service.ScheduleService;
import com.wepiao.goods.service.SeatPcsTaskService;
import com.wepiao.goods.vo.Schedule;
import com.wepiao.goods.vo.SeatPcsTaskParam;

/**
 *  Seat Purchase Task 的 Controller.
 */
@Controller
@RequestMapping("/tasks")
public class SeatPcsTaskController {

  @Autowired
  SeatPcsTaskService seatPurchaseService;

  @Autowired
  private ScheduleService scheduleService;

  private Logger logger = LoggerFactory.getLogger(SeatPcsTaskController.class);

  @RequestMapping(value = "/seat/purchase", method = RequestMethod.POST)
  @ResponseBody
  public CommonResponse<String> seatPruchase(@Valid SeatPcsTaskParam taskParam,
      BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      for (ObjectError error : bindingResult.getAllErrors()) {
        logger.error("seatc purchase bindingResult error : {}", error.getDefaultMessage());
      }
      return ResponseMessage.SE90001.getResponse();
    }
    int successNum = seatPurchaseService.addTask(taskParam);
    return CommonResponse.success(String.valueOf(successNum));
  }

  @RequestMapping(value = "/seat/purchaseForTest", method = RequestMethod.POST)
  @ResponseBody
  public CommonResponse<String> seatPruchaseForTest(Integer scheduleId,
      Integer cinemaNo, Integer channelId, String seats) {
    Schedule schedule = scheduleService.getSchedule(scheduleId,cinemaNo,channelId);
    SeatPcsTaskParam taskParam=new SeatPcsTaskParam();
    taskParam.setHallId(schedule.getHallNo());
    taskParam.setHallName(schedule.getHallName());
    taskParam.setSectionNo(schedule.getSectionNo());
    taskParam.setShowTime(schedule.getShowTime());
    taskParam.setShowTime(DateUtil.format(schedule.getShowDatetime(),"yyyyMMddHHmmss"));
    taskParam.setSource((byte)0);
    taskParam.setBaseCinemaNo(cinemaNo);
    taskParam.setScheduleId(scheduleId);
    taskParam.setSeats(seats);
    int successNum = seatPurchaseService.addTask(taskParam);
    return CommonResponse.success(String.valueOf(successNum));
  }
}
