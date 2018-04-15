package me.codetalk.apps.xiaoma.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import me.codetalk.Constants;
import me.codetalk.apps.service.AppService;
import me.codetalk.auth.aop.ThreadSession;
import me.codetalk.mesg.KeyedMessages;
import me.codetalk.util.JsonUtils;

public abstract class AbstractAppService implements AppService {

	@Autowired
	protected KeyedMessages km;

	@Autowired
	protected ThreadSession threadSession;
	
	private static final Map<String, String> URI_SERVICE_MAPPING = new HashMap<>();
	static {
		// URI - comment
		URI_SERVICE_MAPPING.put("/xiaoma/api/post/comment/create", "commentService");
		URI_SERVICE_MAPPING.put("/xiaoma/api/post/comment", "commentService");
		
		// URI - app login
		URI_SERVICE_MAPPING.put("/xiaoma/api/login", "appAuthService");
		
		// URI - post
		URI_SERVICE_MAPPING.put("/xiaoma/api/post", "postService");
		URI_SERVICE_MAPPING.put("/xiaoma/api/post/create", "postService");
		URI_SERVICE_MAPPING.put("/xiaoma/api/post/like", "postService");
		
		// URI - project
		URI_SERVICE_MAPPING.put("/xiaoma/api/user/project/list", "userProjectService");
		URI_SERVICE_MAPPING.put("/xiaoma/api/user/project/create", "userProjectService");
		URI_SERVICE_MAPPING.put("/xiaoma/api/user/project/update", "userProjectService");
		URI_SERVICE_MAPPING.put("/xiaoma/api/user/project/delete", "userProjectService");
		
		// URI - work
		URI_SERVICE_MAPPING.put("/xiaoma/api/user/work/list", "userWorkService");
		URI_SERVICE_MAPPING.put("/xiaoma/api/user/work/create", "userWorkService");
		URI_SERVICE_MAPPING.put("/xiaoma/api/user/work/update", "userWorkService");
		URI_SERVICE_MAPPING.put("/xiaoma/api/user/work/delete", "userWorkService");
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
