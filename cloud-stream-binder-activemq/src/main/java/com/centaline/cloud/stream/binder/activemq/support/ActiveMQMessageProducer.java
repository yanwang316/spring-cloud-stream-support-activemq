package com.centaline.cloud.stream.binder.activemq.support;

import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.jms.JmsProperties.AcknowledgeMode;
import org.springframework.context.Lifecycle;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.jms.DefaultJmsHeaderMapper;
import org.springframework.integration.jms.JmsHeaderMapper;
import org.springframework.integration.support.DefaultMessageBuilderFactory;
import org.springframework.integration.support.MessageBuilderFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.messaging.MessageChannel;

public class ActiveMQMessageProducer implements MessageProducer, MessageListener, Lifecycle, InitializingBean{

	private MessageChannel outputChannel;
	
	private MessageBuilderFactory messageBuilderFactory = new DefaultMessageBuilderFactory();
	
	private JmsHeaderMapper headerMapper = new DefaultJmsHeaderMapper();
	
	private MessageConverter messageConverter = new SimpleMessageConverter();
	
	private DefaultMessageListenerContainer container;
	
	private JmsTemplate jmsTemplate;
	
	private String selector;
	
	private Destination destination;
	
	private boolean isTransaction = false;
	
	public void setTransaction(boolean isTransaction) {
		this.isTransaction = isTransaction;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	@Override
	public void setOutputChannel(MessageChannel outputChannel) {
		this.outputChannel = outputChannel;
	}

	@Override
	public MessageChannel getOutputChannel() {
		return this.outputChannel;
	}

	@Override
	public void onMessage(Message message) {
		Object payload;
		try {
			payload = this.messageConverter.fromMessage(message);
			Map<String, Object> headers = this.headerMapper.toHeaders(message);
			org.springframework.messaging.Message<?> requestMessage = messageBuilderFactory.withPayload(payload).copyHeaders(headers).build();
			outputChannel.send(requestMessage);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

	public void init() {
		DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
		if (!StringUtils.isEmpty(this.selector)) {
			container.setMessageSelector(this.selector);
		}
		container.setMessageListener(this);
		container.setDestination(destination);
		ConnectionFactory producerConnectionFactory = createProducerConnectionFactory();
		container.setConnectionFactory(producerConnectionFactory);
		container.setSessionAcknowledgeMode(AcknowledgeMode.AUTO.getMode());
		container.setSessionTransacted(this.isTransaction);
		this.container = container;
	}
	
	private ConnectionFactory createProducerConnectionFactory() {
		// 构建接收者
		ConnectionFactory factory = jmsTemplate.getConnectionFactory();
		if (factory instanceof PooledConnectionFactory && ((PooledConnectionFactory) factory).getConnectionFactory() instanceof ActiveMQConnectionFactory) {
			PooledConnectionFactory receivePool = new PooledConnectionFactory();
			ActiveMQConnectionFactory internalFactory = new ActiveMQConnectionFactory();
			BeanUtils.copyProperties(factory, receivePool);
			BeanUtils.copyProperties(((PooledConnectionFactory) factory).getConnectionFactory(), internalFactory);
			receivePool.setConnectionFactory(internalFactory);
			return receivePool;
		}else if (factory instanceof ActiveMQConnectionFactory) {
			ActiveMQConnectionFactory receivePool = new ActiveMQConnectionFactory();
			BeanUtils.copyProperties(factory, receivePool);
			return receivePool;
		}
		return jmsTemplate.getConnectionFactory();
	}

	@Override
	public void start() {
		this.container.start();
	}

	@Override
	public void stop() {
		this.container.stop();
	}

	@Override
	public boolean isRunning() {
		return this.container.isRunning();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		init();
		// 创建生产者队列
		this.container.afterPropertiesSet();
	}
}
