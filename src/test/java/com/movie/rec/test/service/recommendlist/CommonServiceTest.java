
package com.movie.rec.test.service.recommendlist;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * Date:     2016年2月29日 下午6:02:24
 * @author   dynamo
 * @version
 * @see
 */
import org.springframework.beans.factory.annotation.Value;

import com.google.common.collect.Maps;
import com.movie.rec.test.service.BaseTest;
import com.wx.movie.rec.recommendlist.common.CommonService;
import com.wx.movie.rec.recommendlist.pojo.Movie;
import com.wx.movie.rec.recommendlist.pojo.User;
/**
 * @author dynamo
 * @version
 */
public class CommonServiceTest extends BaseTest {
  @Autowired
  private CommonService commonService;
  @Value("${rec.list.count}")
  private int recCount;


  public void getUserList() {
    List<User> list = commonService.getUserFromCache();
    System.out.println("size==" + list.size() + list);
  }

  @Test
  public void getMovieList() {
    List<Movie> list = commonService.getMovieFromCache();
    System.out.println("size==" + list.size() + list);
  }


  public void getUserLike() {
    Set<String> set = commonService.getUsrLikeFromCache("8477");
    System.out.println("size==" + set.size() + set);
  }


  public void sortMap() {
    Map<String, Double> map = Maps.newHashMap();
    map.put("U1", 0.02);
    map.put("U2", 0.2);
    map.put("U3", 0.2);
    map.put("U4", 0.1);
    map.put("U5", 0.12);
    map.put("U6", 0.07);
    Map map1 = commonService.sortedSimilarity(map);
    System.out.println(map1);
  }
}
