package com.wepiao.goods.mq;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rabbitmq.client.Channel;
import com.wepiao.common.utils.JsonMapper;
import com.wepiao.goods.common.enums.RabbitMqName;
import com.wepiao.goods.mapper.goods.SeatPurchaseTaskMapper;
import com.wepiao.goods.model.goods.LocalOrderInfo;
import com.wepiao.goods.service.LocalRefundService;
import com.wepiao.goods.service.SeatLockService;
import com.wepiao.goods.service.ZoListenerHelper;

/**
 * Rabbit MQ listener to handle seat purchase task.
 */
public class LocalRefundQListener implements ChannelAwareMessageListener {

  private static final Logger logger = LoggerFactory.getLogger(LocalRefundQListener.class);

  @Autowired
  SeatLockService seatLockService;

  @Autowired
  LocalRefundService localTicketRefundService;

  @Autowired
  SeatPurchaseTaskMapper seatPurchaseTaskMapper;

  @Autowired
  ZoListenerHelper mqHelper;

  @Value("${seatPurchaseTask.draw.interval}")
  private Integer drawInterval;

  @Value("${seatPurchaseTask.retry.interval}")
  private Integer retryInterval;

  @Value("${seatPurchaseTask.retry.times}")
  private Integer retryTimes;

  @Autowired
  @Qualifier("jsonRabbitTemplate")
  private AmqpTemplate amqpTemplate;

  private TypeReference<LocalRefundMessage> tr;

  @PostConstruct
  public void init() {
    tr = new TypeReference<LocalRefundMessage>() {};
  }

  @Override
  public void onMessage(Message message, Channel channel) {
    try {
      LocalRefundMessage localRefundMessage =
          JsonMapper.getInstance().fromJson(message.getBody(), tr);
      LocalOrderInfo localOrderInfo = localRefundMessage.getLocalOrderInfo();
      logger.debug("onMessage: {}", JsonMapper.getInstance().toJson(localOrderInfo));

      // 退票
      this.refund(localRefundMessage);

    } catch (Throwable e) {
      logger.error("LocalRefundQListener process message failed.", e);
    } finally {
      mqHelper.sendAck(message, channel);
    }
  }

  private void delay(int dealyInterval) {
    try {
      Thread.sleep(dealyInterval);
    } catch (Exception e) {
      logger.error("delay error: ", e);
    }
  }

  private boolean refund(LocalRefundMessage localRefundMessage) {
    boolean isSuccess =
        localTicketRefundService.refundLocalSeats(localRefundMessage.getLocalOrderInfo());

    if (!isSuccess) {
      // 退票失败， 如果没有超过重试次数， 放入队列继续处理。
      logger.error("cron refund failed : {}",
          localRefundMessage.getLocalOrderInfo().getFixOrderId());
      retry(localRefundMessage);
    }
    return isSuccess;
  }

  private void retry(LocalRefundMessage localRefundMessage) {
    // 操作失败， 放入队列继续处理。
    localRefundMessage.setRetryTimes(localRefundMessage.getRetryTimes() + 1);
    if (localRefundMessage.getRetryTimes() > retryTimes) {
      logger.debug("cron refund retry times exceeds {}", localRefundMessage.getRetryTimes());
      localTicketRefundService.refundTicketFail(localRefundMessage.getLocalOrderInfo());

    } else {
      logger.debug("cron refund retry times: {}", localRefundMessage.getRetryTimes());
      delay(retryInterval);
      amqpTemplate.convertAndSend(RabbitMqName.Goods_CronJob_LocalRefund_Queue.name(),
          localRefundMessage);
    }
  }

}
