# spring-cloud-stream-support-activemq

It's required ActiveMQ 5.13.0 or more

The ActiveMQ Binder use 'Virtual Topic' feature to implement the Group and Partition, 
so use it after make sure you open the function in ActiveMQ

Any Configuration Properties, See properties class
com.centaline.cloud.stream.binder.activemq.properties.ActiveMQExtendedBindingProperties

Any Configuration, See configuration class
com.centaline.cloud.stream.binder.activemq.config.ActiveMQMessageChannelBinderConfiguration
