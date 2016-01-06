package com.wepiao.goods.service.impl;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wepiao.common.dao.BisCinemaDao;
import com.wepiao.common.entity.BisCinema;
import com.wepiao.common.entity.BisCinemaHall;
import com.wepiao.common.redis.RedisUtils;
import com.wepiao.goods.common.utils.Constants;
import com.wepiao.goods.mapper.goods.LocalOrderInfoMapper;
import com.wepiao.goods.mapper.open.ForbidSeatMapper;
import com.wepiao.goods.service.CommonService;
import com.wepiao.goods.service.ScheduleService;
import com.wepiao.goods.vo.Schedule;
import com.wxmovie.bis.ticketplatform.BisGateFacade;
import com.wxmovie.bis.ticketplatform.entity.ScheduleInfo;
import com.wxmovie.bis.ticketplatform.result.IBisResult;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wepiao.goods.common.entity.GoodsException;
import com.wepiao.goods.service.SeatAvailableService;

/**
 * Created by qilei on 15/12/25.
 */
@Service
public class SeatAvailableServiceImpl implements SeatAvailableService {

  private static Logger logger = LoggerFactory.getLogger(SeatAvailableServiceImpl.class);

  @Autowired
  private LocalOrderInfoMapper localOrderInfoMapper;

  @Autowired
  private BisCinemaDao bisCinemaDao;

  @Autowired
  private ScheduleService scheduleService;

  @Autowired
  private RedisUtils redisUtils;

  @Autowired
  private ForbidSeatMapper forbidSeatMapper;

  @Autowired
  private CommonService commonService;

  @Override
  public String getSalableSeats(Integer scheduleId, Integer cinemaNo, Integer channelId)
      throws GoodsException {
    Stopwatch timer = Stopwatch.createStarted();
    Set<String> localSalableSeats = Sets.newHashSet();// 本地库存中可售座位
    Schedule schedule =
        scheduleService.getSchedule(scheduleId, cinemaNo, channelId);
    // 1.根据排期批价ID查询影院排期信息
    if (null == schedule || schedule.getBaseScheduleId() == null) {
      throw new GoodsException("排期ID不存在");
    }
    // 2.调用影院接口，获取不可售座位
    Set<String> cinemaUnsaleSeats = getCinemaUnSalableSeats(schedule);// 影院不可售座位
    logger.debug("Cinema unsaleable seats hava : {}", cinemaUnsaleSeats.size());
    // 3.查询静态座位图
    Set<String> allStaticSeats = commonService.getStaticSeat(schedule.getBisServerId()
        ,schedule.getBisCinemaNo(),schedule.getHallNo());// 所有排期对应的静态座位图
    logger.debug("All static seats hava : {}", allStaticSeats.size());
    // 4.根据排期查询本地库存中的可售座位
    List<String> localHotSeatInfos =
        localOrderInfoMapper.getSeatsBySchedule(scheduleId);
    if (null != localHotSeatInfos && localHotSeatInfos.size() > 0) {
      localSalableSeats = Sets.newHashSet(localHotSeatInfos);
    }
    // 5.取出所有的禁售座位
    Date showTime = null;
    try {
      showTime =
          new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(schedule.getShowDate() + " "
              + schedule.getShowTime());
    } catch (ParseException e) {
      e.printStackTrace();
    }
    Set<String> allForbidSeats = getAllForbidSeats(cinemaNo, showTime);
    logger.debug("All forbid seats have : {}", allForbidSeats.size());
    // 6.可售座位 (静态座位, 影院所有不可售座位, 禁售座位)求补集后，再与(本地库存座位)求并集
    Set<String> allunsalableSeats = Sets.union(cinemaUnsaleSeats, allForbidSeats);
    Sets.SetView<String> cinemaSalableSeats = Sets.difference(allStaticSeats, allunsalableSeats);
    logger.debug("Union unsalable seats have : {}", allunsalableSeats.size());
    logger.debug("Cinema salable seats have : {}", cinemaSalableSeats.size());
    Set<String> allSeats = Sets.newHashSet();
    for (String item : localSalableSeats) {
      allSeats.add(item + "-0");
    }
    for (String item : cinemaSalableSeats) {
      allSeats.add(item + "-1");
    }
    logger.info("SalableSeatsServiceImpl.getAllSalableSeats take time : {}", timer.stop());
    logger.info("Has salable seats : {}", allSeats.size());
    return generateFinalSeats(allSeats);
  }

  private String generateFinalSeats(Set<String> salableSeats) {
    Stopwatch timer = Stopwatch.createStarted();
    Map<String, Set<String>> map = new TreeMap<String, Set<String>>();
    for (String item : salableSeats) {
      String[] strings = item.split(":");
      String row = strings[1];
      Set<String> cols = map.get(row);
      if (null != cols) {
        cols.add(strings[2]);
      } else {
        cols = new HashSet<String>();
        cols.add(strings[2]);
        map.put(row, cols);
      }
    }
    List<String> rows = Lists.newArrayList();
    for (String i : map.keySet()) {
      Set<String> cols = map.get(i);
      ImmutableList<String> strings1 =
          FluentIterable.from(cols).toSortedList(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
              Integer col1 = Integer.parseInt(o1.split("-")[0]);
              Integer col2 = Integer.parseInt(o2.split("-")[0]);
              return col1 - col2;
            }
          });
      String joinedCols = Joiner.on(",").join(strings1);
      rows.add(Joiner.on(":").join("01", i, joinedCols));
    }
    String result = Joiner.on("|").join(rows);
    logger.info("generateFinalSeats take time:{}", timer.stop());
    return result;
  }

  /**
   * getCinemaUnSalableSeats:获取影院不可售座位. <br/>
   *
   * @param movieScheduleInfos 影院排期信息对象
   * @return 解析后的座位集合
   */
  private Set<String> getCinemaUnSalableSeats(Schedule movieScheduleInfos)
      throws GoodsException {
    Set<String> finalSeats = Sets.newHashSet();
    // 封装影院排期信息对象
    ScheduleInfo scheduleInfo = new ScheduleInfo();
    scheduleInfo.setScheduleId(String.valueOf(movieScheduleInfos.getBisScheduleId()));
    scheduleInfo.setCinemaNo(movieScheduleInfos.getBisCinemaNo());
    scheduleInfo.setHallNo(movieScheduleInfos.getHallNo());
    scheduleInfo.setLocNo(movieScheduleInfos.getSectionNo());
    scheduleInfo.setCinemaLinkId(movieScheduleInfos.getCinemaLinkId());
    scheduleInfo.setFilmNo(movieScheduleInfos.getBisMovieNo());
    scheduleInfo.setShowSeqNo(movieScheduleInfos.getShowSeqNo());
    scheduleInfo.setShowDate(movieScheduleInfos.getShowDate());
    scheduleInfo.setShowTime(movieScheduleInfos.getShowTime());
    scheduleInfo.setBisServerNo(movieScheduleInfos.getBisServerId());
    scheduleInfo.setSeqNo(movieScheduleInfos.getSeqNo());
    scheduleInfo.setBaseCinemaNo(String.valueOf(movieScheduleInfos.getCinemaNo()));
    // 调用影院接口
    IBisResult result = null;
    try {
      result = BisGateFacade.unSalesQuery(scheduleInfo);
    } catch (Exception e) {
      //todo
      e.printStackTrace();
    }
    if (null != result) {
      if (Constants.SUCC.equalsIgnoreCase(result.getErrorMsg())) {
        // 返回值事例 "null:4:09@null:6:02@null:7:07@null:8:03,08,15";
        String strSeat = result.getUnSalesSeats();
        if (!Strings.isNullOrEmpty(strSeat)) {
          finalSeats = parseSeat(strSeat, "@");
        }
      } else {
        //todo
        throw new GoodsException(result.getErrorMsg());
      }
    } else {
      //todo
      throw new GoodsException();
    }
    return finalSeats;
  }

  /**
   * parseSeat: 解析影院静态座位. <br/>
   *
   * @param schedule 原始座位信息
   * @return 解析后的座位信息集合
   */
  private Set<String> getStaticSeat(Schedule schedule) {
    Stopwatch timer = Stopwatch.createStarted();
    Set<String> seats = Sets.newHashSet();
    String keyPrefix = "GoodsCenter:StaticSeat:";
    // redis key为：GoodsCenter:StaticSeat:bisServerId_cinemaNo_hallNo
    String keySuffix = Joiner.on("_")
        .join(schedule.getBisServerId(), schedule.getBisCinemaNo(),
            schedule.getHallNo()).trim();
    String staticSeat = redisUtils.getT(keyPrefix + keySuffix, String.class);
    //判断缓存中是否有该排期的静态座位图
    if (Strings.isNullOrEmpty(staticSeat)) {
      //将该影院的所有座位图放到缓存
      logger.debug("getStaticSeat query db");
      BisCinema bisCinema =
          bisCinemaDao.findOne(schedule.getBisCinemaNo(), schedule.getBisServerId());
      List<BisCinemaHall> cinemaHalls = bisCinema.getCinemaHalls();
      for (BisCinemaHall cinemaHall : cinemaHalls) {
        if (!Strings.isNullOrEmpty(cinemaHall.getRoomSeat()) && cinemaHall.getRoomSeat()
            .contains("*")) {
          String key =
              keyPrefix + Joiner.on("_").join(bisCinema.getBisServerId(), bisCinema.getCinemaNo(),
                  cinemaHall.getHallNo().trim());
          redisUtils.setT(key, cinemaHall.getRoomSeat());
        }
      }
      staticSeat = redisUtils.getT(keyPrefix + keySuffix, String.class);
      logger.info("Init static seats take : {}", timer.stop());
    }
    return parseStaticSeat(seats, staticSeat);
  }

  private Set<String> parseStaticSeat(Set<String> seats, String initSeat) {
    Stopwatch timer = Stopwatch.createStarted();
    if (!Strings.isNullOrEmpty(initSeat)) {
      int sectionNoEndIndex = initSeat.indexOf(':');
      String sectionNo = initSeat.substring(sectionNoEndIndex - 2, sectionNoEndIndex);
      int startIndex = initSeat.indexOf(':', sectionNoEndIndex + 1);
      String[] rows = initSeat.substring(startIndex + 1).split("\\|");
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

  /**
   * parseSeat:解析座位信息. <br>
   *
   * @param strSeat 原始禁售座位字符串
   * @return 解析后的座位集合 格式：区号：行号：列号
   */
  private Set<String> parseSeat(String strSeat, String splitter) {
    Stopwatch timer = Stopwatch.createStarted();
    Set<String> finalSeats = Sets.newHashSet();
    // 解析影院不可售座位
    Iterable<String> seats = Splitter.on(splitter).trimResults().split(strSeat);
    for (String seat : seats) {
      // 座位格式 区号：行号：列号：座位状态
      List<String> seatArray = Splitter.on(":").trimResults().splitToList(seat);
      //String sourceAreaNo = seatArray.get(0);
      //String areaNo =
      //    (null == sourceAreaNo || "".equals(sourceAreaNo)) ? "01" : sourceAreaNo;
      if (null != seatArray.get(1).trim()) {
        String rowNo = seatArray.get(1).trim();
        List<String> columnArray = Splitter.on(",").trimResults().splitToList(seatArray.get(2));
        // 拆出列
        for (String columnNo : columnArray) {
          StringBuffer seatBuffer = new StringBuffer();
          seatBuffer.append("01").append(":");
          seatBuffer.append(rowNo).append(":");
          seatBuffer.append(columnNo);
          finalSeats.add(seatBuffer.toString());
        }
      }
    }
    logger.info("Parse cinema seats take time : {}", timer.stop());
    return finalSeats;
  }

  /**
   * getAllForbidSeats: 查询所有禁售座位. <br>
   *
   * @return 禁售座位集合
   */
  private Set<String> getAllForbidSeats(Integer baseCinemaNo, Date showTime) {
    Stopwatch timer = Stopwatch.createStarted();
    Set<String> finalSeats = Sets.newHashSet();
    Map<String, Object> params = Maps.newHashMap();
    params.put("baseCinemaNo", baseCinemaNo);
    params.put("showTime", showTime);
    List<String> forbidSeats = forbidSeatMapper.getForbidSeat(params);
    for (String forbidSeat : forbidSeats) {
      Set<String> parseSeat = parseSeat(forbidSeat, "|");
      finalSeats.addAll(parseSeat);
    }
    logger.info("SalableSeatsServiceImpl.getAllForbidSeats take time : {}", timer.stop());
    return finalSeats;
  }
}
