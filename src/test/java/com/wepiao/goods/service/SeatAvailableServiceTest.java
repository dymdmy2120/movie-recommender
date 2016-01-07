package com.wepiao.goods.service;

import java.util.Set;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by qilei on 15/12/31.
 */
public class SeatAvailableServiceTest extends BaseTest {

  @Autowired
  private SeatAvailableService seatAvailableService;

  @Test
  public void testGetSalableSeats() throws Exception {
    String salableSeats = seatAvailableService.getSalableSeats(1374, 1000103, 0);
    assertTrue(salableSeats!=null);
  }
}