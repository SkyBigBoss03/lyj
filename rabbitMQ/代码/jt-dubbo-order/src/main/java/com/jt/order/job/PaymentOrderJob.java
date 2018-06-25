package com.jt.order.job;

import java.util.Date;

import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.jt.order.mapper.OrderMapper;

//定义一个任务
public class PaymentOrderJob extends QuartzJobBean{

	//定时执行任务
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		//获取spring容器对象
		ApplicationContext applicationContext = 
				(ApplicationContext) context.getJobDetail().getJobDataMap().get("applicationContext");
		
		//从容器中获取订单对象
		OrderMapper orderMapper = applicationContext.getBean(OrderMapper.class);
		
		//获取2天之前的时间
		Date date = new DateTime().minusDays(2).toDate(); //计算2天之前数据
		
		orderMapper.updatePayment(date);
		
		System.out.println("定时任务执行成功");
	}

}
