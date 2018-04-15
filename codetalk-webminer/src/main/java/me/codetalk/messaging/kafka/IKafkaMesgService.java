package me.codetalk.messaging.kafka;

public interface IKafkaMesgService {

	public void sendMessage(String topic, Object obj);
	
	public void sendMessage(String topic, int partition, Object obj);
	
}
