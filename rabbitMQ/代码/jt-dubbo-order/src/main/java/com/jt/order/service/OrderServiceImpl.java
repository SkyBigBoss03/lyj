package com.jt.order.service;

import java.util.Date;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jt.dubbo.pojo.Order;
import com.jt.dubbo.pojo.OrderItem;
import com.jt.dubbo.pojo.OrderShipping;
import com.jt.dubbo.service.DubboOrderService;
import com.jt.order.mapper.OrderItemMapper;
import com.jt.order.mapper.OrderMapper;
import com.jt.order.mapper.OrderShippingMapper;

@Service
public class OrderServiceImpl implements DubboOrderService {
	
	
	
	@Autowired
	private OrderMapper orderMapper;
	
	@Autowired
	private OrderItemMapper orderItemMapper;
	
	@Autowired
	private OrderShippingMapper orderShippingMapper;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	
	/**
	 * 1.实现三张表入库操作
	 * 2.创建orderId  订单号：登录用户id+当前时间戳
	 * 3.入库order表
	 * 4.入库orderShipping表
	 * 5.入库OrderItem表
	 */
	@Override
	public String saveOrder(Order order) {
		
		String orderId = order.getUserId() + "" + System.currentTimeMillis();
		
		//使用rabbitMQ插入数据
		
		order.setOrderId(orderId);
		rabbitTemplate.convertAndSend("save.order",order);
		System.out.println("生产者发送消息!!!!!");
		
		
		
		
		/*//将Order对象入库操作
		order.setOrderId(orderId);
		order.setStatus(1);	//表示未支付状态
		order.setCreated(new Date());
		order.setUpdated(order.getCreated());
		orderMapper.insert(order);
		System.out.println("订单插入完成");
		//插入订单物流信息
		OrderShipping orderShipping = order.getOrderShipping();
		orderShipping.setOrderId(orderId);
		orderShipping.setCreated(order.getCreated());
		orderShipping.setUpdated(order.getCreated());
		orderShippingMapper.insert(orderShipping);
		System.out.println("物流插入完成");
		
		List<OrderItem> orderItems = order.getOrderItems();
		
		for (OrderItem orderItem : orderItems) {
			orderItem.setOrderId(orderId);
			orderItem.setCreated(order.getCreated());
			orderItem.setUpdated(order.getCreated());
			orderItemMapper.insert(orderItem);
		}
		
		System.out.println("订单商品插入完成");	*/
		return orderId;
	}


	//实现批量查询操作
	@Override
	public Order findOrderById(String orderId) {
		
		Order order = orderMapper.selectByPrimaryKey(orderId);
		
		OrderShipping orderShipping = orderShippingMapper.selectByPrimaryKey(orderId);
		
		OrderItem orderItem = new OrderItem();
		orderItem.setOrderId(orderId);
		List<OrderItem> orderItemList = orderItemMapper.select(orderItem);
		
		//return orderMapper.findOrderById(orderId);
		order.setOrderShipping(orderShipping);
		order.setOrderItems(orderItemList);
		return order;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
