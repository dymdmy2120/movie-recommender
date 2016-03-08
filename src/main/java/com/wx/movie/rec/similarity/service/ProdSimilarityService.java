package com.wx.movie.rec.similarity.service;

import com.wx.movie.rec.pojo.UserActionData;

/**
 * Date:     2016年2月16日 上午9:44:36 <br/>
 * 相似度计算服务接口
 * @author   dynamo
 */
public interface ProdSimilarityService {
/**
 * 根据生成的特征向量，计算相似度
 * @author dynamo
 * @param bseUsrActionData 基于用户的特征向量
 * @param bseMovieActionData 基于有影片的特征向量
 */
  void prodSimilarity(UserActionData actionData);

  /**
   * 当各个操作完成后生成最终相似度
   */
  void prodFinalSimilarity();
}
