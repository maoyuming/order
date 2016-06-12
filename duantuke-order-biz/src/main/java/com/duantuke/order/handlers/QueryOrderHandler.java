package com.duantuke.order.handlers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duantuke.order.mappers.OrderMapper;
import com.duantuke.order.model.Order;
import com.duantuke.order.utils.log.LogUtil;

/**
 * 查询订单处理器
 * 
 * @author 须俊杰
 * @date 2016年6月12日
 */
@Service
public class QueryOrderHandler {

	public static final LogUtil logger = new LogUtil(QueryOrderHandler.class);
	@Autowired
	private OrderMapper orderMapper;
	
	public List<Order> queryOrders(){
		return orderMapper.queryOrders();
	}
}
