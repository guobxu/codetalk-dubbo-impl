package me.codetalk.auth;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by guobxu on 18/7/2017.
 */
public class AuthConstants {

	public static final String APP_FLOW = "me.codetalk.flowapp"; // 
	public static final String SERVICE_KEY_FLOW = "C6B0E40CBFED1E72F3993BD32954471C"; // 服务票据key
	
	public static final Long AUTH_STR_EXPIRE = 15 * 60 * 1000L; // 通信认证串有效时间, 15分钟
	
	// 服务密钥 app -> key
	public static final Map<String, String> APP_SERVICE_KEYS = new HashMap<>();
	static {
		APP_SERVICE_KEYS.put(APP_FLOW, SERVICE_KEY_FLOW);
	}
	
	// channel: user-create
	public static final String MESG_USER_SIGNUP = "auth-user-signup";
	
	
    
	
}






