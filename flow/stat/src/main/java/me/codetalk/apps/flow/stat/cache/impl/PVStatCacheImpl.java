package me.codetalk.apps.flow.stat.cache.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.codetalk.apps.flow.stat.cache.IPVStatCache;
import me.codetalk.stat.StatSupport;

@Component("pageViewCache")
public class PVStatCacheImpl implements IPVStatCache {

	private static final Logger LOGGER = LoggerFactory.getLogger(PVStatCacheImpl.class);
	
	public static final String STAT_USER_PV = "STAT-USER-PV-";			// 用户PV
	public static final String STAT_PF_PV = "STAT-PLATFORM-PV-";		// 平台PV
	public static final String STAT_GUEST_PV = "STAT-GUEST-PV";			// 游客PV
	
	@Autowired
	private StatSupport statCache;
	
	@Override
	public void incrUserPV(Long userId, String date) {
		String statType = STAT_USER_PV + date + "-";
		
		statCache.incrRangeStatById(userId, statType, 1);
	}

	@Override
	public void incrPlatformPV(Integer platform, String date) {
		String statType = STAT_PF_PV + date;
		
		statCache.incrStatById(String.valueOf(platform), statType, 1);
	}

	@Override
	public void incrGuestPV(String date) {
		statCache.incrStatById(date, STAT_GUEST_PV, 1);
	}

}
