package com.duantuke.order.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duantuke.order.mappers.OrderMapper;
import com.duantuke.order.model.CreateOrderRequest;

/**
 * 创建订单处理器
 * 
 * @author 须俊杰
 * @since 1.0
 */
@Service
public class CreateOrderHandler {

	@Autowired
	private OrderMapper orderMapper;

	public void create(CreateOrderRequest request) {

	}

	/**
	 * 验证请求参数
	 * 
	 * @param request
	 */
	public boolean validate(CreateOrderRequest request) {
		return true;
	}
}
