package me.codetalk.apps.flow.stat.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.codetalk.apps.flow.stat.cache.IPVStatCache;
import me.codetalk.apps.flow.stat.service.IPageViewStatService;

@Service("pvStatService")
public class PageViewStatServiceImpl implements IPageViewStatService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PageViewStatServiceImpl.class);
	
	@Autowired
	private IPVStatCache pvCache;
	
	@Override
	public void incrUserPV(Long userId, String date) {
		pvCache.incrUserPV(userId, date);
	}

	@Override
	public void incrPlatformPV(Integer platform, String date) {
		pvCache.incrPlatformPV(platform, date);
	}

	@Override
	public void incrGuestPV(String date) {
		pvCache.incrGuestPV(date);
	}

}
