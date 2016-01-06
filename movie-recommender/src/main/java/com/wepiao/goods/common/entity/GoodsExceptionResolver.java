package com.wepiao.goods.common.entity;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.wepiao.common.utils.JsonMapper;


@Component
public class GoodsExceptionResolver implements HandlerExceptionResolver {

  private final Logger logger = LoggerFactory.getLogger(GoodsExceptionResolver.class);

  @Override
  public ModelAndView resolveException(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, Object o, Exception e) {
    CommonResponse<?> response = null;
    // 暂时只考虑异步请求
    if (e instanceof GoodsException) {
      GoodsException goodsException = (GoodsException) e;
      response = goodsException.getCommonResponse();
      if (response == null) {
        response = CommonResponse.fail();
      }

    } else {
      logger.error("GoodsExceptionResolver resolve exception", e);
      response = CommonResponse.fail();
    }
    try {
      httpServletResponse.setContentType("application/json");
      httpServletResponse.setCharacterEncoding("UTF-8");
      httpServletResponse.getWriter().print(new JsonMapper().toJson(response));
    } catch (IOException e1) {
      logger.error("GoodsExceptionResolver write error", e1);
    }
    return null;
  }
}
