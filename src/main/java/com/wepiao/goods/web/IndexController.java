package com.wepiao.goods.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by qilei on 15/12/24.
 */
@Controller
public class IndexController {

  @RequestMapping(value={"","/"},produces = "application/json; charset=UTF-8")
  @ResponseBody
  public String index() {
    return "Goods Center is OK!";
  }
}
