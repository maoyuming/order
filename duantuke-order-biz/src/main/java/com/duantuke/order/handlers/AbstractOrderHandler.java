package com.duantuke.order.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.duantuke.basic.face.bean.SkuRequest;
import com.duantuke.basic.face.bean.SkuResponse;
import com.duantuke.basic.face.service.SaleService;
import com.duantuke.basic.face.service.SkuService;
import com.duantuke.basic.po.Sale;
import com.duantuke.order.mappers.OrderDetailMapper;
import com.duantuke.order.mappers.OrderDetailPriceMapper;
import com.duantuke.order.mappers.OrderMapper;
import com.duantuke.order.model.Order;
import com.duantuke.order.model.OrderContext;
import com.duantuke.order.model.OrderDetail;
import com.duantuke.order.utils.log.LogUtil;

public abstract class AbstractOrderHandler {

	public static final LogUtil logger = new LogUtil(AbstractOrderHandler.class);
	@Autowired
	protected OrderMapper orderMapper;
	@Autowired
	protected OrderDetailMapper orderDetailMapper;
	@Autowired
	private SkuService skuService;
	@Autowired
	private SaleService saleService;
	@Autowired
	protected OrderDetailPriceMapper orderDetailPriceMapper;

	/**
	 * 查询订单信息(只查询主表)
	 * 
	 * @param id
	 * @return
	 */
	protected Order getOrderById(Long id) {
		logger.info("开始查询订单信息");
		Order order = orderMapper.selectByPrimaryKey(id);
		logger.info("订单信息查询完成,结果:{}", JSON.toJSONString(order));
		return order;
	}

	/**
	 * 获取sku信息
	 * 
	 * @param order
	 * @return
	 */
	protected SkuResponse getSkuInfo(Order order) {
		logger.info("开始获取sku信息");
		List<OrderDetail> orderDetails = order.getOrderDetails();

		SkuRequest request = new SkuRequest();
		request.setBeginTime(order.getBeginTime());
		request.setEndTime(order.getEndTime());
		Map<Integer, List<Long>> skuMap = new HashMap<Integer, List<Long>>();
		for (OrderDetail orderDetail : orderDetails) {
			Long skuId = orderDetail.getSkuId();
			Integer skuType = orderDetail.getSkuType();

			if (skuMap.containsKey(skuType)) {
				List<Long> skus = skuMap.get(skuType);
				skus.add(skuId);
			} else {
				List<Long> skus = new ArrayList<Long>();
				skus.add(skuId);
				skuMap.put(skuType, skus);
			}
		}
		request.setSkuMap(skuMap);

		logger.info("开始调用SkuService,参数:{}", JSON.toJSONString(request));
		SkuResponse skuResponse = skuService.querySku(request);

		logger.info("sku信息获取完成,结果:{}", JSON.toJSONString(skuResponse));
		return skuResponse;
	}

	/**
	 * 格式化操作人信息
	 * 
	 * @param context
	 * @return
	 */
	protected String formatOperator(OrderContext<?> context) {
		StringBuilder sb = new StringBuilder();
		sb.append(context.getOperatorId());
		if (StringUtils.isNotBlank(context.getOperatorName())) {
			sb.append("(");
			sb.append(context.getOperatorName());
			sb.append(")");
		}

		String result = sb.toString();
		return result.equals("null") ? null : result;
	}

	/**
	 * 获取销售信息
	 * 
	 * @param hotelId
	 * @return
	 */
	protected Sale getSalesInfo(Long hotelId) {
		logger.info("开始获取销售信息,hotelId = {}", hotelId);
		Sale sales = saleService.querySaleByHotelId(hotelId);
		logger.info("销售信息获取完成,结果 = {}", JSON.toJSONString(sales));
		return sales;
	}
}
