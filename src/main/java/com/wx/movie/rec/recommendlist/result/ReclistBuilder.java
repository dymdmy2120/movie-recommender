package com.wx.movie.rec.recommendlist.result;

import com.wx.movie.rec.recommendlist.pojo.RecList;

/**
 *对象的建造模式
 */
public interface ReclistBuilder {
/**
 * 从根据基于用户算法中取出推荐结果
 */
void bseOnUsrReclst();
/**
 * 从根据基于用户影片中取出推荐结果
 */
void bseOnMovieReclst();
/**
 * 从其他用户反馈结果取出推荐结果
 */
void bseFeedBackReclst();
/**
 * 从配置文件中加载默认推荐结果
 */
void bseDefaultReclst();
void set(String uid);
RecList buildRecList();
}
