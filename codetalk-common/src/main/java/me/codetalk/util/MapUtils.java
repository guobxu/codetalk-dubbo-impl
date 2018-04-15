package me.codetalk.util;

import java.util.HashMap;
import java.util.Map;

public final class MapUtils {
	
	public static Map<String, Object> toMap(String key, Object val) {
		Map<String, Object> map = new HashMap<>();
		map.put(key, val);
		
		return map;
	}
	
	public static Map<String, Object> toMap(String[] keys, Object[] vals) {
		Map<String, Object> map = new HashMap<>();
		for(int i = 0; i < keys.length; i++) {
			map.put(keys[i], vals[i]);
		}
		
		return map;
	}
	

}
