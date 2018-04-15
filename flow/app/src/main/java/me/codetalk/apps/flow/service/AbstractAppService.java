package me.codetalk.apps.flow.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import me.codetalk.Constants;
import me.codetalk.mesg.KeyedMessages;
import me.codetalk.util.JsonUtils;

public abstract class AbstractAppService implements AppService {

	@Autowired
	protected KeyedMessages km;

	private static final Map<String, String> URI_SERVICE_MAPPING = new HashMap<>();
	static {
		// URI - article
		URI_SERVICE_MAPPING.put("/flow/article/suggest", "userArticleService");
		
		// URI - comment
		URI_SERVICE_MAPPING.put("/flow/post/comment/create", "commentService");
		URI_SERVICE_MAPPING.put("/flow/post/comment/like", "commentService");
		URI_SERVICE_MAPPING.put("/flow/post/comment", "commentService");
		
		// URI - app login
		URI_SERVICE_MAPPING.put("/flow/login", "appAuthService");
		
		// URI - post
		URI_SERVICE_MAPPING.put("/flow/post", "postService");
		URI_SERVICE_MAPPING.put("/flow/post/create", "postService");
		URI_SERVICE_MAPPING.put("/flow/post/search", "postService");
		URI_SERVICE_MAPPING.put("/flow/post/listbyuser", "postService");
		URI_SERVICE_MAPPING.put("/flow/post/likebyuser", "postService");
		URI_SERVICE_MAPPING.put("/flow/post/like", "postService");
		
		// URI - tag
		URI_SERVICE_MAPPING.put("/flow/fnd/tag/list", "tagService");
		URI_SERVICE_MAPPING.put("/flow/fnd/tag/topbyday", "tagService");
		
		URI_SERVICE_MAPPING.put("/flow/fnd/usertag", "userTagService");
		URI_SERVICE_MAPPING.put("/flow/fnd/usertag/update", "userTagService");
		URI_SERVICE_MAPPING.put("/flow/fnd/usertag/add", "userTagService");
		
		// URI - follow
		URI_SERVICE_MAPPING.put("/flow/fnd/user/follow", "followService");
		URI_SERVICE_MAPPING.put("/flow/fnd/user/follow/exist", "followService");
		URI_SERVICE_MAPPING.put("/flow/fnd/user/follow/count", "followService");
	}
	
	protected String getServiceName(String uri) {
		return URI_SERVICE_MAPPING.get(uri);
	}
	
	/************************************** Utils **************************************/
	
	protected String errorWithMsg(String msg) {
		return String.format(Constants.RESPONSE_ERROR_MSG, msg);
	}
	
	protected String errorWithKey(String messageKey) {
		return String.format(Constants.RESPONSE_ERROR_MSG, km.get(messageKey));
	}
    
	protected String errorWithCodeKey(int errcode, String key) {
		return String.format(Constants.RESPONSE_ERROR_CODE_MSG, errcode, km.get(key));
	}
	
	protected String errParamMsg() {
		return errorWithKey("sys_err_param");
	}
	
	protected String errSessionMsg() {
		return errorWithCodeKey(Constants.CODE_ERROR_RELOGIN, "auth_err_session_notfound");
	}
    
	protected String successWithData(Object data) {
    	Map<String, Object> rtObj = new HashMap<String, Object>();
    	rtObj.put(Constants.KEY_CODE, Constants.CODE_SUCCESS);
    	rtObj.put(Constants.KEY_DATA, data);
    	
    	return JsonUtils.toJson(rtObj);
    }
	
}
