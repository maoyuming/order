package com.duantuke.order.handlers;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.duantuke.order.mappers.OrderMapper;
import com.duantuke.order.model.Order;
import com.duantuke.order.utils.log.LogUtil;

public abstract class AbstractOrderHandler {

	public static final LogUtil logger = new LogUtil(AbstractOrderHandler.class);
	@Autowired
	protected OrderMapper orderMapper;

	/**
	 * 查询订单信息(只查询主表)
	 * 
	 * @param id
	 * @return
	 */
	protected Order getOrderById(Long id) {
		logger.info("开始查询订单信息");
		Order order = orderMapper.selectByPrimaryKey(id);
		logger.info("订单信息查询完成,结果:{}", JSON.toJSONString(order));
		return order;
	}
}
