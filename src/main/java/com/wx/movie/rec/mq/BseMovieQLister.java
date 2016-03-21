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

public class BseMovieQLister implements ChannelAwareMessageListener {
  @Autowired
  private ZoListenerHelper mqHelper;
  @Qualifier("bseMovieEigVec")
  @Autowired
  private ProdEigenVectorService proBseMovieVecService;

  private static final Logger logger = LoggerFactory.getLogger(BseMovieQLister.class);

  @Override
  public void onMessage(Message message, Channel channel) {
    try {
      /**
       * 返回的是一个 UserActionData
       * 有用户操作行为属性，Map<String,Set<String>> 用户对那些影片操作过
       */
      UserActionData bseMovieActionData = JsonMapperUtil.getInstance().fromJson(message.getBody(),UserActionData.class);
      proBseMovieVecService.produceEigenVector(bseMovieActionData);
      logger.info("Rec BaseMovie Action :{}",
          JsonMapperUtil.getInstance().toJson(bseMovieActionData));
    } catch (Throwable e) {
      logger.error("UserActionDataQLister process message failed.", e);
    } finally {
      mqHelper.sendAck(message, channel);
    }
  }
}