package com.duantuke.order.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.duantuke.basic.face.bean.SkuRequest;
import com.duantuke.basic.face.bean.SkuResponse;
import com.duantuke.basic.face.bean.SkuSubRequest;
import com.duantuke.basic.face.service.CustomerService;
import com.duantuke.basic.face.service.SaleService;
import com.duantuke.basic.face.service.SkuService;
import com.duantuke.basic.po.Customer;
import com.duantuke.basic.po.Sale;
import com.duantuke.order.mappers.OrderDetailMapper;
import com.duantuke.order.mappers.OrderDetailPriceMapper;
import com.duantuke.order.mappers.OrderMapper;
import com.duantuke.order.model.Order;
import com.duantuke.order.model.OrderContext;
import com.duantuke.order.model.OrderDetail;
import com.duantuke.order.utils.log.LogUtil;

public abstract class AbstractOrderHandler {

	public static final LogUtil logger = new LogUtil(AbstractOrderHandler.class);
	@Autowired
	protected OrderMapper orderMapper;
	@Autowired
	protected OrderDetailMapper orderDetailMapper;
	@Autowired
	private SkuService skuService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private SaleService saleService;
	@Autowired
	protected OrderDetailPriceMapper orderDetailPriceMapper;

	/**
	 * 查询订单信息(只查询主表)
	 * 
	 * @param id
	 * @return
	 */
	protected Order getOrderById(Long id) {
		logger.info("开始查询订单信息");
		Order order = orderMapper.selectByPrimaryKey(id);
		logger.info("订单信息查询完成,结果:{}", JSON.toJSONString(order));
		return order;
	}

	/**
	 * 获取sku信息
	 * 
	 * @param order
	 * @return
	 */
	protected SkuResponse getSkuInfo(Order order) {
		logger.info("开始获取sku信息");
		List<OrderDetail> orderDetails = order.getOrderDetails();

		SkuRequest request = new SkuRequest();
		request.setBeginTime(order.getBeginTime());
		request.setEndTime(order.getEndTime());
		Map<Integer, List<SkuSubRequest>> skuMap = new HashMap<Integer, List<SkuSubRequest>>();
		for (OrderDetail orderDetail : orderDetails) {
			Long skuId = orderDetail.getSkuId();
			int num = orderDetail.getNum();
			Integer skuType = orderDetail.getSkuType();

			if (skuMap.containsKey(skuType)) {
				List<SkuSubRequest> list = skuMap.get(skuType);
				SkuSubRequest skuSubRequest = new SkuSubRequest();
				skuSubRequest.setSkuId(skuId);
				skuSubRequest.setNum(num);
				list.add(skuSubRequest);
			} else {
				List<SkuSubRequest> list = new ArrayList<SkuSubRequest>();
				SkuSubRequest skuSubRequest = new SkuSubRequest();
				skuSubRequest.setSkuId(skuId);
				skuSubRequest.setNum(num);
				list.add(skuSubRequest);
				skuMap.put(skuType, list);
			}
		}
		request.setSkuMap(skuMap);

		logger.info("开始调用SkuService,参数:{}", JSON.toJSONString(request));
		SkuResponse skuResponse = skuService.querySku(request);

		logger.info("sku信息获取完成,结果:{}", JSON.toJSONString(skuResponse));
		return skuResponse;
	}

	/**
	 * 格式化操作人信息
	 * 
	 * @param context
	 * @return
	 */
	protected String formatOperator(OrderContext<?> context) {
		return this.formatOperator(context.getOperatorId(), context.getOperatorName());
	}

	/**
	 * 格式化操作人信息
	 * 
	 * @param operatorId
	 * @param operatorName
	 * @return
	 */
	protected String formatOperator(String operatorId, String operatorName) {
		StringBuilder sb = new StringBuilder();
		sb.append(operatorId);
		if (StringUtils.isNotBlank(operatorName)) {
			sb.append("(");
			sb.append(operatorName);
			sb.append(")");
		}

		String result = sb.toString();
		return result.equals("null") ? null : result;
	}

	/**
	 * 获取销售信息
	 * 
	 * @param hotelId
	 * @return
	 */
	protected Sale getSalesInfo(Long hotelId) {
		logger.info("开始获取销售信息,hotelId = {}", hotelId);
		Sale sales = saleService.querySaleByHotelId(hotelId);
		logger.info("销售信息获取完成,结果 = {}", JSON.toJSONString(sales));
		return sales;
	}

	/**
	 * 获取用户信息
	 * 
	 * @param customerId
	 * @return
	 */
	protected Customer getCustomerById(Long customerId) {
		logger.info("开始获取用户信息,hotelId = {}", customerId);
		Customer customer = customerService.queryCustomerById(customerId);
		logger.info("用户信息获取完成,结果 = {}", JSON.toJSONString(customer));
		return customer;
	}
}
