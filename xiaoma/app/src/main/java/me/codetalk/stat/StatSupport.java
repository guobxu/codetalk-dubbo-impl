package me.codetalk.stat;

import java.util.List;
import java.util.Map;

// 统计支持
public interface StatSupport {

	public Long incrStat(String statType, long delta);
	
	public Long decrStat(String statType, long delta);
	
	public Long incrStatById(Long entityId, String statType, long delta);
	
	public Long incrStatById(String entityId, String statType, long delta);
	
	public Long decrStatById(Long entityId, String statType, long delta);
	
	public Long decrStatById(String entityId, String statType, long delta);
	
	public Long getStatById(String entityId, String statType);
	
	public Long getRangeStatById(Long entityId, String statType);
	
	public Map<String, Long> getStats(List<String> entityIdList, String statType);

	// By Range
	public Long incrRangeStatById(Long entityId, String statType, long delta);
	
	public Long decrRangeStatById(Long entityId, String statType, long delta);
	
	public Map<Long, Long> getRangeStats(List<Long> entityIdList, String statType);
}