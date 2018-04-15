package me.codetalk;

public final class Constants {

	public static final String ENCODING_UTF8 = "UTF-8";

    public static final int CONST_N = 0; 		// 0 表示否 
	public static final int CONST_Y = 1;		// 1 表示是
	
	// Platform Type
 	public static final int PF_WEB = 1;
 	public static final int PF_ANDROID = 2;
 	public static final int PF_IOS = 3;
    
 	// Parameters
 	public static final String PARAM_URI = "uri";
 	public static final String PARAM_CLIENT_IP = "client_ip";
 	public static final String PARAM_USER_ID = "user_id";
  	public static final String PARAM_ACCESS_TOKEN = "access_token";
  	public static final String PARAM_AUTH_STR = "auth_str";
  	public static final String PARAM_PF_TYPE = "pf_type";
 	
    // Response 
    public static final int CODE_SUCCESS = 1;
    public static final int CODE_ERROR = 2;
    
    public static final String RESPONSE_SUCCESS = "{\"ret_code\": 1}";
    public static final String RESPONSE_ERROR = "{\"ret_code\": 2}";
    
    public static final String RESPONSE_SUCCESS_DATA = "{\"ret_code\": 1, \"ret_data\":\"%s\"}";
    
    public static final String RESPONSE_ERROR_MSG = "{\"ret_code\":2, \"ret_msg\":\"%s\"}";
    public static final String RESPONSE_SUCCESS_MSG = "{\"ret_code\":1, \"ret_msg\":\"%s\"}";
    
    public static final String RESPONSE_ERROR_CODE_MSG = "{\"ret_code\": %d, \"ret_msg\":\"%s\"}";
    
    public static final String KEY_CODE = "ret_code";
    public static final String KEY_MSG = "ret_msg";
    public static final String KEY_DATA = "ret_data";
	
    // stat
    public static final int CACHE_STAT_HASH_RANGE = 10000; // 默认值
    
  	// Common error codes
    public static final int CODE_ERROR_RELOGIN = 2001;	// 需重新登录
    
    // like
    public static final int ACTION_LIKE = 1;			// 点赞
    public static final int ACTION_UNLIKE = 2; 			// 取消
    
    // follow
    public static final int ACTION_FOLLOW = 1;			// 关注
    public static final int ACTION_UNFOLLOW = 2; 		// 取消
    
}










