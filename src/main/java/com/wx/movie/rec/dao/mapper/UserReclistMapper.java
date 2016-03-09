package com.wx.movie.rec.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.wx.movie.rec.dao.entity.UserReclist;

public interface UserReclistMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(UserReclist record);

    UserReclist selectByPrimaryKey(Integer id);
    int updateByPrimaryKeySelective(UserReclist record);

    int updateByPrimaryKeyWithBLOBs(UserReclist record);

  int saceInBatch(@Param("recLists") List<UserReclist> recLists);
}