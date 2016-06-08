package com.duantuke.order.common.enums;

/**
 * 订单类型
 * @author 须俊杰
 *
 */
public enum OrderTypeEnum {
	
	common(0, "一般订单");
	
	private final Integer id;
	private final String name;

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	private OrderTypeEnum(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public static OrderTypeEnum getOrderTypeEnum(Integer id) {
		for (OrderTypeEnum temp : OrderTypeEnum.values()) {
			if (temp.getId().equals(id)) {
				return temp;
			}
		}
		return null;
	}
}
