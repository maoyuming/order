package com.duantuke.order.handlers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.duantuke.order.common.enums.OrderErrorEnum;
import com.duantuke.order.common.enums.OrderStatusEnum;
import com.duantuke.order.common.enums.PayStatusEnum;
import com.duantuke.order.exception.OrderException;
import com.duantuke.order.model.Base;
import com.duantuke.order.model.Order;
import com.duantuke.order.model.OrderContext;
import com.duantuke.order.model.Request;
import com.duantuke.order.utils.PropertyConfigurer;

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
		int result = orderMapper.updateOrderStatus(order);

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

	/**
	 * 订单自动完成<br>
	 * 此接口由订单完成worker触发，批量修改过了预离时间的订单，统一改成完成
	 * 
	 * @param context
	 * @return
	 */
	public List<Order> autoFinish(OrderContext<Request<Base>> context) {
		logger.info("开始处理可完成的订单");

		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式
		String endTime = sdf.format(now);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("endTime", endTime);
		logger.info("开始查询待完成的订单,参数:{}", JSON.toJSONString(params));
		List<Order> orders = orderMapper.queryOrdersByEndTime(params);
		logger.info("待完成的订单查询完成,结果:{}", JSON.toJSONString(orders));

		// 执行完成操作
		for (Order order : orders) {
			order.setStatus(OrderStatusEnum.finished.getId());
			order.setUpateTime(now);
			order.setUpdateBy(formatOperator(context));
			int result = orderMapper.updateOrderStatus(order);
			logger.info("订单[{}]执行完成操作,结果:{}", order.getId(), result);
		}

		logger.info("订单处理完成");
		return orders;
	}

	/**
	 * 支付完成更新订单
	 * 
	 * @param id
	 */
	public void updateOrderAfterPaid(Long id) {
		logger.info("准备执行更新操作");
		Order order = new Order();
		order.setId(id);
		order.setPayStatus(PayStatusEnum.paymentSuccess.getId());
		order.setUpateTime(new Date());
		order.setUpdateBy(PropertyConfigurer.getProperty("system"));

		logger.info("开始执行更新操作,参数:{}", JSON.toJSONString(order));
		int result = orderMapper.updateOrderAfterPaid(order);
		logger.info("更新完成,结果:{}", result);
	}
}
