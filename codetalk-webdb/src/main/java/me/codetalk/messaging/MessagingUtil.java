package me.codetalk.messaging;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.codetalk.util.JsonUtils;

public final class MessagingUtil {

	public static MesgObj convertAsMesgObj(String mesgType, Object obj) {
		Map<String, Object> data = JsonUtils.toMap(obj);
		
		return convertAsMesgObj(mesgType, data);
	}
	
	public static MesgObj convertAsMesgObj(String mesgType, Map<String, Object> data) {
		return _convertAsMesgObj(mesgType, data);
	}

	public static MesgObj convertAsMesgObj(String mesgType, List<Map<String, Object>> data) {
		return _convertAsMesgObj(mesgType, data);
	}
	
	public static MesgObj convertAsMesgObj(String mesgType, String s) {
		return _convertAsMesgObj(mesgType, s);
	}
	
	private static MesgObj _convertAsMesgObj(String mesgType, Object data) {
		MesgObj mo = new MesgObj();
		mo.setId(UUID.randomUUID().toString());
		mo.setType(mesgType);
		mo.setData(data);
		mo.setCreateDate(System.currentTimeMillis());
		
		return mo;
	}
	
}
