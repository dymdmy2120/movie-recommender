package com.wepiao.goods.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.wepiao.common.utils.DateUtil;
import com.wepiao.goods.common.entity.GoodsException;
import com.wepiao.goods.common.entity.ResponseMessage;
import com.wepiao.goods.common.enums.RabbitMqName;
import com.wepiao.goods.common.enums.SeatTaskStatus;
import com.wepiao.goods.mapper.goods.SeatPurchaseTaskMapper;
import com.wepiao.goods.model.goods.SeatPurchaseTask;
import com.wepiao.goods.mq.SeatPcsTaskMessage;
import com.wepiao.goods.service.SeatPcsTaskService;
import com.wepiao.goods.vo.SeatPcsTaskParam;

@Service
public class SeatPcsTaskServiceImpl implements  SeatPcsTaskService {

  @Autowired
  SeatPurchaseTaskMapper seatPurchaseTaskMapper;

  @Autowired
  @Qualifier("jsonRabbitTemplate")
  private AmqpTemplate amqpTemplate;

  private Logger logger = LoggerFactory.getLogger(SeatPcsTaskServiceImpl.class);

  @Override
  public int addTask(SeatPcsTaskParam taskParam) {
    // 获取推送过来的座位的集合，座位号以逗号隔开
    String seats = taskParam.getSeats();
    String sectionNo = taskParam.getSectionNo();
    String[] seatsArray = seats.split(",");

    SeatPurchaseTask task = new SeatPurchaseTask();
    task.setScheduleId(taskParam.getScheduleId());
    task.setCinemaNo(taskParam.getBaseCinemaNo());
    task.setHallId(taskParam.getHallId());
    task.setHallName(taskParam.getHallName());
    task.setSeatStatus(SeatTaskStatus.INIT.getVal());
    Date createdTime = new Date();
    task.setCreatedTime(createdTime);
    task.setModifiedTime(createdTime);
    task.setSource(taskParam.getSource());
    Date showTime = DateUtil.string2date(taskParam.getShowTime(), "yyyyMMddHHmmss");
    if (showTime != null) {
      task.setShowTime(showTime);
    } else {
      logger.error("invalid showTime format: {}", taskParam.getShowTime());
      throw new GoodsException(ResponseMessage.SE90001);
    }

    int insertCount = 0;
    try {
      for (String seat : seatsArray) {
        //task.setSeat(String.format("%s:%s", sectionNo, seat.trim()));
        task.setSeat(seat.trim());
        seatPurchaseTaskMapper.insert(task);
        insertCount++;
      }
    } catch (Exception e) {
      logger.error("insert task to seat_purchase_task error: ", e);
    }
    return insertCount;
  }

  @Override
  public void cronLockAndDraw() {
//    Integer fetchCount = 100;
    List<SeatPurchaseTask> tasks =
        seatPurchaseTaskMapper.getBySeatStatus(SeatTaskStatus.INIT.getVal());
    logger.debug("cronLockAndDraw get {} tasks", tasks.size());

    // 把任务放入队列
    for (SeatPurchaseTask task : tasks) {
      SeatPcsTaskMessage seatPcsTaskMsg = new SeatPcsTaskMessage(task);
      logger.debug("Send seatPurchaseTask to Queue: {}",
          RabbitMqName.Goods_CronJob_SeatLockAndDraw_Queue.name());
      amqpTemplate.convertAndSend(RabbitMqName.Goods_CronJob_SeatLockAndDraw_Queue.name(),
          seatPcsTaskMsg);
    }
  }

}
