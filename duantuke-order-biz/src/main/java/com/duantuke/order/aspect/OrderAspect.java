package com.duantuke.order.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.duantuke.order.common.ThreadLocalContext;
import com.duantuke.order.common.enums.OrderErrorEnum;
import com.duantuke.order.exception.OrderException;
import com.duantuke.order.model.Base;
import com.duantuke.order.model.Header;
import com.duantuke.order.model.Request;
import com.duantuke.order.model.Response;
import com.duantuke.order.utils.log.LogUtil;

@Component
@Aspect
public class OrderAspect {
	private static final LogUtil logger = new LogUtil(OrderAspect.class);

	@Pointcut("execution(* com.duantuke.order.service.impl.OrderServiceImpl.*(..))")
	private void aspect() {
	}// 定义一个切入点

	@SuppressWarnings("rawtypes")
	@Around("aspect() && args(request)")
	public Object parseRequest(ProceedingJoinPoint pjp, Request request) throws Throwable {
		Response response = new Response();
		try {
			if (request == null) {
				logger.error("请求参数request不能为空");
				throw new OrderException(OrderErrorEnum.paramsError);
			}

			Header header = request.getHeader();
			if (header == null 
					|| header.getTimeStamp() == null) {
				logger.error("request验证失败，请检查timestamp");
				throw new OrderException(OrderErrorEnum.paramsError);
			}
			
			Object o = request.getData();
			if (o instanceof Base) {
				Base base = (Base) o;
				if (base != null) {
					// 统一将酒店id和订单id放入线程本地变量中，主要为了统一日志输出
					ThreadLocalContext.set("supplierId", base.getSupplierId());
					ThreadLocalContext.set("orderId", base.getOrderId());
					ThreadLocalContext.set("operator", base.getOperatorId());
				}
			}
		} catch (Exception e) {
			logger.error("订单拦截器处理异常", e);
			response.setSuccess(false);
			response.setErrorCode(OrderErrorEnum.paramsError.getErrorCode());
			response.setErrorMessage(OrderErrorEnum.paramsError.getErrorMsg());
			return response;
		}

		return pjp.proceed();
	}
}
