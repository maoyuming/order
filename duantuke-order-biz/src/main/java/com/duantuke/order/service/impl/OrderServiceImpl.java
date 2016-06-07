package com.duantuke.order.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duantuke.order.common.enums.OrderErrorEnum;
import com.duantuke.order.handlers.CreateOrderHandler;
import com.duantuke.order.model.CreateOrderRequest;
import com.duantuke.order.model.CreateOrderResponse;
import com.duantuke.order.model.Request;
import com.duantuke.order.model.Response;
import com.duantuke.order.service.OrderService;
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
			CreateOrderRequest createOrderRequest = request.getData();
			// 参数合法性校验
			if (createOrderHandler.validate(createOrderRequest)) {

			}
		} catch (Exception e) {
			logger.error("创建订单异常", e);
			response.setSuccess(false);
			response.setErrorCode(OrderErrorEnum.customError.getErrorCode());
			response.setErrorMessage(OrderErrorEnum.customError.getErrorMsg());
		}

		return response;
	}

}
