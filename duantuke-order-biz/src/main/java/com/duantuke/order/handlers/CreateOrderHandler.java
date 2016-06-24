package com.duantuke.order.handlers;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.duantuke.basic.enums.SkuTypeEnum;
import com.duantuke.basic.face.bean.RoomTypeInfo;
import com.duantuke.basic.face.bean.SkuInfo;
import com.duantuke.basic.face.bean.SkuResponse;
import com.duantuke.basic.po.Meal;
import com.duantuke.basic.po.Sale;
import com.duantuke.order.common.enums.OrderErrorEnum;
import com.duantuke.order.common.enums.OrderStatusEnum;
import com.duantuke.order.common.enums.OrderTypeEnum;
import com.duantuke.order.common.enums.PayStatusEnum;
import com.duantuke.order.exception.OrderException;
import com.duantuke.order.model.CreateOrderRequest;
import com.duantuke.order.model.Order;
import com.duantuke.order.model.OrderContext;
import com.duantuke.order.model.OrderDetail;
import com.duantuke.order.model.OrderDetailPrice;
import com.duantuke.order.model.Request;
import com.duantuke.order.utils.log.LogUtil;

/**
 * 创建订单处理器
 * 
 * @author 须俊杰
 * @since 1.0
 */
@Service
public class CreateOrderHandler extends AbstractOrderHandler {

	private static final LogUtil logger = new LogUtil(CreateOrderHandler.class);

	public void create(OrderContext<Request<CreateOrderRequest>> context) {
		logger.info("开始创建订单");

		// 构建订单信息
		Order order = buildOrder(context);
		// 保存订单主表
		orderMapper.insertSelective(order);

		// 保存订单明细
		saveOrderDetails(order, context);

		// 保存订单价格信息
		saveOrderDetailPrice(order, context);

		context.setOrder(order);
		logger.info("订单创建成功,orderId = {}", order.getId());
	}

	/**
	 * 验证请求参数
	 * 
	 * @param request
	 * @throws ParseException
	 */
	public void validate(CreateOrderRequest request, OrderContext<Request<CreateOrderRequest>> context)
			throws ParseException {
		logger.info("开始验证请求参数");
		Order order = request.getOrder();

		// 获取sku信息
		SkuResponse skuResponse = super.getSkuInfo(order);
		context.setSkuInfo(skuResponse);

		// 验证订单主信息
		validateOrder(order);

		// 验证订单明细
		validateOrderDetail(order.getOrderDetails(), context);
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
		if (order.getBeginTime() == null) {
			throw new OrderException(OrderErrorEnum.paramsError.getErrorCode(), "预抵时间不能为空");
		}
		if (order.getEndTime() == null) {
			throw new OrderException(OrderErrorEnum.paramsError.getErrorCode(), "预离时间不能为空");
		}
		logger.info("订单主信息验证通过");
	}

	/**
	 * 验证订单明细
	 * 
	 * @param orderDetail
	 * @throws ParseException
	 */
	@SuppressWarnings("rawtypes")
	private void validateOrderDetail(List<OrderDetail> orderDetails, OrderContext<Request<CreateOrderRequest>> context)
			throws ParseException {
		logger.info("开始验证订单明细");
		if (CollectionUtils.isEmpty(orderDetails)) {
			throw new OrderException(OrderErrorEnum.paramsError.getErrorCode(), "订单明细不能为空");
		}

		SkuResponse skuResponse = context.getSkuInfo();
		List<SkuInfo> skuInfoList = skuResponse.getList();
		BigDecimal totalPrice = BigDecimal.ZERO;
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

			// 验证订单价格
			logger.info("开始验证订单价格信息");
			List<OrderDetailPrice> priceList = orderDetail.getPriceDetails();
			if (priceList == null) {
				logger.error("Sku:{}缺少价格明细", orderDetail.getSkuId());
				throw new OrderException(OrderErrorEnum.orderPriceError.getErrorCode(),
						"Sku:" + orderDetail.getSkuId() + "缺少价格明细");
			}
			logger.info("Sku:{}下单传入的价格列表是:{}", orderDetail.getSkuId(), JSON.toJSONString(priceList));
			for (OrderDetailPrice orderDetailPrice : priceList) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String date = sdf.format(orderDetailPrice.getActionTime());
				for (SkuInfo<?> skuInfo : skuInfoList) {
					if (!skuInfo.getSkuId().equals(orderDetail.getSkuId())) {
						continue;
					}
					logger.info("验证Sku:{}的价格", skuInfo.getSkuId());
					// 房间模型
					if (skuInfo.getType().equals(SkuTypeEnum.roomtype.getCode())) {
						RoomTypeInfo roomTypeInfo = (RoomTypeInfo) skuInfo.getInfo();
						Map<String, BigDecimal> priceDetails = roomTypeInfo.getPrices();
						BigDecimal price = priceDetails.get(date);
						if (price == null) {
							throw new OrderException(OrderErrorEnum.orderPriceError.getErrorCode(),
									"日期" + date + "的价格不存在");
						}
						if (price.compareTo(orderDetailPrice.getPrice()) != 0) {
							logger.error("Sku:{}价格验证不通过", skuInfo.getSkuId());
							throw new OrderException(OrderErrorEnum.orderPriceError);
						}
					}

					// 餐饮模型
					if (skuInfo.getType().equals(SkuTypeEnum.meal.getCode())) {
						Meal meal = (Meal) skuInfo.getInfo();
						logger.info("Sku:{}的价格是{}", skuInfo.getSkuId(), meal.getPrice());
						if (meal.getPrice().compareTo(orderDetailPrice.getPrice()) != 0) {
							logger.error("Sku:{}价格验证不通过", skuInfo.getSkuId());
							throw new OrderException(OrderErrorEnum.orderPriceError);
						}
					}
				}
				totalPrice = totalPrice.add(orderDetailPrice.getPrice());
			}
			logger.info("Sku单价验证通过");
		}

		// 验证订单总价格
		logger.info("验证订单总价格,SkuService计算的结果是:{},订单下单传入的明细结算后的结果是:{}", skuResponse.getTotalPrice(), totalPrice);
		if (totalPrice.compareTo(skuResponse.getTotalPrice()) != 0) {
			logger.error("订单总价格验证不通过");
			throw new OrderException(OrderErrorEnum.orderPriceError);
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
			BigDecimal price = orderDetail.getTotalPrice();
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
		order.setCustomerId(Long.parseLong(context.getOperatorId()));
		order.setCreateTime(context.getCurrentTime());
		order.setCreateBy(formatOperator(context));

		// 获取sku信息
		SkuResponse skuResponse = super.getSkuInfo(order);
		context.setSkuInfo(skuResponse);
		SkuInfo<?> skuInfo = skuResponse.getList().get(0);

		order.setSupplierId(skuInfo.getSupplierId());
		order.setSupplierName(skuInfo.getSupplierName());
		order.setTotalPrice(skuResponse.getTotalPrice());

		logger.info("订单主信息构建完成,结果:{}", JSON.toJSONString(order));
		return order;
	}

	/**
	 * 保存订单明细
	 * 
	 * @param order
	 */
	private void saveOrderDetails(Order order, OrderContext<Request<CreateOrderRequest>> context) {
		logger.info("开始保存订单明细");
		List<OrderDetail> orderDetails = order.getOrderDetails();

		for (OrderDetail orderDetail : orderDetails) {
			orderDetail.setOrderId(order.getId());
			orderDetail.setCreateTime(context.getCurrentTime());
			orderDetail.setCreateBy(formatOperator(context));
			orderDetailMapper.insertSelective(orderDetail);
		}

		logger.info("订单明细保存完成");
	}

	/**
	 * 订单创建后，通过MQ消息，异步更新一些无需同步记录的信息<br>
	 * 主要是为了保证同步接口的执行效率<br>
	 * 
	 * @param order
	 */
	public Order updateOrderInfoAfterCreated(Order order) {
		logger.info("开始更新订单信息");
		// 获取销售信息
		Sale sales = getSalesInfo(order.getSupplierId());
		if (sales != null) {
			order.setSalesId(sales.getSalesId());
			order.setSalesName(sales.getMemberName());

			int result = orderMapper.updateOrderInfoAfterCreated(order);
			logger.info("订单信息更新完成,结果:{}", result);
		}
		return order;
	}

	/**
	 * 保存订单价格信息
	 * 
	 * @param order
	 * @param context
	 * @return
	 */
	private void saveOrderDetailPrice(Order order, OrderContext<Request<CreateOrderRequest>> context) {
		logger.info("开始保存订单价格信息");

		List<OrderDetail> orderDetails = order.getOrderDetails();
		for (OrderDetail orderDetail : orderDetails) {
			if (orderDetail.getPriceDetails() != null) {
				for (OrderDetailPrice orderDetailPrice : orderDetail.getPriceDetails()) {
					orderDetailPrice.setOrderId(order.getId());
					orderDetailPrice.setOrderDetailId(orderDetail.getId());
					orderDetailPrice.setSkuId(orderDetail.getSkuId());
					orderDetailPrice.setSkuName(orderDetail.getSkuName());
					orderDetailPrice.setCreateTime(context.getCurrentTime());
					orderDetailPrice.setCreateBy(formatOperator(context));
					orderDetailPriceMapper.insertSelective(orderDetailPrice);
				}
			}
		}

		logger.info("订单价格信息保存完成");
	}
}
