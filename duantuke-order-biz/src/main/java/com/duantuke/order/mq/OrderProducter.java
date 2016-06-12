package com.duantuke.order.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mk.kafka.client.stereotype.MkMessageService;
import com.mk.kafka.client.stereotype.MkTopicProducer;

/**
 * 订单消息生产
 * 
 * @author 须俊杰
 * @date 2016年6月12日
 */
@MkMessageService
public class OrderProducter {

	private static final Logger logger = LoggerFactory.getLogger(OrderProducter.class);

	/**
	 * 订单消息
	 *
	 * @param message
	 */
	@MkTopicProducer(topic = "orderMessage", replicationFactor = 1, serializerClass = "com.mk.kafka.client.serializer.SerializerEncoder")
	public void sendMessage(String message) {
		logger.info("订单消息报文：{}", message);
	}
}
