package com.duantuke.order.model;

import java.io.Serializable;

/**
 * 标准接口请求定义
 * 
 * @author 须俊杰
 * @date 2016年1月26日
 *
 */
public class Request<T> implements Serializable {

	private static final long serialVersionUID = 1547857359048613213L;
	/**
	 * 请求头信息
	 */
	private Header header;
	/**
	 * 业务参数
	 */
	private T data;

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
