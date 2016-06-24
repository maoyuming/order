package com.duantuke.order.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.duantuke.mongo.bislog.BisLog;
import com.duantuke.mongo.bislog.BisLogDelegate;
import com.duantuke.order.common.enums.BusinessTypeEnum;
import com.duantuke.order.common.enums.OrderErrorEnum;
import com.duantuke.order.exception.OrderException;
import com.duantuke.order.handlers.CancelOrderHandler;
import com.duantuke.order.handlers.CreateOrderHandler;
import com.duantuke.order.handlers.QueryOrderHandler;
import com.duantuke.order.handlers.UpdateOrderHandler;
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
import com.duantuke.order.utils.PropertyConfigurer;
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
	private UpdateOrderHandler updateOrderHandler;
	@Autowired
	private OrderProducter orderProducter;
	@Autowired
	private BisLogDelegate bisLogDelegate;

	@Override
	public Response<CreateOrderResponse> create(Request<CreateOrderRequest> request) {

		Response<CreateOrderResponse> response = new Response<CreateOrderResponse>();
		// 初始化订单上下文
		OrderContext<Request<CreateOrderRequest>> context = new OrderContext<Request<CreateOrderRequest>>();
		try {
			logger.info("接收到订单创建请求,入参:{}", JSON.toJSONString(request));

			CreateOrderRequest createOrderRequest = request.getData();
			// 参数合法性校验
			createOrderHandler.validate(createOrderRequest, context);

			context.setRequest(request);
			context.setCurrentTime(new Date());
			context.setOperatorId(createOrderRequest.getOperatorId());
			context.setOperatorName(createOrderRequest.getOperatorName());

			// 开始创建订单
			createOrderHandler.create(context);

			// 创建订单后处理，如果发生异常也需要正常返回成功，此处可降级
			try {
				// 发送消息
				orderProducter.sendCreatingMessage(buildMessage(context.getOrder()));

				// 保存业务日志
				saveLog(context.getOrder(), BusinessTypeEnum.CREATE, "订单创建成功");
			} catch (Exception e) {
				logger.error("创建订单后处理异常", e);
			}
			
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
			queryOrderHandler.validatePageInfo(queryOrderRequest);

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
			context.setOperatorId(cancelOrderRequest.getOperatorId());
			context.setOperatorName(cancelOrderRequest.getOperatorName());

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

		try {
			// 发送消息
			orderProducter.sendCanceledMessage(buildMessage(context.getOrder()));

			// 记录日志
			saveLog(context.getOrder(), BusinessTypeEnum.CANCEL, "订单已取消");
		} catch (Exception e) {
			logger.error("取消订单后处理异常", e);
		}

		logger.info("取消订单全部执行完成,返回值:{}", JSON.toJSONString(response));
		return response;
	}

	@Override
	public Response<Order> confirm(Request<Base> request) {
		Response<Order> response = new Response<Order>();
		// 初始化上下文
		OrderContext<Request<Base>> context = new OrderContext<Request<Base>>();
		try {
			logger.info("接收到确认订单请求,入参:{}", JSON.toJSONString(request));
			// 参数合法性校验
			Base base = request.getData();
			if (base == null || base.getOrderId() == null || base.getOrderId() < 1) {
				throw new OrderException(OrderErrorEnum.paramsError);
			}

			// 设置订单上下文
			context.setRequest(request);
			context.setCurrentTime(new Date());
			context.setOperatorId(base.getOperatorId());
			context.setOperatorName(base.getOperatorName());

			// 开始确认订单
			updateOrderHandler.confirm(context);

			// 封装返回信息
			Order order = context.getOrder();
			response.setSuccess(true);
			response.setData(order);
		} catch (OrderException e) {
			// 特殊处理订单已取消异常，需返回true保证接口幂等性
			OrderErrorEnum orderErrorEnum = e.getErrorEnum();
			if (orderErrorEnum.getErrorCode().equals(OrderErrorEnum.orderConfirmed.getErrorCode())) {
				logger.warn("针对订单已确认的场景特殊处理返回值");
				response.setSuccess(true);
				Order order = context.getOrder();
				response.setData(order);
			} else {
				logger.error("确认订单异常", e);
				response.setSuccess(false);
				response.setErrorCode(e.getErrorCode());
				response.setErrorMessage(e.getErrorMsg());
			}
		} catch (Exception ex) {
			logger.error("确认订单异常", ex);
			response.setSuccess(false);
			response.setErrorCode(OrderErrorEnum.customError.getErrorCode());
			response.setErrorMessage(OrderErrorEnum.customError.getErrorMsg());
		}

		try {
			// 发送消息
			orderProducter.sendConfirmedMessage(buildMessage(context.getOrder()));

			// 记录日志
			saveLog(context.getOrder(), BusinessTypeEnum.CONFIRM, "订单已确认");
		} catch (Exception e) {
			logger.error("确认订单后处理异常", e);
		}

		logger.info("确认订单全部执行完成,返回值:{}", JSON.toJSONString(response));
		return response;
	}

	@Override
	public Response<String> autoFinish(Request<Base> request) {
		Response<String> response = new Response<String>();
		// 初始化上下文
		OrderContext<Request<Base>> context = new OrderContext<Request<Base>>();
		List<Order> orders = null;
		try {
			logger.info("接收到自动完成订单请求,入参:{}", JSON.toJSONString(request));

			Base base = request.getData();
			// 设置订单上下文
			context.setRequest(request);
			context.setCurrentTime(new Date());
			context.setOperatorId(base.getOperatorId());
			context.setOperatorName(base.getOperatorName());

			// 开始处理
			orders = updateOrderHandler.autoFinish(context);

			// 封装返回信息
			response.setSuccess(true);
			response.setData(null);
		} catch (OrderException e) {
			logger.error("自动完成订单异常", e);
			response.setSuccess(false);
			response.setErrorCode(e.getErrorCode());
			response.setErrorMessage(e.getErrorMsg());
		} catch (Exception ex) {
			logger.error("自动完成订单异常", ex);
			response.setSuccess(false);
			response.setErrorCode(OrderErrorEnum.customError.getErrorCode());
			response.setErrorMessage(OrderErrorEnum.customError.getErrorMsg());
		}

		try {
			// 发送消息，记录日志
			for (Order order : orders) {
				orderProducter.sendFinishedMessage(buildMessage(order));

				saveLog(order, BusinessTypeEnum.FINISHED, "订单已完成");
			}
		} catch (Exception e) {
			logger.error("完成订单后处理异常", e);
		}

		logger.info("自动完成订单全部执行完成,返回值:{}", JSON.toJSONString(response));
		return response;
	}

	/**
	 * 保存业务日志
	 * 
	 * @param order
	 *            订单对象
	 * @param businessTypeEnum
	 *            日志类型
	 * @param content
	 *            日志内容
	 */
	private void saveLog(Order order, BusinessTypeEnum businessTypeEnum, String content) {
		logger.info("准备记录日志");
		BisLog bisLog = new BisLog();
		bisLog.setSystem(PropertyConfigurer.getProperty("system"));
		bisLog.setOperator(order.getUpdateBy() == null ? order.getCreateBy() : order.getUpdateBy());
		bisLog.setBussinessId(String.valueOf(order.getId()));
		bisLog.setBussinssType(businessTypeEnum.getId());
		bisLog.setContent(content);
		logger.info("开始记录日志,参数:{}", JSON.toJSONString(bisLog));
		this.bisLogDelegate.saveBigLog(bisLog);
		logger.info("日志记录完成");
	}

	@Override
	public Response<Integer> queryOrdersCount(Request<QueryOrderRequest> request) {
		Response<Integer> response = new Response<Integer>();
		try {
			logger.info("接收到订单数查询请求,入参:{}", JSON.toJSONString(request));
			// 参数合法性校验
			QueryOrderRequest queryOrderRequest = request.getData();
			queryOrderHandler.validate(queryOrderRequest);

			// 执行查询操作
			Integer count = queryOrderHandler.queryOrdersCount(queryOrderRequest);

			// 封装返回信息
			response.setSuccess(true);
			response.setData(count);
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

		logger.info("订单数查询全部完成,返回值:{}", JSON.toJSONString(response));
		return response;
	}

}
