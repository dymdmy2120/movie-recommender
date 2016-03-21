package com.wx.movie.rec.filter.service;

import java.util.Map;

/**
 * Date:     2016年2月27日 下午2:19:24
 * @author   dynamo
 * @version
 * @see
 */
/**
 * 对初步推荐列表进行过滤
 * @author dynamo
 * @version
 */
public interface FilterRecListService {
  /**
   * 进行对初步得到推荐列表进行过滤 按对影片的兴趣程度从大到小排序，根据用户喜欢的类型
   *
   * @param map
   * @param uid
   * @return 经过过滤后的最终推荐列表
   */
  Map<String, Double> filter(Map<String, Double> candidateListMap, String uid);
}
