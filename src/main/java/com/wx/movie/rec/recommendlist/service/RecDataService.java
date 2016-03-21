
package com.wx.movie.rec.recommendlist.service;

import java.util.List;

import com.wx.movie.rec.dao.entity.FeedBack;
import com.wx.movie.rec.dao.entity.UserReclist;

/**
 * Date:     2016年3月9日 上午11:45:57
 * @author   dynamo
 * @version
 * @see
 */
/**
 * @author dynamo
 * @version
 */
public interface RecDataService {
  int saveRecList(List<UserReclist> recLists);

  List<FeedBack> selectFeeBack();
}
