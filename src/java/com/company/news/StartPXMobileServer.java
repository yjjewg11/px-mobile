package com.company.news;

import org.apache.log4j.Logger;

import com.company.mq.MQConstants;
import com.company.mq.MQUtils;
import com.company.mq.PxBaseDoJob;
import com.company.mq.RedisSubscribeClient;

/**
 * 启动服务时，加载数据
 * @author liumingquan
 *context\applicationContext.xml 配置
 *
 */
public class StartPXMobileServer {
	private static Logger logger = Logger.getLogger("StartPXMobileServer");
	
	
	public static  void init(){
		
		
			//启动发布与订阅
				String queueName=ProjectProperties.getProperty("mq.queueName", MQConstants.QueueName_PxTask);
				//初始化发布队列
				MQUtils.initPubClient(queueName);
		
//		String enable=ProjectProperties.getProperty("mq.subscribe", "true");
//		if(enable.equals("true")){
//			MQUtils.startSubscribeClient(MQConstants.QueueName_PxTask);
//			logger.info("start mq.subscribe.queueName="+MQConstants.QueueName_PxTask);
//		}else{
//			logger.info("no use mq.subscribe.queueName="+MQConstants.QueueName_PxTask);
//		}
	
	}
	
	static{
		init();
	}
}
