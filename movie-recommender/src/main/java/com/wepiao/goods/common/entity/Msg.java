package com.wepiao.goods.common.entity;

public class Msg {
  public static final String RESULT_SUCCESS = "SUCCESS";
  public static final String RESULT_FAIL = "FAIL";

  /**
   * 校验失败
   */
  public static final String E00001 = "-1";
  /**
   * 数据或逻辑异常
   */
  public static final String E00002 = "-2";
  /**
   * 系统异常
   */
  public static final String E00003 = "-3";
  /**
   * 加解密异常
   */
  public static final String E00004 = "-4";
  /**
   * 其他异常
   */
  public static final String E00009 = "-9";

  public static CommonResponse SE00000 = new CommonResponse();

  public static CommonResponse SE90001 = new CommonResponse(E00001, "90001", "场次信息未获取到");
  public static CommonResponse SE90002 = new CommonResponse(E00001, "90002", "当前场次已经过期");
  public static CommonResponse SE90003 = new CommonResponse(E00001, "90003", "锁定的座位已过期");
  public static CommonResponse SE90004 = new CommonResponse(E00001, "90004", "单人购买数量到达上限");
  public static CommonResponse SE90005 = new CommonResponse(E00001, "90005", "当前场次可售卖总量购买数量到达上限");
  public static CommonResponse SE90006 = new CommonResponse(E00001, "90006", "sign签名校验失败");
  public static CommonResponse SE90007 = new CommonResponse(E00001, "90007", "paysign签名校验失败");
  public static CommonResponse SE90008 = new CommonResponse(E00001, "90008", "请求参数为空或非法");
  public static CommonResponse SE90009 = new CommonResponse(E00001, "90009", "参数解析失败");
  public static CommonResponse SE90010 = new CommonResponse(E00001, "90010", "非法的支付");
  public static CommonResponse SE90011 = new CommonResponse(E00001, "90011", "当前批价ID不存在");
  public static CommonResponse SE90012 = new CommonResponse(E00001, "90012", "不存在的订单");
  public static CommonResponse SE90013 = new CommonResponse(E00001, "90013", "当前批价流水记录不存在");
  public static CommonResponse SE90014 = new CommonResponse(E00001, "90014", "当前订单已经支付成功");
  public static CommonResponse SE90015 = new CommonResponse(E00001, "90015", "开放平台对应影院不存在");
  public static CommonResponse SE90016 = new CommonResponse(E00001, "90016", "批价发生变化,更新失败");
  public static CommonResponse SE90017 = new CommonResponse(E00001, "90017", "支付相关参数不全");
  public static CommonResponse SE90018 = new CommonResponse(E00001, "90018", "存在未支付订单");
  public static CommonResponse SE90019 = new CommonResponse(E00001, "90019", "座位信息不合法");
  public static CommonResponse SE90020 = new CommonResponse(E00001, "90020", "当前订座系统影院信息不存在");
  public static CommonResponse SE90021 = new CommonResponse(E00001, "90021", "当前订单不存在退款信息");
  public static CommonResponse SE90022 = new CommonResponse(E00001, "90022", "当前订单的退款信息状态不正确");
  public static CommonResponse SE90023 = new CommonResponse(E00001, "90023", "当前订单的退款失败");
  public static CommonResponse SE90024 = new CommonResponse(E00001, "90024", "当前订单的支付下单失败");
  public static CommonResponse SE90025 = new CommonResponse(E00001, "90025", "当前订单的支付回调处理失败");
  public static CommonResponse SE90026 = new CommonResponse(E00001, "90026", "影片不存在");
  public static CommonResponse SE90027 = new CommonResponse(E00001, "90027", "影院不存在");
  public static CommonResponse SE90028 = new CommonResponse(E00001, "90028", "请求type类型不存在");
  public static CommonResponse SE90029 = new CommonResponse(E00001, "90029", "支付价格不合法");
  public static CommonResponse SE90030 = new CommonResponse(E00001, "90030", "没有查到支付信息");
  public static CommonResponse SE90031 = new CommonResponse(E00001, "90031", "订单数据非法");
  public static CommonResponse SE90032 = new CommonResponse(E00001, "90032", "当前订单已打票");
  public static CommonResponse SE90033 = new CommonResponse(E00001, "90033", "当前订单状态不可用");
  public static CommonResponse SE90034 = new CommonResponse(E00001, "90034", "初始化财付通支付串失败，未获取到token信息");
  public static CommonResponse SE90035 = new CommonResponse(E00001, "90035", "该订单不属于该用户");
  public static CommonResponse SE90036 = new CommonResponse(E00001, "90036", "支付证书不存在");
  public static CommonResponse SE90037 = new CommonResponse(E00001, "90037", "当前订单的支付查询失败");

  public static CommonResponse SE80001 = new CommonResponse(E00002, "80001", "基础信息【微信公众号】数据不完整");
  public static CommonResponse SE80002 = new CommonResponse(E00002, "80002", "基础信息【票品】数据不完整");
  public static CommonResponse SE80003 = new CommonResponse(E00003, "80003", "基础信息【订座接口系统】数据不完整");


  public static CommonResponse SE70001 = new CommonResponse(E00003, "70001", "系统异常");
  public static CommonResponse SE70002 = new CommonResponse(E00003, "70002", "消息队列系统异常");
  public static CommonResponse SE70003 = new CommonResponse(E00003, "70003", "订座影院接口系统异常");
  public static CommonResponse SE70004 = new CommonResponse(E00003, "70004", "数据库系统异常");
  public static CommonResponse SE70005 = new CommonResponse(E00003, "70005", "缓存数据库系统异常");
  public static CommonResponse SE70006 = new CommonResponse(E00003, "70006", "万达锁座异常");
  public static CommonResponse SE70007 = new CommonResponse(E00003, "70007", "短信发送异常");
  public static CommonResponse SE70008 = new CommonResponse(E00003, "70008", "短信发送参数请求异常");
  public static CommonResponse SE70009 = new CommonResponse(E00003, "70009", "请求红包预售卷错误");
  public static CommonResponse SE70010 = new CommonResponse(E00003, "70010", "该订单已经被平台解锁");

  public static CommonResponse SE10000 = new CommonResponse(E00003, "10000", "暂不支持该接口服务");
  public static CommonResponse SE10001 = new CommonResponse(E00003, "10001", "发送红包状态获取失败可重试");
  public static CommonResponse SE10002 = new CommonResponse(E00003, "10002", "用户被微信屏蔽无法发送红包");
  public static CommonResponse SE10003 = new CommonResponse(E00003, "10003", "发放失败金额错误");
  public static CommonResponse SE10004 = new CommonResponse(E00003, "10004", "不可重试的发放失败");
  public static CommonResponse SE10005 = new CommonResponse(E00003, "10005", "账户余额不足");
  public static CommonResponse SE10006 = new CommonResponse(E00003, "10006", "购票实际支付金额小于活动最低金额");
  public static CommonResponse SE10009 = new CommonResponse(E00003, "10009", "其他异常请在日志中查看");

  public static CommonResponse SE20001 = new CommonResponse(E00002, "20001", "订单与渠道不匹配");
  public static CommonResponse SE20002 = new CommonResponse(E00002, "20002", "订单状态不可退");
  public static CommonResponse SE20003 = new CommonResponse(E00002, "20003", "订单时间不可退");
  public static CommonResponse SE20004 = new CommonResponse(E00002, "20004", "订单所属影院不许退款");
  public static CommonResponse SE20005 = new CommonResponse(E00002, "20005", "已超出本月退款限定");
  public static CommonResponse SE20006 = new CommonResponse(E00002, "20006", "订单退款已被受理");
  public static CommonResponse SE20007 = new CommonResponse(E00002, "20007", "退票失败无法退款");
  public static CommonResponse SE20008 = new CommonResponse(E00002, "20008", "售票系统数据异常");

}
