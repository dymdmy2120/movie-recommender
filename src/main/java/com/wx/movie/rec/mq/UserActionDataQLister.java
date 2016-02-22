package com.wx.movie.rec.mq;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rabbitmq.client.Channel;
import com.wx.movie.rec.common.util.JsonMapperUtil;
import com.wx.movie.rec.pojo.UserActionData;
import com.wx.movie.rec.eigenvector.service.ProdEigenVectorService;

public class UserActionDataQLister implements ChannelAwareMessageListener {
  @Autowired
  private ZoListenerHelper mqHelper;
  
  @Qualifier("prodEigenVector")
  @Autowired
private ProdEigenVectorService proEigVecService;
  
  private TypeReference<List<UserActionData>> tr;
  
  private static final Logger logger = LoggerFactory.getLogger(UserActionDataQLister.class);
/**
 * 返回的是一个 UserActionData 对象json字符串
 * 有用户操作行为属性，Map<String,Set<String>> 用户对那些影片操作过
 */
  
  @PostConstruct
public void init() {
    tr = new TypeReference<List<UserActionData>>(){};
}
  @Override
  public void onMessage(Message message, Channel channel) {
    try {
      List<UserActionData> userActionDatas = JsonMapperUtil.getInstance().fromJson(message.getBody(), tr);
      proEigVecService.produceEigenVector(userActionDatas);
      logger.info("Rec User Action Data :{}", JsonMapperUtil.getInstance().toJson(userActionDatas));
    } catch (Throwable e) {
      logger.error("UserActionDataQLister process message failed.", e);
    } finally {
      mqHelper.sendAck(message, channel);
    }
  }
}