package com.duantuke.order.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.duantuke.order.common.enums.OrderErrorEnum;
import com.duantuke.order.exception.OrderException;
import com.duantuke.order.handlers.CancelOrderHandler;
import com.duantuke.order.handlers.CreateOrderHandler;
import com.duantuke.order.handlers.QueryOrderHandler;
import com.duantuke.order.model.Base;
import com.duantuke.order.model.CancelOrderRequest;
import com.duantuke.order.model.CancelOrderResponse;
import com.duantuke.order.model.CreateOrderRequest;
import com.duantuke.order.model.CreateOrderResponse;
import com.duantuke.order.model.Message;
import com.duantuke.order.model.Order;
import com.duantuke.order.model.OrderContext;
import com.duantuke.order.model.QueryOrderRequest;
import com.duantuke.order.model.Request;
import com.duantuke.order.model.Response;
import com.duantuke.order.mq.OrderProducter;
import com.duantuke.order.service.OrderService;
import com.duantuke.order.utils.log.LogUtil;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

	public static final LogUtil logger = new LogUtil(OrderServiceImpl.class);
	@Autowired
	private CreateOrderHandler createOrderHandler;
	@Autowired
	private QueryOrderHandler queryOrderHandler;
	@Autowired
	private CancelOrderHandler cancelOrderHandler;
	@Autowired
	private OrderProducter orderProducter;

	@Override
	public Response<CreateOrderResponse> create(Request<CreateOrderRequest> request) {

		Response<CreateOrderResponse> response = new Response<CreateOrderResponse>();
		try {
			logger.info("接收到订单创建请求,入参:{}", JSON.toJSONString(request));

			CreateOrderRequest createOrderRequest = request.getData();
			// 参数合法性校验
			createOrderHandler.validate(createOrderRequest);

			// 初始化订单上下文
			OrderContext<Request<CreateOrderRequest>> context = new OrderContext<Request<CreateOrderRequest>>();
			context.setRequest(request);
			context.setCurrentTime(new Date());

			// 开始创建订单
			createOrderHandler.create(context);

			// 发送消息
			orderProducter.sendMessage(buildMessage(context.getOrder()));

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

		logger.info("订单创建全部完成,返回值:{}", JSON.toJSONString(response));
		return response;
	}

	/**
	 * 构建消息报文
	 * 
	 * @param order
	 * @return
	 */
	private String buildMessage(Order order) {
		logger.info("开始构建订单消息");
		Message message = new Message();
		message.setOrder(order);

		logger.info("订单消息构建完成");
		return JSON.toJSONString(message);
	}

	@Override
	public Response<List<Order>> queryOrders(Request<QueryOrderRequest> request) {

		Response<List<Order>> response = new Response<List<Order>>();
		try {
			logger.info("接收到订单查询请求,入参:{}", JSON.toJSONString(request));
			// 参数合法性校验
			QueryOrderRequest queryOrderRequest = request.getData();
			queryOrderHandler.validate(queryOrderRequest);

			// 执行查询操作
			List<Order> orders = queryOrderHandler.queryOrders(queryOrderRequest);

			// 封装返回信息
			response.setSuccess(true);
			response.setData(orders);
		} catch (OrderException e) {
			logger.error("查询订单异常", e);
			response.setSuccess(false);
			response.setErrorCode(e.getErrorCode());
			response.setErrorMessage(e.getErrorMsg());
		} catch (Exception ex) {
			logger.error("查询订单异常", ex);
			response.setSuccess(false);
			response.setErrorCode(OrderErrorEnum.customError.getErrorCode());
			response.setErrorMessage(OrderErrorEnum.customError.getErrorMsg());
		}

		logger.info("订单查询全部完成,返回值:{}", JSON.toJSONString(response));
		return response;
	}

	@Override
	public Response<Order> queryOrderByOrderId(Request<Base> request) {
		Response<Order> response = new Response<Order>();
		try {
			logger.info("接收到订单明细查询请求,入参:{}", JSON.toJSONString(request));
			// 参数合法性校验
			Base base = request.getData();
			if (base == null || base.getOrderId() == null || base.getOrderId() < 1) {
				throw new OrderException(OrderErrorEnum.paramsError);
			}

			// 执行查询操作
			Order order = queryOrderHandler.queryOrderAndDetailsByOrderId(base.getOrderId());
			// 封装返回信息
			response.setSuccess(true);
			response.setData(order);
		} catch (OrderException e) {
			logger.error("查询订单明细异常", e);
			response.setSuccess(false);
			response.setErrorCode(e.getErrorCode());
			response.setErrorMessage(e.getErrorMsg());
		} catch (Exception ex) {
			logger.error("查询订单明细异常", ex);
			response.setSuccess(false);
			response.setErrorCode(OrderErrorEnum.customError.getErrorCode());
			response.setErrorMessage(OrderErrorEnum.customError.getErrorMsg());
		}
		return response;
	}

	@Override
	public Response<CancelOrderResponse> cancel(Request<CancelOrderRequest> request) {
		Response<CancelOrderResponse> response = new Response<CancelOrderResponse>();
		// 初始化上下文
		OrderContext<Request<CancelOrderRequest>> context = new OrderContext<Request<CancelOrderRequest>>();
		try {
			logger.info("接收到取消订单请求,入参:{}", JSON.toJSONString(request));
			// 参数合法性校验
			CancelOrderRequest cancelOrderRequest = request.getData();
			if (cancelOrderRequest == null || cancelOrderRequest.getOrderId() == null
					|| cancelOrderRequest.getOrderId() < 1) {
				throw new OrderException(OrderErrorEnum.paramsError);
			}
			
			// 设置订单上下文
			context.setRequest(request);
			context.setCurrentTime(new Date());

			// 开始取消订单
			cancelOrderHandler.cancel(context);

			// 封装返回信息
			Order order = context.getOrder();
			response.setSuccess(true);
			CancelOrderResponse cancelOrderResponse = new CancelOrderResponse();
			cancelOrderResponse.setOrderId(order.getId());
			cancelOrderResponse.setOrderStatus(order.getStatus());
			response.setData(cancelOrderResponse);
		} catch (OrderException e) {
			// 特殊处理订单已取消异常，需返回true保证接口幂等性
			OrderErrorEnum orderErrorEnum = e.getErrorEnum();
			if (orderErrorEnum.getErrorCode().equals(OrderErrorEnum.orderCanceled.getErrorCode())) {
				logger.warn("针对订单已取消的场景特殊处理返回值");
				response.setSuccess(true);
				Order order = context.getOrder();
				CancelOrderResponse cancelOrderResponse = new CancelOrderResponse();
				cancelOrderResponse.setOrderId(order.getId());
				cancelOrderResponse.setOrderStatus(order.getStatus());
				response.setData(cancelOrderResponse);
			} else {
				logger.error("取消订单异常", e);
				response.setSuccess(false);
				response.setErrorCode(e.getErrorCode());
				response.setErrorMessage(e.getErrorMsg());
			}
		} catch (Exception ex) {
			logger.error("取消订单异常", ex);
			response.setSuccess(false);
			response.setErrorCode(OrderErrorEnum.customError.getErrorCode());
			response.setErrorMessage(OrderErrorEnum.customError.getErrorMsg());
		}

		logger.info("取消订单全部执行完成,返回值:{}", JSON.toJSONString(response));
		return response;
	}
}
