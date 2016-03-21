package com.wx.movie.rec.recommendlist.pojo;

import java.util.List;

/**
 * Date:     2016年2月27日 上午8:34:17
 * @author   dynamo
 * @version
 * @see
 */
/**
 * 影片推荐列表
 * 
 * @author dynamo
 * @version
 */
public class RecList {
  private String uid;
  private List<String> movieNos;

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public List<String> getMovieNos() {
    return movieNos;
  }

  public void setMovieNos(List<String> movieNos) {
    this.movieNos = movieNos;
  }

}
