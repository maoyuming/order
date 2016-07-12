package com.duantuke.order.common.enums;

/**
 * 业务类型，用作日志记录
 * 
 * @author 须俊杰
 * @date 2016年6月22日
 */
public enum BusinessTypeEnum {
	
	CSREMARK("10", "客服备注"),
	CREATE("100", "创建订单"),
	CONFIRM("120", "确认订单"),
	CHANGE_CONTACTS("130", "订单修改"),
	CHECKIN("150", "已入住"),
	FINISHED("200", "已完成"),
	NOSHOW("220", "客户未到店"),
	SETTLE("300", "已结算"),
	CANCEL("500", "取消订单"),
	REFUNDED("520","已退款");

	private final String id;
	private final String name;

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	private BusinessTypeEnum(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public static BusinessTypeEnum getEnum(String id) {
		for (BusinessTypeEnum temp : BusinessTypeEnum.values()) {
			if (temp.getId().equals(id)) {
				return temp;
			}
		}
		return null;
	}
}