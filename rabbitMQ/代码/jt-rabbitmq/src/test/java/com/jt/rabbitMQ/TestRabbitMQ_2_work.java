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

public class TestRabbitMQ_2_work {
	
	//工作模式:多个人一起消费一个队列消息.内部轮询机制
	
	
	/**
	 * 1.定义rabbmq地址 ip:端口
	 * 2.定义虚拟主机
	 * 3.定义用户名和密码
	 * @throws IOException 
	 */
	private Connection connection = null;
	@Before
	public void init() throws IOException{
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.126.146");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/jt");
		connectionFactory.setUsername("jtadmin");
		connectionFactory.setPassword("jtadmin");
		
		//获取链接
		connection = connectionFactory.newConnection();
	}
	
	@Test
	public void provider() throws IOException{
		//定义通道对象
		Channel channel = connection.createChannel();
		
		//定义队列
		channel.queueDeclare("queue_work", false, false, false, null);
		
		//定义广播的消息
		String msg = "我是工作模式";
		
		//发送消息
		channel.basicPublish("", "queue_work", null, msg.getBytes());
		
		//关闭流文件
		channel.close();
		connection.close();
	}
	
	
	//定义消费者
	@Test
	public void consumer1() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException{
		//定义通道
		Channel channel = connection.createChannel();
		
		//定义队列
		channel.queueDeclare("queue_work", false, false, false, null);
		
		//定义消费数  每次只能消费一条记录.当消息执行后需要返回ack确认消息 才能执行下一条
		channel.basicQos(1);
		
		//定义消费者
		QueueingConsumer consumer = new QueueingConsumer(channel);
		
		//将队列和消费者绑定  false表示手动返回ack
		channel.basicConsume("queue_work", false, consumer);
		
		while(true){
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String msg = new String(delivery.getBody());
			System.out.println("队列A获取消息:"+msg);
			//deliveryTag 队列下标位置
			//multiple是否批量返回
			channel.basicAck(delivery.getEnvelope().getDeliveryTag(), true);
		}
	}
	
	
	//定义消费者
		@Test
		public void consumer2() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException{
			//定义通道
			Channel channel = connection.createChannel();
			
			//定义队列
			channel.queueDeclare("queue_work", false, false, false, null);
			
			//定义消费数  每次只能消费一条记录.当消息执行后需要返回ack确认消息 才能执行下一条
			channel.basicQos(1);
			
			//定义消费者
			QueueingConsumer consumer = new QueueingConsumer(channel);
			
			//将队列和消费者绑定  false表示手动返回ack
			channel.basicConsume("queue_work", false, consumer);
			
			while(true){
				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				String msg = new String(delivery.getBody());
				System.out.println("队列B获取消息:"+msg);
				//deliveryTag 队列下标位置
				//multiple是否批量返回
				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), true);
			}
		}
	
	
		//定义消费者
				@Test
				public void consumer3() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException{
					//定义通道
					Channel channel = connection.createChannel();
					
					//定义队列
					channel.queueDeclare("queue_work", false, false, false, null);
					
					//定义消费数  每次只能消费一条记录.当消息执行后需要返回ack确认消息 才能执行下一条
					channel.basicQos(1);
					
					//定义消费者
					QueueingConsumer consumer = new QueueingConsumer(channel);
					
					//将队列和消费者绑定  false表示手动返回ack
					channel.basicConsume("queue_work", false, consumer);
					
					while(true){
						QueueingConsumer.Delivery delivery = consumer.nextDelivery();
						String msg = new String(delivery.getBody());
						System.out.println("队列C获取消息:"+msg);
						//deliveryTag 队列下标位置
						//multiple是否批量返回
						channel.basicAck(delivery.getEnvelope().getDeliveryTag(), true);
					}
				}
	
	
	
	
}
