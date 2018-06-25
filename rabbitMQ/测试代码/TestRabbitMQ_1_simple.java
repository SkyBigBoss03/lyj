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

public class TestRabbitMQ_1_simple {
	
	private Connection connection = null;
	
	//定义公共连接
	/**
	 * 1.定义rabbmq地址 ip:端口
	 * 2.定义虚拟主机
	 * 3.定义用户名和密码
	 * @throws IOException 
	 */
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
	
	
	//定义生产者
	@Test
	public void proverder() throws IOException{
		//定义通道Chneel接口
		Channel channel = connection.createChannel();
		
		//定义队列
		/**
		 * queue:队列名称
		 * durable:是否持久化  true 和false
		 * exclusive: 如果为true 表示为生产者独有
		 * autoDelete:当消息消费完成后是否自动删除
		 * arguments:是否传递参数 一般为空
		 */
		channel.queueDeclare("queue_1", false, false, false, null);
		
		//定义发送的消息
		String msg = "我是单工模式";
		
		
		//发送消息
		/**
		 * exchange:交换机名称   如果没有交换机 添加""串
		 * routingKey:路由KEy 信息标识符,没有添加队列名称
		 * props 携带的参数
		 * body.传递信息的字节码文件
		 */
		channel.basicPublish("", "queue_1",null, msg.getBytes());
		
		channel.close();
		connection.close();
	}
	
	//定义消费者
	@Test
	public void consumer() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException{
		//获取通道
		Channel channel = connection.createChannel();
		//定义队列
		channel.queueDeclare("queue_1", false, false, false, null);
		//定义消费者对象
		QueueingConsumer consumer = new QueueingConsumer(channel);
		//将消费者与队列绑定
		channel.basicConsume("queue_1", true, consumer);
		//循环获取信息
		while(true){
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			
			String msg = new String(delivery.getBody());
			
			System.out.println("获取信息:"+msg);	
		}
		
	}
	
	
	
	
	
	
	
	
}
