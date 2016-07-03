package com.duantuke.order.common.enums;

/**
 * 支付状态
 * @author 须俊杰
 * @date 2016年6月8日
 */
public enum PayStatusEnum {
	noNeedToPay(10, "无需支付"),
	waitForPayment(30, "等待支付"),
	paymentSuccess(50, "支付成功"),
	paymentFailure(70, "支付失败"),
	refunded(90, "已退款");

	private final Integer id;
	private final String name;

	private PayStatusEnum(Integer id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static PayStatusEnum getPayStatusEnum(Integer id) {
		for (PayStatusEnum temp : PayStatusEnum.values()) {
			if (temp.getId().equals(id)) {
				return temp;
			}
		}
		return null;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return id.toString();
	}
}
