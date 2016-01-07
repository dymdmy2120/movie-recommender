package com.wepiao.goods.service;

import com.wepiao.goods.model.goods.LocalOrderInfo;

/**
 * Created by zhangliang on 2016/1/5.
 * Function:自有库存退票服务
 */
public interface LocalRefundService extends TicketRefundService {

    public boolean refundLocalSeats(LocalOrderInfo localOrderInfo);

    public void refundTicketFail(LocalOrderInfo localOrderInfo);

    public void cronLocalRefund();
}
