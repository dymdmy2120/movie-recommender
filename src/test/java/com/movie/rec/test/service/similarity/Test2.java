package com.movie.rec.test.service.similarity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 * Date:     2016年2月19日 上午10:59:11 <br/>
 * @author   dynamo
 */
public class Test2 {

public static void main(String[] args) {
    Date d = new Date();
    Calendar c = Calendar.getInstance();
    c.add(Calendar.DAY_OF_MONTH, -1);
    System.out.println(c.getTimeInMillis());
    String f = "%04d";
    String str = String.format(f, d.getTime());
    System.out.println(d.getTime());
    System.out.println(str.substring(0, 2) + ":" + str.substring(2, 4));
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
