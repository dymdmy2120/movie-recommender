package com.wepiao.goods.mapper.goods;

import com.wepiao.goods.mapper.BaseDaoTest;
import com.wepiao.goods.model.goods.LocalOrderInfo;
import com.wepiao.goods.service.BaseTest;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by qilei on 15/12/30.
 */
public class LocalOrderInfoMapperTest extends BaseDaoTest {

  @Autowired
  private LocalOrderInfoMapper localOrderInfoMapper;

  @Test
  public void testGetByOrderId() throws Exception {
    List<LocalOrderInfo> localOrderInfos = localOrderInfoMapper.getByOrderId("150804172939345756");
    assertTrue(localOrderInfos.size()>0);
  }
}