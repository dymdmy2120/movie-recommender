package com.wx.movie.rec.eigenvector.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.wx.movie.rec.eigenvector.service.ProdEigenVectorService;
import com.wx.movie.rec.pojo.UserActionData;
import com.wx.movie.rec.similarity.service.ProdSimilarityService;

/**
 * 生成基于影片推荐方式所使用的特征向量
 *
 * @author dynamo
 */
@Service("bseMovieEigVec")
public class ProdEigVecBseMovieServiceImpl implements ProdEigenVectorService {
  @Qualifier("bseMovieSimilarity")
  @Autowired
  private ProdSimilarityService bseMovieSimilarityService;

  private static final Logger logger = LoggerFactory.getLogger(ProdEigVecBseMovieServiceImpl.class);

  @Override
  @Async("prodEigenVectorExecutor")
  public void produceEigenVector(UserActionData userActionData) {
    // 调用上一层，相似度计算层
    bseMovieSimilarityService.prodSimilarity(userActionData);
  }

}
