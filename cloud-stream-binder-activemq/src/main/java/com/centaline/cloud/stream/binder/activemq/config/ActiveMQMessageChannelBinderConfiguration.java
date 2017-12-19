package com.centaline.cloud.stream.binder.activemq.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.config.codec.kryo.KryoCodecAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.integration.codec.Codec;
import org.springframework.jms.core.JmsTemplate;

import com.centaline.cloud.stream.binder.activemq.ActiveMQMessageChannelBinder;
import com.centaline.cloud.stream.binder.activemq.properties.ActiveMQExtendedBindingProperties;
import com.centaline.cloud.stream.binder.activemq.provisioning.ActiveMQQueueProvisioner;

@Configuration
@AutoConfigureAfter({JmsAutoConfiguration.class})
@Import({PropertyPlaceholderAutoConfiguration.class, KryoCodecAutoConfiguration.class})
@EnableConfigurationProperties({ActiveMQExtendedBindingProperties.class})
public class ActiveMQMessageChannelBinderConfiguration {

	@Autowired
	private ActiveMQExtendedBindingProperties extendedProperties;
	
	@Bean
	@ConditionalOnMissingBean
	ActiveMQMessageChannelBinder activeMQMessageChannelBinder(JmsTemplate jmsTemplate,Codec codec, Environment env) {
		ActiveMQMessageChannelBinder binder = new ActiveMQMessageChannelBinder(new ActiveMQQueueProvisioner());
		binder.setEnv(env);
		binder.setCodec(codec);
		binder.setExtendedProperties(extendedProperties);
		binder.setJmsTemplate(jmsTemplate);
		return binder;
	}
}
