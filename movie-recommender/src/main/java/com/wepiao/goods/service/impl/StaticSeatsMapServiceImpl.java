package com.wepiao.goods.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.wepiao.common.dao.BisCinemaDao;
import com.wepiao.common.entity.BisCinema;
import com.wepiao.common.entity.BisCinemaHall;
import com.wepiao.common.redis.RedisUtils;
import com.wepiao.goods.common.enums.RedisKey;
import com.wepiao.goods.service.StaticSeatsMapService;

/**
 * Created by Wangyu on 2016/1/4.
 */
@Service
public class StaticSeatsMapServiceImpl implements StaticSeatsMapService {

  private static final int DEFALT_EXPIRE_TIME = 24 * 60 * 60;//1天

  @Autowired
  private BisCinemaDao bisCinemaDao;

  @Autowired
  private RedisUtils redisUtils;

//  @Override
//  public  void  init() {
//    ExecutorService fixedThreadPool =
//        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//    asyncInitCache(fixedThreadPool);
//  }

//  @Async
//  public void asyncInitCache(ExecutorService fixedThreadPool) {
//
//    // 遍历影院下的影厅
//    fixedThreadPool.execute(new Runnable() {
//      @Override
//      public void run() {
//        List<BisCinema> allBisCinemas = bisCinemaDao.findAll();
//        for (BisCinema bisCinema : allBisCinemas) {
//          List<BisCinemaHall> cinemaHalls = bisCinema.getCinemaHalls();
//          for (BisCinemaHall cinemaHall : cinemaHalls) {
//            if (!Strings.isNullOrEmpty(cinemaHall.getRoomSeat()) && cinemaHall.getRoomSeat()
//                .contains("*")) {
//              String key = RedisKey.STATIC_SEAT_KEY_PREFIX.getVal() + Joiner.on("_")
//                  .join(bisCinema.getBisServerId(), bisCinema.getCinemaNo(),
//                      cinemaHall.getHallNo().trim());
//              redisUtilsNew.setT(key, cinemaHall.getRoomSeat());
//              redisUtilsNew.expire(key, DEFALT_EXPIRE_TIME);
//            }
//          }
//        }
//      }
//    });
//  }

  @Override
  public void updateCache() {
    List<BisCinema> allBisCinemas = bisCinemaDao.findAll();
    for (BisCinema bisCinema : allBisCinemas) {
      List<BisCinemaHall> cinemaHalls = bisCinema.getCinemaHalls();

      for (BisCinemaHall cinemaHall : cinemaHalls) {
        String roomSeats = cinemaHall.getRoomSeat();

        if (!Strings.isNullOrEmpty(roomSeats) && roomSeats.contains("*")) {

          String key = RedisKey.STATIC_SEAT_KEY_PREFIX.getVal() + Joiner.on("_").join(
              bisCinema.getBisServerId(), bisCinema.getCinemaNo(), cinemaHall.getHallNo().trim());
          redisUtils.setT(key, roomSeats);
          redisUtils.expire(key, DEFALT_EXPIRE_TIME);
        }
      }
    }
  }
}

