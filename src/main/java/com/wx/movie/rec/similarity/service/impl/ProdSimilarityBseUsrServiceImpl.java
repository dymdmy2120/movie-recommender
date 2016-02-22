package com.wx.movie.rec.similarity.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Stopwatch;
import com.wx.movie.rec.common.enums.Constant;
import com.wx.movie.rec.common.enums.RedisKey;
import com.wx.movie.rec.pojo.UserActionData;
import com.wx.movie.rec.redis.RedisUtils;
import com.wx.movie.rec.similarity.common.CommonService;
import com.wx.movie.rec.similarity.service.ProdSimilarityService;

/**
 * Date: 2016年2月16日 上午10:32:57 <br/>
 * 
 * @author dynamo
 */
@Service("bseUsrSimilarity")
public class ProdSimilarityBseUsrServiceImpl implements ProdSimilarityService {
  @Autowired
  private CommonService commonService;
  @Autowired
  private RedisUtils redisUtils;
  private static final Logger logger = LoggerFactory.getLogger(ProdSimilarityBseUsrServiceImpl.class);
  /**
   * 基于用户 计算相似度
   *  1、计算 用户uId1 和用户uId2交集，就是求出用户uId1对应的Set<String>影片集合和uId2对应的Set<String>交集 
   * 2、计算用户uId1对应的Set<String>影片集合的元素个数 和uId2对应的Set<String>的个数 
   * 3、 交集/影片集合的元素个数开根号 * 影片集合的元素个数开根号
   */
  @Override
  public void prodSimilarity(UserActionData actionData) {
    Stopwatch timer = Stopwatch.createStarted();
    commonService.handleUserActionData(actionData.getAction(), actionData.getUserActionMap(),
        Constant.BSE_USE);
    //标识 当前用户行为操作的相似度完成的个数，当所有用户行为操作完成了后，再进行根据行为比例，得到最终相似度
    redisUtils.incr(String.format(RedisKey.COUNT_SIMILARITY, Constant.BSE_USE));
    logger.info("ProdSimilarityBseUsrServiceImspl.prodSimilarity useraction is {} base on {},take total time {}",
      actionData.getAction(), Constant.BSE_USE, timer.stop());
  }

}
