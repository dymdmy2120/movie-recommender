package com.wepiao.goods.service;

import com.wepiao.goods.service.impl.ScheduleServiceImpl;
import com.wepiao.goods.vo.Schedule;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by qilei on 15/12/25.
 */
public class ScheduleServiceTest {

  private ScheduleService scheduleService;

  @Before
  public void setUp() throws Exception {
    scheduleService = new ScheduleServiceImpl("http://192.168.101.40:8080");
  }

  @Test
  public void testGetSchedule() throws Exception {
    Schedule schedule = scheduleService.getSchedule(1374, 1000103, 0);
    System.out.println(schedule);
  }

  @Test
  public void testGetScheduleByPriceRecordId(){
    Schedule schedule = scheduleService.getScheduleByPriceRecordId(15350);
    assertTrue(null != schedule);
  }

  @Test(expected = RuntimeException.class)
  public void testGetSchedule_not_found(){
    Schedule schedule = scheduleService.getSchedule(137411, 1000103, 0);
    System.out.println(schedule);
  }
}