/**
 * Project Name:wp-local-stock File Name:SecKillSeatsController.java Package
 * Name:com.wepiao.products.stock.seat.controller Date:2015年7月17日下午11:38:10
 */

package com.wepiao.goods.web;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wepiao.goods.common.entity.CommonResponse;
import com.wepiao.goods.model.goods.LocalOrderInfo;
import com.wepiao.goods.service.SecKillSeatService;

/**
 * ClassName:SecKillSeatsController <br/> Function: 秒杀座位控制层. <br/> Reason: TODO ADD REASON. <br/>
 * Date: 2015年7月17日 下午11:38:10 <br/>
 *
 * @author zhiyong.fan
 * @see
 */
@Controller
@RequestMapping("/api/seat")
public class SecKillSeatsController {

  private Logger logger = LoggerFactory.getLogger(SecKillSeatsController.class);

  @Autowired
  private SecKillSeatService secKillSeatServiceImpl;

  @RequestMapping("/pushSecKillSeats")
  @ResponseBody
  public CommonResponse<List<LocalOrderInfo>> pushSecKillSeats(int secKillCount) {
    return null;
  }

  @RequestMapping("/pullSecKillSeats")
  public void pullFailSecKillSeats(@RequestParam("data") String failedSecKillSeats) {
    return;
  }

  /**
   * parseJsonToMap: 解析json字符串为List<Map<String, List<String>>>. <br/> key:排期ID value:该排期对应的所有座位
   * <br/>
   *
   * @param result 生成的Map<String, List<String>>
   * @param jsonStr 源始的json字符串
   */
  private void parseJsonToMap(List<Map<String, List<String>>> result, String jsonStr) {
    Gson gson = new Gson();
    JsonObject jsonObj = gson.fromJson(jsonStr, JsonObject.class);
    JsonArray jsonArray = jsonObj.get("data").getAsJsonArray();
    Iterator<JsonElement> iterator = jsonArray.iterator();
    JsonElement jsonEle = null;
    Map<String, List<String>> values = Maps.newHashMap();
    String key = null;
    List<String> value = Lists.newArrayList();
    while (iterator.hasNext()) {
      jsonEle = iterator.next();
      key = jsonEle.getAsJsonObject().get("scheduleId").getAsString();
      value = Splitter.on("|").splitToList(jsonEle.getAsJsonObject().get("seats").getAsString());
      values.put(key, value);
      result.add(values);
    }
  }
}
