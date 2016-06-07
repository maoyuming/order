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
	 * 酒店id1070182
	 */
	private Long hotelId;
	
	/**
	 * 酒店名称
	 */
	private String hotelName;
	
	
	public Long getHotelId() {
		return hotelId;
	}

	public void setHotelId(Long hotelId) {
		this.hotelId = hotelId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	/**
	 * @return the hotelName
	 */
	public String getHotelName() {
		return hotelName;
	}

	/**
	 * @param hotelName the hotelName to set
	 */
	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	
}
