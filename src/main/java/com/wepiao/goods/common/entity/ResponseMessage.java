package com.wepiao.goods.common.entity;

public enum ResponseMessage implements MessageCodes {
  SE00000(),

  // 校验失败
  SE90001(E00001, "90001", "请求参数为空或非法"),
 SE90002(E00001, "90002", "不存在的订单"),

   // 系统异常
  SE70001(E00003, "70001", "查询票状态失败"),
  SE70002(E00003, "70002", "查询出票信息失败"),
  SE70003(E00003, "70003", "查询二维码信息失败"),
  SE70004(E00003, "70004", "核销票操作失败"),
  SE70005(E00003, "70005", "锁座自由库存失败"),
 SE70006(E00003, "70006", "出票失败,或票已出"),
  SE70007(E00003, "70005", "解锁自由库存失败"),
  SE70008(E00003, "70005", "解锁非自由库存失败"),


  // Ticket Platform 异常
  TP_ERROR(E00008, "", ""),
  SE80001(E00008, "80001", "Ticket platform exception"),
  SE80002(E00008, "80002", "Ticket platform 锁座失败");

  private CommonResponse<?> response = null;

  private ResponseMessage() {
    response = new CommonResponse<Object>();
  }

  private ResponseMessage(String ret, String sub, String msg) {
    response = new CommonResponse<Object>(ret, sub, msg);
  }

  /**
   * 定制message信息.
   */
  public ResponseMessage withMsg(String msg) {
    this.response.setMsg(msg);
    return this;
  }

  /**
   * 定制sub信息.
   */
  public ResponseMessage withSub(String sub) {
    this.response.setSub(sub);;
    return this;
  }

  @SuppressWarnings("unchecked")
  public <T> CommonResponse<T> getResponse() {
    return (CommonResponse<T>) this.response;
  }
}
