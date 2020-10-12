package com.fengyang.order.enums;

/**
 * @author bystander
 * @date 2018/10/5
 */
public enum  OrderStatusEnum {

    INIT(1, "初始化，未付款"),
    PAY_UP(2, "已付款，未发货"),
    DELIVERED(3, "已发货，未确认"),
    CONFIRMED(4, "已确认,未评价"),
    CLOSED(5, "已关闭"),
    RATED(6, "已评价，交易结束")
    ;

    private Integer code;
    private String desc;

    OrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer value(){
        return this.code;
    }

    public String desc(){
        return desc;
    }
}
