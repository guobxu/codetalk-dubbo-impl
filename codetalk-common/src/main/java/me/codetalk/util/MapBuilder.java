package me.codetalk.util;

import java.util.HashMap;
import java.util.Map;

public final class MapBuilder<K, V> {
		
	private Map<K, V> map = new HashMap<>();
	
	public MapBuilder<K, V> put(K key, V val) {
		map.put(key, val);
			
		return this;
	}
		
	public Map<K, V> build() {
		return map;
	}
		
}