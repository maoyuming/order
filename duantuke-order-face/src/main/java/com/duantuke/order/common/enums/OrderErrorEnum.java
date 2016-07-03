package com.duantuke.order.common.enums;

import com.duantuke.order.exception.OrderException;

/**
 * 订单系统错误信息枚举类
 */
public enum OrderErrorEnum {
	
	// 酒店错误信息
	findHotelinfo("11000", "酒店信息错误。"),
	notfindHotel("11001", "酒店不存在。"),
	roomNUll("13000","酒店房间已满,客官请下次再来."),
	notfindHotelRoomtype("11002","酒店房型不存在。"),
    
	// 订单错误信息
	orderCanceled("10001","订单已取消"),
	orderConfirmed("10002","订单已确认"),
	orderPriceError("10003","订单金额错误"),
	orderStatusError("10004","非法订单状态"),
	orderNotPaid("10005","订单未支付"),
	orderFinished("10006","订单已完成"),
	orderCanNotBeCanceled("10007","订单不能取消"),
	
	// 通用错误信息
	customError("0","系统错误"),
	DBError("100","数据库错误"),
	paramsError("101", "非法参数请求");
	
	private final String errorCode;
	private final String errorMsg;
	
	private OrderErrorEnum(String errorCode,String errorMsg){
		this.errorCode=errorCode;
		this.errorMsg=errorMsg;
	}
	
	public OrderException getException(){
		return getOrderException(errorMsg);
	}
	
	public OrderException getOrderException(String msg){
		return new OrderException(errorCode,  msg);//  返回输入的错误信息
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	 
	public String getErrorMsg() {
		return errorMsg;
	}
	
	public static OrderErrorEnum findByCode(String code){
		for (OrderErrorEnum value : OrderErrorEnum.values()) {
			if(value.errorCode.equalsIgnoreCase(code)){
				return value;
			}
		}
		return null;
	}
}
