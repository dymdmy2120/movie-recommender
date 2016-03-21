package com.wx.movie.rec.similarity.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.common.base.Stopwatch;
import com.wx.movie.rec.common.enums.RecommendType;
import com.wx.movie.rec.common.enums.RedisKey;
import com.wx.movie.rec.pojo.UserActionData;
import com.wx.movie.rec.recommendlist.service.ProRecListSerivce;
import com.wx.movie.rec.redis.RedisUtils;
import com.wx.movie.rec.similarity.common.FinalSilityCommonService;
import com.wx.movie.rec.similarity.common.ProSilityCommonService;
import com.wx.movie.rec.similarity.service.ProdSimilarityService;

/**
 * Date: 2016年2月16日 上午10:32:57 <br/>
 *
 * @author dynamo
 */
@Service("bseMovieSimilarity")
public class ProdSimilarityBseMovieServiceImpl implements ProdSimilarityService {
  @Autowired
  private ProSilityCommonService proSimComService;
  @Autowired
  private FinalSilityCommonService fnlSimComService;
  @Autowired
  @Qualifier("bseMovieRecList")
  private ProRecListSerivce bseMovieProRecListService;
  @Autowired
  private RedisUtils redisUtils;
  private static final Logger logger = LoggerFactory.getLogger(ProdSimilarityBseMovieServiceImpl.class);

  /**
   * 基于影片 计算相似度
   *  1、计算 影片M1 和影片M2交集，就是求出影片M1对应的Set<String>用户集合和M2对应的Set<String>交集
   * 2、计算影片M1对应的Set<String>用户集合的元素个数 和M2对应的Set<String>的个数
   * 3、 交集/用户集合的元素个数开根号 * 用户集合的元素个数开根号
   */
  @Override
  public void prodSimilarity(UserActionData actionData) {
    Stopwatch timer = Stopwatch.createStarted();
    proSimComService.handleUserActionData(actionData.getAction(), actionData.getUserActionMap(),
        RecommendType.BSE_MOVIE);
  //标识 当前用户行为操作的相似度完成的个数，当所有用户行为操作完成了后，再进行根据行为比例，得到最终相似度
    redisUtils.incr(String.format(RedisKey.COUNT_SIMILARITY, RecommendType.BSE_MOVIE));
    logger.info("ProdSimilarityBseMovie.prodSimilarity useraction is {} base on {},take total time {}",
        actionData.getAction(), RecommendType.BSE_MOVIE, timer.stop());
  }

  @Override
  @Async("computeFinalSimilarityExecutor")
  public void prodFinalSimilarity() {
    while (true) {
      String rtKey = String.format(RedisKey.COUNT_SIMILARITY, RecommendType.BSE_MOVIE);
      int times = fnlSimComService.getActionTimes(rtKey);

      if (times == fnlSimComService.getActionSize()) {
        Stopwatch timer = Stopwatch.createStarted();
        // 计算最终的相似度
        Map<String, Map<String, Double>> finalSimilarity =
            fnlSimComService.getFinalSimilarity(RecommendType.BSE_MOVIE);
        // writeDataToFile(finalSimilarity,"/home/dynamo/bseMovie.txt");
        // 调用生成推荐列表模块
        bseMovieProRecListService.productRecList(finalSimilarity);
        // 计算完后将标志位置为0
        redisUtils.setInt(rtKey, 0);
        logger.info("BseUsrFinalSimilarity Base on Movie take time is {}", timer.stop());
      }
    }
    }
}
