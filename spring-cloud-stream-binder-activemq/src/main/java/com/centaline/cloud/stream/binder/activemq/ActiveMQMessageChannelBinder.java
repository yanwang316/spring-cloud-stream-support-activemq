package com.centaline.cloud.stream.binder.activemq;


import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.stream.binder.AbstractMessageChannelBinder;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.binder.ExtendedPropertiesBinder;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.core.env.Environment;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.jms.JmsSendingMessageHandler;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import com.centaline.cloud.stream.binder.activemq.mapper.PartitionJmsHeaderMapper;
import com.centaline.cloud.stream.binder.activemq.mapper.StreamActivemqHeaderProperty;
import com.centaline.cloud.stream.binder.activemq.properties.ActiveMQConsumerProperties;
import com.centaline.cloud.stream.binder.activemq.properties.ActiveMQExtendedBindingProperties;
import com.centaline.cloud.stream.binder.activemq.properties.ActiveMQProducerProperties;
import com.centaline.cloud.stream.binder.activemq.provisioning.ActiveMQQueueProvisioner;
import com.centaline.cloud.stream.binder.activemq.support.ActiveMQMessageProducer;

public class ActiveMQMessageChannelBinder 
	extends AbstractMessageChannelBinder<ExtendedConsumerProperties<ActiveMQConsumerProperties>, 
			ExtendedProducerProperties<ActiveMQProducerProperties>, ActiveMQQueueProvisioner>
	implements ExtendedPropertiesBinder<MessageChannel, ActiveMQConsumerProperties, ActiveMQProducerProperties>{
	
	private ActiveMQExtendedBindingProperties extendedProperties;
	
	private static final String VIRTUAL_TOPIC_PREFIX = "VirtualTopic.";
	
	private static final String VIRTUAL_QUEUE_PREFIX = "Consumer.";
	
	private JmsTemplate jmsTemplate;
	
	private Environment env;

	public void setEnv(Environment env) {
		this.env = env;
	}

	public ActiveMQMessageChannelBinder(
			ActiveMQQueueProvisioner provisioningProvider) {
		super(true, new String[0], provisioningProvider);
	}
	
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setExtendedProperties(ActiveMQExtendedBindingProperties extendedProperties) {
		this.extendedProperties = extendedProperties;
	}

	@Override
	protected MessageHandler createProducerMessageHandler(ProducerDestination destination,
			ExtendedProducerProperties<ActiveMQProducerProperties> producerProperties, MessageChannel errorChannel)
			throws Exception {
		JmsSendingMessageHandler handler = new JmsSendingMessageHandler(jmsTemplate);
		Destination destinationObject = null;
		PartitionJmsHeaderMapper headerMapper = new PartitionJmsHeaderMapper();
		if (!StringUtils.isEmpty(producerProperties.getExtension().getPartition())) {
			headerMapper.put(StreamActivemqHeaderProperty.PARTITION_PEOPERTY, producerProperties.getExtension().getPartition());
		}
		if (producerProperties.getExtension().isTransaction()) {
			jmsTemplate.setSessionTransacted(true);
		}
		destinationObject = new ActiveMQTopic(getVirtualTopic(destination.getName()));
		handler.setDestination(destinationObject);
		handler.setHeaderMapper(headerMapper);
		return handler;
	}

	@Override
	protected MessageProducer createConsumerEndpoint(ConsumerDestination destination, String group,
			ExtendedConsumerProperties<ActiveMQConsumerProperties> properties) throws Exception {
		ActiveMQMessageProducer producer = new ActiveMQMessageProducer();
		String realGroup = StringUtils.isEmpty(group) ? getDefalutGroup() : group;
		Destination destinationObject = new ActiveMQQueue(getQueueForVirtualTopic(destination.getName(), realGroup));
		producer.setDestination(destinationObject);
		if (!StringUtils.isEmpty(properties.getExtension().getPartition())) {
			producer.setSelector("" + StreamActivemqHeaderProperty.PARTITION_PEOPERTY +"='"+ properties.getExtension().getPartition() +"'");
		}else {
			producer.setSelector("" + StreamActivemqHeaderProperty.PARTITION_PEOPERTY +" is null");
		}
		if (properties.getExtension().isTransaction()) {
			producer.setTransaction(true);
		}
		producer.setJmsTemplate(jmsTemplate);
		return producer;
	}

	@Override
	public ActiveMQConsumerProperties getExtendedConsumerProperties(String channelName) {
		return this.extendedProperties.getExtendedConsumerProperties(channelName);
	}

	@Override
	public ActiveMQProducerProperties getExtendedProducerProperties(String channelName) {
		return this.extendedProperties.getExtendedProducerProperties(channelName);
	}
	
	private String getVirtualTopic(String destination) {
		return VIRTUAL_TOPIC_PREFIX + destination;
	}
	
	private String getQueueForVirtualTopic(String destination, String group) {
		return VIRTUAL_QUEUE_PREFIX + group + "." + VIRTUAL_TOPIC_PREFIX + destination;
	}
	
	private String getDefalutGroup() {
		return env.getRequiredProperty("spring.application.name");
	}
}
