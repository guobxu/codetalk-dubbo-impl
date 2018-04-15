package me.codetalk.messaging.redis.listener.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;

import me.codetalk.apps.flow.stat.StatConstants;
import me.codetalk.apps.flow.stat.service.IUserStatService;
import me.codetalk.cache.service.CacheService;
import me.codetalk.messaging.MesgObj;
import me.codetalk.messaging.redis.listener.AbstractMessageListener;

@Component("userMesgListener")
public class UserMesgListener extends AbstractMessageListener {

	@Autowired
	private CacheService cacheService;
	
	@Autowired
	private IUserStatService userStatService;
	
	public void onMessage(Message message, byte[] pattern) {
		MesgObj msgobj = mesgToObj(message);
		
		String cacheKey = this.getClass().getName() + "-" + msgobj.getId();
		if(!cacheService.setNX(cacheKey, "X")) return;
		cacheService.expire(cacheKey, LOCK_EXPIRE_SECONDS);
		
		String type = msgobj.getType();
		if(StatConstants.MESG_USER_SIGNUP.equals(type)) {
			handleUserSignupMessage(msgobj);
		}
	}
	
	private void handleUserSignupMessage(MesgObj msgobj) {
		Map<String, Object> data = (Map<String, Object>)msgobj.getData();
		
		String date = getDate();
		userStatService.incrUserSignup(date);
	}

}
