package com.wx.movie.rec.similarity.pojo;
/**
 * 根据用户的行为特征(评论，浏览，关注，搜素)不同，设置不同的比例，得到最终的相似度
 * Date:     2016年2月17日 上午10:54:45 <br/>
 * @author   dynamo
 */
public class UserActionProportion {
private String action;
private double proportion;

public String getAction() {
  return action;
}
public void setAction(String action) {
  this.action = action;
}
public double getProportion() {
  return proportion;
}
public void setProportion(double proportion) {
  this.proportion = proportion;
}
@Override
public String toString() {
  return "UserActionProportion [action=" + action + ", proportion=" + proportion + "]";
}
@Override
public int hashCode() {
  final int prime = 31;
  int result = 1;
  result = prime * result + ((action == null) ? 0 : action.hashCode());
  return result;
}
@Override
public boolean equals(Object obj) {
  if (this == obj) return true;
  if (obj == null) return false;
  if (getClass() != obj.getClass()) return false;
  UserActionProportion other = (UserActionProportion) obj;
  if (action == null) {
    if (other.action != null) return false;
  } else if (!action.equals(other.action)) return false;
  return true;
}

}
