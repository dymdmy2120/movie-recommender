/**
* Project Name:movie-data-source
 * File Name:RedisKey.java
 * Package Name:com.wx.movie.data.common.enums
 * Date:2016年1月23日上午12:08:00
 *
*/

package com.wx.movie.rec.common.enums;
/**
 * @author   dynamo
 */
public interface RedisKey {
  String USER_ACTION_SIMILARITY = "similarity_user_action_%s_method_%s";//用户行为操作相似度
  String  COUNT_SIMILARITY = "similarity_method_%s";

  String USERLIST = "user_list";// 用户列表，value为List 集合
  String MOVIELIST = "movie_list";// 影片列表 ,value为List集合
  String USER_LIKE_MAP = "user_like_uid_%s";// 用户喜欢的影片列表，用户和影片的映射 value：List<UserLike>
}