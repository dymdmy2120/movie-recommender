package com.wx.movie.rec.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.rabbitmq.client.Channel;
import com.wx.movie.rec.common.util.JsonMapperUtil;
import com.wx.movie.rec.eigenvector.service.ProdEigenVectorService;
import com.wx.movie.rec.pojo.UserActionData;

public class BseUsrQLister implements ChannelAwareMessageListener {
  @Autowired
  private ZoListenerHelper mqHelper;

  @Qualifier("bseUserEigVec")
  @Autowired
  private ProdEigenVectorService proBseUsrVecService;
  private static final Logger logger = LoggerFactory.getLogger(BseUsrQLister.class);

  @Override
  public void onMessage(Message message, Channel channel) {
    try {
      UserActionData userActionData =
          JsonMapperUtil.getInstance()
.fromJson(message.getBody(), UserActionData.class);
      proBseUsrVecService.produceEigenVector(userActionData);
      logger.info("Rec BaseUser Action  Data :{}",
          JsonMapperUtil.getInstance().toJson(userActionData));
    } catch (Throwable e) {
      logger.error("UserActionDataQLister process message failed.", e);
    } finally {
      mqHelper.sendAck(message, channel);
    }
  }
}