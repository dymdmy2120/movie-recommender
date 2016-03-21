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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wx.movie.rec.common.enums.RecommendType;
import com.wx.movie.rec.common.util.JsonMapperUtil;
import com.wx.movie.rec.dao.entity.UserReclist;
import com.wx.movie.rec.filter.service.FilterRecListService;
import com.wx.movie.rec.recommendlist.common.CommonService;
import com.wx.movie.rec.recommendlist.pojo.User;
import com.wx.movie.rec.recommendlist.service.ProRecListSerivce;
import com.wx.movie.rec.recommendlist.service.RecDataService;

/**
 * 基于用户推荐 生成推荐列表
 * Date:     2016年2月26日 下午4:13:05 <br/>
 * @author   dynamo
 */
@Service("bseUsrRecList")
public class ProRecListBseUsrServiceIml implements ProRecListSerivce {
  @Autowired
  private CommonService commonService;
  @Autowired
  private FilterRecListService filterRecListService;
  @Autowired
  private RecDataService recDataServcie;
  private static final Logger logger = LoggerFactory.getLogger(ProRecListBseUsrServiceIml.class);

  @Override
  public void productRecList(Map<String, Map<String, Double>> finalSimilarityMap) {
    Stopwatch timer = Stopwatch.createStarted();
    List<User> users = commonService.getUserFromCache();
    if (CollectionUtils.isEmpty(users)) {
      return;
    }
    List<UserReclist> userRecLists = Lists.newArrayList();
    Map<String, Map<String, Double>> finalRecMap = Maps.newHashMap();// 为了展现出数据是否正确，才定义，否则没有意义
    for (User user : users) {
      String uid = String.valueOf(user.getUid());
      Set<String> userLikes = commonService.getUsrLikeFromCache(uid);
      if (userLikes == null) {
        //logger  .warn("when productRecList Set<String> userLikes is null , uid is {}", entry.getKey());
        continue;
      }
      Map<String, Double> candidateList = getCandidateList(uid, finalSimilarityMap, userLikes);
      if (candidateList == null) {
        continue;
      }
      // 根据条件对初步推荐结果过滤
      Map<String, Double> finalRecList = filterRecListService.filter(candidateList, uid);

      // 封装推荐结果entity
      UserReclist userRecList =
          commonService.packagUserRecLists(uid, finalRecList, RecommendType.BSE_USER);
      userRecLists.add(userRecList);
      finalRecMap.put(uid, finalRecList);

      // 保存到缓存中
      commonService.setRecListToCache(uid, finalRecList.keySet(), RecommendType.BSE_USER);
    }
    // 最终推荐结果保存到数据库中
    int ret = recDataServcie.saveRecList(userRecLists);
    logger.debug("Base On User save UserRecList success influence size is {},useRecList size is{}",
        ret, userRecLists.size());
    // 写入到缓存中

    logger
        .info(
            "productRecList Base On User take total time {}, finalSimilarityMap size is {} And fianlRecList Result is {}",
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
      Map<String, Map<String, Double>> finalSimilarityMap, Set<String> userLikes) {
    Map<String, Double> similiarty = finalSimilarityMap.get(uid);
    // FIXME 当uid不存在 finalSimilarityMap 需要如何操作？在uid获取推荐列表时,默认推荐热门影片
    if (similiarty == null) {
      logger.warn("uid:{} is not mapped in finalSimilarityMap", uid);
      return null;
    }
    // 获取和用户 uid相似度最高的前十几个用户
    Map<String, Double> topSimilarity = commonService.sortedSimilarity(similiarty);
    Map<String, List<String>> relateMap = relateUidMoive(topSimilarity);
    // 候选推荐列表
    Map<String, Double> candidateSimilarity = Maps.newHashMap();
    for (Map.Entry<String, List<String>> entry : relateMap.entrySet()) {
      String movieNo = entry.getKey();
      if (commonService.isUserLike(uid, movieNo)) {
        continue;
      }
      double interest = 0.0;
      List<String> uIds = entry.getValue();
      for (String uId : uIds) {
        Double similarity = similiarty.get(uId);
        interest = interest + similarity;
      }
      candidateSimilarity.put(movieNo, interest);
    }
    return candidateSimilarity;
  }

  /**
   * 根据和用户 uid最相似的前十几个用户，然后将这十几个用户喜欢的影片和用户关联
   *
   * @param topSimilarity
   * @return <M1 ,[uid1,uid2]>表示影片M1被用户uid1 uid2所喜欢
   */
  private Map<String, List<String>> relateUidMoive(Map<String, Double> topSimilarity) {
    Stopwatch timer = Stopwatch.createStarted();
    // 将用户uid和喜欢影片进行关联，key: movieNo value:uid集合
    Map<String, List<String>> relationMap = Maps.newHashMap();
    for (Map.Entry<String, Double> entry : topSimilarity.entrySet()) {
      String uId = entry.getKey();
      Set<String> movies = commonService.getUsrLikeFromCache(uId);
      if (movies == null) {
        logger.warn("base on user uid:{} do not have UserLike in Cache", uId);
        continue;
      }
      for (String movie : movies) {
        if (relationMap.containsKey(movie)) {
          List<String> uIds = relationMap.get(movie);
          uIds.add(uId);
          relationMap.put(movie, uIds);
        } else {
          List<String> list = Lists.newArrayList();
          list.add(uId);
          relationMap.put(movie, list);
        }
      }
    }
    logger.debug("relateUidMoive take time is {}, topSimilarity size is{} ", timer.stop(),
        topSimilarity.size());
    return relationMap;
  }
}
