package com.duantuke.order.model;

import java.util.Date;

/**
 * 查询订单请求参数
 * 
 * @author 须俊杰
 * @date 2016年6月13日
 */
public class QueryOrderRequest extends Base {

	private static final long serialVersionUID = 1L;

	/**
	 * 订单类型
	 */
	private Integer orderType;

	/**
	 * 订单状态
	 */
	private Integer[] orderStatus;

	/**
	 * 支付类型
	 */
	private Integer payType;

	/**
	 * 支付状态
	 */
	private Integer payStatus;

	/**
	 * 联系人
	 */
	private String contact;

	/**
	 * 联系人电话
	 */
	private String contactPhone;

	/**
	 * C端用户id
	 */
	private Long customerId;

	/**
	 * 销售id
	 */
	private Long salesId;

	/**
	 * 开始时间
	 */
	private Date startDate;
	
	/**
	 * 结束时间
	 */
	private Date endDate;
	
	/**
	 * 页数
	 */
	private Integer pageNo;
	/**
	 * 每页条数
	 */
	private Integer pageSize;

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Integer[] getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer[] orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Integer getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Long getSalesId() {
		return salesId;
	}

	public void setSalesId(Long salesId) {
		this.salesId = salesId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	
}
