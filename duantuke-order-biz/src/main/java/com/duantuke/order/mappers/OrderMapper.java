package com.duantuke.order.mappers;

import java.util.List;
import java.util.Map;

import com.duantuke.order.model.Order;

public interface OrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);
    
    List<Order> queryOrders(Map<String, Object> params);
    
    Order selectOrderAndDetailsById(Long id);
    
    int cancelOrder(Order record);
    
    int updateOrderStatus(Order record);
    
    List<Order> queryOrdersByEndTime(Map<String, Object> params);
    
    int updateOrderInfoAfterCreated(Order record);
    
    int queryOrdersCount(Map<String, Object> params);
    
    int updateOrderAfterPaid(Order record);
    
    int updateOrderAfterRefunded(Order record);
}