package me.codetalk.stat.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.codetalk.Constants;
import me.codetalk.cache.service.CacheService;
import me.codetalk.stat.StatSupport;

/**
 * 基于redis的统计支持
 * @author guobxu
 *
 */
@Component("cacheStatSupport")
public class HashStatSupport implements StatSupport {

	@Autowired
	private CacheService cacheService;
	
	@Override
	public Long incrStatById(String entityId, String statType, long delta) {
		return cacheService.hIncrBy(statType, entityId, delta);
	}
	
	public Long incrRangeStatById(Long entityId, String statType, long delta) {
		String cacheKey = getStatCacheKey(statType, entityId);
		String field = String.valueOf(entityId);
		
		return cacheService.hIncrBy(cacheKey, field, delta);
	}

	@Override
	public Long decrStatById(String entityId, String statType, long delta) {
		return cacheService.hIncrBy(statType, entityId, -1 * delta);
	}
	
	public Long decrRangeStatById(Long entityId, String statType, long delta) {
		String cacheKey = getStatCacheKey(statType, entityId);
		String field = String.valueOf(entityId);
		
		return cacheService.hIncrBy(cacheKey, field, -1 * delta);
	}
	
	@Override
	public Long incrStatById(Long entityId, String statType, long delta) {
		return incrStatById(String.valueOf(entityId), statType, delta);
	}

	@Override
	public Long decrStatById(Long entityId, String statType, long delta) {
		return decrStatById(String.valueOf(entityId), statType, delta);
	}

	public Long incrStat(String statType, long delta) {
		return cacheService.incrBy(statType, delta);
	}
	
	public Long decrStat(String statType, long delta) {
		return cacheService.incrBy(statType, -1 * delta);
	}
	
	@Override
	public Long getStatById(String entityId, String statType) {
		Object num =  cacheService.hGet(statType, entityId);
		
		return num == null ? 0 : Long.valueOf(num.toString());
	}
	
	@Override
	public Long getRangeStatById(Long entityId, String statType) {
		String cacheKey = getStatCacheKey(statType, entityId);
		Object num =  cacheService.hGet(cacheKey, String.valueOf(entityId));
		
		return num == null ? 0 : Long.valueOf(num.toString());
	}
	
	public Map<String, Long> getStats(List<String> entityIdList, String statType) {
		List statNums = (List)cacheService.hMGet(statType, entityIdList);
		
		Map<String, Long> statMap = new HashMap<>();
		for(int i = 0; i < entityIdList.size(); i++) {
			Object num = statNums.get(i);
			statMap.put(entityIdList.get(i), num == null ? 0 : Long.valueOf(num.toString()));
		}
		
		return statMap;
	}
	
	public Map<Long, Long> getRangeStats(List<Long> entityIdList, String statType) {
		Map<String, List<String>> idPartions = new HashMap<String, List<String>>();
		for(Long entityId : entityIdList) {
			String cacheKey = getStatCacheKey(statType, entityId);
			List<String> idPartion = idPartions.get(cacheKey);
			if(idPartion == null) {
				idPartion = new ArrayList<String>();
				idPartions.put(cacheKey, idPartion);
			}
			
			idPartion.add(String.valueOf(entityId));
		}
		
		// result
		Map<Long, Long> statMap = new HashMap<Long, Long>();
		for(Map.Entry<String, List<String>> kv : idPartions.entrySet()) {
			String cacheKey = kv.getKey();
			List<String> pidPartion = kv.getValue();
			List statNums = (List)cacheService.hMGet(cacheKey, pidPartion);
			
			for(int i = 0; i < pidPartion.size(); i++) {
				String entityId = pidPartion.get(i);
				Object num = statNums.get(i);
				statMap.put(Long.valueOf(entityId), num == null ? 0 : Long.valueOf(num.toString()));
			}
		}
		
		return statMap;
	}
	
	protected String getStatCacheKey(String statType, Long entityId) {
		Integer range = getHashRange(statType);
		
		long tmp = entityId / range;
		Long low = range * tmp + 1, high = range * ( tmp + 1 );

		String cacheKey = statType + low + "-" + high;
		
		return cacheKey;
	}

	// TODO
	public Integer getHashRange(String statType) {
		return Constants.CACHE_STAT_HASH_RANGE;
	}

}