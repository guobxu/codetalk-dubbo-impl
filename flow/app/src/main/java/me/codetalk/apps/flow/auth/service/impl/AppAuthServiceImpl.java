package me.codetalk.apps.flow.auth.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.codetalk.Constants;
import me.codetalk.apps.flow.AppConstants;
import me.codetalk.apps.flow.auth.service.AppAuthService;
import me.codetalk.apps.flow.service.AbstractAppService;
import me.codetalk.auth.entity.Session;
import me.codetalk.auth.exception.AuthServiceException;
import me.codetalk.auth.service.SessionService;
import me.codetalk.cipher.service.ICipherService;

@Service("appAuthService")
public class AppAuthServiceImpl extends AbstractAppService implements AppAuthService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppAuthServiceImpl.class);
	
	@Autowired
	private ICipherService cipherService;
	
	@Autowired
	private SessionService sessionService;
	
	/**
	 * 服务登录
	 *  2. 认证结果密文
	 *		Base64(AES("32Byte传输密钥+登录名+Token", KEY("32Byte随机密钥")))
	 *		传输密钥用作后续的接口调用中加密私密信息, Token用作接口用户认证
	 *	3. 服务票据串
	 *		Base64(AES("32Byte传输密钥+登录名+Token", KEY("32Byte服务密钥")))
 	 *
	 */
	@Override
	public void appLogin(Long userId, String authStr, String svcTicket) throws AuthServiceException {
		// 服务票据明文: 32Byte传输密钥+用户ID/登录名+Token
		String svcTicketText = null;
		try {
			svcTicketText = cipherService.decipher(svcTicket, AppConstants.APP_SERVICE_KEY);
		} catch(Exception ex) {
			throw new AuthServiceException(km.get("sys_err_cipher"), ex);
		}
		
		String tktKey = null, tkUserId = null, tkUserLogin = null, tkToken = null;
		int tkLen = svcTicketText.length(), keyLen = 32, tokenLen = 36;
		if(tkLen < keyLen + tokenLen + 3) {
			throw new AuthServiceException(km.get("auth_err_svcticket"));
		} else {
			tktKey = svcTicketText.substring(0, keyLen);
			
			int slashPos = svcTicketText.indexOf('/');
			tkUserId = svcTicketText.substring(keyLen, slashPos);
			tkUserLogin = svcTicketText.substring(slashPos + 1, tkLen - tokenLen);
			tkToken = svcTicketText.substring(tkLen - tokenLen);
		}
		
		// 比较uid
		if(!String.valueOf(userId).equals(tkUserId)) {
			throw new AuthServiceException(km.get("auth_err_svcticket"));
		}
		
		// 通信认证
		requestAuth(authStr, tktKey, tkUserLogin);
		
		// 保存会话
		Session session = new Session();
		session.setUserId(userId);
		session.setUserLogin(tkUserLogin);
		session.setAccessToken(tkToken);
		session.setTransportKey(tktKey);
		
		Long currentMillis = System.currentTimeMillis();
		session.setCreateDate(currentMillis);
		session.setLastUpdate(currentMillis);
		
		sessionService.createSession(session);
	}

	@Override
	public Session requestAuth(Long userId, String accessToken, String authStr) throws AuthServiceException {
		Session session = sessionService.findSession(userId, accessToken);
		if(session == null) {
			throw new AuthServiceException(km.get("auth_err_session_notfound"));
		}
		
		requestAuth(authStr, session.getTransportKey(), session.getUserLogin());
		
		return session;
	}
	
	private void requestAuth(String authStr, String transportKey, String userLogin) throws AuthServiceException {
		// 通信认证串: Base64(AES("UserLogin +时间戳", KEY("32Byte传输密钥"))), 时间戳长度13位
		String authText = null;
		try {
			authText = cipherService.decipher(authStr, transportKey);
		} catch(Exception ex) {
			throw new AuthServiceException(km.get("sys_err_cipher"), ex);
		}
		
		String authUserLogin = null;
		Long authMills = null;
		int authLen = authText.length(), millisLen = 13;
		if(authLen <= millisLen) {
			throw new AuthServiceException(km.get("auth_err_authstr"));
		} else {
			authUserLogin = authText.substring(0, authLen - 13);
			try {
				authMills = Long.parseLong(authText.substring(authLen - 13));
			} catch(NumberFormatException ex) {
				LOGGER.error(ex.getMessage(), ex);
				
				throw new AuthServiceException(km.get("auth_err_authstr"), ex);
			}
			
		}

		// 对比登录名
		if(!userLogin.equals(authUserLogin)) {
			throw new AuthServiceException(km.get("auth_err_authstr"));
		}
		
		// 检查认证串时间10分钟以内有效
		if(Math.abs(System.currentTimeMillis() - authMills) > AppConstants.AUTH_STR_EXPIRE) {
			throw new AuthServiceException(km.get("auth_err_authstr"));
		}
	}
	
	/*************************** App Service ***************************/
	static final String URI_APP_LOGIN = "/flow/login";
	
	public String doService(String uri, Map<String, Object> params) {
		if(URI_APP_LOGIN.equals(uri)) {
			return appLogin(params); 
		}
		
		return null;
	}
	
	/**
	 * Params:
	 * {
		"pf_type":0 
		"user_id": 0L // 用户id
		"access_token": "xx" // token
		"svc_ticket_str": "xx" // 服务票据
		}
	 *
	 * @param params
	 * @return
	 */
	private String appLogin(Map<String, Object> params) {
		LOGGER.info("In appLogin... Params = " + params);
		
		Long userIdParam = Long.parseLong(params.get("user_id").toString());
		String authStrParam = params.get("auth_str").toString(), 
				svcTktParam = params.get("svc_ticket_str").toString();
		
		try {
			appLogin(userIdParam, authStrParam, svcTktParam);
			
			return Constants.RESPONSE_SUCCESS;
		} catch(AuthServiceException ex) {
			return errorWithMsg(ex.getMessage());
		}
	}

}
