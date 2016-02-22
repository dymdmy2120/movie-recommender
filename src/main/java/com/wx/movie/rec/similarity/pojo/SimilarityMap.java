package com.wx.movie.rec.similarity.pojo;

import java.util.Map;

/**
 * Date: 2016年2月16日 上午9:32:38 <br/>
 * 
 * @author dynamo
 */
public class SimilarityMap {
  /**
   * 基于用户：similarityKey 表示用户id1，以及similarityValue表示与用户id1相似度值集合 基于影片：similarityKey
   * 表示影片no1，以及similarityValue表示与影片no1相似度值集合
   */
  private String similarityKey;
  private Map<String, Double> similarityValue;

  public String getSimilarityKey() {
    return similarityKey;
  }

  public void setSimilarityKey(String similarityKey) {
    this.similarityKey = similarityKey;
  }

  public Map<String, Double> getSimilarityValue() {
    return similarityValue;
  }

  public void setSimilarityValue(Map<String, Double> similarityValue) {
    this.similarityValue = similarityValue;
  }
}
