package com.wx.movie.rec.similarity.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.wx.movie.rec.common.enums.Constant;
import com.wx.movie.rec.common.enums.RedisKey;
import com.wx.movie.rec.redis.RedisUtils;

/**
 * Date:     2016年2月16日 下午10:48:02 <br/>
 * @author   dynamo
 */
@Service
public class CommonService {
  @Autowired
  private RedisUtils redisUtils;
/**
 * 抽象出来，作为公共服务
 * @author dynamo
 * @param action 用户行为操作
 * @param actionMap 用户操作映射(基于用户 或者基于影片的行为操作数据)
 * @param method 基于用户计算相似度，还是基于影片计算相似度 bseUsr或者bseMovie
 */
  public void handleUserActionData(String action,Map<String,Set<String>> actionMap,String method){
  Set<String> uidOrMovNoSets = actionMap.keySet();
  List<String> uidOrMoVNoLists = new ArrayList<String>(uidOrMovNoSets);
  int size = uidOrMoVNoLists.size();
  Map<String,Double> similarityMap = Maps.newHashMap();
  
  for(int i=0; i<size; i++){
    String movieNo = uidOrMoVNoLists.get(i);
    Set<String> uIds1 = actionMap.get(movieNo);
    int uIds1Size = uIds1.size();
    
    for(int k=i+1; k<size; k++){
      String movieNo1 = uidOrMoVNoLists.get(k);
      Set<String> uIds2 = actionMap.get(movieNo1);
      int intersection = intersection(uIds1,uIds2);
     double similarityValue =  computeSimilarity(intersection,uIds1Size,uIds2.size());
     similarityMap.put(movieNo1, similarityValue);
    }
    //相似度设入到缓存中，key代表行为操作， value:为哈希表(key 影片no1 value:一个和影片no1相似的map集合)
    String rtKey = String.format(RedisKey.USER_ACTION_SIMILARITY, action,method);
    redisUtils.hset(rtKey, movieNo, similarityMap);
  }
}
/**
 * 求出集合set1 和集合set2 之间的交集
 * @author dynamo
 * @param set1 
 * @param set2
 * @return 两个集合交集后的元素个数
 */
private int intersection(Set<String> set1,Set<String> set2){
set1.retainAll(set2);
return set1.size();
}
/**
* 计算相似度
* @author dynamo
* @param intersection 交集
* @param count1
* @param count2
* @return
*/
private double computeSimilarity(double intersection,int count1,int count2){
if(intersection == 0){
  intersection = Constant.DEFAULT_VALUE;
}
return intersection/Math.sqrt(count1)*Math.sqrt(count2);
}
}
