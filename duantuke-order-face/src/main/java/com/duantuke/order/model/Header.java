package com.duantuke.order.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 请求头信息
 * 
 * @author 须俊杰
 * @date 2016年1月25日
 *
 */
public class Header implements Serializable {
	private static final long serialVersionUID = 4882140048672696514L;
	/**
	 * 时间
	 */
	private Date timeStamp;
	/**
	 * 授权信息
	 */
	private String token;

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
