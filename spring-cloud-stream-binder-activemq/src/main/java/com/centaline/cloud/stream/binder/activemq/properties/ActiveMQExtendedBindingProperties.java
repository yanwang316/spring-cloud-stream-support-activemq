package com.centaline.cloud.stream.binder.activemq.properties;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.stream.binder.ExtendedBindingProperties;

@ConfigurationProperties("spring.cloud.stream.centaline.activemq")
public class ActiveMQExtendedBindingProperties implements ExtendedBindingProperties<ActiveMQConsumerProperties, ActiveMQProducerProperties>{

	private Map<String, ActiveMQBindingProperties> bindings = new HashMap<>();
	
	public Map<String, ActiveMQBindingProperties> getBindings() {
		return bindings;
	}

	public void setBindings(Map<String, ActiveMQBindingProperties> bindings) {
		this.bindings = bindings;
	}

	@Override
	public ActiveMQConsumerProperties getExtendedConsumerProperties(String channelName) {
		if (bindings.containsKey(channelName) && null != bindings.get(channelName).getConsumer()) {
			return bindings.get(channelName).getConsumer();
		}else {
			return new ActiveMQConsumerProperties();
		}
	}

	@Override
	public ActiveMQProducerProperties getExtendedProducerProperties(String channelName) {
		if (bindings.containsKey(channelName) && null != bindings.get(channelName).getProducer()) {
			return bindings.get(channelName).getProducer();
		}else {
			return new ActiveMQProducerProperties();
		}
	}

}
