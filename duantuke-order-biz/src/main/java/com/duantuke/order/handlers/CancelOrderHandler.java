package com.duantuke.order.handlers;

import org.springframework.stereotype.Service;

import com.duantuke.order.common.enums.OrderErrorEnum;
import com.duantuke.order.common.enums.OrderStatusEnum;
import com.duantuke.order.exception.OrderException;
import com.duantuke.order.model.CancelOrderRequest;
import com.duantuke.order.model.Order;
import com.duantuke.order.model.OrderContext;
import com.duantuke.order.model.Request;
import com.duantuke.order.utils.log.LogUtil;

/**
 * 取消订单处理器
 * 
 * @author 须俊杰
 * @date 2016年6月17日
 */
@Service
public class CancelOrderHandler extends AbstractOrderHandler {

	public static final LogUtil logger = new LogUtil(CancelOrderHandler.class);

	public void cancel(OrderContext<Request<CancelOrderRequest>> context) {
		logger.info("开始取消订单");

		// 业务合法性验证
		validate(context);

		// 执行取消操作
		doCancel(context);

		logger.info("取消订单完成");
	}

	/**
	 * 取消订单业务合法性验证
	 * 
	 * @param request
	 */
	public void validate(OrderContext<Request<CancelOrderRequest>> context) {
		logger.info("开始进行取消订单业务合法性验证");
		CancelOrderRequest request = context.getRequest().getData();

		Order order = super.getOrderById(request.getOrderId());
		context.setOrder(order);

		if (order.getStatus().equals(OrderStatusEnum.canceled.getId())) {
			logger.warn("订单已取消");
			throw new OrderException(OrderErrorEnum.orderCanceled);
		}

		logger.info("取消订单业务合法性验证通过");
	}

	/**
	 * 执行取消操作
	 * 
	 * @param context
	 */
	public void doCancel(OrderContext<Request<CancelOrderRequest>> context) {
		logger.info("开始执行取消操作");
		CancelOrderRequest cancelOrderRequest = context.getRequest().getData();
		Order order = context.getOrder();
		order.setStatus(OrderStatusEnum.canceled.getId());
		order.setCancelReason(cancelOrderRequest.getReason());
		order.setUpdateTime(context.getCurrentTime());
		order.setUpdateBy(formatOperator(context));

		int result = orderMapper.cancelOrder(order);

		logger.info("取消操作执行完成,返回值：{}", result);
	}
}
