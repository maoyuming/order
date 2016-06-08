package com.duantuke.order.common.enums;

/**
 * 支付类型
 * @author 须俊杰
 * 
 */
public enum PayTypeEnum {
	prepay(100, "预付"),
	cashPayment(200, "现付");

	private final Integer id;
	private final String name;

	private PayTypeEnum(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public static PayTypeEnum getPayTypeEnum(Integer id) {
		for (PayTypeEnum temp : PayTypeEnum.values()) {
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
