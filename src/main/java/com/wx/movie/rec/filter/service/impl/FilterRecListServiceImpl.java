
package com.wx.movie.rec.filter.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.base.Stopwatch;
import com.wx.movie.rec.common.util.SortMap;
import com.wx.movie.rec.filter.service.FilterRecListService;

/**
 * Date:     2016年2月27日 下午2:22:45
 * @author   dynamo
 * @version
 * @see
 */
/**
 * @author dynamo
 * @version
 */
@Service
public class FilterRecListServiceImpl implements FilterRecListService {
  @Value("${rec.list.count}")
  private int recListCount;
  private static final Logger logger = LoggerFactory.getLogger(FilterRecListServiceImpl.class);
  @Override
  public Map<String, Double> filter(Map<String, Double> candidateList, String uid) {
    Stopwatch timer = Stopwatch.createStarted();
    Map<String, Double> sortedMap = SortMap.sortedMapByValue(candidateList, recListCount);
    logger.info("FilterRecListServiceImpl  take total time {}, candidateList is {}", timer.stop(),
        candidateList.size());
    return sortedMap;
  }

}
