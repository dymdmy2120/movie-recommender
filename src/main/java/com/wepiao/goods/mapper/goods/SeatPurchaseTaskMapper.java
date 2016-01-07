package com.wepiao.goods.mapper.goods;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.wepiao.goods.model.goods.SeatPurchaseTask;

public interface SeatPurchaseTaskMapper {

  int insert(SeatPurchaseTask record);

  int updateSelective(SeatPurchaseTask record);

  SeatPurchaseTask getById(Integer id);

  List<SeatPurchaseTask> getByCriteria(SeatPurchaseTask record);

  List<SeatPurchaseTask> getByCreateTime(@Param("count") int count,
      @Param("createdTime") Date createdTime);

  List<SeatPurchaseTask> getBySeatStatus(@Param("seatStatus")byte... seatStatus);

}
