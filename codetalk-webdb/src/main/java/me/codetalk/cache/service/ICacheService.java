package me.codetalk.cache.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.RedisCallback;

/**
 * Created by Administrator on 2017/4/6.
 */
public interface ICacheService {

	public Long incr(String key);
	
	public Long incrBy(String key, long delta);
	
    public boolean exists(String key);

    public boolean set(byte[] key, byte[] value, long activeTime);

    public boolean set(String key, Object obj, long activeTime);

    public boolean set(String key, Object obj);

    public long delete(String key);
    
    public long delete(String... keys);

    public void expire(String key, long activeTime);
    
    public Object get(String key);
    
    // hash
    public Object hGet(String hash, String key);
    
    public Long hDel(String hash, String key);
    
    public List<Object> hMGet(String hash, List<String> keys);
    
    public List<Object> hMGet(String hash, String[] keys);
  
    // 返回值仅用于判断是新增加键值对, 或是更新了已有键值对
    public boolean hSet(String hash, String key, Object obj);

    public long hIncrBy(String hash, String key, long delta);
    
    public Object doCallback(RedisCallback callback);
    
    boolean setNX(String key, Object obj);
    
    boolean setNX(byte[] key, byte[] value);
    
	Set<byte[]> keys(String key);

    boolean setNX(String key, Object obj,long activeTime);
    
	boolean hsetNX(String key, String field, Object obj);
	
	Map<byte[], byte[]> hGetAll(String key);
	
	Long hDel(String key,List<String> keys);

}
