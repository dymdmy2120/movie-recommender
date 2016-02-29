package com.wx.movie.rec.recommendlist.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.wx.movie.rec.common.util.JsonMapperUtil;
import com.wx.movie.rec.filter.service.FilterRecListService;
import com.wx.movie.rec.recommendlist.common.CommonService;
import com.wx.movie.rec.recommendlist.pojo.Movie;
import com.wx.movie.rec.recommendlist.pojo.User;
import com.wx.movie.rec.recommendlist.service.ProRecListSerivce;

/**
 * 基于影片推荐 生成推荐列表 Date: 2016年2月26日 下午4:13:05 <br/>
 *
 * @author dynamo
 */
@Service("bseMovieRecList")
public class ProRecListBseMovieServiceIml implements ProRecListSerivce {
  @Autowired
  private CommonService commonService;
  @Autowired
  private FilterRecListService filterRecListService;

  private static final Logger logger = LoggerFactory.getLogger(ProRecListBseMovieServiceIml.class);

  @Override
  public void productRecList(Map<String, Map<String, Double>> finalSimilarityMap) {
    Stopwatch timer = Stopwatch.createStarted();
    List<Movie> movies = commonService.getMovieFromCache();
    List<User> users = commonService.getUserFromCache();
    if (CollectionUtils.isEmpty(movies) || CollectionUtils.isEmpty(users)) {
      return;
    }
    Map<String, Map<String, Double>> finalRecMap = Maps.newHashMap();// 为了展现出数据是否正确，才定义，否则没有意义
    for (Map.Entry<String, Map<String, Double>> entry : finalSimilarityMap.entrySet()) {
      String uid = entry.getKey();
      Set<String> userLikes = commonService.getUsrLikeFromCache(uid);
      if (userLikes == null) {
        logger.warn("when productRecList Set<String> userLikes is null , uid is {}",
            entry.getKey());
        continue;
      }
      Map<String, Double> candidateList =
          getCandidateList(uid, finalSimilarityMap, movies, userLikes);
      Map<String, Double> finalRecList = filterRecListService.filter(candidateList, uid);
      finalRecMap.put(uid, finalRecList);
    }
    logger
        .info(
            "productRecList Base On Movie take total time {}, finalSimilarityMap size is {} And fianlRecList Result is {}",
            timer.stop(), finalSimilarityMap.size(),
            JsonMapperUtil.getInstance().toJson(finalRecMap));
  }

  /**
   * 获取初步推荐的列表(获选推荐列表)
   *
   * @param uid
   * @param finalSimilarityMap
   * @param movies
   * @param userLikes
   * @return 影片no1，uid感兴趣程度
   */
  private Map<String, Double> getCandidateList(String uid,
      Map<String, Map<String, Double>> finalSimilarityMap,
      List<Movie> movies, Set<String> userLikes) {
    Stopwatch timer = Stopwatch.createStarted();
    Map<String, Double> candidateSimilarity = Maps.newHashMap();

    for (Movie movie : movies) {
      String movieNo = movie.getMovieNo();
      // 如果推荐影片在用户喜爱列表之内则忽视
      if (commonService.isUserLike(uid, movieNo)) {
        continue;
      }
      double interest = 0.0;
      for (String likeMovieNo : userLikes) {
        Map<String, Double> tempSimilarityMap = finalSimilarityMap.get(likeMovieNo);
        if (tempSimilarityMap == null) {
          logger.warn("base on movie likeMovieNo:{} is not mapped in finalSimilarityMap",
              likeMovieNo);
          continue;
        }
        Double similarity = tempSimilarityMap.get(movieNo);
        interest = interest + (similarity == null ? 0.0 : similarity);
      }
      candidateSimilarity.put(movieNo, interest);
    }
    logger.debug(
        "base on movie getCandidateList take total time {}, movie size is {}, userlike size is",
        timer.stop(), movies.size(), userLikes.size());
    return candidateSimilarity;
  }
}
