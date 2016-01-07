package com.wepiao.goods.mapper.goods;

import java.util.List;

import java.util.Map;
import java.util.Set;
import org.apache.ibatis.annotations.Param;

import com.wepiao.goods.model.goods.LocalOrderInfo;

public interface LocalOrderInfoMapper {

    int insert(LocalOrderInfo record);

    int updateSelective(LocalOrderInfo record);

    LocalOrderInfo getById(Integer id);

    /**
     * 通过订单中心订单号查询订单
     * @param orderId 订单中心订单号
     * @return
     */
    List<LocalOrderInfo> getByOrderId(String orderId);

    List<String> getSeatsBySchedule(Integer scheduleId);

    /**
     * 根据条件查询本地库存
     */
    public List<LocalOrderInfo> getByCriteria(LocalOrderInfo localOrderInfo);

    /**
     * 查询锁座超时未出票订单号
     * @return
     */
    List<String> getOvertimeOrders();

    /**
     * 把超时的订单座位状态设成 0 - 未锁座
     * @param orderId
     * @return
     */
    int unlockLocalSeat(final String fixOrderId);

  /**
   * 
   * updateLocalOrderInfoBatch:批量更新自由库存的值. <br/>
   * TODO(这里描述这个方法适用条件 – 可选).<br/>
   * 
   * @author dynamo
   * @param localOrderInfos
   * @return
   */
  int updateLocalOrderInfoBatch(List<LocalOrderInfo> localOrderInfos);

    /**
     * selectAllRefundTickets:查询本地库存中的待退票. <br>
     *
     * @return 待退的票集合
     */
    public List<LocalOrderInfo> selectAllRefundTickets();

    /**
     * deleteLocalRefundOkTickets:删除本地库存中退票成功的票记录. <br>
     *
     * @return 删除成功的数量
     */
    public int deleteLocalRefundOkTickets(LocalOrderInfo localOrderInfo);

    /**
     * selectLocalSeatsByOrderId:根据用户订单号查询本地库存中的座位信息. <br/>
     *
     * @param orderId 用户订单号
     * @return 本地库存中该订单对应的座位集合
     */
    public List<LocalOrderInfo> selectLocalSeatsByOrderId(@Param("orderId") String orderId);

  /**
   * 自有库存锁坐
   */
  public int lockSeats(@Param("ids") Set<Integer> ids, @Param("orderId") String orderId);


    /**
   * 出票
   */
  Integer drawTickets(@Param("ids") Set<Integer> ids);
}
