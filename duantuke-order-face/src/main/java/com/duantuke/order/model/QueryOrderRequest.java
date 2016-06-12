package com.duantuke.order.model;

import java.util.Date;

public class QueryOrderRequest extends Base{

	private static final long serialVersionUID = 1L;

	private Integer type;

	private Integer status;

	private Integer payType;

	private Integer payStatus;

	private String contact;

	private String contactPhone;

	private Long customerId;

	private String sales;

	private Date createTime;
}
