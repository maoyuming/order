package com.duantuke.order.handlers;

import java.util.List;

import org.springframework.stereotype.Service;

import com.duantuke.order.common.enums.OrderErrorEnum;
import com.duantuke.order.common.enums.OrderStatusEnum;
import com.duantuke.order.exception.OrderException;
import com.duantuke.order.model.Base;
import com.duantuke.order.model.Order;
import com.duantuke.order.model.OrderContext;
import com.duantuke.order.model.Request;

/**
 * 修改订单处理器
 * 
 * @author 须俊杰
 * @date 2016年6月18日
 */
@Service
public class UpdateOrderHandler extends AbstractOrderHandler {

	/**
	 * 订单确认
	 * 
	 * @param context
	 */
	public void confirm(OrderContext<Request<Base>> context) {
		logger.info("开始确认订单");

		// 业务合法性校验
		validate(context);

		// 执行修改操作
		Order order = context.getOrder();
		order.setStatus(OrderStatusEnum.confirmed.getId());
		order.setUpateTime(context.getCurrentTime());
		order.setUpdateBy(formatOperator(context));
		int result = orderMapper.confirmOrder(order);

		logger.info("订单确认完成,结果:{}", result);
	}

	/**
	 * 确认订单业务合法性验证
	 * 
	 * @param request
	 */
	public void validate(OrderContext<Request<Base>> context) {
		logger.info("开始进行确认订单业务合法性验证");
		Base base = context.getRequest().getData();

		Order order = super.getOrderById(base.getOrderId());
		context.setOrder(order);

		if (order.getStatus().equals(OrderStatusEnum.confirmed.getId())) {
			logger.warn("订单已确认");
			throw new OrderException(OrderErrorEnum.orderConfirmed);
		}

		logger.info("确认订单业务合法性验证通过");
	}
	
	public List<Order> finish(){
		return null;
	}
}
