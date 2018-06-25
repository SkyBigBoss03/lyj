package com.jt.rabbitMQ;


import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class TestRabbitMQ_4_redirect {

//发布订阅模式
private Connection connection = null;
	
	//定义rabbit连接池
	@Before
	public void initConnection() throws IOException{
		//定义工厂对象
		ConnectionFactory connectionFactory = new ConnectionFactory();
		//设定参数
		connectionFactory.setHost("192.168.126.146");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/jt");
		connectionFactory.setUsername("jtadmin");
		connectionFactory.setPassword("jtadmin");
		//创建连接
		connection = connectionFactory.newConnection();	
	}
	
	
	
	//定义生产者
	@Test
	public void  proverder() throws IOException{
		Channel channel = connection.createChannel();
		
		//定义交换机名称
		String exchange_name = "redirect";
		
		
		//定义发布订阅模式    fanout    direct 路由模式    topic 主题模式
		channel.exchangeDeclare(exchange_name, "direct");
		
		for(int i=0;i<10; i++){
			String msg = "生产者发送消息"+i;
			String rontKey = "1707B";
			
			/**
			 * exchange:交换机名称
			 * routingKey:路由key
			 * props:参数
			 * body:发送消息
			 */
			channel.basicPublish(exchange_name, rontKey, null, msg.getBytes());
		}
		
		channel.close();
		connection.close();
	}
	
	/**
	 * 消费者需要定义队列名称  并且与交换机绑定
	 * @throws IOException
	 * @throws InterruptedException 
	 * @throws ConsumerCancelledException 
	 * @throws ShutdownSignalException 
	 */
	@Test
	public void consumer1() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException{
		
		//定义通道
		Channel channel = connection.createChannel();
		
		//定义交换机名称
		String exchange_name = "redirect";
		
		//定义队列名称
		String queue_name = "c_r_1";
		
		//定义交换机模式
		channel.exchangeDeclare(exchange_name, "direct");
		
		//定义队列
		channel.queueDeclare(queue_name, false, false, false, null);
		
		//将队列和交换机绑定
		/**
		 * 参数介绍:
		 * 	queue:队列名称
		 *  exchange:交换机名称
		 *  routingKey:路由key
		 */
		//channel.queueBind(queue, exchange, routingKey)
		channel.queueBind(queue_name, exchange_name, "1707A");
		
		//定义消费个数
		channel.basicQos(1);
		
		//定义消费者
		QueueingConsumer consumer = new QueueingConsumer(channel);
		
		//绑定消息与消费者
		channel.basicConsume(queue_name, false, consumer);
		
		while(true){
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			
			String msg = "路由模式-消费者1"+new String(delivery.getBody());
			System.out.println(msg);
			
			//手动回复 一个一个回复
			channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
		}

	}
	
	
	/**
	 * 消费者需要定义队列名称  并且与交换机绑定
	 * @throws IOException
	 * @throws InterruptedException 
	 * @throws ConsumerCancelledException 
	 * @throws ShutdownSignalException 
	 */
	@Test
	public void consumer2() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException{
		Channel channel = connection.createChannel();
		
		String exchange_name = "redirect";
		String queue_name = "c_r_2";
		
		//定义交换机模式
		channel.exchangeDeclare(exchange_name, "direct");
		
		//定义队列
		channel.queueDeclare(queue_name, false, false, false, null);
		
		//将队列和交换机绑定
		channel.queueBind(queue_name, exchange_name, "1707B");
		
		channel.basicQos(1);
		
		//定义消费者
		QueueingConsumer consumer = new QueueingConsumer(channel);
		
		//定义回复方式
		channel.basicConsume(queue_name, false, consumer);
		
		while(true){
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			
			String msg = "路由模式-消费者2"+new String(delivery.getBody());
			System.out.println(msg);
			
			channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
		}
	}
}
