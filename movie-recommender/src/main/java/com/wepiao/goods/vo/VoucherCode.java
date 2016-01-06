
package com.wepiao.goods.vo;

/**
 * 票的二维码
 */
public class VoucherCode {

  private String tpOrderId;
  private String voucherCode;

  public VoucherCode() {

  }

  public VoucherCode(String tpOrderId, String voucherCode) {
    this.tpOrderId = tpOrderId;
    this.voucherCode = voucherCode;
  }
  public String getTpOrderId() {
    return tpOrderId;
  }

  public void setTpOrderId(String tpOrderId) {
    this.tpOrderId = tpOrderId;
  }

  public String getVoucherCode() {
    return voucherCode;
  }

  public void setVoucherCode(String voucherCode) {
    this.voucherCode = voucherCode;
  }

}
