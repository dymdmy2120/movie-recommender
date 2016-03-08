package com.wx.movie.rec.dao.entity;

import java.util.Date;

public class UserReclist {
    private Integer id;

    private String uId;

    private Byte recType;

    private Date modifyTime;

    private String reclistJson;

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

    public Byte getRecType() {
        return recType;
    }

    public void setRecType(Byte recType) {
        this.recType = recType;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getReclistJson() {
        return reclistJson;
    }

    public void setReclistJson(String reclistJson) {
        this.reclistJson = reclistJson == null ? null : reclistJson.trim();
    }
}