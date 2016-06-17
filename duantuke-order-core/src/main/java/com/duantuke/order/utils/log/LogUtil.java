package com.duantuke.order.utils.log;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duantuke.order.common.ThreadLocalContext;


/**
 * 日志工具
 * 
 * @author 须俊杰
 * @date 2015-12-25
 */
public class LogUtil {

	private Logger logger = null;

	public LogUtil(Class<?> clazz) {
		logger = LoggerFactory.getLogger(clazz);
	}

	public void info(String format, Object... arguments) {
		LogBean logBean = buildMessage(format, arguments);
		logger.info(logBean.getFormat(), logBean.getArguments());
	}

	public void debug(String format, Object... arguments) {
		LogBean logBean = buildMessage(format, arguments);
		logger.debug(logBean.getFormat(), logBean.getArguments());
	}

	public void error(String format, Object... arguments) {
		LogBean logBean = buildMessage(format, arguments);
		logger.error(logBean.getFormat(), logBean.getArguments());
	}

	public void warn(String format, Object... arguments) {
		LogBean logBean = buildMessage(format, arguments);
		logger.error(logBean.getFormat(), logBean.getArguments());
	}

	public void error(String format, Throwable t) {
		LogBean logBean = buildMessage(format);
		logger.error(logBean.getFormat(), t);
	}

	private LogBean buildMessage(String format, Object... arguments) {
		StringBuilder sb = new StringBuilder();
		Object orderid = ThreadLocalContext.get("orderId");
		if (orderid != null) {
			sb.append(",orderId = ");
			sb.append(String.valueOf(orderid));
			arguments = ArrayUtils.add(arguments, orderid);
		}
		Object hotelid = ThreadLocalContext.get("supplierId");
		if (hotelid != null) {
			sb.append(",supplierId = ");
			sb.append(String.valueOf(hotelid));
			arguments = ArrayUtils.add(arguments, hotelid);
		}
		Object customerId = ThreadLocalContext.get("customerId");
		if (hotelid != null) {
			sb.append(",customerId = ");
			sb.append(String.valueOf(customerId));
			arguments = ArrayUtils.add(arguments, customerId);
		}

		LogBean logBean = new LogBean();
		logBean.setFormat(format + sb.toString());
		logBean.setArguments(arguments);
		return logBean;
	}

}
