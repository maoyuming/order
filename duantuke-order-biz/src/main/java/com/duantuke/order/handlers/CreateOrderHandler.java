package com.duantuke.order.handlers;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

/**
 * 创建订单处理器
 * 
 * @author 须俊杰
 * @since 1.0
 */
@Service
public class CreateOrderHandler {

	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private OrderDetailMapper orderDetailMapper;

	public void create(OrderContext<Request<CreateOrderRequest>> context) {
		// 构建订单信息
		Order order = buildOrder(context);
		
		// 保存订单主表
		orderMapper.insertSelective(order);

		// 构建订单明细
		List<OrderDetail> orderDetails = buildOrderDetail(order, context);

		// 保存订单明细
		orderDetailMapper.batchInsert(orderDetails);
		
		context.setOrder(order);
	}

	/**
	 * 验证请求参数
	 * 
	 * @param request
	 */
	public void validate(CreateOrderRequest request) {
		Order order = request.getOrder();

		// 验证订单主信息
		validateOrder(order);

		// 验证订单明细
		validateOrderDetail(order.getOrderDetails());
	}

	/**
	 * 验证订单主信息
	 * 
	 * @param order
	 */
	private void validateOrder(Order order) {
		if (order.getPayType() == null) {
			throw new OrderException(OrderErrorEnum.paramsError.getErrorCode(), "联系人不能为空");
		}
		if (StringUtils.isBlank(order.getContact())) {
			throw new OrderException(OrderErrorEnum.paramsError.getErrorCode(), "联系人不能为空");
		}
		if (StringUtils.isBlank(order.getContactPhone())) {
			throw new OrderException(OrderErrorEnum.paramsError.getErrorCode(), "联系人电话不能为空");
		}
	}

	/**
	 * 验证订单明细
	 * 
	 * @param orderDetail
	 */
	private void validateOrderDetail(List<OrderDetail> orderDetails) {
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
	}

	/**
	 * 计算订单总金额
	 * 
	 * @param orderDetails
	 * @return
	 */
	private BigDecimal calculateTotalPrice(List<OrderDetail> orderDetails) {
		BigDecimal totalPrice = BigDecimal.ZERO;
		for (OrderDetail orderDetail : orderDetails) {
			BigDecimal price = orderDetail.getPrice();
			BigDecimal num = new BigDecimal(orderDetail.getNum());
			BigDecimal totalPriceOfSku = price.multiply(num);
			totalPrice = totalPrice.add(totalPriceOfSku);
		}
		return totalPrice;
	}

	/**
	 * 构建订单信息
	 * 
	 * @param order
	 */
	private Order buildOrder(OrderContext<Request<CreateOrderRequest>> context) {
		Order order = context.getRequest().getData().getOrder();
		order.setType(OrderTypeEnum.common.getId());
		order.setStatus(OrderStatusEnum.toBeConfirmed.getId());
		order.setPayStatus(PayStatusEnum.waitForPayment.getId());
		order.setTotalPrice(calculateTotalPrice(order.getOrderDetails()));
		order.setCreateTime(context.getCurrentTime());
		order.setUpateTime(context.getCurrentTime());
		order.setCreateBy(context.getOperator());
		order.setUpdateBy(context.getOperator());
		
		return order;
	}

	/**
	 * 构建订单明细
	 * 
	 * @param order
	 */
	private List<OrderDetail> buildOrderDetail(Order order, OrderContext<Request<CreateOrderRequest>> context) {
		List<OrderDetail> orderDetails = order.getOrderDetails();
		for (OrderDetail orderDetail : orderDetails) {
			orderDetail.setOrderId(order.getId());
			orderDetail.setCreateTime(context.getCurrentTime());
			orderDetail.setCreateBy(context.getOperator());
			orderDetail.setUpdateTime(context.getCurrentTime());
			orderDetail.setUpdateBy(context.getOperator());
		}

		return orderDetails;
	}
}
