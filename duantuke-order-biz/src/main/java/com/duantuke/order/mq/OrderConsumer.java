package com.duantuke.order.mq;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.duantuke.order.common.enums.BusinessTypeEnum;
import com.duantuke.order.exception.OrderException;
import com.duantuke.order.handlers.CreateOrderHandler;
import com.duantuke.order.handlers.UpdateOrderHandler;
import com.duantuke.order.model.Message;
import com.duantuke.order.model.Order;
import com.duantuke.order.utils.PropertyConfigurer;
import com.duantuke.order.utils.log.LogUtil;
import com.mk.kafka.client.exception.KafkaMessageConsumeException;
import com.mk.kafka.client.stereotype.MkMessageService;
import com.mk.kafka.client.stereotype.MkTopicConsumer;

/**
 * 订单消费者
 * 
 * @author 须俊杰
 * @date 2016年6月22日
 */
@MkMessageService
public class OrderConsumer {

	private static final LogUtil logger = new LogUtil(OrderConsumer.class);
	@Autowired
	private CreateOrderHandler createOrderHandler;
	@Autowired
	private UpdateOrderHandler updateOrderHandler;
	@Autowired
	private OrderProducter orderProducter;

	/**
	 * 接收订单创建中的消息
	 * 
	 * @param message
	 */
	@MkTopicConsumer(topic = "order_creating", group = "OrderGroup", serializerClass = "com.mk.kafka.client.serializer.StringDecoder")
	public void updateOrderAfterCreated(String message) {
		try {
			logger.info("接收到创建中的订单消息,报文:{}", message);
			if (StringUtils.isNotBlank(message)) {
				Message m = JSON.parseObject(message, Message.class);

				Order order = createOrderHandler.updateOrderInfoAfterCreated(m.getOrder());

				logger.info("订单创建后信息更新完成,开始发送订单创建消息");

				// 发送创建订单MQ消息
				Message orderCreatedMessage = new Message();
				orderCreatedMessage.setOrder(order);
				orderProducter.sendCreatedMessage(JSON.toJSONString(orderCreatedMessage));
				logger.info("订单创建消息发送成功");
			}
		} catch (OrderException e) {
			logger.info("消费order_creating消息异常", e);
		} catch (Exception ex) {
			logger.info("消费order_creating消息异常", ex);
			throw new KafkaMessageConsumeException(ex);
		}
	}

	/**
	 * 订单支付完成消息，更新订单信息
	 */
	@MkTopicConsumer(topic = "sc_pay_success_topic", group = "OrderGroup", serializerClass = "com.mk.kafka.client.serializer.StringDecoder")
	public void updateOrderAfterPaid(String message) {
		try {
			logger.info("接收到支付完成消息,报文:{}", message);
			if (StringUtils.isNotBlank(message)) {
				int index = message.indexOf(":");
				String orderIdStr = message.substring(index + 1, message.length() - 1);
				long orderId = Long.parseLong(orderIdStr);
				updateOrderHandler.updateOrderAfterPaid(orderId);
				
				// 纪录业务日志
				updateOrderHandler.saveBusinessLog(orderId, BusinessTypeEnum.PAID, "订单已支付", PropertyConfigurer.getProperty("system"));
			}
		} catch (OrderException e) {
			logger.info("消费sc_pay_success_topic消息异常", e);
		} catch (Exception ex) {
			logger.info("消费sc_pay_success_topic消息异常", ex);
			throw new KafkaMessageConsumeException(ex);
		}
		logger.info("支付完成消息消费完成");
	}

	/**
	 * 订单已退款消息，更新订单信息
	 */
	@MkTopicConsumer(topic = "sc_refund_success_topic", group = "OrderGroup", serializerClass = "com.mk.kafka.client.serializer.StringDecoder")
	public void updateOrderAfterRefunded(String message) {
		try {
			logger.info("接收到已退款消息,报文:{}", message);
			if (StringUtils.isNotBlank(message)) {
				int index = message.indexOf(":");
				String orderIdStr = message.substring(index + 1, message.length() - 1);
				long orderId = Long.parseLong(orderIdStr);
				updateOrderHandler.updateOrderAfterRefunded(orderId);

				// 纪录业务日志
				updateOrderHandler.saveBusinessLog(orderId, BusinessTypeEnum.REFUNDED, "订单已退款",
						PropertyConfigurer.getProperty("system"));
			}
		} catch (OrderException e) {
			logger.info("消费sc_refund_success_topic消息异常", e);
		} catch (Exception ex) {
			logger.info("消费sc_refund_success_topic消息异常", ex);
			throw new KafkaMessageConsumeException(ex);
		}
		logger.info("已退款消息消费完成");
	}
}
