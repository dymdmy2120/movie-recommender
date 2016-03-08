package com.movie.rec.test.service.similarity;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wx.movie.rec.common.util.JsonMapperUtil;



/**
 * Date:     2016年2月19日 上午10:59:11 <br/>
 * @author   dynamo
 */
public class Test2 {

public static void main(String[] args) {
    Test2 t = new Test2();
    System.out.println("255.120.10.111".hashCode() % 2);
    String json =
        "{\"action\":\"browse_movie\",\"userActionMap\":{\"1573\":[\"8491\",\"8504\",\"8530\",\"8581\",\"8551\",\"8499\",\"8575\",\"60000209\",\"8613\",\"8639\",\"8579\",\"8646\",\"8640\",\"8479\",\"8644\",\"8632\",\"8525\",\"8501\",\"8502\",\"8512\",\"8513\",\"8483\",\"60001008\"],\"1575\":[\"8506\",\"8491\",\"8540\",\"8516\",\"8582\",\"60001010\",\"8508\",\"8490\",\"8499\",\"8574\",\"8495\",\"60000882\",\"60000334\",\"8611\",\"60000888\",\"8644\",\"8522\",\"8524\"],\"1523\":[\"8506\",\"8492\",\"8545\",\"8518\",\"8535\",\"8560\",\"8574\",\"8583\",\"8637\",\"8495\",\"8476\",\"8620\",\"8642\",\"8500\",\"8483\"],\"1525\":[\"8504\",\"8503\",\"8551\",\"60000859\",\"8543\",\"8608\",\"8638\",\"8619\",\"8594\",\"8613\",\"8611\",\"8621\",\"8487\",\"8538\",\"8528\",\"8548\"],\"1728\":[\"8517\",\"8507\",\"8626\",\"8638\",\"60000882\",\"8496\",\"8578\",\"8616\",\"8498\",\"8620\",\"8611\",\"8621\",\"60000199\",\"8488\",\"8480\",\"8522\",\"8483\",\"8510\"],\"1729\":[\"8554\",\"8541\",\"60000741\",\"8493\",\"60000939\",\"8575\",\"8606\",\"8497\",\"8578\",\"8487\",\"60000199\",\"8645\",\"584\",\"8527\",\"8501\",\"8537\",\"8522\",\"8510\"],\"1726\":[\"8554\",\"8517\",\"8504\",\"8519\",\"8561\",\"60000216\",\"8639\",\"60000241\",\"8595\",\"8485\",\"8621\",\"60000888\",\"5\",\"8539\",\"8527\",\"60001008\",\"8524\",\"8511\",\"8559\"],\"1727\":[\"60000742\",\"8570\",\"60001011\",\"8636\",\"8638\",\"8574\",\"60000334\",\"8578\",\"8620\",\"8611\",\"8476\",\"8486\",\"8489\",\"5\",\"8538\",\"8537\",\"8521\",\"8481\",\"8483\",\"8482\"]}}";
    System.out.println(json);
    A a = new A();
    Map<String, List<String>> map = Maps.newHashMap();
    List l = Lists.newArrayList();
    l.add("33");
    l.add("34");
    l.add("35");
    map.put("s1", l);
    map.put("s2", l);
    a.setA(2);
    a.setMap(map);
    byte[] bytes = JsonMapperUtil.getInstance().toJson(a).getBytes();

    System.out.println(JsonMapperUtil.getInstance().fromJson(json, B.class));
  }

  void m2() {
    A a = new A();
    System.out.println("a" + a);
    m(a);
    System.out.println("a" + a);
  }

  public void m(A a) {
    a.setA(1);
    System.out.println("am" + a);
}

}


class B {
  private String action;
  Map<String, Set<String>> userActionMap;

  public String getA() {
    return action;
  }

  public void setA(String a) {
    this.action = a;
  }


  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public Map<String, Set<String>> getUserActionMap() {
    return userActionMap;
  }

  public void setUserActionMap(Map<String, Set<String>> userActionMap) {
    this.userActionMap = userActionMap;
  }

  @Override
  public String toString() {
    return "B [action=" + action + ", map=" + userActionMap + "]";
  }
}


class A {
  private int a;
  Map<String, List<String>> map;

  public int getA() {
    return a;
  }

  public void setA(int a) {
    this.a = a;
  }


  public Map<String, List<String>> getMap() {
    return map;
  }

  public void setMap(Map<String, List<String>> map) {
    this.map = map;
  }

  @Override
  public String toString() {
    return "A [a=" + a + ", map=" + map + "]";
  }
}
