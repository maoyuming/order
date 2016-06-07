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
	orderAlreadyCheckIn("10003","此订单已经入住"),
    cancelOrderForMaxTime("10005","订单取消失败,订单超过30分钟!"),
    orderIdError("10001","订单号不正确"),
    DBError("10006","数据库错误"),
    pmsCancelOrderError("14001","PMS取消订单错误"),
    pmsUpdateOrderError("14002","PMS修改订单错误"),
    
     //表单校验
  	priceError("12000","订单价格错误"),
    stockNull("13000","酒店房间已满"),
    moneyError("14000","钱不够"),

    
	// 其他错误信息
	customError("0000",""),
	dataError("8","数据错误"),
	paramsError("6", "非法参数请求"),
	orderStatusError("5","订单状态错误");
	
	
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
		return new OrderException(errorCode, "", msg);//  返回输入的错误信息
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
