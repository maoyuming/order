package com.duantuke.order.common.enums;

/**
 * 订单状态
 * @author 须俊杰
 * @date 2016年6月8日
 */
public enum OrderStatusEnum {
	/**
	 * 待确认
	 */
	toBeConfirmed(30, "待确认"),
	/**
	 * 已确认
	 */
	confirmed(50, "已确认"),
	/**
	 * 入住
	 */
	checkedIn(100, "已入住"),
	/**
	 * 完成
	 */
	finished(200, "完成"),
	/**
	 * 用户未入住
	 */
	noshow(250, "用户未到店"),
	/**
	 * 渠道取消
	 */
	canceled(500, "取消");

	private final Integer id;
	private final String name;

	public Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	private OrderStatusEnum(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public static OrderStatusEnum getEnum(Integer id) {
		for (OrderStatusEnum temp : OrderStatusEnum.values()) {
			if (temp.getId().equals(id)) {
				return temp;
			}
		}
		return null;
	}
}
