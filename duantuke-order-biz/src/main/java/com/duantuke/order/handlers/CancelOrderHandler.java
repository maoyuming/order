package com.duantuke.order.handlers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.duantuke.order.common.enums.CancelTypeEnum;
import com.duantuke.order.common.enums.OrderErrorEnum;
import com.duantuke.order.common.enums.OrderStatusEnum;
import com.duantuke.order.common.enums.PayStatusEnum;
import com.duantuke.order.exception.OrderException;
import com.duantuke.order.model.CancelOrderRequest;
import com.duantuke.order.model.Order;
import com.duantuke.order.model.OrderContext;
import com.duantuke.order.model.Request;
import com.duantuke.order.utils.PropertyConfigurer;
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

		// 目前限制到了预抵时间当天的已确认订单不允许取消
		Date beginDate = order.getBeginTime();
		Date now = context.getCurrentTime();
		if (order.getStatus().equals(OrderStatusEnum.confirmed.getId()) && now.after(beginDate)
				&& CancelTypeEnum.common.getId().equals(request.getCancelType())) {
			logger.error("订单已确认且已到预抵日期，不能取消");
			throw new OrderException(OrderErrorEnum.orderCanNotBeCanceled.getErrorCode(),
					"订单已确认且已到预抵日期，不能取消，如有疑问请联系客服!");
		}

		if (order.getStatus().equals(OrderStatusEnum.finished.getId())) {
			logger.error("订单已完成，不能取消");
			throw new OrderException(OrderErrorEnum.orderFinished);
		}

		logger.info("取消订单业务合法性验证通过");
	}

	/**
	 * 执行取消操作
	 * 
	 * @param context
	 */
	public void doCancel(OrderContext<Request<CancelOrderRequest>> context) {
		logger.info("准备取消参数");
		CancelOrderRequest cancelOrderRequest = context.getRequest().getData();
		Order order = context.getOrder();
		order.setCancelReason(cancelOrderRequest.getReason());
		order.setUpdateTime(context.getCurrentTime());
		order.setUpdateBy(formatOperator(context));
		this.doCancel(order);
	}

	/**
	 * 执行取消操作
	 * 
	 * @param context
	 */
	public void doCancel(Order order) {
		logger.info("开始执行取消操作");
		order.setStatus(OrderStatusEnum.canceled.getId());

		int result = orderMapper.cancelOrder(order);
		logger.info("取消操作执行完成,返回值：{}", result);
	}

	/**
	 * 自动取消订单
	 * 
	 * @param context
	 * @return
	 */
	public List<Order> autoCancel(OrderContext<Request<CancelOrderRequest>> context) {
		logger.info("开始处理需要取消的订单");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("time", Integer.parseInt(PropertyConfigurer.getProperty("autoCancelInterval")));
		params.put("status", OrderStatusEnum.initial.getId());
		params.put("payStatus", PayStatusEnum.waitForPayment.getId());
		logger.info("开始查询待取消的订单,参数:{}", JSON.toJSONString(params));
		List<Order> orders = orderMapper.queryOrdersToCancel(params);
		logger.info("待取消的订单查询完成,结果:{}", JSON.toJSONString(orders));

		// 执行取消操作
		for (Order order : orders) {
			order.setCancelReason("系统自动取消");
			order.setUpdateTime(context.getCurrentTime());
			order.setUpdateBy(formatOperator(context));
			this.doCancel(order);
		}

		logger.info("订单处理完成,共取消订单数:{}", orders.size());
		return orders;
	}
}
