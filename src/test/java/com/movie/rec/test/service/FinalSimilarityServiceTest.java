package com.movie.rec.test.service;

import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.wx.movie.rec.common.enums.Constant;
import com.wx.movie.rec.similarity.async.FinalSimilarityService;
import com.wx.movie.rec.similarity.pojo.UserActionProportion;

/**
 * Date:     2016年2月21日 下午11:24:11 <br/>
 * @author   dynamo
 */
public class FinalSimilarityServiceTest extends BaseTest{

  @Autowired
  private FinalSimilarityService finalSimilarityService;
  
   @Test
  public void testGetSimilarityMapLists(){
    Map<UserActionProportion, Map<String, Map<String, Double>>> map =  finalSimilarityService.getSimilarityMapLists(Constant.BSE_MOVIE);
  }
}
