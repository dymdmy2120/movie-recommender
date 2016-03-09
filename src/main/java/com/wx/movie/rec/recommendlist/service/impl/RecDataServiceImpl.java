
package com.wx.movie.rec.recommendlist.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wx.movie.rec.dao.entity.FeedBack;
import com.wx.movie.rec.dao.entity.UserReclist;
import com.wx.movie.rec.dao.mapper.FeedBackMapper;
import com.wx.movie.rec.dao.mapper.UserReclistMapper;
import com.wx.movie.rec.recommendlist.service.RecDataService;

/**
 * Date:     2016年3月9日 上午11:49:53
 * @author   dynamo
 * @version
 * @see
 */
/**
 * @author dynamo
 * @version
 */
@Service
public class RecDataServiceImpl implements RecDataService {
  @Autowired
  private UserReclistMapper usreRecListMapper;
  @Autowired
  private FeedBackMapper feedBackMapper;
  private static final Logger logger = LoggerFactory.getLogger(RecDataServiceImpl.class);
  @Override
  public int saveRecList(List<UserReclist> recLists) {
    int ret = usreRecListMapper.saceInBatch(recLists);
    if (ret != recLists.size()) {
      logger.warn("Save UserRecList In Batch fail influence size{}, recLists size is{}", ret,
          recLists.size());
    }
    return ret;
  }

  @Override
  public List<FeedBack> selectFeeBack() {
    List<FeedBack> feedBacks = feedBackMapper.getRandomList();
    return feedBacks;
  }

}
