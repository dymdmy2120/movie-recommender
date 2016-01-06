package com.wepiao.goods.mapper.open;

import com.google.common.collect.Maps;
import com.wepiao.goods.mapper.BaseDaoTest;
import com.wepiao.goods.service.BaseTest;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by qilei on 15/12/30.
 */
public class ForbidSeatMapperTest extends BaseDaoTest {

  @Autowired
  private ForbidSeatMapper forbidSeatMapper;

  @Test
  public void testGetForbidSeat() throws Exception {
    Map<String, Object> map = Maps.newHashMap();
    map.put("baseCinemaNo", "1000103");
    map.put("showTime", new DateTime(2015,6,1,13,0));
    List<String> forbidSeat = forbidSeatMapper.getForbidSeat(map);
    //todo
    assertTrue(forbidSeat ==null);
  }
}