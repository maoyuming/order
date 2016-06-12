package com.duantuke.order.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.duantuke.order.common.enums.OrderErrorEnum;
import com.duantuke.order.exception.OrderException;
import com.duantuke.order.handlers.CreateOrderHandler;
import com.duantuke.order.model.CreateOrderRequest;
import com.duantuke.order.model.CreateOrderResponse;
import com.duantuke.order.model.OrderContext;
import com.duantuke.order.model.Request;
import com.duantuke.order.model.Response;
import com.duantuke.order.service.OrderService;
import com.duantuke.order.utils.PropertyConfigurer;
import com.duantuke.order.utils.log.LogUtil;

@Service
public class OrderServiceImpl implements OrderService {

	public static final LogUtil logger = new LogUtil(OrderServiceImpl.class);
	@Autowired
	private CreateOrderHandler createOrderHandler;

	@Override
	public Response<CreateOrderResponse> createOrder(Request<CreateOrderRequest> request) {

		Response<CreateOrderResponse> response = new Response<CreateOrderResponse>();
		try {
			logger.info("创建订单入参:{}", JSON.toJSONString(request));

			CreateOrderRequest createOrderRequest = request.getData();
			// 参数合法性校验
			createOrderHandler.validate(createOrderRequest);
			
			// 构建订单上下文
			OrderContext<Request<CreateOrderRequest>> context = new OrderContext<Request<CreateOrderRequest>>();
			context.setRequest(request);
			context.setCurrentTime(new Date());
			context.setOperator(PropertyConfigurer.getProperty("system"));
			
			// 开始创建订单
			createOrderHandler.create(context);
			
			// 封装返回信息
			CreateOrderResponse createOrderResponse = new CreateOrderResponse();
			createOrderResponse.setOrder(context.getOrder());
			response.setSuccess(true);
			response.setData(createOrderResponse);
		} catch (OrderException e) {
			logger.error("创建订单异常", e);
			response.setSuccess(false);
			response.setErrorCode(e.getErrorCode());
			response.setErrorMessage(e.getErrorMsg());
		} catch (Exception ex) {
			logger.error("创建订单异常", ex);
			response.setSuccess(false);
			response.setErrorCode(OrderErrorEnum.customError.getErrorCode());
			response.setErrorMessage(OrderErrorEnum.customError.getErrorMsg());
		}

		logger.info("订单创建成功,返回值:{}", JSON.toJSONString(response));
		return response;
	}

}
