package com.duantuke.order.handlers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.duantuke.order.utils.log.LogUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/*.xml" })
public class UpdateOrderHandlerTest {

	public static final LogUtil logger = new LogUtil(UpdateOrderHandlerTest.class);
	
	@Autowired
	private UpdateOrderHandler updateOrderHandler;
	
	@Test
	public void testUpdateOrderAfterPaid(){
		updateOrderHandler.updateOrderAfterPaid(82l);
	}
	
	@Test
	public void testUpdateOrderAfterRefunded(){
		updateOrderHandler.updateOrderAfterRefunded(108l);
	}
}
