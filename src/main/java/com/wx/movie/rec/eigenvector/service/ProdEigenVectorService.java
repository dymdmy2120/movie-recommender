
package com.wx.movie.rec.eigenvector.service;

import com.wx.movie.rec.pojo.UserActionData;

/**
 * 产生特征向量的服务接口
 * Date:     2016年2月14日 下午8:49:12 <br/>
 */
public interface ProdEigenVectorService {
/**
 *
 * @author dynamo
 * @param userActionDatas :用户行为数据集合
 * 第一个元素是基于影片的行为操作数据，第二个元素是基于用户的行为操作数据
 */
  void produceEigenVector(UserActionData userActionDatas);

}
