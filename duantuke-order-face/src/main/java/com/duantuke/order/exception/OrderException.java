package com.duantuke.order.exception;

import java.io.Serializable;

import com.duantuke.order.common.enums.OrderErrorEnum;

/**
 * 订单系统自定义异常
 * @author 须俊杰
 * @date 2016年6月8日
 */
public class OrderException extends RuntimeException implements Serializable {

	private static final long serialVersionUID = 1L;

	private String errorCode;
	private String errorMsg;

	public OrderException(String errorCode,String errorMsg) {
		super(errorMsg);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public OrderException() {

	}

	public OrderException(OrderErrorEnum errorEnum) {
		this(errorEnum.getErrorCode(), errorEnum.getErrorMsg());
	}

	public OrderErrorEnum getErrorEnum() {
		return OrderErrorEnum.findByCode(errorCode);
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
