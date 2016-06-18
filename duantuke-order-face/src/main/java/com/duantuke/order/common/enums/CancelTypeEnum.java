package com.duantuke.order.common.enums;

public enum CancelTypeEnum {

	common(0, "普通取消"),
	compulsory(10,"强制取消");
	
	private final Integer id;
	private final String name;

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	private CancelTypeEnum(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public static CancelTypeEnum getCancelTypeEnum(Integer id) {
		for (CancelTypeEnum temp : CancelTypeEnum.values()) {
			if (temp.getId().equals(id)) {
				return temp;
			}
		}
		return null;
	}
}
