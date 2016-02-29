package com.wx.movie.rec.recommendlist.common;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.base.Stopwatch;
import com.wx.movie.rec.common.enums.RedisKey;
import com.wx.movie.rec.common.util.SortMap;
import com.wx.movie.rec.recommendlist.pojo.Movie;
import com.wx.movie.rec.recommendlist.pojo.User;
import com.wx.movie.rec.redis.RedisUtils;

/**
 * 处理最终相似度公共服务 Date: 2016年2月26日 下午4:14:39 <br/>
 *
 * @author dynamo
 */
@Service
public class CommonService {
  /**
   * 对相似度进行按相似值排序，取出前面topCount个元素
   */
  @Value("${user.top.count}")
  private int topCount;
  @Autowired
  private RedisUtils redisUtils;

  private static final Logger logger = LoggerFactory.getLogger(CommonService.class);


  /**
   * 对最终相似度的map进行按照相似度从大到小排序,并取出前面 topCount个元素
   *
   * @param map
   * @return
   */
  public Map<String, Double> sortedSimilarity(Map<String, Double> map) {
    Stopwatch timer = Stopwatch.createStarted();
    Map<String, Double> sortedMap = SortMap.sortedMapByValue(map, topCount);
   logger.debug("sortSimilarity take total time {}, volume is {}",timer.stop(),map.size());
    return sortedMap;
  }

  /**
   * 从缓存中获取用户喜爱列表
   *
   * @param uid
   * @return
   */
  public Set<String> getUsrLikeFromCache(String uid) {
    @SuppressWarnings("unchecked")
    Set<String> userLikes = redisUtils.getT(String.format(RedisKey.USER_LIKE_MAP, uid), Set.class);
    if (CollectionUtils.isEmpty(userLikes)) {
      logger.warn("Get UserLike From Cache Fail,And Set<String> is null , UID is {}", uid);
      return null;
    }
    return userLikes;
  }

  /**
   * 从缓存还总获取用户列表
   *
   * @return
   */
  public List<User> getUserFromCache() {
    List<User> users = redisUtils.getList(User.class,String.format(RedisKey.USERLIST));
    if(users == null){
      logger.warn("Get UserLike From Cache Fail,And List<UserLike> is null");
      return null;
    }
    return users;
  }

  /**
   * 从缓存还总获取影片列表
   *
   * @return
   */
  public List<Movie> getMovieFromCache() {
    List<Movie> movies = redisUtils.getList(Movie.class,String.format(RedisKey.MOVIELIST));
    if(movies == null){
      logger.warn("Get UserLike From Cache Fail,And List<UserLike> is null");
      return null;
    }
    return movies;
  }

  /**
   * 查看用户 uid是否喜欢影片movieNo
   *
   * @param uid
   * @param movieNo
   * @return
   */
  public boolean isUserLike(String uid, String movieNo) {
    Set<String> userLikes = getUsrLikeFromCache(uid);
    if(userLikes == null){
      return true;
    }
    return userLikes.contains(movieNo);
  }
}
