package com.wx.movie.rec.similarity.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.wx.movie.rec.similarity.service.ProdSimilarityService;

/**
 * 得到基于用户或基于影片的最终的相似度 Date: 2016年2月18日 下午1:06:09 <br/>
 *
 * @author dynamo
 */
@Service("finalSimilarityService")
public class FinalSimilarityService {
  @Autowired
  @Qualifier("bseUsrSimilarity")
  private ProdSimilarityService bseUsrFnlSimiServcie;

  @Autowired
  @Qualifier("bseMovieSimilarity")
  private ProdSimilarityService bseMovieFnlSimiServcie;

  public void compuFinalSimilarity() {
    /**
     * 基于用户的最终相似度
     */
    bseUsrFnlSimiServcie.prodFinalSimilarity();
    /**
     * 基于影片的最终相似度
     */
    bseMovieFnlSimiServcie.prodFinalSimilarity();
  }
}
