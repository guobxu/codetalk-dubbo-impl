package me.codetalk.cache.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.RedisCallback;

/**
 * Created by Administrator on 2017/4/6.
 */
public interface CacheService {

	public static final Long DEFAULT_CACHE_EXPIRE = 2 * 60 * 60L; // 默认过期时间2小时
	
	public Long incr(String key);
	
	public Long incrBy(String key, long delta);
	
    public boolean exists(String key);

    public boolean set(byte[] key, byte[] value, long activeTime);

    public boolean set(String key, Object obj, long activeTime);

    public boolean set(String key, Object obj);

    public boolean set(String key, Object obj, boolean defaultExpire);
    
    public long delete(String key);
    
    public long delete(String... keys);

    public Object get(String key);
    
    // hash
    public Object hGet(String hash, String key);
    
    public Long hDel(String hash, String key);
    
    public List<Object> hMGet(String hash, List<String> keys);
    
    public List<Object> hMGet(String hash, String[] keys);
  
    // 返回值仅用于判断是新增加键值对, 或是更新了已有键值对
    public boolean hSet(String hash, String key, Object obj);
    
    public void hMSet(String hash, String[] keys, Object[] objs);
    
    public void hMSet(String hash, List<String> keys, List<Object> objs);

    public long hIncrBy(String hash, String key, long delta);
    
    public Object doCallback(RedisCallback callback);
    
    boolean setNX(String key, Object obj);
    
    boolean setNX(byte[] key, byte[] value);
    
	Set<byte[]> keys(String key);

    boolean setNX(String key, Object obj,long activeTime);
    
	boolean hsetNX(String key, String field, Object obj);
	
	Map<byte[], byte[]> hGetAll(String key);
	
	Long hDel(String key,List<String> keys);
	
	boolean expire(String key, long seconds);
	
	// 指定member的分数 + scoreDelta
	Double zIncrBy(String key, String member, double scoreDelta);
	
	/**
	 * Note:
	 * 1. zset按分数从低到高排序
	 * 2. 下标从0开始, -1 表示最后一个, -2表示倒数第二个; 一次类推
	 * 3. 示例: start = 0, stop = -1 表示获取所有的
	 * @param key
	 * @param start
	 * @param end
	 * @return 如果没有找到 返回空(非null)
	 */
	List<String> zRange(String key, long start, long end);
	
	List<String> zRevRange(String key, long start, long end);
	
	/**
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return Ordered Map
	 */
	Map<String, Double> zRangeWithScore(String key, long start, long end);
	
	Map<String, Double> zRevRangeWithScore(String key, long start, long end);

	Long zRem(String key, List<String> memberList);
	
	Long zRem(String key, String[] members);
	
	Long zLen(String key);
	
	void zAdd(String key, String member, Double score);
	
	void zAdd(String key, List<String> memberList, List<Double> scoreList);
	
}




