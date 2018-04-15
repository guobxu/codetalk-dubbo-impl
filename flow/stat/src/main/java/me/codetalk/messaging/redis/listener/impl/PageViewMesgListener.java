package me.codetalk.messaging.redis.listener.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;

import me.codetalk.apps.flow.stat.StatConstants;
import me.codetalk.apps.flow.stat.service.IPageViewStatService;
import me.codetalk.cache.service.CacheService;
import me.codetalk.messaging.MesgObj;
import me.codetalk.messaging.redis.listener.AbstractMessageListener;

@Component("pvMesgListener")
public class PageViewMesgListener extends AbstractMessageListener {

private static final Logger LOGGER = LoggerFactory.getLogger(PageViewMesgListener.class);
	
	@Autowired
	private CacheService cacheService;
	
	@Autowired
	private IPageViewStatService pvStatService;
	
	public void onMessage(Message message, byte[] pattern) {
		MesgObj msgobj = mesgToObj(message);
		
		String cacheKey = this.getClass().getName() + "-" + msgobj.getId();
		if(!cacheService.setNX(cacheKey, "X")) return;
		cacheService.expire(cacheKey, LOCK_EXPIRE_SECONDS);
		
		String type = msgobj.getType();
		if(StatConstants.MESG_PAGE_VIEW.equals(type)) {
			handlePVMessage(msgobj);
		}
	}
	
	private void handlePVMessage(MesgObj msgobj) {
		Map<String, Object> data = (Map<String, Object>)msgobj.getData();
		
		Map<String, Object> params = (Map<String, Object>)msgobj.getData();
		Object userIdObj = params.get("user_id"), pfTypeObj = params.get("pf_type");
		String date = getDate();
		
		if( userIdObj != null ) {
			pvStatService.incrUserPV(Long.parseLong(userIdObj.toString()), date);
		} else {
			pvStatService.incrGuestPV(date);
		}
		
		pvStatService.incrPlatformPV(Integer.parseInt(pfTypeObj.toString()), date);
	}

}
