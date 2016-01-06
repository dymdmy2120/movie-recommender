package com.wepiao.goods.vo;

import com.wepiao.common.utils.DateUtil;
import com.wxmovie.bis.ticketplatform.util.StringUtility;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;

/**
 * @author wangzhijiang
 * @date 2014-9-23
 * @description 排期批价实体类
 */
public class Schedule implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /** 开放平台排期Id */
  private Integer baseScheduleId;

  /** 第三方平台Id/公众号Id */
  private String tpId;

  /** 订座接口系统Id */
  private String bisId;

  /** 订座接口系统服务器Id */
  private String bisServerId;

  /** 订座系统排期Id */
  private Integer bisScheduleId;

  /** 自动批价规则Id */
  private String priceRuleId;

  /** 开放平台影院编号 */
  private Integer cinemaNo;

  /** 开放平台影片编号 */
  private Integer movieNo;

  /** 开放平台影片名称 */
  private String movieName;

  /** 开放平台影院 */
  private String cinemaName;

  /** 排期编号 */
  private String seqNo;

  /** 场次内部编号 */
  private String showSeqNo;

  /** 放映日期 */
  private long showDate;

  /** 放映时间 */
  private Short showTime;

  /** 放映类型 */
  private String showType;

  /** 语言 */
  private String language;

  /** 影厅编号 */
  private String hallNo;

  /** 影厅名称 */
  private String hallName;

  /** 场区编号 */
  private String sectionNo;

  /** 门市价 */
  private BigDecimal cinemaPrice;

  /** 销售价 */
  private BigDecimal salePrice;

  /** 最低保护价 */
  private BigDecimal lowestPrice;

  /** 订座系统费用 */
  private BigDecimal bookingSystemFee;

  /** 开放平台费用 */
  private BigDecimal openSystemFee;

  /** 影院费用 */
  private BigDecimal cinemaFee;

  /** 结算价 */
  private BigDecimal settlementPrice;

  /** 实际售卖价 */
  private BigDecimal actualSellPrice;

  /** 截止售票时间 */
  private long closeTime;

  /** 城市编码 */
  private String cityNo;

  /** 批价流水号 */
  private Integer recordId;

  /** 批价类型 */
  private Byte priceType;

  /** 排期状态 */
  private Byte scheduleStatus;

  /** 批价状态 */
  private Byte priceStatus;

  /**
   * 数据变更类型
   */
  private Integer opType;

  /** 座位数量 **/
  private Integer hallSeat;

  /** BIS影院编号 */
  private String bisCinemaNo;

  /** BIS影片编号 */
  private String bisMovieNo;

  /** BIS影院LinkId */
  private String cinemaLinkId;

  public String getBisCinemaNo() {
    return bisCinemaNo;
  }

  public void setBisCinemaNo(String bisCinemaNo) {
    this.bisCinemaNo = bisCinemaNo;
  }

  public String getBisMovieNo() {
    return bisMovieNo;
  }

  public void setBisMovieNo(String bisMovieNo) {
    this.bisMovieNo = bisMovieNo;
  }

  public String getCinemaLinkId() {
    return cinemaLinkId;
  }

  public void setCinemaLinkId(String cinemaLinkId) {
    this.cinemaLinkId = cinemaLinkId;
  }

  public String getTpId() {
    return tpId;
  }

  public void setTpId(String tpId) {
    this.tpId = tpId;
  }

  public String getBisId() {
    return bisId;
  }

  public void setBisId(String bisId) {
    this.bisId = bisId;
  }

  public String getBisServerId() {
    return bisServerId;
  }

  public void setBisServerId(String bisServerId) {
    this.bisServerId = bisServerId;
  }

  public String getPriceRuleId() {
    return priceRuleId;
  }

  public void setPriceRuleId(String priceRuleId) {
    this.priceRuleId = priceRuleId;
  }

  public String getMovieName() {
    return movieName;
  }

  public void setMovieName(String movieName) {
    this.movieName = movieName;
  }

  public String getCinemaName() {
    return cinemaName;
  }

  public void setCinemaName(String cinemaName) {
    this.cinemaName = cinemaName;
  }

  public String getSeqNo() {
    return seqNo;
  }

  public void setSeqNo(String seqNo) {
    this.seqNo = seqNo;
  }

  public String getShowSeqNo() {
    return showSeqNo;
  }

  public void setShowSeqNo(String showSeqNo) {
    this.showSeqNo = showSeqNo;
  }

  public String getShowDate() {
    return StringUtility.getDateToString(new Date(showDate), "yyyy-MM-dd");
  }

  public void setShowDate(long showDate) {
    this.showDate = showDate;
  }

  public void setShowTime(Short showTime) {
    this.showTime = showTime;
  }

  public Date getShowDatetime(){
    Date date = new Date(showDate);
    String showTimeStr = String.format("%04d", this.showTime);
    LocalTime localTime = DateUtil.parseTime(showTimeStr, "HHmm");
    return new DateTime(date).withTime(localTime).toDate();
  }

  public String getShowType() {
    return showType;
  }

  public void setShowType(String showType) {
    this.showType = showType;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getHallNo() {
    return hallNo;
  }

  public void setHallNo(String hallNo) {
    this.hallNo = hallNo;
  }

  public String getHallName() {
    return hallName;
  }

  public void setHallName(String hallName) {
    this.hallName = hallName;
  }

  public String getSectionNo() {
    return sectionNo;
  }

  public void setSectionNo(String sectionNo) {
    this.sectionNo = sectionNo;
  }

  public BigDecimal getCinemaPrice() {
    return cinemaPrice;
  }

  public void setCinemaPrice(BigDecimal cinemaPrice) {
    this.cinemaPrice = cinemaPrice;
  }

  public BigDecimal getSalePrice() {
    return salePrice;
  }

  public void setSalePrice(BigDecimal salePrice) {
    this.salePrice = salePrice;
  }

  public BigDecimal getLowestPrice() {
    return lowestPrice;
  }

  public void setLowestPrice(BigDecimal lowestPrice) {
    this.lowestPrice = lowestPrice;
  }

  public BigDecimal getBookingSystemFee() {
    return bookingSystemFee;
  }

  public void setBookingSystemFee(BigDecimal bookingSystemFee) {
    this.bookingSystemFee = bookingSystemFee;
  }

  public BigDecimal getOpenSystemFee() {
    return openSystemFee;
  }

  public void setOpenSystemFee(BigDecimal openSystemFee) {
    this.openSystemFee = openSystemFee;
  }

  public BigDecimal getCinemaFee() {
    return cinemaFee;
  }

  public void setCinemaFee(BigDecimal cinemaFee) {
    this.cinemaFee = cinemaFee;
  }

  public BigDecimal getSettlementPrice() {
    return settlementPrice;
  }

  public void setSettlementPrice(BigDecimal settlementPrice) {
    this.settlementPrice = settlementPrice;
  }

  public BigDecimal getActualSellPrice() {
    return actualSellPrice;
  }

  public void setActualSellPrice(BigDecimal actualSellPrice) {
    this.actualSellPrice = actualSellPrice;
  }

  public Date getCloseTime() {
    return new Date(closeTime);
  }

  public void setCloseTime(long closeTime) {
    this.closeTime = closeTime;
  }

  public String getCityNo() {
    return cityNo;
  }

  public void setCityNo(String cityNo) {
    this.cityNo = cityNo;
  }

  public Byte getPriceType() {
    return priceType;
  }

  public void setPriceType(Byte priceType) {
    this.priceType = priceType;
  }

  public Byte getScheduleStatus() {
    return scheduleStatus;
  }

  public void setScheduleStatus(Byte scheduleStatus) {
    this.scheduleStatus = scheduleStatus;
  }

  public Byte getPriceStatus() {
    return priceStatus;
  }

  public void setPriceStatus(Byte priceStatus) {
    this.priceStatus = priceStatus;
  }

  public Integer getOpType() {
    return opType;
  }

  public void setOpType(Integer opType) {
    this.opType = opType;
  }

  public Integer getHallSeat() {
    return hallSeat;
  }

  public void setHallSeat(Integer hallSeat) {
    this.hallSeat = hallSeat;
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public Integer getBaseScheduleId() {
    return baseScheduleId;
  }

  public void setBaseScheduleId(Integer baseScheduleId) {
    this.baseScheduleId = baseScheduleId;
  }

  public Integer getBisScheduleId() {
    return bisScheduleId;
  }

  public void setBisScheduleId(Integer bisScheduleId) {
    this.bisScheduleId = bisScheduleId;
  }

  public Integer getCinemaNo() {
    return cinemaNo;
  }

  public void setCinemaNo(Integer cinemaNo) {
    this.cinemaNo = cinemaNo;
  }

  public Integer getMovieNo() {
    return movieNo;
  }

  public void setMovieNo(Integer movieNo) {
    this.movieNo = movieNo;
  }

  public Integer getRecordId() {
    return recordId;
  }

  public void setRecordId(Integer recordId) {
    this.recordId = recordId;
  }

  public String getShowTime() {
    String f = "%04d";
    String str = String.format(f, showTime);
    return str.substring(0, 2) + ":" + str.substring(2, 4);
  }

  //public Date getShowDatetime() {
  //  String datetime = getShowDate() + " " + getShowTime();
  //  return DateUtil.parse(datetime, DateUtil.FORMAT_DATETIME);
  //}
}
