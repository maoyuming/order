package com.duantuke.order.model;

import java.util.Date;

import com.duantuke.basic.face.bean.SkuResponse;

/**
 * 订单上下文
 * 
 * @author 须俊杰
 * @date 2016年6月12日
 */
public class OrderContext<T> {

	/**
	 * 请求参数
	 */
	private T request;

	/**
	 * 订单
	 */
	private Order order;
	
	/**
	 * sku信息
	 */
	private SkuResponse skuInfo;
	
	/**
	 * 操作人
	 */
	private String operator;
	
	/**
	 * 当前时间
	 */
	private Date currentTime;

	public T getRequest() {
		return request;
	}

	public void setRequest(T request) {
		this.request = request;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Date getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(Date currentTime) {
		this.currentTime = currentTime;
	}

	public SkuResponse getSkuInfo() {
		return skuInfo;
	}

	public void setSkuInfo(SkuResponse skuInfo) {
		this.skuInfo = skuInfo;
	}
}
