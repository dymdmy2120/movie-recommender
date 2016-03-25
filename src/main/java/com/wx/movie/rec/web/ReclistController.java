package com.wx.movie.rec.web;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wx.movie.rec.common.exception.CommonResponse;
import com.wx.movie.rec.common.exception.ResponseMessage;
import com.wx.movie.rec.recommendlist.result.Director;

@Controller
public class ReclistController {
	@Autowired
	private Director direcotr;
    @ResponseBody
	@RequestMapping("/reclist/{uid}")
	public CommonResponse<?> getReclist(@PathVariable("uid") String uid) {
		if (uid == null) {
			return ResponseMessage.SE10001.getResponse();
		}
		return CommonResponse.success(direcotr.construtRecList(uid));
	}
}
