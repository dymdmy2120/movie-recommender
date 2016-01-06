package com.wepiao.goods.mapper.goods;

import com.wepiao.goods.model.goods.BisOrderInfo;
import org.apache.ibatis.annotations.Param;

public interface BisOrderInfoMapper {

    int insert(BisOrderInfo record);

    int updateSelective(BisOrderInfo record);

    BisOrderInfo getById(Integer id);

    /**
     * 通过订单号（与接入商交互的订单号）查询订单
     * @param fixOrderId 与接入商交互的订单号
     * @return
     */
    BisOrderInfo getByOrderId(String fixOrderId);

    /**
     * updateRefundCommonSeatByOrderId: 通过订单号更新普通座位状态. <br>
     *
     * @param userOrderId 用户订单号
     * @param ticketStatus 票状态
     * @return 成功更新的记录数
     */
    public int updateRefundCommonSeatByFixOrderId(@Param("fixOrderId") String fixOrderId,
            @Param("seatStatus") byte seatStatus);
}