package com.duantuke.order.service;

import java.util.List;

import com.duantuke.order.model.CreateOrderRequest;
import com.duantuke.order.model.CreateOrderResponse;
import com.duantuke.order.model.Order;
import com.duantuke.order.model.QueryOrderRequest;
import com.duantuke.order.model.Request;
import com.duantuke.order.model.Response;

/**
 * 订单服务
 * <p>
 *
 * 所有的接口采用标准化的接口入参和出参
 * <p>
 *
 * 入参：Request,其中Header里封装了基本的参数，包括token,timeStamp<br/>
 * data是业务参数对象,是个泛型类型,不同接口需要传入相对应的类型
 *
 * <pre>
 * 	public class Request<T> {
 *
 * 		private Header header;
 *
 * 		private T data;
 *
 * 		...
 * 	}
 * </pre>
 *
 * 出参：Response,其中 success表示返回是否成功,data是业务返回对象,是个泛型类型,<br/>
 * 不同接口需要传入相对应的类型,errorCode表示错误码，errorMessage是错误描述
 *
 * <pre>
 *  public class Response<T> {
 *
 * 		private boolean success;
 *
 * 		private T data;
 *
 * 		private String errorCode;
 *
 * 		private String errorMessage;
 *
 * 		...
 * 	}
 * </pre>
 *
 * @author 须俊杰
 * @since v1.0
 */
public interface OrderService {

	/**
	 * 创建订单
	 *
	 * @param request
	 * @return
	 */
	Response<CreateOrderResponse> create(Request<CreateOrderRequest> request);

	/**
	 * 查询订单列表(多个查询条件，结果集取交集)
	 * 
	 * @param request
	 * @return
	 */
	Response<List<Order>> queryOrders(Request<QueryOrderRequest> request);
}
