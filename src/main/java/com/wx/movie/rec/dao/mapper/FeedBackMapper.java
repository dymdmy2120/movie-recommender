package com.wx.movie.rec.dao.mapper;

import java.util.List;

import com.wx.movie.rec.dao.entity.FeedBack;

public interface FeedBackMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FeedBack record);

    int insertSelective(FeedBack record);

    FeedBack selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FeedBack record);

    int updateByPrimaryKey(FeedBack record);

  /**
   * 从反馈结果中随机选出几条数据，供没有得到推荐结果的用户用
   */
  List<FeedBack> getRandomList();
}