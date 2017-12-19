package com.centaline.cloud.stream.binder.activemq.properties;

public class ActiveMQConsumerProperties{

	private String destination;
	
	private String partition;
	
	private boolean transaction;
	
	public boolean isTransaction() {
		return transaction;
	}

	public void setTransaction(boolean transaction) {
		this.transaction = transaction;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getPartition() {
		return partition;
	}

	public void setPartition(String partition) {
		this.partition = partition;
	}
	
}
