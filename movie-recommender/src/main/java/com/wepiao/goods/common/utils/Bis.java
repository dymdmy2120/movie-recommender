/**
 * Project Name:wp-local-stock File Name:Bis.java Package Name:com.wepiao.products.stock.common.utils
 * Date:2015年7月14日上午11:29:04
 */

package com.wepiao.goods.common.utils;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.wepiao.goods.common.entity.GoodsException;
import com.wepiao.goods.common.entity.ResponseMessage;
import com.wxmovie.bis.ticketplatform.BisGateFacade;
import com.wxmovie.bis.ticketplatform.entity.BisOrderInfo;
import com.wxmovie.bis.ticketplatform.entity.BisTicketInfo;
import com.wxmovie.bis.ticketplatform.entity.CinemaInfo;
import com.wxmovie.bis.ticketplatform.entity.FilmInfo;
import com.wxmovie.bis.ticketplatform.entity.HallInfo;
import com.wxmovie.bis.ticketplatform.entity.ReturnTicketInfo;
import com.wxmovie.bis.ticketplatform.entity.ScheduleInfo;
import com.wxmovie.bis.ticketplatform.entity.ScheduleResult;
import com.wxmovie.bis.ticketplatform.enumerate.BisPlatType;
import com.wxmovie.bis.ticketplatform.result.IBisResult;

/**
 * ClassName:Bis <br/> Function: TODO ADD FUNCTION. <br/> Reason: TODO ADD REASON. <br/> Date:
 * 2015年7月14日 上午11:29:04 <br/>
 *
 * @author Zeng
 * @see
 */
public class Bis {

  private static Logger log = LoggerFactory.getLogger(Bis.class);

  /**
   * 查询票面二维码
   *
   * @param bisTicketInfo 票务对象
   * @param bisServerNo 服务器编号(工厂所用慎重设置）
   * @throws Exception
   */
  public static IBisResult qryCode(BisTicketInfo bisTicketInfo, String bisServerNo) {
    IBisResult result = null;
    try {
      result = BisGateFacade.qryCode(bisTicketInfo,bisServerNo);
    } catch (Exception e) {
      throw new GoodsException(ResponseMessage.SE80001.withMsg(e.getMessage()));
    }
    return result;
  }

  /**
   * 打票方法
   *
   * @param bisTicketInfo 票务对象
   * @param bisServerNo 服务器编号(工厂所用慎重设置）
   * @throws Exception
   */
  public static IBisResult printTicket(BisTicketInfo bisTicketInfo, String bisServerNo)
      throws GoodsException {
    IBisResult result = null;
    try {
      result = BisGateFacade.printTicket(bisTicketInfo, bisServerNo);
    } catch (Exception e) {
      throw new GoodsException(ResponseMessage.SE80001.withMsg(e.getMessage()));
    }
    return result;
  }

  /**
   * 查询票状态
   *
   * @param bisTicketInfo 票务对象
   * @param bisServerNo 服务器编号(工厂所用慎重设置）
   * @throws Exception
   */
  public static IBisResult queryTicketStatus(BisTicketInfo bisTicketInfo, String bisServerNo)
      throws GoodsException {
    IBisResult result = null;
    try {
      result = BisGateFacade.queryTicketStatus(bisTicketInfo, bisServerNo);
    } catch (Exception e) {
      throw new GoodsException(ResponseMessage.SE80001.withMsg(e.getMessage()));
    }
    return result;
  }

  /**
   * 场次不可售座位查询
   *
   * @param scheduleInfo 排期编号
   * @throws Exception
   */
  public static IBisResult unSalesQuery(ScheduleInfo scheduleInfo) throws GoodsException {
    Stopwatch timer = Stopwatch.createStarted();
    IBisResult result = null;
    try {
      result = BisGateFacade.unSalesQuery(scheduleInfo);
    } catch (Exception e) {
      throw new GoodsException(ResponseMessage.SE80001.withMsg(e.getMessage()));
    }
    log.info("BisGateFacade.unSalesQuery take time:{}", timer.stop());
    return result;
  }

  /**
   * 锁定座位接口
   *
   * @param bisOrderInfo 订单详情
   * @param bisServerNo 服务器编号(工厂所用慎重设置）
   * @throws Exception
   */
  public static IBisResult lockSeat(BisOrderInfo bisOrderInfo, String bisServerNo)
      throws GoodsException {
    Stopwatch timer = Stopwatch.createStarted();
    IBisResult result = null;
    try {
      result = BisGateFacade.lockSeat(bisOrderInfo, bisServerNo);
    } catch (Exception e) {
      throw new GoodsException(ResponseMessage.SE80001.withMsg(e.getMessage()));
    }
    log.info("BisGateFacade.lockSeat take time:{}", timer.stop());
    return result;
  }

  /**
   * 取消锁定座位接口[]
   *
   * @param bisOrderInfo 订单详情
   * @param bisServerNo 服务器编号(工厂所用慎重设置）
   * @throws Exception
   */
  public static IBisResult unLockSeat(BisOrderInfo bisOrderInfo, String bisServerNo)
      throws GoodsException {
    Stopwatch timer = Stopwatch.createStarted();
    IBisResult result = null;
    try {
      result = BisGateFacade.unLockSeat(bisOrderInfo, bisServerNo);
    } catch (Exception e) {
      throw new GoodsException(ResponseMessage.SE80001.withMsg(e.getMessage()));
    }
    log.info("BisGateFacade.unlockSeat take time:{}", timer.stop());
    return result;
  }

  /**
   * 查询出票情况
   *
   * @param bisOrderInfo 接口订单号
   * @throws Exception
   */
  public static IBisResult queryTicket(BisOrderInfo bisOrderInfo, String bisServerNo)
      throws GoodsException {
    IBisResult result = null;
    try {
      result=BisGateFacade.queryTicket(bisOrderInfo,bisServerNo);
    }catch (Exception e){
      throw new GoodsException(ResponseMessage.SE80001.withMsg(e.getMessage()));
    }
    return result;
  }

  /**
   * 出票
   *
   * @throws Exception
   */
  public static IBisResult drawTicket(BisOrderInfo bisOrderInfo, String bisServerNo)
      throws GoodsException {
    Stopwatch timer = Stopwatch.createStarted();
    IBisResult result = null;
    try {
      result = BisGateFacade.drawTicket(bisOrderInfo, bisServerNo);
    } catch (Exception e) {
      throw new GoodsException(ResponseMessage.SE80001.withMsg(e.getMessage()));
    }
    log.info("BisGateFacade.drawTicket take time:{}", timer.stop());
    return result;
  }

  /**
   * 退票
   *
   * @param retrunTicketInfo 退票实体信息
   * @param bisServerNo 服务器编号(工厂所用慎重设置）
   * @throws Exception
   */
  public static IBisResult returnTicket(ReturnTicketInfo retrunTicketInfo, String bisServerNo)
      throws Exception {
    return BisGateFacade.returnTicket(retrunTicketInfo, bisServerNo);
  }

  /**
   * 获取影片列表[如果两个服务器编号则请求两个接口，BisServerNo做了区分]
   */
  public static ArrayList<FilmInfo> getFilmList() {
    return BisGateFacade.getFilmList();
  }

  /**
   * 获取影片列表[如果两个服务器编号则请求两个接口，BisServerNo做了区分]
   */
  public static ArrayList<FilmInfo> getFilmList(String bisServerNo) {
    return BisGateFacade.getFilmList(bisServerNo);
  }

  /**
   * 获取影院列表[如果两个服务器编号则请求两个接口，BisServerNo做了区分]
   */
  public static ArrayList<CinemaInfo> getCinemasList() {
    return BisGateFacade.getCinemasList();
  }

  /**
   * 获取影院列表[如果两个服务器编号则请求两个接口，BisServerNo做了区分]
   */
  public static ArrayList<CinemaInfo> getCinemasList(String bisServerNo) {
    return BisGateFacade.getCinemasList(bisServerNo);
  }

  /**
   * 获取影厅列表
   *
   * @return 影厅（包含生成座位图的大字段
   */
  public static ArrayList<HallInfo> getHallsList(String cinemaNo, String cinemaLinkId,
      String bisServerNo) {
    return BisGateFacade.getHallsList(cinemaNo, cinemaLinkId, bisServerNo);
  }

  /**
   * 按照影院编号获取排期[如果两个服务器编号则请求两个接口，BisServerNo做了区分]
   *
   * @param cinemaId 第三方影院编号
   * @param cinemaLinkId 第三方影院链接编号
   * @return 排期列表
   */
  public static ScheduleResult getScheduleList(String cinemaId, String cinemaLinkId,
      String bisServerNo) {
    return BisGateFacade.getScheduleList(cinemaId, cinemaLinkId, bisServerNo);
  }

  /**
   * 获取场次放映前多长时间停止网售
   */
  public static int getNetSaleTimeout(String bisServerNo) {
    return BisGateFacade.getNetSaleTimeout(bisServerNo);
  }

  /**
   * 获取万达ase256加密
   */
  public static String getWandaAes256(String bisServerNo) {
    return BisGateFacade.getWandaAes256(bisServerNo);
  }

  public static BisPlatType getBisPlatType(String bisServerNo) {
    return BisGateFacade.getBisPlatType(bisServerNo);
  }
}
