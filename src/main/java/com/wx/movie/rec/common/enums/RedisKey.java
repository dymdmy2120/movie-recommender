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
}