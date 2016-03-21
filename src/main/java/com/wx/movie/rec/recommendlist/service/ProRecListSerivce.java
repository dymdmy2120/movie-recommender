package com.wx.movie.rec.recommendlist.service;

import java.util.Map;

/**
 * 根据最终相似度生成初步的推荐列表
 * Date:     2016年2月26日 下午4:04:03 <br/>
 * @author   dynamo
 */
public interface ProRecListSerivce {

  void productRecList( Map<String, Map<String, Double>> finalSimilarityMap);
}
