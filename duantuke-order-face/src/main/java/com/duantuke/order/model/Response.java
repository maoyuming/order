package com.duantuke.order.model;

import java.io.Serializable;

/**
 * 标准接口返回定义
 * 
 * @author 须俊杰
 * @date 2015年12月28日
 *
 */
public class Response<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7783475488658605915L;

	public Response() {
	}
	/**
	 * 是否成功 <br/>
	 * true:成功 false:失败
	 */
	private boolean success;
	/**
	 * 返回数据，json格式
	 */
	private T data;
	/**
	 * 错误码
	 */
	private String errorCode;
	/**
	 * 错误描述
	 */
	private String errorMessage;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}