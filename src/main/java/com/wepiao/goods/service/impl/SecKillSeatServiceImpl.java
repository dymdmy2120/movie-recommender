package com.wepiao.goods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.wepiao.goods.common.entity.GoodsException;
import com.wepiao.goods.model.goods.LocalOrderInfo;
import com.wepiao.goods.service.SecKillSeatService;

@Service
public class SecKillSeatServiceImpl implements SecKillSeatService {

  @Override
  public List<LocalOrderInfo> getSecKillSeats(int secKillCount) throws GoodsException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int changeFailedSecKillSeatStatus(List<Map<String, List<String>>> secKillFailSeats) {
    // TODO Auto-generated method stub
    return 0;
  }

}
