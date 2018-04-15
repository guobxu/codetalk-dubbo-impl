package me.codetalk.lock.service;

public interface LockService {

	public static final long DEFAULT_TIMEOUT = 5 * 60L; // 默认释放锁时间, 5分钟(单位: 秒)
	
	/**
	 * 获取锁, 在默认过期时间(5分钟)后释放
	 * 
	 * @param resource
	 * @return
	 */
	public boolean lock(Object resource);
	
	/**
	 * 获取锁, 并在指定时间后释放
	 * @param resource
	 * @param seconds	单位: 秒
	 * @return
	 */
	public boolean lock(Object resource, long seconds);
	
}
