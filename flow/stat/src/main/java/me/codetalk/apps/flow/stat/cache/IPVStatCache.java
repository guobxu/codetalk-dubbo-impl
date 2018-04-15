package me.codetalk.apps.flow.stat.cache;

public interface IPVStatCache {

	public void incrUserPV(Long userId, String date);
	
	public void incrPlatformPV(Integer platform, String date);
	
	public void incrGuestPV(String date);
	
}
