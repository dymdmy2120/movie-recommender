package com.wx.movie.rec.similarity.async;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wx.movie.rec.common.enums.Constant;
import com.wx.movie.rec.common.enums.RedisKey;
import com.wx.movie.rec.common.util.JsonMapperUtil;
import com.wx.movie.rec.redis.RedisUtils;
import com.wx.movie.rec.similarity.pojo.UserActionProportion;

/**
 * 得到基于用户或基于影片的最终的相似度 Date: 2016年2月18日 下午1:06:09 <br/>
 * 
 * @author dynamo
 */
@Service
public class FinalSimilarityService implements InitializingBean {
  @Autowired
  private RedisUtils redisUtils;

  @Value("${user.action.json}")
  private String userActionJson;

  private List<UserActionProportion> userActionProportions;
  private TypeReference<List<UserActionProportion>> tr;
  private TypeReference<Map<String,Double>> tr1;
  private Logger logger = LoggerFactory.getLogger(FinalSimilarityService.class);

  /**
   * 基于用户的最终相似度
   * 
   * @author dynamo
   */
  @Async("computeFinalSimilarityExecutor")
  public void bseUsrFinalSimilarity() {
    while (true) {
      String rtKey = String.format(RedisKey.COUNT_SIMILARITY, Constant.BSE_USE);
      int times = getActionTimes(rtKey);
      logger.info("Base on User getActionTimes times is {},  userActionProportion.size() is {} ",
          times, userActionProportions.size());
      if (times == userActionProportions.size()) {
        Stopwatch timer = Stopwatch.createStarted();
        // 计算最终的相似度
        getFinalSimilarity(Constant.BSE_USE);
        // 调用生成推荐列表模块
        // 计算完后将标志位置为0
        redisUtils.setInt(rtKey, 0);
        logger.info("BseUsrFinalSimilarity Base on User take time is {}", timer.stop());
      }

    }
  }

  /**
   * 基于影片的最终相似度
   * 
   * @author dynamo
   */
  @Async("computeFinalSimilarityExecutor")
  public void bseMovieFinalSimilarity() {

    while (true) {
      String rtKey = String.format(RedisKey.COUNT_SIMILARITY, Constant.BSE_MOVIE);
      int times = getActionTimes(rtKey);
      logger.info("Base on Movie getActionTimes times is {},  userActionProportion.size() is {} ",
          times, userActionProportions.size());
      if (times == userActionProportions.size()) {
        Stopwatch timer = Stopwatch.createStarted();
        // 计算最终的相似度
        getFinalSimilarity(Constant.BSE_MOVIE);
        // 调用生成推荐列表模块
        // 计算完后将标志位置为0
        redisUtils.setInt(rtKey, 0);
        logger.info("BseUsrFinalSimilarity Base on Movie take time is {}", timer.stop());
      }
    }
  }

  @PostConstruct
  public void init() {
    tr1 =new TypeReference<Map<String,Double>>() {};
    tr = new TypeReference<List<UserActionProportion>>() {};
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    FileInputStream fis = null;
    try {
      // 得到classpath目录路径
      String path = this.getClass().getResource("/").toURI().getPath();
      path = path + userActionJson;
      logger.info("配置用户操作文件路径：" + path);
      File jsonFile = new File(path);
      if (!jsonFile.exists()) {
        jsonFile = new File(userActionJson);
      }
      fis = new FileInputStream(jsonFile);
      Long filelength = jsonFile.length();
      byte[] fileContent = new byte[filelength.intValue()];
      fis.read(fileContent);
      String actionJson = new String(fileContent, "UTF-8");
      userActionProportions = JsonMapperUtil.getInstance().fromJson(actionJson, tr);
      logger.info("获取用户行为操作：{}", actionJson);
    } catch (Exception e) {
      logger.error("parse user_action.json fail", e);
    } finally {
      if (fis != null) fis.close();
    }
  }

  private int getActionTimes(String rtKey) {
    if (redisUtils.exists(rtKey)) {
      return redisUtils.getInt(rtKey);
    }
    return 0;
  }

  @SuppressWarnings("unchecked")
  private Map<String, Map<String, Double>> getFinalSimilarity(String method) {
    Stopwatch timer = Stopwatch.createStarted();
    // 最终相似度映射
    Map<String, Map<String, Double>> finalSimarityMap = Maps.newHashMap();

    Map<UserActionProportion, Map<String, Map<String, Double>>> map = getSimilarityMapLists(method);
    List<Object> list = getMaxScope(method);

    UserActionProportion userActionPro = (UserActionProportion) list.get(0);
    double proportion = userActionPro.getProportion();

    // 从map中移除掉最大范围key的元素
    map.remove(userActionPro);

    Map<String, Map<String, Double>> similarityMap = (Map<String, Map<String, Double>>) list.get(1);
    for (Map.Entry<String, Map<String, Double>> entry : similarityMap.entrySet()) {
      Map<String, Double> tempSimilarityMap = Maps.newHashMap();
      for (Map.Entry<String, Double> entrySimilarity : entry.getValue().entrySet()) {
        double similarityValue =
            computeFinalSimilarity(entry.getKey(), entrySimilarity.getKey(),
                entrySimilarity.getValue() * proportion, map);
        tempSimilarityMap.put(entrySimilarity.getKey(), similarityValue);
      }
      finalSimarityMap.put(entry.getKey(), tempSimilarityMap);
    }
    logger.info("GetFinalSimilarity take time is {}  and finalSimilarityMap is {}", timer.stop(),
        finalSimarityMap);
    return finalSimarityMap;
  }

  public Map<UserActionProportion, Map<String, Map<String, Double>>> getSimilarityMapLists(
      String method) {
    Stopwatch timer = Stopwatch.createStarted();
    Map<UserActionProportion, Map<String, Map<String, Double>>> map = Maps.newHashMap();

    for (UserActionProportion userActionPro : userActionProportions) {
      String rKey = String.format(RedisKey.USER_ACTION_SIMILARITY, userActionPro.getAction(), method);
      Map<String, Map<String, Double>> similarityMap = redisUtils.hgetAll(rKey, tr1);
      map.put(userActionPro, similarityMap);
    }
    logger.info("GetSimilarityMapLists take time is {}", timer.stop());
    return map;
  }

  /**
   * 得到用户操作行为数据相似度中 Map<String,Map> 集合中key的元素最多个一个Map集合 目的是为了遍历最大范围的Map,不漏掉uIds或movieNo
   * 
   * @param List 第一个元素为 UserActionProportion 第二个元素为 Map集合
   * @return
   */
  private List<Object> getMaxScope(String method) {
    Stopwatch timer = Stopwatch.createStarted();
    String rKeyTemp = null;
    int maxSize = 0;
    UserActionProportion userActionProTemp = null;
    Map<String, Map<String, Double>> similarityMap;
    List<Object> lists = Lists.newArrayList();

    for (UserActionProportion userActionPro : userActionProportions) {
      String rKey =
          String.format(RedisKey.USER_ACTION_SIMILARITY, userActionPro.getAction(), method);
      Set<String> set = redisUtils.hkeys(rKey);
      int size = set.size();
      if (size > maxSize) {
        maxSize = size;
        rKeyTemp = rKey;
        userActionProTemp = userActionPro;
      }
    }
    similarityMap = redisUtils.hgetAll(rKeyTemp, tr1);
    lists.add(userActionProTemp);
    lists.add(similarityMap);
    logger.info("GetMaxScope take time is {}", timer.stop());
    return lists;
  }

  /**
   * 根据不同行为的占比，计算最终的相似度
   * 
   * @param key 一个大map中的 key Map<String,Map<String,Double>>
   * @param subKey 大map中的 value对应的Map中的key
   * @param value
   * @param map
   * @return
   */
  private double computeFinalSimilarity(String key, String subKey, double similarity,
      Map<UserActionProportion, Map<String, Map<String, Double>>> map) {
    Stopwatch timer = Stopwatch.createStarted();
    double totalSimilarity = similarity;

    for (Map.Entry<UserActionProportion, Map<String, Map<String, Double>>> entry : map.entrySet()) {
      UserActionProportion usreActionPro = entry.getKey();
      Map<String, Double> similarityMap = entry.getValue().get(key);
      if (similarityMap == null) {
        continue;
      }
      Double similarityValue = similarityMap.get(subKey);
      if (similarityValue == null) {
        continue;
      }
      totalSimilarity = totalSimilarity + similarityValue * usreActionPro.getProportion();
    }
    logger.info(
        "ComputeFinalSimilarity take time is {}, key is {} subKey is {} totalSimilarity is {}",
        timer.stop(), key, subKey, totalSimilarity);
    return totalSimilarity;
  }
}
