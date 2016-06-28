package com.duantuke.order.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.duantuke.basic.enums.SkuTypeEnum;
import com.duantuke.order.common.enums.OrderTypeEnum;
import com.duantuke.order.common.enums.PayTypeEnum;
import com.duantuke.order.model.Base;
import com.duantuke.order.model.CancelOrderRequest;
import com.duantuke.order.model.CancelOrderResponse;
import com.duantuke.order.model.CreateOrderRequest;
import com.duantuke.order.model.CreateOrderResponse;
import com.duantuke.order.model.Header;
import com.duantuke.order.model.Order;
import com.duantuke.order.model.OrderDetail;
import com.duantuke.order.model.OrderDetailPrice;
import com.duantuke.order.model.QueryOrderRequest;
import com.duantuke.order.model.Request;
import com.duantuke.order.model.Response;
import com.duantuke.order.utils.DateUtil;
import com.duantuke.order.utils.log.LogUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/*.xml" })
public class OrderServiceTest {

	public static final LogUtil logger = new LogUtil(OrderServiceTest.class);

	@Autowired
	private OrderService orderService;

	@Test
	public void testCreate() {
		Request<CreateOrderRequest> request = new Request<CreateOrderRequest>();
		Header header = new Header();
		header.setTimeStamp(new Date());
		request.setHeader(header);

		Date today = new Date();
		Date tomorrow = DateUtils.addDays(today, 1);
		CreateOrderRequest createOrderRequest = new CreateOrderRequest();
		Order order = new Order();
		order.setPayType(PayTypeEnum.prepay.getId());
		order.setContact("张三");
		order.setContactPhone("13333333333");
		order.setMemo("尽量安排无烟房");
		order.setBeginTime(today);
		order.setEndTime(tomorrow);

		List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setSkuId(1l);
		orderDetail.setSkuName("大床房");
		orderDetail.setSkuType(SkuTypeEnum.roomtype.getCode());
		orderDetail.setNum(1);
		
		List<OrderDetailPrice> priceDetails = new ArrayList<OrderDetailPrice>();
		OrderDetailPrice orderDetailPrice = new OrderDetailPrice();
		orderDetailPrice.setActionTime(DateUtil.getDateFromString("20160628"));
		orderDetailPrice.setPrice(new BigDecimal("122"));
		priceDetails.add(orderDetailPrice);
		orderDetail.setPriceDetails(priceDetails);
		
		orderDetails.add(orderDetail);
		
		orderDetail = new OrderDetail();
		orderDetail.setSkuId(2l);
		orderDetail.setSkuName("老北京火锅");
		orderDetail.setSkuType(SkuTypeEnum.meal.getCode());
		orderDetail.setNum(1);

		priceDetails = new ArrayList<OrderDetailPrice>();
		orderDetailPrice = new OrderDetailPrice();
		orderDetailPrice.setActionTime(DateUtil.getDateFromString("20160628"));
		orderDetailPrice.setPrice(new BigDecimal("2"));
		priceDetails.add(orderDetailPrice);
		orderDetail.setPriceDetails(priceDetails);
		orderDetails.add(orderDetail);
		
		order.setOrderDetails(orderDetails);

		createOrderRequest.setOrder(order);
		request.setData(createOrderRequest);
		createOrderRequest.setOperatorId("123");
		createOrderRequest.setOperatorName("张三");

		Response<CreateOrderResponse> response = orderService.create(request);
		Assert.assertTrue(response.isSuccess());
	}

	@Test
	public void testQueryOrders() throws ParseException {
		Request<QueryOrderRequest> request = new Request<QueryOrderRequest>();
		Header header = new Header();
		header.setTimeStamp(new Date());
		request.setHeader(header);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = sdf.parse("2016-06-12");
		Date endDate = sdf.parse("2016-06-29");

		QueryOrderRequest queryOrderRequest = new QueryOrderRequest();
		queryOrderRequest.setOrderType(OrderTypeEnum.common.getId());
		queryOrderRequest.setContact("张三");
		queryOrderRequest.setStartDate(startDate);
		queryOrderRequest.setEndDate(endDate);
		queryOrderRequest.setPageNo(1);
		queryOrderRequest.setPageSize(10);

		request.setData(queryOrderRequest);

		Response<List<Order>> response = orderService.queryOrders(request);
		Assert.assertTrue(response.isSuccess());
	}

	@Test
	public void testQueryOrderByOrderId() {
		Request<Base> request = new Request<Base>();
		Header header = new Header();
		header.setTimeStamp(new Date());
		request.setHeader(header);

		Base base = new Base();
		base.setOrderId(63l);
		request.setData(base);

		Response<Order> response = orderService.queryOrderByOrderId(request);
		System.out.println(JSON.toJSONString(response));
	}

	@Test
	public void testCancel() {
		Request<CancelOrderRequest> request = new Request<CancelOrderRequest>();
		Header header = new Header();
		header.setTimeStamp(new Date());
		request.setHeader(header);

		CancelOrderRequest cancelOrderRequest = new CancelOrderRequest();
		cancelOrderRequest.setOrderId(19l);
		cancelOrderRequest.setReason("测试取消理由");
		request.setData(cancelOrderRequest);

		Response<CancelOrderResponse> response = orderService.cancel(request);
		System.out.println(JSON.toJSONString(response));
	}

	@Test
	public void testConfirm() {
		Request<Base> request = new Request<Base>();
		Header header = new Header();
		header.setTimeStamp(new Date());
		request.setHeader(header);

		Base base = new Base();
		base.setOrderId(19l);
		request.setData(base);

		Response<Order> response = orderService.confirm(request);
		System.out.println(JSON.toJSONString(response));
	}

	@Test
	public void testAutoFinish() {
		Request<Base> request = new Request<Base>();
		Header header = new Header();
		header.setTimeStamp(new Date());
		request.setHeader(header);

		Base base = new Base();
		base.setOperatorId("OrderFinishWorker");
		request.setData(base);

		Response<String> response = orderService.autoFinish(request);
		System.out.println(JSON.toJSONString(response));
	}
	
	@Test
	public void testQueryOrdersCount() throws ParseException{
		Request<QueryOrderRequest> request = new Request<QueryOrderRequest>();
		Header header = new Header();
		header.setTimeStamp(new Date());
		request.setHeader(header);

		QueryOrderRequest queryOrderRequest = new QueryOrderRequest();
		queryOrderRequest.setOrderType(OrderTypeEnum.common.getId());
		queryOrderRequest.setContact("张三");

		request.setData(queryOrderRequest);

		Response<Integer> response = orderService.queryOrdersCount(request);
		Assert.assertTrue(response.isSuccess());
	}
}
