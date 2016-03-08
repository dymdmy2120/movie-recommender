package com.wx.movie.rec.dao.entity;

import java.util.Date;

public class FeedBack {
    private Integer id;

    private String uId;

    private String movieNo;

    private Date modifyTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId == null ? null : uId.trim();
    }

    public String getMovieNo() {
        return movieNo;
    }

    public void setMovieNo(String movieNo) {
        this.movieNo = movieNo == null ? null : movieNo.trim();
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}