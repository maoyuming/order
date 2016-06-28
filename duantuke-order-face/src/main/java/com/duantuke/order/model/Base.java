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
	 * 操作人id
	 */
	private String operatorId;
	/**
	 * 操作人名称
	 */
	private String operatorName;
	/**
	 * 备注
	 */
	private String remark;

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

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
