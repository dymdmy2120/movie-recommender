package com.wx.movie.rec.recommendlist.pojo;


public class UserLike {
  private Integer uid;
  private String movieNo;


  public Integer getUid() {
    return uid;
  }

  public void setUid(Integer uid) {
    this.uid = uid;
  }

  public String getMovieNo() {
    return movieNo;
  }

  public void setMovieNo(String movieNo) {
    this.movieNo = movieNo == null ? null : movieNo.trim();
  }
}

