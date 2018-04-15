package me.codetalk.auth.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.codetalk.auth.AuthConstants;
import me.codetalk.auth.entity.Session;
import me.codetalk.auth.exception.AuthServiceException;
import me.codetalk.auth.service.AppAuthService;
import me.codetalk.auth.service.SessionService;
import me.codetalk.cipher.service.ICipherService;
import me.codetalk.mesg.KeyedMessages;

@Service("appAuthService")
public class AppAuthServiceImpl implements AppAuthService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppAuthServiceImpl.class);
	
	@Autowired
	private ICipherService cipherService;
	
	@Autowired
	private SessionService sessionService;
	
	@Autowired
	private KeyedMessages km;
	
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
		if(Math.abs(System.currentTimeMillis() - authMills) > AuthConstants.AUTH_STR_EXPIRE) {
			throw new AuthServiceException(km.get("auth_err_authstr"));
		}
	}

}
