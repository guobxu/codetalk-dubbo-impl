package me.codetalk.apps.flow.stat.service;

/**
 * 
 * @author guobxu
 *
 */
public interface IPageViewStatService {

	public void incrUserPV(Long userId, String date);
	
	public void incrPlatformPV(Integer platform, String date);
	
	public void incrGuestPV(String date);
	
}