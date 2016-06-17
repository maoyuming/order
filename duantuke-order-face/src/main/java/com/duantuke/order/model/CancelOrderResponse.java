package com.duantuke.order.model;

/**
 * 取消订单返回参数
 * 
 * @author 须俊杰
 * @date 2016年6月17日
 */
public class CancelOrderResponse extends Base{

	private static final long serialVersionUID = 1L;
	private Integer orderStatus;

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
}
