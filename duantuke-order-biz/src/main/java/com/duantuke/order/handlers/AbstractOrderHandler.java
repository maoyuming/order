package com.duantuke.order.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.duantuke.basic.face.bean.SkuRequest;
import com.duantuke.basic.face.bean.SkuResponse;
import com.duantuke.basic.face.service.SkuService;
import com.duantuke.order.mappers.OrderMapper;
import com.duantuke.order.model.Order;
import com.duantuke.order.model.OrderDetail;
import com.duantuke.order.utils.log.LogUtil;

public abstract class AbstractOrderHandler {

	public static final LogUtil logger = new LogUtil(AbstractOrderHandler.class);
	@Autowired
	protected OrderMapper orderMapper;
	@Autowired
	private SkuService skuService;

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
		Map<Integer, List<Long>> skuMap = new HashMap<Integer, List<Long>>();
		for (OrderDetail orderDetail : orderDetails) {
			Long skuId = orderDetail.getSkuId();
			Integer skuType = orderDetail.getSkuType();
			Date beginTime = orderDetail.getBeginTime();
			Date endTime = orderDetail.getEndTime();

			if (skuMap.containsKey(skuType)) {
				List<Long> skus = skuMap.get(skuType);
				skus.add(skuId);
			} else {
				List<Long> skus = new ArrayList<Long>();
				skus.add(skuId);
				skuMap.put(skuType, skus);
			}

			// 目前逻辑暂定所有sku预抵时间和预离时间都相同，支取一个即可
			if (request.getBeginTime() == null && request.getEndTime() == null && beginTime != null
					&& endTime != null) {
				 request.setBeginTime(beginTime);
				 request.setEndTime(endTime);
			}
		}
		request.setSkuMap(skuMap);

		logger.info("开始调用SkuService,参数:{}", JSON.toJSONString(request));
		SkuResponse skuResponse = skuService.querySku(request);

		logger.info("sku信息获取完成,结果:{}", JSON.toJSONString(skuResponse));
		return skuResponse;
	}
}
