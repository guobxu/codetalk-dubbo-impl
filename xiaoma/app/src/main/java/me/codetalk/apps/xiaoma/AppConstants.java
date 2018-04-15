package me.codetalk.apps.xiaoma;

public final class AppConstants {

	public static final String APP_ID = "me.codetalk.xmapp";
	public static final String APP_SERVICE_KEY = "7AC0A95BF0E6294652EC12CACAE4D4FC"; // 服务票据key
	
	public static final Long AUTH_STR_EXPIRE = 15 * 60 * 1000L; // 通信认证串有效时间, 15分钟
	
	// ES
	public static final String INDEX_XMAPP = "xmapp";
	
	public static final String INDEX_TYPE_POST = "post";
	
	// MESG
	public static final String MESG_POST_CREATE = "flow-post-create";
	public static final String MESG_COMMENT_CREATE = "flow-comment-create";
	
    
    
    
}
