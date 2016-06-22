package com.duantuke.order.model;

import java.io.Serializable;

/**
 * 创建订单请求参数
 * 
 * @author 须俊杰
 * @date 2016年1月25日
 *
 */
public class CreateOrderRequest extends Base implements Serializable {

	private static final long serialVersionUID = 1L;

	private Order order;

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
}
