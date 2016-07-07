package com.duantuke.order.model;

import java.io.Serializable;
import java.util.List;

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
	
	private List<Long> promotions;

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public List<Long> getPromotions() {
		return promotions;
	}

	public void setPromotions(List<Long> promotions) {
		this.promotions = promotions;
	}
}
