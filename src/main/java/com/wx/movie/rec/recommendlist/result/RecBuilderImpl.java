package com.wx.movie.rec.recommendlist.result;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.wx.movie.rec.common.enums.RecommendType;
import com.wx.movie.rec.common.enums.RedisKey;
import com.wx.movie.rec.dao.entity.FeedBack;
import com.wx.movie.rec.recommendlist.pojo.RecList;
import com.wx.movie.rec.recommendlist.service.RecDataService;
import com.wx.movie.rec.redis.RedisUtils;

@Component
public class RecBuilderImpl implements ReclistBuilder {
	@Autowired
	private RedisUtils redisUtil;
	@Autowired
	private RecDataService recDataService;
	private String uid;
	private RecList recList;
	private static final Logger logger = LoggerFactory
			.getLogger(RecBuilderImpl.class);

	public RecBuilderImpl() {

	}

	@Override
	public void set(String uid) {
		recList = null;
		this.uid = uid;
	}

	@Override
	public void bseOnUsrReclst() {
		if (!isExistsKey(uid, RecommendType.BSE_USER)) {
			return;
		}
		Set<String> movieNos = getMoviNos(uid, RecommendType.BSE_USER);
		recList = packageRecList(uid, movieNos);
		logger.info("uid {} get recList from cache bseOnUser", uid);
	}

	@Override
	public void bseOnMovieReclst() {
		if (recList != null) {
			return;
		}
		if (!isExistsKey(uid, RecommendType.BSE_USER)) {
			return;
		}
		Set<String> movieNos = getMoviNos(uid, RecommendType.BSE_MOVIE);
		recList = packageRecList(uid, movieNos);
		logger.info("uid {} get recList from cache bseOnMovie", uid);
	}

	@Override
	public void bseFeedBackReclst() {
		if (recList != null) {
			return;
		}
		List<FeedBack> feedBacks = recDataService.selectFeeBack();
		if (CollectionUtils.isEmpty(feedBacks)) {
			return;
		}
		Set<String> movieNos = Sets.newHashSet();
		for (FeedBack feedBack : feedBacks) {
			movieNos.add(feedBack.getMovieNo());
		}
		recList = packageRecList(uid, movieNos);
		logger.info("uid {} get recList from feedback", uid);
	}

	@Override
	public void bseDefaultReclst() {
		if (recList != null) {
			return;
		}
		// 这里需要读取配置文件，文件中配置了热门的电影
		Set<String> movieNos = Sets.newHashSet();
		movieNos.add("1698");
		movieNos.add("1543");
		movieNos.add("1640");
		recList = packageRecList(uid, movieNos);
		logger.info("uid {} get recList from default", uid);
	}

	@Override
	public RecList buildRecList() {
		return recList;
	}

	private RecList packageRecList(String uid, Set<String> movieNos) {
		RecList recList = new RecList();
		recList.setUid(uid);
		recList.setMovieNos(movieNos);
		return recList;
	}

	@SuppressWarnings("unchecked")
	private Set<String> getMoviNos(String uid, RecommendType method) {
		String retKey = String.format(RedisKey.UID_TYPE_REC_LIST, uid, method.getVal());
		return redisUtil.getT(retKey, Set.class);
	}

	private boolean isExistsKey(String uid, RecommendType method) {
		String retKey = String.format(RedisKey.UID_TYPE_REC_LIST, uid, method.getVal());
		if (!redisUtil.exists(retKey)) {
			return false;
		}
		return true;
	}
}
