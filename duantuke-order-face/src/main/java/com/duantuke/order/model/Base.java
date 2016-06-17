package com.duantuke.order.model;

import java.io.Serializable;

/**
 * 请求参数基础模型
 *
 * @author 须俊杰
 * @since v1.0
 */
public class Base implements Serializable {

	private static final long serialVersionUID = 4377893284204298358L;
	/**
	 * 订单id
	 */
	private Long orderId;
	/**
	 * 供应商
	 */
	private Long supplierId;
	/**
	 * C端用户id
	 */
	private Long customerId;

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
}
