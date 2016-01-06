package com.wepiao.goods.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Stopwatch;
import com.wepiao.goods.common.entity.CommonResponse;
import com.wepiao.goods.common.utils.http.ApiException;
import com.wepiao.goods.common.utils.http.HttpClient;
import com.wepiao.goods.common.utils.http.Parameter;
import com.wepiao.goods.service.ScheduleService;
import com.wepiao.goods.vo.Schedule;

/**
 * Created by qilei on 15/12/25.
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

  private Logger logger = LoggerFactory.getLogger(ScheduleServiceImpl.class);
  private HttpClient httpClient = new HttpClient();
  @Value("${schedule.baseUrl}")
  private String baseUrl;

  public ScheduleServiceImpl() {
  }

  public ScheduleServiceImpl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  @Override public Schedule getSchedule(Integer scheduleId, Integer cinemalNo, Integer channelId) {
    Stopwatch timer = Stopwatch.createStarted();
    Parameter[] params = new Parameter[3];
    params[0] = new Parameter("scheduleId", scheduleId);
    params[1] = new Parameter("tpId", channelId);
    params[2] = new Parameter("baseCinemaNo", cinemalNo);
    String url = baseUrl +"/api/schedulePrice/getOnePrice";
    try {
      CommonResponse<Schedule> commonResponse =
          httpClient.get(url, params, new TypeReference<CommonResponse<Schedule>>() {
          });
      if (commonResponse.isSucc()) {
        logger.info("ScheduleService.getSchedule take time:{}", timer.stop());
        return commonResponse.getData();
      } else {
        throw new RuntimeException("获取排期异常,scheduleId:" + scheduleId + " " + commonResponse.getMsg());
      }
    } catch (ApiException e) {
      throw new RuntimeException(e.getMessage(),e);
    }
  }

  @Override public Schedule getScheduleByPriceRecordId(Integer priceRecordId) {
    String url = baseUrl + "/api/schedulePrice/getOnePriceByRecoreId";
    Parameter[] params = new Parameter[]{new Parameter("priceRecordId",priceRecordId)};
    try {
      CommonResponse<Schedule> commonResponse =
          httpClient.get(url, params, new TypeReference<CommonResponse<Schedule>>() {
          });
      if (commonResponse.isSucc()) {
        return commonResponse.getData();
      }else{
        throw new RuntimeException("获取排期异常:priceRecordId:" + priceRecordId + " " + commonResponse.getMsg());
      }
    } catch (ApiException e) {
      throw new RuntimeException(e.getMessage(),e);
    }
  }
}
