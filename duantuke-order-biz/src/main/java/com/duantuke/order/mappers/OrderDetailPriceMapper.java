package com.duantuke.order.mappers;

import java.util.List;

import com.duantuke.order.model.OrderDetailPrice;

public interface OrderDetailPriceMapper {
	int deleteByPrimaryKey(Long id);

	int insert(OrderDetailPrice record);

	int insertSelective(OrderDetailPrice record);

	OrderDetailPrice selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(OrderDetailPrice record);

	int updateByPrimaryKey(OrderDetailPrice record);

	void batchInsert(List<OrderDetailPrice> priceDetails);
}