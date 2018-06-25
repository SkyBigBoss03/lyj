package com.jt.order.mapper;

import java.util.Date;

import com.jt.common.mapper.SysMapper;
import com.jt.dubbo.pojo.Order;

public interface OrderMapper extends SysMapper<Order>{

	Order findOrderById(String orderId);

	void updatePayment(Date date);

}
