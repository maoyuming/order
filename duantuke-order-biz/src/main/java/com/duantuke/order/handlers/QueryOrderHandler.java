package com.duantuke.order.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.duantuke.order.common.enums.OrderErrorEnum;
import com.duantuke.order.exception.OrderException;
import com.duantuke.order.mappers.OrderMapper;
import com.duantuke.order.model.Order;
import com.duantuke.order.model.QueryOrderRequest;
import com.duantuke.order.utils.PropertyConfigurer;
import com.duantuke.order.utils.log.LogUtil;

/**
 * 查询订单处理器
 * 
 * @author 须俊杰
 * @date 2016年6月12日
 */
@Service
public class QueryOrderHandler {

	public static final LogUtil logger = new LogUtil(QueryOrderHandler.class);
	@Autowired
	private OrderMapper orderMapper;

	public List<Order> queryOrders(QueryOrderRequest request) {
		logger.info("开始查询订单");
		Map<String, Object> params = buildParameters(request);

		List<Order> orders = orderMapper.queryOrders(params);

		logger.info("订单查询成功");
		return orders;
	}

	/**
	 * 构建查询参数
	 * 
	 * @param request
	 * @return
	 */
	private Map<String, Object> buildParameters(QueryOrderRequest request) {
		logger.info("开始构建查询参数");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("supplierId", request.getSupplierId());
		params.put("orderType", request.getOrderType());
		params.put("orderStatus", request.getOrderStatus());
		params.put("payType", request.getPayType());
		params.put("payStatus", request.getPayStatus());
		params.put("contact", request.getContact());
		params.put("contactPhone", request.getContactPhone());
		params.put("customerId", request.getCustomerId());
		params.put("salesId", request.getSalesId());
		params.put("startDate", request.getStartDate());
		params.put("endDate", request.getEndDate());
		params.put("startNum", (request.getPageNo() - 1) * request.getPageSize());
		params.put("pageSize", request.getPageSize());
		logger.info("查询参数构建完成,参数明细:{}", JSON.toJSONString(params));
		return params;
	}

	/**
	 * 查询订单入参合法性验证
	 * 
	 * @param request
	 */
	public void validate(QueryOrderRequest request) {
		logger.info("开始验证请求参数");

		// 验证至少要有一个查询条件
		if (request == null || (request.getOrderId() == null && request.getSupplierId() == null
				&& request.getOrderType() == null && request.getOrderStatus() == null && request.getPayType() == null
				&& request.getPayStatus() == null && request.getContact() == null && request.getContactPhone() == null
				&& request.getCustomerId() == null && request.getSalesId() == null && request.getStartDate() == null
				&& request.getEndDate() == null)) {

			logger.error("至少填写一个查询条件");
			throw new OrderException(OrderErrorEnum.paramsError);
		}

		if (request.getPageNo() == null || request.getPageNo() == null) {
			logger.error("必须填写分页信息");
			throw new OrderException(OrderErrorEnum.paramsError.getErrorCode(), "必须填写分页信息");
		}

		int orderListPageNoStartIndex = Integer.parseInt(PropertyConfigurer.getProperty("orderListPageNoStartIndex"));
		if (request.getPageNo() < orderListPageNoStartIndex) {
			logger.error("PageNo必须大于等于" + orderListPageNoStartIndex);
			throw new OrderException(OrderErrorEnum.paramsError.getErrorCode(),
					"PageNo必须大于等于" + orderListPageNoStartIndex);
		}

		int orderListPageSize = Integer.parseInt(PropertyConfigurer.getProperty("orderListPageSize"));
		if (request.getPageSize() > orderListPageSize) {
			logger.error("PageSize必须小于等于" + orderListPageSize);
			throw new OrderException(OrderErrorEnum.paramsError.getErrorCode(), "PageSize必须小于等于" + orderListPageSize);
		}

		logger.info("请求参数验证通过");
	}

	/**
	 * 根据订单号查询订单信息，包含明细
	 * 
	 * @param orderId
	 * @return
	 */
	public Order queryOrderAndDetailsByOrderId(Long orderId) {
		logger.info("开始查询订单");
		Order order = orderMapper.selectOrderAndDetailsById(orderId);
		logger.info("订单查询完成");
		return order;
	}
}
