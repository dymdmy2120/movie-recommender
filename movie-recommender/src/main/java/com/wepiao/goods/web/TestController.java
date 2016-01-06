package com.wepiao.goods.web;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wepiao.goods.common.entity.CommonResponse;
import com.wepiao.goods.mapper.goods.BisOrderInfoMapper;
import com.wepiao.goods.mapper.goods.LocalOrderInfoMapper;
import com.wepiao.goods.model.goods.BisOrderInfo;
import com.wepiao.goods.model.goods.LocalOrderInfo;
import com.wepiao.goods.vo.Schedule;

/**
 * Created by qilei on 15/8/11.
 */
@Controller
public class TestController {
  private static Logger logger = LoggerFactory.getLogger(TestController.class);
  @Autowired
  private LocalOrderInfoMapper localOrderInfoMapper;
  @Autowired
  private BisOrderInfoMapper bisOrderInfoMapper;
  @Value("${env}")
  private String env;
  @Value("${http.getSchedules}")
  private String urlGetSchedules;
  @Value("${http.getAvailableSeats}")
  private String urlGetAvailableSeats;

  @RequestMapping("/test")
  public String index() {
    return "index";
  }

  @RequestMapping("/test/schedules")
  public String showSchedules(Integer baseCinemaNo
      , @RequestParam(value = "tpId", defaultValue = "0", required = false) String tpId
      , Model model) {
    RestTemplate restTemplate = new RestTemplate();
    //urlGetSchedules = "http://192.168.101.40:8080/api/schedulePrice/getList?baseCinemaNo=%s";
    String resourceUrl = String.format(urlGetSchedules, baseCinemaNo);
    CommonResponse<Map<Integer, List<Schedule>>> result = restTemplate.exchange(resourceUrl,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<CommonResponse<Map<Integer, List<Schedule>>>>() {
        }).getBody();
    List<Schedule> list = filterByTpId(result.getData(), tpId);
    model.addAttribute("scheduleList", list);
    return "test/scheduleList";
  }

  @RequestMapping("/test/seats")
  public String showSeats(Integer scheduleId, Integer cinemaNo, Integer channelId, Model model) {
    RestTemplate restTemplate = new RestTemplate();
    //urlGetAvailableSeats = "http://localhost:8080/seats/available?scheduleId=%s&cinemaNo=%s&channelId=%s";
            //String resourceUrl = String.format("http://localhost:8080/api/seat/salableSeats?scheduleId=%s&cinemaNo=%s&tpId=%s", scheduleId,cinemaNo,tpId);
    String resourceUrl = String.format(urlGetAvailableSeats, scheduleId, cinemaNo, channelId);

    CommonResponse<String> result = restTemplate.exchange(resourceUrl,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<CommonResponse<String>>() {
        }).getBody();
    Set<String> seats = Sets.newHashSet();
    String resultStr = result.getData();
    if (!Strings.isNullOrEmpty(resultStr)) {
      //        String resultStr = "[01:1:1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17:1|01:2:1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17:1|01:3:1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17:1|01:4:1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17:1|01:5:1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17:1|01:6:1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17:1|01:7:1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17:1|01:8:1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17:1|01:9:1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17:1]";
      resultStr = resultStr.replaceAll("\\[|\\]", "");
      String[] rows = resultStr.split("\\|");
      for (String row : rows) {
        String[] parts = row.split(":");
        String section = parts[0];
        String rowNum = parts[1];
        String[] cols = parts[2].split(",");
        for (String col : cols) {
          seats.add(Joiner.on(":").join(section, rowNum, col));
        }
      }
    }
    Stopwatch timer = Stopwatch.createStarted();
    String[] objects = seats.toArray(new String[0]);
    Arrays.sort(objects, new Comparator<String>() {
      @Override
      public int compare(String seats1, String seats2) {
        if (seats1.split(":")[1].compareTo(seats2.split(":")[1]) == 0) {
          return Integer.valueOf(seats1.split(":")[2].split("-")[0])
              .compareTo(Integer.valueOf(seats2.split(":")[2].split("-")[0]));
        } else {
          return Integer.valueOf(seats1.split(":")[1])
              .compareTo(Integer.valueOf(seats2.split(":")[1]));
        }
      }
    });
    logger.info("Seats sort take time:{}", timer.stop());
    model.addAttribute("seats", objects);
    return "test/seats";
  }

  @RequestMapping("/test/order")
  public String drawTicket(String orderId, Model model) {
    List<LocalOrderInfo> localOrderInfoList = localOrderInfoMapper.getByOrderId(orderId);
    BisOrderInfo bisOrderInfo = bisOrderInfoMapper.getByOrderId(orderId);
    model.addAttribute("localOrderInfoList", localOrderInfoList);
    model.addAttribute("bisOrderInfo", bisOrderInfo);
    model.addAttribute("env", env);
    return "test/order";
  }

  private List<Schedule> filterByTpId(Map<Integer, List<Schedule>> data,
      String tpId) {
    List<Schedule> r = Lists.newArrayList();
    for (Integer scheduleId : data.keySet()) {
      List<Schedule> Schedules = data.get(scheduleId);
      for (Schedule schedule : Schedules) {
        if (schedule.getTpId().equals(tpId)) {
          r.add(schedule);
        }
      }
    }
    return r;
  }
}
