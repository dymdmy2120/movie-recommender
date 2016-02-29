package com.movie.rec.test.service.similarity;


/**
 * Date:     2016年2月19日 上午10:59:11 <br/>
 * @author   dynamo
 */
public class Test2 {

public static void main(String[] args) {
    Test2 t = new Test2();
    System.out.println("255.120.10.111".hashCode() % 2);
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

  class A {
    private int a;

    public int getA() {
      return a;
    }

    public void setA(int a) {
      this.a = a;
    }

    @Override
    public String toString() {
      return "A [a=" + a + "]";
    }

}
}
