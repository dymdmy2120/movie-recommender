package com.wepiao.goods.mq;

import java.util.Date;

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
import com.wepiao.goods.common.entity.CommonResponse;
import com.wepiao.goods.common.enums.RabbitMqName;
import com.wepiao.goods.common.enums.SeatTaskStatus;
import com.wepiao.goods.mapper.goods.SeatPurchaseTaskMapper;
import com.wepiao.goods.model.goods.SeatPurchaseTask;
import com.wepiao.goods.service.SeatLockService;
import com.wepiao.goods.service.TicketDrawService;
import com.wepiao.goods.service.ZoListenerHelper;
import com.wepiao.goods.vo.SeatLockParam;

/**
 * Rabbit MQ listener to handle seat purchase task.
 */
public class SeatLockAndDrawQListener implements ChannelAwareMessageListener {

  private static final Logger logger = LoggerFactory.getLogger(SeatLockAndDrawQListener.class);

  @Autowired
  SeatLockService seatLockService;

  @Autowired
  TicketDrawService ticketDrawService;

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

  private TypeReference<SeatPcsTaskMessage> tr;

  @PostConstruct
  public void init() {
    tr = new TypeReference<SeatPcsTaskMessage>() {};
  }

  @Override
  public void onMessage(Message message, Channel channel) {
    try {
      SeatPcsTaskMessage seatPcsTaskMsg = JsonMapper.getInstance().fromJson(message.getBody(), tr);
      SeatPurchaseTask purchaseTask = seatPcsTaskMsg.getSeatPurchaseTask();
      logger.debug("onMessage: {}", JsonMapper.getInstance().toJson(purchaseTask));

      boolean locked = true;

      // 锁座
      if (seatPcsTaskMsg.getSeatStatus().equals(SeatTaskStatus.INIT)) {
        locked = this.lock(seatPcsTaskMsg);
      }

      // 如果锁座成功, 出票
      if (locked) {
         // 锁座成功后， delay一段时间再出票。
        logger.info("Cron lock seat succeed: {}", purchaseTask.getSeat());
        delay(drawInterval);
        draw(seatPcsTaskMsg);
        logger.info("Cron draw ticket succeed: {}", purchaseTask.getSeat());
      }

    } catch (Throwable e) {
      logger.error("SeatLockAndDrawQListener process message failed.", e);
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

  private boolean lock(SeatPcsTaskMessage seatPcsTaskMsg) {
    boolean result = false;
    SeatLockParam lockSeatParam = seatPcsTaskMsg.getSeatLockParam();
    CommonResponse<?> lockResp = seatLockService.lock(lockSeatParam);

    if (lockResp.isSucc()) {
      result = true;
      seatPcsTaskMsg.setRetryTimes(0);
      seatPcsTaskMsg.setSeatStatus(SeatTaskStatus.LOCKED); // 不计入数据库

    } else {
      result = false;
      // 锁座失败， 如果没有超过重试次数， 放入队列继续处理。
      logger.error("cron lock seat failed : {}", lockSeatParam.getSeats());
      retry(seatPcsTaskMsg);
    }
    return result;
  }

  private boolean draw(SeatPcsTaskMessage seatPcsTaskMsg) {
    boolean result;
    SeatPurchaseTask purchaseTask = seatPcsTaskMsg.getSeatPurchaseTask();
    SeatLockParam lockSeatParam = seatPcsTaskMsg.getSeatLockParam();

    CommonResponse<?> drawResp = ticketDrawService.drawOneTicketByBis(lockSeatParam.getOrderId(),
        lockSeatParam.getMobile(), lockSeatParam.getRequestId());

    if (drawResp.isSucc()) {
      result = true;
      // 出票成功，更新任务表。
      updateSeatPcsTask(purchaseTask, SeatTaskStatus.DRAWED);

    } else {
      result = false;
      logger.error("cron draw ticket failed : {}", lockSeatParam.getSeats());
      retry(seatPcsTaskMsg);
    }
    return result;
  }

  private void retry(SeatPcsTaskMessage seatPcsTaskMsg) {
    // 锁座或出票失败， 放入队列继续处理。
    seatPcsTaskMsg.setRetryTimes(seatPcsTaskMsg.getRetryTimes() + 1);
    if (seatPcsTaskMsg.getRetryTimes() > retryTimes) {
      // 更新任务表。
      logger.debug("cron lockAndDraw retry times exceeds {}", seatPcsTaskMsg.getRetryTimes());
      updateSeatPcsTask(seatPcsTaskMsg.getSeatPurchaseTask(), SeatTaskStatus.FAILED);
    } else {
      logger.debug("cron lockAndDraw retry times: {}", seatPcsTaskMsg.getRetryTimes());
      delay(retryInterval);
      amqpTemplate.convertAndSend(RabbitMqName.Goods_CronJob_SeatLockAndDraw_Queue.name(),
          seatPcsTaskMsg);
    }
  }

  private void updateSeatPcsTask(SeatPurchaseTask purchaseTask, SeatTaskStatus status) {
    SeatPurchaseTask updateTask = new SeatPurchaseTask();
    updateTask.setId(purchaseTask.getId());
    updateTask.setSeatStatus(status.getVal());
    updateTask.setModifiedTime(new Date());
    seatPurchaseTaskMapper.updateSelective(updateTask);
  }

}
