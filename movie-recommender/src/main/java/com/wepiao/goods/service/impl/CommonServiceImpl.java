package com.wepiao.goods.service.impl;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;
import com.wepiao.common.dao.BisCinemaDao;
import com.wepiao.common.entity.BisCinema;
import com.wepiao.common.entity.BisCinemaHall;
import com.wepiao.goods.mapper.goods.LocalOrderInfoMapper;
import com.wepiao.goods.model.goods.LocalOrderInfo;
import com.wepiao.goods.service.CommonService;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by qilei on 15/7/31.
 */
@Service
public class CommonServiceImpl implements CommonService {
  private Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

  @Autowired
  private BisCinemaDao bisCinemaDao;
  @Autowired
  private LocalOrderInfoMapper localOrderInfoMapper;

  /**
   * 座位按照是否为本地库存分组
   */
  @Override
  public void groupSeat(Integer scheduleId, List<String> seatList, List<LocalOrderInfo> localSeats,
      List<String> cinemaSeats) {

    Stopwatch timer = Stopwatch.createStarted();
    LocalOrderInfo criteria = new LocalOrderInfo();
    criteria.setScheduleId(scheduleId);
    List<LocalOrderInfo> localOrderInfoList = localOrderInfoMapper.getByCriteria(criteria);
    logger.info("localOrderInfoMapper.getByCriteria took time: {}", timer.stop());

    for (final String seat : seatList) {
      Optional<LocalOrderInfo> match =
          FluentIterable.from(localOrderInfoList).firstMatch(new Predicate<LocalOrderInfo>() {
            @Override
            public boolean apply(LocalOrderInfo localOrderInfo) {
              return localOrderInfo.getSeat().equals(seat);
            }
          });
      if (match.isPresent()) {
        LocalOrderInfo localOrderInfo = match.get();
        localSeats.add(localOrderInfo);
      } else {
        cinemaSeats.add(seat);
      }
    }
  }

  @Override
  public Set<String> getStaticSeat(String bisServerId, String bisCinemaNo, String hallNo) {
    Set<String> seats = Sets.newHashSet();
    BisCinema bisCinema = bisCinemaDao.findOne(bisCinemaNo, bisServerId);
    List<BisCinemaHall> cinemaHalls = bisCinema.getCinemaHalls();
    for (BisCinemaHall cinemaHall : cinemaHalls) {
      String roomSeat = cinemaHall.getRoomSeat();
      String theHallNo = cinemaHall.getHallNo();
      if (!Strings.isNullOrEmpty(roomSeat) && !Strings.isNullOrEmpty(theHallNo)) {
        if (hallNo.equals(theHallNo.trim())) {
          seats = parseStaticSeat(roomSeat.trim());
          break;
        }
      }
    }
    return seats;
  }

  /**
   * 解析静态座位图
   *
   * @param staticSeats 01:普通区:1*0*ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL|2*0*ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL,ZL|3*1*ZL,ZL,20(N-N-0),19(N-N-0),18(N-N-0),17(N-N-0),16(N-N-0),15(N-N-0),14(N-N-0),13(N-N-0),12(N-N-0),11(N-N-0),10(N-N-0),9(N-N-0),8(N-N-0),7(N-N-0),6(N-N-0),5(N-N-0),4(N-N-0),3(N-N-0),2(N-N-0),1(N-N-0),ZL,ZL,ZL,ZL|4*2*ZL,ZL,20(N-N-0),19(N-N-0),18(N-N-0),17(N-N-0),16(N-N-0),15(N-N-0),14(N-N-0),13(N-N-0),12(N-N-0),11(N-N-0),10(N-N-0),9(N-N-0),8(N-N-0),7(N-N-0),6(N-N-0),5(N-N-0),4(N-N-0),3(N-N-0),2(N-N-0),1(N-N-0),ZL,ZL,ZL,ZL|5*3*ZL,ZL,20(N-N-0),19(N-N-0),18(N-N-0),17(N-N-0),16(N-N-0),15(N-N-0),14(N-N-0),13(N-N-0),12(N-N-0),11(N-N-0),10(N-N-0),9(N-N-0),8(N-N-0),7(N-N-0),6(N-N-0),5(N-N-0),4(N-N-0),3(N-N-0),2(N-N-0),1(N-N-0),ZL,ZL,ZL,ZL|6*4*ZL,ZL,20(N-N-0),19(N-N-0),18(N-N-0),17(N-N-0),16(N-N-0),15(N-N-0),14(N-N-0),13(N-N-0),12(N-N-0),11(N-N-0),10(N-N-0),9(N-N-0),8(N-N-0),7(N-N-0),6(N-N-0),5(N-N-0),4(N-N-0),3(N-N-0),2(N-N-0),1(N-N-0),ZL,ZL,ZL,ZL|7*5*ZL,ZL,20(N-N-0),19(N-N-0),18(N-N-0),17(N-N-0),16(N-N-0),15(N-N-0),14(N-N-0),13(N-N-0),12(N-N-0),11(N-N-0),10(N-N-0),9(N-N-0),8(N-N-0),7(N-N-0),6(N-N-0),5(N-N-0),4(N-N-0),3(N-N-0),2(N-N-0),1(N-N-0),ZL,ZL,ZL,ZL|8*6*ZL,ZL,20(N-N-0),19(N-N-0),18(N-N-0),17(N-N-0),16(N-N-0),15(N-N-0),14(N-N-0),13(N-N-0),12(N-N-0),11(N-N-0),10(N-N-0),9(N-N-0),8(N-N-0),7(N-N-0),6(N-N-0),5(N-N-0),4(N-N-0),3(N-N-0),2(N-N-0),1(N-N-0),ZL,ZL,ZL,ZL|9*7*ZL,ZL,20(N-N-0),19(N-N-0),18(N-N-0),17(N-N-0),16(N-N-0),15(N-N-0),14(N-N-0),13(N-N-0),12(N-N-0),11(N-N-0),10(N-N-0),9(N-N-0),8(N-N-0),7(N-N-0),6(N-N-0),5(N-N-0),4(N-N-0),3(N-N-0),2(N-N-0),1(N-N-0),ZL,ZL,ZL,ZL|10*8*ZL,ZL,22(N-N-0),21(N-N-0),20(N-N-0),19(N-N-0),18(N-N-0),17(N-N-0),16(N-N-0),15(N-N-0),14(N-N-0),13(N-N-0),12(N-N-0),11(N-N-0),10(N-N-0),9(N-N-0),8(N-N-0),7(N-N-0),6(N-N-0),5(N-N-0),4(N-N-0),3(N-N-0),ZL,ZL,2(N-N-0),1(N-N-0)|11*9*ZL,ZL,22(N-N-0),21(N-N-0),20(N-N-0),19(N-N-0),18(N-N-0),17(N-N-0),16(N-N-0),15(N-N-0),14(N-N-0),13(N-N-0),12(N-N-0),11(N-N-0),10(N-N-0),9(N-N-0),8(N-N-0),7(N-N-0),6(N-N-0),5(N-N-0),4(N-N-0),3(N-N-0),ZL,ZL,2(N-N-0),1(N-N-0)|12*10*26(N-N-0),25(N-N-0),24(N-N-0),23(N-N-0),22(N-N-0),21(N-N-0),20(N-N-0),19(N-N-0),18(N-N-0),17(N-N-0),16(N-N-0),15(N-N-0),14(N-N-0),13(N-N-0),12(N-N-0),11(N-N-0),10(N-N-0),9(N-N-0),8(N-N-0),7(N-N-0),6(N-N-0),5(N-N-0),4(N-N-0),3(N-N-0),2(N-N-0),1(N-N-0)
   */
  private Set<String> parseStaticSeat(String staticSeats) {
    Stopwatch timer = Stopwatch.createStarted();
    Set<String> seats = Sets.newHashSet();
    if (!Strings.isNullOrEmpty(staticSeats)) {
      int sectionNoEndIndex = staticSeats.indexOf(':');
      String sectionNo = staticSeats.substring(sectionNoEndIndex - 2, sectionNoEndIndex);
      int startIndex = staticSeats.indexOf(':', sectionNoEndIndex + 1);
      String[] rows = staticSeats.substring(startIndex + 1).split("\\|");
      for (String row : rows) {
        int rowNoIndex = row.indexOf('*');
        int seatBeginIndex = row.indexOf('*', rowNoIndex + 1);
        if (seatBeginIndex - rowNoIndex <= 1) {
          continue;
        }
        String rowNo = row.substring(rowNoIndex + 1, seatBeginIndex);
        String[] seatArray = row.substring(seatBeginIndex + 1).split(",");
        for (String seatColumn : seatArray) {
          // 座位格式：16(N-N-0)
          if (!seatColumn.matches(".+\\(.+-.+-.+\\)")) {
            continue;
          }
          int columnNoEndIndex = seatColumn.indexOf('(');
          String columnNo = seatColumn.substring(0, columnNoEndIndex);
          seats.add(sectionNo + ":" + rowNo + ":" + columnNo);
        }
      }
    }
    logger.info("SalableSeatsServiceImpl.parseStaticSeat take time : {}", timer.stop());
    return seats;
  }
}
