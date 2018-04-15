package me.codetalk.webminer;

public final class MinerConstants {

	public static final String HTTP_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36";
	
	public static final String PROTO_HTTP = "http://";
	
	public static final String PROTO_HTTPS = "https://";
	
	// attr types
	public static final int ATTR_TYPE_HTML = 1;
	public static final int ATTR_TYPE_TEXT = 2;
	public static final int ATTR_TYPE_HTML_FIRST = 3;
	public static final int ATTR_TYPE_TEXT_FIRST = 4;
	
	// topic prefix
	public static final String KAFKA_TOPIC_PREFIX = "webminer-";
	
	public static final String TARGET_WEBMINER_PREFIX = "webminer-";
	
	// page status
	public static final int PAGE_STATUS_NOT_FETCHED = 1; 	// 未抓取
	public static final int PAGE_STATUS_FETCHED = 2; 		// 未抓取
	public static final int PAGE_STATUS_ERR = 3; 			// 抓取出错
	
	// list type
	public static final int LIST_TYPE_HTML = 1;				// html - jsoup
	public static final int LIST_TYPE_JSON = 2;				// json
	public static final int LIST_TYPE_HTML_HTTPCLIENT = 3; 	// html - http client
	public static final int LIST_TYPE_JSON_HTTPCLIENT = 4; 	// json - http client
	
	// page type
	public static final int PAGE_TYPE_HTML = 1;							// page -html
	public static final int PAGE_TYPE_HTML_HTTPCLIENT = 2;				// page -http client
	
}
