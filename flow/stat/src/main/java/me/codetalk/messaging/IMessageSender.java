package me.codetalk.messaging;

public interface IMessageSender {

	/**
	 * 异步发送消息
	 * @param target
	 * @param msgobj
	 */
	public void sendMessage(String target, MesgObj msgobj);
	
	/**
	 * 发送消息
	 * @param target 	消息目标
	 * @param msgobj 	消息
	 * @param isAsync	是否异步
	 */
	public void sendMessage(String target, MesgObj msgobj, boolean isAsync);
	
}
