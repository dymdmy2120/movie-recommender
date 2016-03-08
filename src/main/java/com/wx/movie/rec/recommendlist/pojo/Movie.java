package com.wx.movie.rec.recommendlist.pojo;

public class Movie {
  private String movieno;


  public String getMovieno() {
    return movieno;
  }


  public void setMovieno(String movieno) {
    this.movieno = movieno;
  }


  @Override
  public String toString() {
    return "Movie [movieNo=" + movieno + "]";
  }


}
