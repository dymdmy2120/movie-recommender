package com.wx.movie.rec.common.enums;

/**
 * Date: 2016年3月9日 下午12:09:48
 *
 * @author dynamo
 * @version
 * @see
 */
/**
 * @author dynamo
 * @version
 */
public enum RecommendType {
  BSE_USER(1), BSE_MOVIE(2);

  private byte val;

  RecommendType(int val) {
    this.val = (byte) val;
  }

  public Byte getVal() {
    return val;
  }
}
