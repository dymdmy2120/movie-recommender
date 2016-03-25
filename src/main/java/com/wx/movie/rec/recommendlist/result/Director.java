package com.wx.movie.rec.recommendlist.result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wx.movie.rec.recommendlist.pojo.RecList;

@Component
public class Director {
	@Autowired
	private ReclistBuilder reclstBuilder;

	public RecList construtRecList(String uid) {
		reclstBuilder.set(uid);
		reclstBuilder.bseOnUsrReclst();
		reclstBuilder.bseOnMovieReclst();
		reclstBuilder.bseFeedBackReclst();
		reclstBuilder.bseDefaultReclst();
		return reclstBuilder.buildRecList();
	}
}
