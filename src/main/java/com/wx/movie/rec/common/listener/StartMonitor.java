package com.wx.movie.rec.common.listener;

import javax.servlet.ServletContextEvent;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.wx.movie.rec.similarity.async.FinalSimilarityService;

/**
 * 继承了spring的监听器类，重写了初始化和销毁方法
 * 目的：在调用异步执行计算用户最终相似度前，spring进行对bean初始化和装配
 * Date:     2016年2月21日 下午9:15:29 <br/>
 * @author   dynamo
 */
public class StartMonitor extends ContextLoaderListener  {
private FinalSimilarityService finalSimilarityService;
 
  @Override
  public void contextInitialized(ServletContextEvent event) {
    super.contextInitialized(event);
    ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());  
    //获取bean  
    finalSimilarityService = (FinalSimilarityService) applicationContext.getBean("finalSimilarityService");   
    //异步执行计算基于用户的最终相似度
    finalSimilarityService.bseUsrFinalSimilarity();
    //异步执行计算基于影片的最终相似度
    finalSimilarityService.bseMovieFinalSimilarity();
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
    super.contextDestroyed(event);
  }

}
