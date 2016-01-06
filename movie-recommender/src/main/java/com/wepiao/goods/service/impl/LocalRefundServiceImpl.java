package com.wepiao.goods.service.impl;

import java.util.List;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.wepiao.goods.common.entity.GoodsException;
import com.wepiao.goods.common.enums.RabbitMqName;
import com.wepiao.goods.common.utils.Bis;
import com.wepiao.goods.model.goods.LocalOrderInfo;
import com.wepiao.goods.model.goods.TicketRefundInfoBean;
import com.wepiao.goods.mq.LocalRefundMessage;
import com.wepiao.goods.service.LocalRefundService;
import com.wxmovie.bis.ticketplatform.entity.ReturnTicketInfo;
import com.wxmovie.bis.ticketplatform.result.IBisResult;

@Service
public class LocalRefundServiceImpl extends BaseSeatRefundService
    implements
      LocalRefundService {

  @Autowired
  @Qualifier("jsonRabbitTemplate")
  private AmqpTemplate amqpTemplate;

  @Override
  public boolean refundTicket(String orderId) throws GoodsException {
    return true;
  }

  @Override
  public boolean refundLocalSeats(LocalOrderInfo localOrderInfo) {
    boolean isSuccess = false;
    try {

      List<Object> returnTicketParams =
          getReturnTicketParams(localOrderInfo.getScheduleId(), localOrderInfo.getCinemaNo(),
              localOrderInfo.getChannelId(), localOrderInfo.getTpOrderId(), 1);

    // 封装退票请求对象
    TicketRefundInfoBean ticketRefundInfoBean = new TicketRefundInfoBean();
    ticketRefundInfoBean.setReturnTicketInfo((ReturnTicketInfo) returnTicketParams.get(0));
    ticketRefundInfoBean.setBisServerId(String.valueOf(returnTicketParams.get(1)));
//    ticketRefundInfoBean.setRetryTimes(0);
    ticketRefundInfoBean.setChannelId(localOrderInfo.getChannelId());

    // 调用影院接口开始退票
    IBisResult result = Bis.returnTicket(ticketRefundInfoBean.getReturnTicketInfo(),
        ticketRefundInfoBean.getBisServerId());
    if (result != null && "succ".equals((result.getErrorMsg()))) {
      refundTicketOk(ticketRefundInfoBean);
      isSuccess = true;
    } else {
//      refundTicketFail(ticketRefundInfoBean);
    }

    } catch (Exception e) {
      logger.error("refundLocalSeats error: ", e);
      isSuccess = false;
    }
    return isSuccess;
  }

  @Override
  public void refundTicketFail(LocalOrderInfo localOrderInfo) {
    List<Object> returnTicketParams =
        getReturnTicketParams(localOrderInfo.getScheduleId(), localOrderInfo.getCinemaNo(),
            localOrderInfo.getChannelId(), localOrderInfo.getTpOrderId(), 1);

    // 封装退票请求对象
    TicketRefundInfoBean ticketRefundInfoBean = new TicketRefundInfoBean();
    ticketRefundInfoBean.setReturnTicketInfo((ReturnTicketInfo) returnTicketParams.get(0));
    ticketRefundInfoBean.setBisServerId(String.valueOf(returnTicketParams.get(1)));
    ticketRefundInfoBean.setRetryTimes(0);
    ticketRefundInfoBean.setChannelId(localOrderInfo.getChannelId());
    super.refundTicketFail(ticketRefundInfoBean);
  }

  @Override
  public void cronLocalRefund() {
    List<LocalOrderInfo> needRefundOrders = localOrderInfoMapper.selectAllRefundTickets();
    logger.debug("cronLocalRefund get {} orders", needRefundOrders.size());

    // 把任务放入队列
    for (LocalOrderInfo order : needRefundOrders) {
      LocalRefundMessage localRefundMsg = new LocalRefundMessage(order);
      logger.debug("Send LocalRefund task to Queue: {}",
          RabbitMqName.Goods_CronJob_LocalRefund_Queue.name());
      amqpTemplate.convertAndSend(RabbitMqName.Goods_CronJob_LocalRefund_Queue.name(),
          localRefundMsg);
    }
  }
}
