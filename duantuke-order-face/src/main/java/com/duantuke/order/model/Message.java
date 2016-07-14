package com.duantuke.order.model;

import java.util.List;

/**
 * 订单消息模型
 * 
 * @author 须俊杰
 * @date 2016年6月12日
 */
public class Message {

	/**
	 * 订单
	 */
	private Order order;
	/**
	 * 促销id列表
	 */
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
