package com.duantuke.order.handlers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.duantuke.basic.face.bean.SkuRequest;
import com.duantuke.basic.face.bean.SkuResponse;
import com.duantuke.basic.face.service.SkuService;
import com.duantuke.order.common.enums.OrderErrorEnum;
import com.duantuke.order.common.enums.OrderStatusEnum;
import com.duantuke.order.common.enums.OrderTypeEnum;
import com.duantuke.order.common.enums.PayStatusEnum;
import com.duantuke.order.exception.OrderException;
import com.duantuke.order.mappers.OrderDetailMapper;
import com.duantuke.order.mappers.OrderMapper;
import com.duantuke.order.model.CreateOrderRequest;
import com.duantuke.order.model.Order;
import com.duantuke.order.model.OrderContext;
import com.duantuke.order.model.OrderDetail;
import com.duantuke.order.model.Request;
import com.duantuke.order.utils.log.LogUtil;

/**
 * 创建订单处理器
 * 
 * @author 须俊杰
 * @since 1.0
 */
@Service
public class CreateOrderHandler {

	private static final LogUtil logger = new LogUtil(CreateOrderHandler.class);
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private OrderDetailMapper orderDetailMapper;
	@Autowired
	private SkuService skuService;

	public void create(OrderContext<Request<CreateOrderRequest>> context) {
		logger.info("开始创建订单");

		// 构建订单信息
		Order order = buildOrder(context);

		// 保存订单主表
		orderMapper.insertSelective(order);

		// 构建订单明细
		List<OrderDetail> orderDetails = buildOrderDetail(order, context);

		// 保存订单明细
		orderDetailMapper.batchInsert(orderDetails);

		context.setOrder(order);
		logger.info("订单创建成功,orderId = {}", order.getId());
	}

	/**
	 * 验证请求参数
	 * 
	 * @param request
	 */
	public void validate(CreateOrderRequest request) {
		logger.info("开始验证请求参数");
		Order order = request.getOrder();

		// 验证订单主信息
		validateOrder(order);

		// 验证订单明细
		validateOrderDetail(order.getOrderDetails());
		logger.info("请求参数验证通过");
	}

	/**
	 * 验证订单主信息
	 * 
	 * @param order
	 */
	private void validateOrder(Order order) {
		logger.info("开始验证订单主信息");
		if (order.getPayType() == null) {
			throw new OrderException(OrderErrorEnum.paramsError.getErrorCode(), "联系人不能为空");
		}
		if (StringUtils.isBlank(order.getContact())) {
			throw new OrderException(OrderErrorEnum.paramsError.getErrorCode(), "联系人不能为空");
		}
		if (StringUtils.isBlank(order.getContactPhone())) {
			throw new OrderException(OrderErrorEnum.paramsError.getErrorCode(), "联系人电话不能为空");
		}
		logger.info("订单主信息验证通过");
	}

	/**
	 * 验证订单明细
	 * 
	 * @param orderDetail
	 */
	private void validateOrderDetail(List<OrderDetail> orderDetails) {
		logger.info("开始验证订单明细");
		if (CollectionUtils.isEmpty(orderDetails)) {
			throw new OrderException(OrderErrorEnum.paramsError.getErrorCode(), "订单明细不能为空");
		}

		for (OrderDetail orderDetail : orderDetails) {
			if (orderDetail.getSkuId() == null) {
				throw new OrderException(OrderErrorEnum.paramsError.getErrorCode(), "SkuId不能为空");
			}
			if (StringUtils.isBlank(orderDetail.getSkuName())) {
				throw new OrderException(OrderErrorEnum.paramsError.getErrorCode(), "Sku名称不能为空");
			}
			if (orderDetail.getSkuType() == null) {
				throw new OrderException(OrderErrorEnum.paramsError.getErrorCode(), "Sku类型不能为空");
			}
			if (orderDetail.getNum() == null) {
				throw new OrderException(OrderErrorEnum.paramsError.getErrorCode(), "数量不能为空");
			}
			if (orderDetail.getPrice() == null) {
				throw new OrderException(OrderErrorEnum.paramsError.getErrorCode(), "单价不能为空");
			}
			if (orderDetail.getBeginTime() == null) {
				throw new OrderException(OrderErrorEnum.paramsError.getErrorCode(), "预抵时间不能为空");
			}
			if (orderDetail.getEndTime() == null) {
				throw new OrderException(OrderErrorEnum.paramsError.getErrorCode(), "预离时间不能为空");
			}
		}

		logger.info("订单明细验证通过");
	}

	/**
	 * 计算订单总金额
	 * 
	 * @param orderDetails
	 * @return
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private BigDecimal calculateTotalPrice(List<OrderDetail> orderDetails) {
		logger.info("开始计算订单总金额");
		BigDecimal totalPrice = BigDecimal.ZERO;
		for (OrderDetail orderDetail : orderDetails) {
			BigDecimal price = orderDetail.getPrice();
			BigDecimal num = new BigDecimal(orderDetail.getNum());
			BigDecimal totalPriceOfSku = price.multiply(num);
			totalPrice = totalPrice.add(totalPriceOfSku);
		}

		logger.info("订单总金额计算完成,totalPrice = {}", totalPrice);
		return totalPrice;
	}

	/**
	 * 构建订单信息
	 * 
	 * @param order
	 */
	private Order buildOrder(OrderContext<Request<CreateOrderRequest>> context) {
		logger.info("开始构建订单主信息");
		Order order = context.getRequest().getData().getOrder();
		order.setType(OrderTypeEnum.common.getId());
		order.setStatus(OrderStatusEnum.toBeConfirmed.getId());
		order.setPayStatus(PayStatusEnum.waitForPayment.getId());
		order.setCreateTime(context.getCurrentTime());
		order.setCreateBy(String.valueOf(order.getCustomerId()));

		// 获取sku信息
		SkuResponse skuResponse = getSkuInfo(order);
		context.setSkuInfo(skuResponse);

//		order.setSupplierId(skuResponse.getSupplierId());
//		order.setSupplierName(skuResponse.getSupplierName());
		order.setTotalPrice(skuResponse.getTotalPrice());

		logger.info("订单主信息构建完成");
		return order;
	}

	/**
	 * 构建订单明细
	 * 
	 * @param order
	 */
	private List<OrderDetail> buildOrderDetail(Order order, OrderContext<Request<CreateOrderRequest>> context) {
		logger.info("开始构建订单明细");
		List<OrderDetail> orderDetails = order.getOrderDetails();
		for (OrderDetail orderDetail : orderDetails) {
			orderDetail.setOrderId(order.getId());
			orderDetail.setCreateTime(context.getCurrentTime());
			orderDetail.setCreateBy(context.getOperator());
			orderDetail.setUpdateTime(context.getCurrentTime());
			orderDetail.setUpdateBy(context.getOperator());
		}

		logger.info("订单明细构建完成");
		return orderDetails;
	}

	/**
	 * 获取sku信息
	 * 
	 * @param order
	 * @return
	 */
	private SkuResponse getSkuInfo(Order order) {
		logger.info("开始获取sku信息");
		List<OrderDetail> orderDetails = order.getOrderDetails();

		SkuRequest request = new SkuRequest();
		Map<Integer, List<Long>> skuMap = new HashMap<Integer, List<Long>>();
		for (OrderDetail orderDetail : orderDetails) {
			Long skuId = orderDetail.getSkuId();
			Integer skuType = orderDetail.getSkuType();
			Date beginTime = orderDetail.getBeginTime();
			Date endTime = orderDetail.getEndTime();

			if (skuMap.containsKey(skuType)) {
				List<Long> skus = skuMap.get(skuType);
				skus.add(skuId);
			} else {
				List<Long> skus = new ArrayList<Long>();
				skus.add(skuId);
				skuMap.put(skuType, skus);
			}

			// 目前逻辑暂定所有sku预抵时间和预离时间都相同，支取一个即可
			if (request.getBeginTime() == null && request.getEndTime() == null && beginTime != null
					&& endTime != null) {
				 request.setBeginTime(beginTime);
				 request.setEndTime(endTime);
			}
		}
		request.setSkuMap(skuMap);

		logger.info("开始调用SkuService,参数:{}", JSON.toJSONString(request));
		SkuResponse skuResponse = skuService.querySku(request);

		logger.info("sku信息获取完成,结果:{}", JSON.toJSONString(skuResponse));
		return skuResponse;
	}
}
