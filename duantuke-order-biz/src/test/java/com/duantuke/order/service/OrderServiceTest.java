package com.duantuke.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.duantuke.order.common.enums.PayTypeEnum;
import com.duantuke.order.model.Base;
import com.duantuke.order.model.CreateOrderRequest;
import com.duantuke.order.model.CreateOrderResponse;
import com.duantuke.order.model.Header;
import com.duantuke.order.model.Order;
import com.duantuke.order.model.OrderDetail;
import com.duantuke.order.model.Request;
import com.duantuke.order.model.Response;
import com.duantuke.order.utils.log.LogUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/*.xml" })
public class OrderServiceTest {

	public static final LogUtil logger = new LogUtil(OrderServiceTest.class);
	
	@Autowired
	private OrderService orderService;
	
	@Test
	public void testCreate(){
		Request<CreateOrderRequest> request = new Request<CreateOrderRequest>();
		Header header = new Header();
		header.setTimeStamp(new Date());
		header.setToken("token");
		request.setHeader(header);
		
		CreateOrderRequest createOrderRequest = new CreateOrderRequest();
		Order order = new Order();
		order.setPayType(PayTypeEnum.prepay.getId());
		order.setContact("张三");
		order.setContactPhone("13333333333");
		order.setMemo("尽量安排无烟房");
		
		List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setSkuId(10000l);
		orderDetail.setSkuName("大床房");
		orderDetail.setSkuType(1);
		orderDetail.setNum(1);
		orderDetail.setPrice(new BigDecimal("190"));
		
		Date today = new Date();
		Date tomorrow = DateUtils.addDays(today, 1);
		orderDetail.setBeginTime(today);
		orderDetail.setEndTime(tomorrow);
		orderDetails.add(orderDetail);
		
		orderDetail = new OrderDetail();
		orderDetail.setSkuId(100002l);
		orderDetail.setSkuName("标准房");
		orderDetail.setSkuType(1);
		orderDetail.setNum(1);
		orderDetail.setPrice(new BigDecimal("200"));
		orderDetail.setBeginTime(today);
		orderDetail.setEndTime(tomorrow);
		orderDetails.add(orderDetail);
		
		order.setOrderDetails(orderDetails);
		
		createOrderRequest.setOrder(order);
		request.setData(createOrderRequest);
		
		Response<CreateOrderResponse> response = orderService.create(request);
		Assert.assertTrue(response.isSuccess());
	}
	
	@Test
	public void testQueryOrders(){
		Request<Base> request = new Request<Base>();
		Header header = new Header();
		header.setTimeStamp(new Date());
		header.setToken("token");
		request.setHeader(header);
		
		Response<List<Order>> response = orderService.queryOrders(request);
		List<Order> orders = response.getData();
		JSON.toJSONString(orders);
	}
}
