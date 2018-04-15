package me.codetalk.proxy;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import me.codetalk.Constants;
import me.codetalk.mesg.KeyedMessages;
import me.codetalk.messaging.IMessageSender;
import me.codetalk.messaging.MessagingUtil;
import me.codetalk.param.checker.ParamCheckResult;
import me.codetalk.util.JsonUtils;
import me.codetalk.util.StringUtils;

public abstract class AbstractServiceProxy {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServiceProxy.class);
	
	private static final String HEADER_PFTYPE = "PF_TYPE";
	
	private static final String HEADER_AUTH = "Authorization";
	
	@Autowired
	protected KeyedMessages km;
	
	@Autowired
	protected IMessageSender mesgSender;
	
//	protected void beforePost(Map<String, Object> params, HttpServletRequest request) {
//    	extractAuthInfo(request, params);
//    	
//    	String uri = request.getRequestURI(), clientIp = request.getRemoteAddr();
//    	// message
//    	params.put(Constants.PARAM_URI, uri);
//    	params.put(Constants.PARAM_CLIENT_IP, clientIp);
//    	sendPageViewMesg(params);
//    }
//    
//	protected void beforeGet(Map<String, Object> params, HttpServletRequest request) {
//    	extractAuthInfo(request, params);
//    	
//    	String uri = request.getRequestURI(), clientIp = request.getRemoteAddr();
//    	// message
//    	params.put(Constants.PARAM_URI, uri);
//    	params.put(Constants.PARAM_CLIENT_IP, clientIp);
//    	sendPageViewMesg(params);
//    }
	
	
	/**
	 * 发送PV消息
	 * 
	 * @param params
	 */
//	protected void sendPageViewMesg(Map<String, Object> params) {
//		mesgSender.sendMessage(ProxyConstants.MESG_PAGE_VIEW, params);
//	}
	
	protected Map<String, Object> normalizeGetParams(Map<String, String[]> getParams) {
    	Map<String, Object> params = new HashMap<>();
    	
    	getParams.forEach((key, vals) -> {
    		params.put(key, vals[0]);
    	});
    	
    	return params;
    }
	
	/**
	 * 请求头信息, 包含: Authorization / PF_TYPE
	 * @param request
	 * @param params
	 */
	protected void extractRequestHeader(HttpServletRequest request, Map<String, Object> params) {
		// PF Type
		String pfTypeHeader = request.getHeader(HEADER_PFTYPE);
		try {
			params.put(Constants.PARAM_PF_TYPE, Integer.parseInt(pfTypeHeader));
		} catch(Exception ex) {
			LOGGER.error("Error parse http header: PF Type = " + pfTypeHeader);
			LOGGER.error(ex.getMessage(), ex);
		}
		
		// AUTH
		String authHeader = request.getHeader(HEADER_AUTH);
		if(StringUtils.isNull(authHeader)) return;
		
		try {
			String authStr = StringUtils.base64Dec(authHeader);
			String[] parts = authStr.split(":");
			
			if(parts.length != 3) return;
			
			params.put(Constants.PARAM_USER_ID, Long.parseLong(parts[0]));
			params.put(Constants.PARAM_ACCESS_TOKEN, parts[1]);
			params.put(Constants.PARAM_AUTH_STR, parts[2]);
		} catch(Exception ex) {
			LOGGER.error("Error parse http header: Authorization = " + authHeader);
			LOGGER.error(ex.getMessage(), ex);
		}
		
	}
	
	/**
	 * 
	 * @param request
	 * @return 0->user_id, 1->access_token, 2->auth_str
	 */
	protected Object[] extractAuthInfo(HttpServletRequest request) {
		String authHeader = request.getHeader(HEADER_AUTH);
		if(StringUtils.isNull(authHeader)) return null;
		
		try {
			String authStr = StringUtils.base64Dec(authHeader);
			String[] parts = authStr.split(":");
			
			if(parts.length != 3) return null;
			
			return new Object[] {Long.parseLong(parts[0]), parts[1], parts[2]};
		} catch(Exception ex) {
			LOGGER.error("Error parse http header: Authorization = " + authHeader);
			LOGGER.error(ex.getMessage(), ex);
		}
		
		return null;
	}
	
	/******************************* Utils *******************************/
	
	protected String errorWithMsg(String msg) {
		return String.format(Constants.RESPONSE_ERROR_MSG, msg);
	}
	
	protected String errorWithKey(String messageKey) {
		return String.format(Constants.RESPONSE_ERROR_MSG, km.get(messageKey));
	}
    
	protected String errorWithCodeKey(int errcode, String key) {
		return String.format(Constants.RESPONSE_ERROR_CODE_MSG, errcode, km.get(key));
	}
    
	protected String successWithData(Object data) {
    	Map<String, Object> rtObj = new HashMap<String, Object>();
    	rtObj.put(Constants.KEY_CODE, Constants.CODE_SUCCESS);
    	rtObj.put(Constants.KEY_DATA, data);
    	
    	return JsonUtils.toJson(rtObj);
    }
	
}
