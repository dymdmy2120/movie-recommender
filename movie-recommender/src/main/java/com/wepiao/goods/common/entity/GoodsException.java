package com.wepiao.goods.common.entity;

/**
 * 商品中心的异常处理类
 */
public class GoodsException extends RuntimeException {

  private static final long serialVersionUID = -8876379099262898617L;
  private CommonResponse<?> commonResponse;

  public GoodsException(CommonResponse<?> commonResponse) {
    super(commonResponse.getMsg());
    this.commonResponse = commonResponse;
  }

  public GoodsException(ResponseMessage responseMessage) {
    super(responseMessage.getResponse().getMsg());
    this.commonResponse = responseMessage.getResponse();
  }

  public GoodsException() {
    super();
  }

  public GoodsException(Throwable cause) {
    super(cause);
  }

  public GoodsException(String message) {
    super(message);
  }

  public GoodsException(String message, Throwable cause) {
    super(message, cause);
  }

  public CommonResponse<?> getCommonResponse() {
    return commonResponse;
  }
}
