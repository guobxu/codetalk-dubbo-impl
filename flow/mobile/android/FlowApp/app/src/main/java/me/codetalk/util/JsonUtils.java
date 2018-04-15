package me.codetalk.util;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

	private static ObjectMapper MAPPER = new ObjectMapper();
	
	public static String toJson(Object obj) {
		try {
			String json = MAPPER.writeValueAsString(obj);
			
			return json;
		} catch(JsonProcessingException ex) {
			ex.printStackTrace();
			
			return null;
		}
	}
	
	public static Map<String, Object> toMap(Object obj) {
		Map<String, Object> map = MAPPER.convertValue(obj, new TypeReference<Map<String, Object>>() {});
		
		return map;
	}
	
	public static Object fromJsonObj(String json, Class clazz) {
		try {
			Object obj = MAPPER.readValue(json, clazz);
			
			return obj;
		} catch(Exception ex) {
			ex.printStackTrace();
			
			return null;
		}
	}
	
	public static List fromJsonArray(String json, Class clazz) {
		try {
			List list = MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
			
			return list;
		} catch(Exception ex) {
			ex.printStackTrace();
			
			return null;
		}
	}
	
	public static <T> T readMap(Map<String, Object> map, Class<T> clazz) {
		return MAPPER.convertValue(map, clazz);
	}
	
	public static <T> List<T> readList(List list, Class<T> clazz) {
		List<T> rtList = MAPPER.convertValue(list, MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
		
		return rtList;
	}
	
	public static void main(String[] args) {
//		String json = "[{\"t\": \"tag1\", \"s\": 1, \"e\": 6}, {\"t\": \"tag2\", \"s\": 67, \"e\": 89}]";
////		List<PostTag> tagmapList = JsonUtils.fromJsonArray(json, PostTag.class);
//		List<Map<String, Object>> tagmapList = (List<Map<String, Object>>)JsonUtils.fromJsonObj(json, List.class);
//		
//		System.out.println(tagmapList);
	}
	
}


