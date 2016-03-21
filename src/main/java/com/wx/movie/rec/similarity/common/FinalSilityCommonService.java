
package com.wx.movie.rec.similarity.common;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wx.movie.rec.common.enums.RecommendType;
import com.wx.movie.rec.common.enums.RedisKey;
import com.wx.movie.rec.common.exception.DataException;
import com.wx.movie.rec.common.util.JsonMapperUtil;
import com.wx.movie.rec.common.util.LoadConfigFileUtil;
import com.wx.movie.rec.redis.RedisUtils;
import com.wx.movie.rec.similarity.pojo.UserActionProportion;

/**
 * Date:     2016年3月8日 下午4:36:53
 * @author   dynamo
 * @version
 * @see
 */
/**
 * @author dynamo
 * @version
 */
@Service
public class FinalSilityCommonService {
  @Autowired
  private RedisUtils redisUtils;
  @Autowired
  private LoadConfigFileUtil loadConfig;
  private Logger logger = LoggerFactory.getLogger(FinalSilityCommonService.class);
  private List<UserActionProportion> userActionProportions;
  private TypeReference<Map<String, Double>> tr1;

  @PostConstruct
  public void init() {
    userActionProportions = loadConfig.getActionProportions();
    tr1 = new TypeReference<Map<String, Double>>() {};
  }

  public int getActionTimes(String rtKey) {
    if (redisUtils.exists(rtKey)) {
      return redisUtils.getInt(rtKey);
    }
    return 0;
  }

  /**
   * 获取user_action文件中用户操作的个数
   */
  public int getActionSize() {
    return userActionProportions.size();
  }
  @SuppressWarnings("unchecked")
  /**
   *
   * @author dynamo
   * @param method
   * @return 最终的相似度  U1 <U2,0.01> <U3,0.002> 或者M1 <M2,0.01> <M3,0.002>
   */
  public Map<String, Map<String, Double>> getFinalSimilarity(RecommendType method) {
    Stopwatch timer = Stopwatch.createStarted();
    // 最终相似度映射
    Map<String, Map<String, Double>> finalSimarityMap = Maps.newHashMap();
    // 例如 u1 <u2,v1> <u3,v2> 等价于 u2 <u1,v1> u3 <u1,v2>
    Map<String, Map<String, Double>> equvialSimilarityMap = Maps.newHashMap();
    Map<UserActionProportion, Map<String, Map<String, Double>>> map = getSimilarityMapLists(method);
    List<Object> list = getMaxScope(method);

    UserActionProportion userActionPro = (UserActionProportion) list.get(0);
    double proportion = userActionPro.getProportion();

    // 从map中移除掉最大范围key的元素
    map.remove(userActionPro);

    Map<String, Map<String, Double>> similarityMap = (Map<String, Map<String, Double>>) list.get(1);
    for (Map.Entry<String, Map<String, Double>> entry : similarityMap.entrySet()) {
      String key = entry.getKey();
      Map<String, Double> tempSimilarityMap = Maps.newHashMap();
      for (Map.Entry<String, Double> entrySimilarity : entry.getValue().entrySet()) {
        double similarityValue =
            computeFinalSimilarity(entry.getKey(), entrySimilarity.getKey(),
                entrySimilarity.getValue() * proportion, map);
        String subKey = entrySimilarity.getKey();
        tempSimilarityMap.put(subKey, similarityValue);
        construtEquialMap(equvialSimilarityMap, subKey, key, similarityValue);
      }
      finalSimarityMap.put(key, tempSimilarityMap);
    }
    mergeSimilarityMap(finalSimarityMap, equvialSimilarityMap);
    logger.info("GetFinalSimilarity take time is {} ", timer.stop());
    return finalSimarityMap;
  }

  /**
   * 由于遍历特征向量时，只得到 u1 和 u2 u3的相似度，但是 u1 和 u2相似度和 u2 u1相等， u1 u3等价与 u3 u1
   *
   * @author dynamo
   * @param key 外面Map中的key
   * @param subKey 里面map中的key
   * @param similarityValue 相似度
   * @return
   */
  private void construtEquialMap(Map<String, Map<String, Double>> map, String key, String subKey,
      double similarityValue) {
    if (map.get(key) == null) {
      Map<String, Double> subMap = Maps.newHashMap();
      subMap.put(subKey, similarityValue);
      map.put(key, subMap);
    } else {
      Map<String, Double> subMap = map.get(key);
      subMap.put(subKey, similarityValue);
      map.put(key, subMap);
    }
  }

  /**
   * 将等价的相似度map合并到 最终相似度map中
   *
   * @author dynamo
   * @param finalMap
   * @param equivalMap
   */
  private void mergeSimilarityMap(Map<String, Map<String, Double>> finalMap,
      Map<String, Map<String, Double>> equivalMap) {
    Stopwatch timer = Stopwatch.createStarted();
    for (Map.Entry<String, Map<String, Double>> entry : equivalMap.entrySet()) {
      String key = entry.getKey();
      if (finalMap.containsKey(key)) {
        Map<String, Double> similarityMap = finalMap.get(key);
        similarityMap.putAll(entry.getValue());
        finalMap.put(key, similarityMap);
      } else {
        finalMap.put(entry.getKey(), entry.getValue());
      }
    }
    logger.info("mergeSimilarityMap take time is {}", timer.stop());
  }

  private Map<UserActionProportion, Map<String, Map<String, Double>>> getSimilarityMapLists(
      RecommendType method) {
    Stopwatch timer = Stopwatch.createStarted();
    Map<UserActionProportion, Map<String, Map<String, Double>>> map = Maps.newHashMap();

    for (UserActionProportion userActionPro : userActionProportions) {
      String rKey =
          String.format(RedisKey.USER_ACTION_SIMILARITY, userActionPro.getAction(), method);
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
  private List<Object> getMaxScope(RecommendType method) {
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
    return totalSimilarity;
  }


  private void writeDataToFile(Map<String, Map<String, Double>> map, String path) {
    BufferedWriter bw = null;
    try {
      OutputStream fos = new FileOutputStream(path);
      bw = new BufferedWriter(new OutputStreamWriter(fos));
      for (Map.Entry<String, Map<String, Double>> entry : map.entrySet()) {
        bw.write(entry.getKey());
        bw.write(JsonMapperUtil.getInstance().toJson(entry.getValue()));
      }
    } catch (IOException e) {
      throw new DataException(e);
    } finally {
      if (bw != null) {
        try {
          bw.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
