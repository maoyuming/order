package com.duantuke.order.model;

import com.duantuke.order.common.enums.CancelTypeEnum;

/**
 * 取消订单请求参数
 * 
 * @author 须俊杰
 * @date 2016年6月17日
 */
public class CancelOrderRequest extends Base {

	private static final long serialVersionUID = 1L;

	/**
	 * 取消理由
	 */
	private String reason;
	/**
	 * 取消类型
	 */
	private CancelTypeEnum cancelTypeEnum;

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public CancelTypeEnum getCancelTypeEnum() {
		return cancelTypeEnum;
	}

	public void setCancelTypeEnum(CancelTypeEnum cancelTypeEnum) {
		this.cancelTypeEnum = cancelTypeEnum;
	}
}
