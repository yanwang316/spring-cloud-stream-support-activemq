package com.centaline.cloud.stream.binder.activemq.properties;

public class ActiveMQBindingProperties {

	private ActiveMQConsumerProperties consumer = new ActiveMQConsumerProperties();
	
	private ActiveMQProducerProperties producer = new ActiveMQProducerProperties();

	public ActiveMQConsumerProperties getConsumer() {
		return consumer;
	}

	public void setConsumer(ActiveMQConsumerProperties consumer) {
		this.consumer = consumer;
	}

	public ActiveMQProducerProperties getProducer() {
		return producer;
	}

	public void setProducer(ActiveMQProducerProperties producer) {
		this.producer = producer;
	}
	
}
