package com.movie.rec.test.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wx.movie.rec.similarity.pojo.UserActionProportion;

/**
 * Date:     2016年2月19日 上午10:59:11 <br/>
 * @author   dynamo
 */
public class Test2 {
public static void main(String[] args) {
  UserActionProportion userAction1 = new UserActionProportion();
  UserActionProportion userAction2 = new UserActionProportion();
  userAction1.setAction("4");
  userAction2.setAction("3");
  
  Map map1 = new HashMap();
  Map map2 =  new HashMap();
  map1.put(userAction1, "f");
  map2.put(userAction2, "d");
  List list = new ArrayList();
  list.add(map1);
  list.add(map2);
  Map map3 = new HashMap();
  UserActionProportion userAction3 = new UserActionProportion();
  userAction3.setAction("4");
  map1.remove(userAction3);
  System.out.println(map1);
}
}
