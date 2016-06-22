package com.duantuke.order.common.enums;

/**
 * 业务类型，用作日志记录
 * 
 * @author 须俊杰
 * @date 2016年6月22日
 */
public enum BusinessTypeEnum {
	/**
	 * 客服（CS）备注
	 */
	CSREMARK("10", "客服备注"),
	/**
	 * 创建订单
	 */
	CREATE("100", "创建订单"),
	/**
	 * 确认订单
	 */
	CONFIRM("120", "确认订单"),
	/**
	 * 修改联系人
	 */
	CHANGE_CONTACTS("130", "订单修改"),
	/**
	 * 已入住
	 */
	CHECKIN("150", "已入住"),
	/**
	 * 已完成
	 */
	FINISHED("200", "已完成"),
	/**
	 * 未入住
	 */
	NOSHOW("220", "客户未到店"),
	/**
	 * 已结算
	 */
	SETTLE("300", "已结算"),
	/**
	 * 取消订单
	 */
	CANCEL("500", "取消订单");

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