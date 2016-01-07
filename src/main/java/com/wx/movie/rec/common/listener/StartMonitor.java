package com.wx.movie.rec.common.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.beans.factory.annotation.Autowired;

import com.wx.movie.rec.similarity.async.FinalSimilarityService;

/**
 * Date:     2016年2月21日 下午9:15:29 <br/>
 * @author   dynamo
 */
public class StartMonitor implements ServletContextListener  {
 @Autowired
private FinalSimilarityService finalSimilarityService;
 
  @Override
  public void contextInitialized(ServletContextEvent arg0) {
    //异步执行计算基于用户的最终相似度
    finalSimilarityService.bseUsrFinalSimilarity();
    //异步执行计算基于影片的最终相似度
    finalSimilarityService.bseMovieFinalSimilarity();
  }

  @Override
  public void contextDestroyed(ServletContextEvent arg0) {
    
  }

}
