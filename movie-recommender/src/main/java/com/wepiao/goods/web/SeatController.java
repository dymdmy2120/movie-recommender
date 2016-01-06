package com.wepiao.goods.web;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wepiao.goods.common.entity.CommonResponse;
import com.wepiao.goods.common.entity.ResponseMessage;
import com.wepiao.goods.service.SeatAvailableService;
import com.wepiao.goods.service.SeatLockService;
import com.wepiao.goods.vo.SeatLockParam;

@Controller
@RequestMapping("/seats")
public class SeatController {

  private Logger logger = LoggerFactory.getLogger(SeatController.class);

  @Autowired
  private SeatAvailableService seatAvailableService;

  @Autowired
  private SeatLockService seatLockService;

  @RequestMapping(value = "/available", method = RequestMethod.GET)
  public @ResponseBody CommonResponse<String> getSalableSeats(Integer scheduleId,
      Integer cinemaNo, Integer channelId,
      @RequestParam(value = "X-Request-Id", required = false) String requestId) {
    String allSalableSeats =
        seatAvailableService.getSalableSeats(scheduleId, cinemaNo, channelId);

    return CommonResponse.success(allSalableSeats);
  }

  @RequestMapping(value = "/lock", method = RequestMethod.POST)
  @ResponseBody
  public CommonResponse<?> lock(@Valid SeatLockParam seatLockParam,
      BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      for (ObjectError error : bindingResult.getAllErrors()) {
        logger.error("seat lock bindingResult error : {}", error.getDefaultMessage());
      }
      return ResponseMessage.SE90001.getResponse();
    }

    return seatLockService.lock(seatLockParam);
  }


  @RequestMapping(value = "/unlock", method = RequestMethod.POST)
  @ResponseBody
  public CommonResponse<Boolean> unlock(@PathVariable("orderId") String orderId,
      @RequestParam("mobileNo") String mobileNo,
      @RequestParam(value = "X-Request-Id", required = false) String requestId) {
    boolean result = seatLockService.unlock(orderId);
    return CommonResponse.success(result);
  }
}
