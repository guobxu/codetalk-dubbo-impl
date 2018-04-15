package me.codetalk.auth.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import me.codetalk.Constants;
import me.codetalk.auth.AuthConstants;
import me.codetalk.auth.aop.ThreadSession;
import me.codetalk.auth.aop.annotation.EnableThreadSession;
import me.codetalk.auth.entity.Session;
import me.codetalk.auth.entity.SignupSession;
import me.codetalk.auth.entity.User;
import me.codetalk.auth.exception.AuthServiceException;
import me.codetalk.auth.service.AppAuthService;
import me.codetalk.auth.service.AuthService;
import me.codetalk.auth.service.SessionService;
import me.codetalk.auth.service.SignupService;
import me.codetalk.auth.service.UserService;
import me.codetalk.cipher.CipherConstants;
import me.codetalk.cipher.service.ICipherService;
import me.codetalk.mesg.KeyedMessages;
import me.codetalk.messaging.IMessageSender;
import me.codetalk.util.JsonUtils;
import me.codetalk.util.MapBuilder;
import me.codetalk.util.MapUtils;
import me.codetalk.util.StringUtils;


/**
 * 
 * @author guobxu
 *
 * Created: 04/14
 *
 */
@Service("authService")
public class AuthServiceImpl implements AuthService {

	private static Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

	@Autowired
	private UserService userService;
	
	@Autowired
	private ICipherService cipherService;

	@Autowired
	private KeyedMessages km;
	
	@Autowired
	private SignupService signupService;
	
	@Autowired
	private SessionService sessionService;
	
	@Autowired
	private ThreadSession threadSession;
	
	@Autowired
	private IMessageSender mesgSender;
	
	@Autowired
	private AppAuthService appAuthService;
	
	// URI
	static final String URI_AUTH_SIGNUP_KEYCODE = "/auth/signup/keycode";
	static final String URI_AUTH_SIGNUP = "/auth/signup";
	static final String URI_AUTH_LOGIN = "/auth/login";
	static final String URI_AUTH_RESETPWD = "/auth/resetpwd";
	static final String URI_AUTH_USERINFO = "/auth/user";
	static final String URI_AUTH_USERUPDATE = "/auth/user/update";
	
	@EnableThreadSession
	public String doService(String uri, Map<String, Object> params) {
		if(URI_AUTH_USERINFO.equals(uri)) {
			return userInfo(params);
		} else if(URI_AUTH_USERUPDATE.equals(uri)) {
			return updateUser(params);
		} else if(URI_AUTH_SIGNUP_KEYCODE.equals(uri)) {
			return keycode(params);
		} else if(URI_AUTH_SIGNUP.equals(uri)) {
			return signup(params);
		} else if(URI_AUTH_LOGIN.equals(uri)) {
			return login(params);
		} else if(URI_AUTH_RESETPWD.equals(uri)) {
			return resetPwd(params);
		}
		
		return errorWithKey("sys_uri_notfound");
	}
	
	/**
	 *
	 * Params:
	 * {
		"pf_type":0 
		"user_id": 0L // 用户ID
		"access_token": "xx" // token
		"user_bgcover": "xx" // 背景图片(可选)
		"user_profile": "xx" // 用户头像(可选)
		"user_name": "xx" // 用户名
		"user_signature": "xx" // 签名(可选)
		"user_location": "xx" // 位置(可选)
		"user_site": "xx" // 网站(可选)
		}
	 * 
	 * @return
	 */
	private String updateUser(Map<String, Object> params) {
		if(!threadSession.authorized()) {
			return errorWithCodeKey(Constants.CODE_ERROR_RELOGIN, "auth_err_session_notfound");
		}
		
		User userForUpdate = new User();
		userForUpdate.setId(threadSession.getUserId());
		userForUpdate.setLogin(threadSession.getUserLogin());
		userForUpdate.setBgcover(StringUtils.toString(params.get("user_bgcover"), false));
		userForUpdate.setProfile(StringUtils.toString(params.get("user_profile"), false));
		userForUpdate.setName(StringUtils.toString(params.get("user_name"), false));
		userForUpdate.setSignature(StringUtils.toString(params.get("user_signature"), false));
		userForUpdate.setLocation(StringUtils.toString(params.get("user_location"), false));
		userForUpdate.setSite(StringUtils.toString(params.get("user_site"), false));
		
		try {
			userService.updateUser(userForUpdate);
		} catch(AuthServiceException ex) {
			LOGGER.error(ex.getMessage(), ex);
			
			return errorWithMsg(ex.getMessage());
		}
		
		return Constants.RESPONSE_SUCCESS;
	}

	/**
	 * 参数列表：
		user_id = 0L // 用户ID
		access_token = "xx" // token
		pf_type=0 // 平台类型1 Web 2 Android 3 iOS
		user_id_get=0 // 需获取信息的用户id
	 * 
	 * @param params
	 * @return
	 */
	private String userInfo(Map<String, Object> params) {
		LOGGER.info("In userInfo... Params = " + params);
		
		Long userIdGetParam = Long.parseLong(params.get("user_id_get").toString());
		
		if(!threadSession.authorized()) {
			return errorWithCodeKey(Constants.CODE_ERROR_RELOGIN, "auth_err_session_notfound");
		}
		
		User user = userService.findUserById(userIdGetParam);
		if(user == null) {
			return errorWithKey("auth_err_usernotfound");
		} else {
			user.setPassword(null);
			return successWithData(user);
		}
	}

	/**
	 * 2.1.1	注册验证码&加密key(游客)
	 * 
	 * 返回:
	 * {
		"ret_code":0 //返回码
		"ret_msg":"xx" //可选，返回错误描述
		"ret_data": {
			"signup_sid":"xxx" // 注册SID(uuid)
			"signup_passwd_key": "xx" // 密码加密key(长度32)
			"signup_code_img":"xxx" //注册验证码图片base64
		}
		}
	 * @return
	 */
	private String keycode(Map<String, Object> params) {
		LOGGER.info("In keycode...");
		
		SignupSession ssess = null;
		try {
			ssess = signupService.initSession();
		} catch(AuthServiceException ase) {
			LOGGER.error(ase.getMessage(), ase);
			
			return errorWithMsg(ase.getMessage());
		}
		
		Map<String, String> data = new MapBuilder<String, String>()
										.put("signup_sid", ssess.getId())
										.put("signup_passwd_key", ssess.getPasswdKey())
										.put("signup_code_img", ssess.getImgdata()).build();
		return successWithData(data);
	}

	/**
	 * 2.1.2	账号注册(游客)
	 * {
		"signup_sid ":"xx" // 注册SID
		"user_login ": "xx" // 登录名, 最长30
		"passwd_str": "xx" // 用户密码
		"signup_code":"xx" //注册验证码
		}
	 * 
	 * @param data
	 * @return
	 */
	private String signup(Map<String, Object> params) {
		LOGGER.info("In signup... Params = " + params);
		
		String ssid = params.get("signup_sid").toString(),
				loginParam = params.get("user_login").toString(), 
				pwdParam = params.get("passwd_str").toString(),
				vcodeParam = params.get("signup_code").toString();
		
		SignupSession ssess = signupService.findSessionById(ssid);
		if(ssess == null) {
			return errorWithKey("auth_err_ssess_notfound");
		}
		
		// check vcode
		if(!ssess.getVcode().equals(vcodeParam)) {
			return errorWithKey("auth_err_signup_vcode");
		}
		
		// password
		String realPwd = null;
		try {
			realPwd = cipherService.decipher(pwdParam, ssess.getPasswdKey());
		} catch(Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			
			return errorWithKey("sys_err_cipher");
		}
		
		try {
			Long userId = userService.createUser(loginParam, realPwd);
			
			signupService.deleteSessionById(ssid);
			
			// send message
			Map<String, Object> data = MapUtils.toMap("user_id", userId);
			mesgSender.sendMessage(AuthConstants.MESG_USER_SIGNUP, data);
			
			return Constants.RESPONSE_SUCCESS;
		} catch(AuthServiceException ex) {
			LOGGER.error(ex.getMessage(), ex);
			
			return errorWithMsg(ex.getMessage());
		}
	}

	/**
	 * 2.1.3	认证登录(游客)
	 * Param:
	 * {
		"user_login": "xx" // 用户名
		"login_auth_str": "xx" // 认证登录密文 Base64(AES("32Byte随机密钥+登录名", KEY("变换后的密码")))
		}
	 * 
	 * @param params
	 * @return
	 */
	private String login(Map<String, Object> params) {
		LOGGER.info("In login... Params = " + params);
		
		String userLoginParam = params.get("user_login").toString(),
				authStrParam = params.get("login_auth_str").toString(), // 32 Client Key + src type / user name
				appIdParam = params.get("app_id").toString(); 
		
		Integer pfTypeParam = Integer.parseInt(params.get("pf_type").toString());
		
		// service key
		String serviceKey = AuthConstants.APP_SERVICE_KEYS.get(appIdParam);
		if(serviceKey == null) {
			return errorWithKey("auth_err_svckeynotfound");
		}
		
		User user = userService.findUserByLogin(userLoginParam);
		if(user == null) {
			return errorWithKey("auth_err_usernotfound");
		}
		
		String clearText = null;
		try {
			clearText = cipherService.decipher(authStrParam, user.getPassword());
		} catch(Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			
			return errorWithKey("auth_err_cipher");
		}
				
		// parse
		if(clearText.length() <= CipherConstants.CIPHER_KEY_LEN) {
			return errorWithKey("auth_err_login_authstr");
		}
		String clientKey = clearText.substring(0, CipherConstants.CIPHER_KEY_LEN), // 客户端随机32位key
				loginExt = clearText.substring(CipherConstants.CIPHER_KEY_LEN); // 登录名
		if(!userLoginParam.equals(loginExt)) {
			return errorWithKey("auth_err_login_authstr");
		}
		
		LOGGER.info("User Login success. Login = " + userLoginParam);
		
		// init session
		Session session = new Session();
		session.setUserId(user.getId());
		session.setUserLogin(user.getLogin());
		
		// access token
		String accessToken = StringUtils.uuid(); 
		session.setAccessToken(accessToken);
		
		// transport key
		String transportKey = StringUtils.randomKey32();
		session.setTransportKey(transportKey);
		session.setPfType(pfTypeParam);
		
		Long currentMillis = System.currentTimeMillis();
		session.setCreateDate(currentMillis);
		session.setLastUpdate(currentMillis);
		
		sessionService.createSession(session);
		
		// 认证结果串： 32Byte传输密钥+登录名+Token
		// 服务票据串: 32Byte传输密钥+用户ID/登录名+Token
		String authRetStr = null, svcTicketStr = null;
		try {
			authRetStr = cipherService.cipher(transportKey + userLoginParam + accessToken, clientKey);
			svcTicketStr = cipherService.cipher(transportKey + user.getId() + "/" + userLoginParam + accessToken, serviceKey);
		} catch(Exception ex) {
			return errorWithKey("auth_err_cipher");
		}
		Map<String, Object> retData = new HashMap<String, Object>();
		retData.put("user_id", user.getId());
		retData.put("auth_ret_str", authRetStr);
		retData.put("svc_ticket_str", svcTicketStr);
		
		return successWithData(retData);
	}

	

	/**
	 * 2.1.4	重置密码
	 * Param:
	 * {
		"user_id": 0L // 用户ID
		"access_token": "xx" // token
		"passwd_str": "xx" 
		}
	 *
	 * @param params
	 * @return
	 */
	private String resetPwd(Map<String, Object> params) {
		LOGGER.info("In resetPwd... Params = " + params);
		
		Long userIdParam = Long.parseLong(params.get("user_id").toString());
		String tokenParam = params.get("access_token").toString(), 
				passwdParam = params.get("passwd_str").toString();
		
		if(!threadSession.authorized()) {
			return errorWithCodeKey(Constants.CODE_ERROR_RELOGIN, "auth_err_session_notfound");
		}
		
		String clearText = null;
		try {
			clearText = cipherService.decipher(passwdParam, threadSession.getTransportKey());
		} catch(Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			
			return errorWithKey("auth_err_cipher");
		}
		
		// validate 
		int slashPos = clearText.lastIndexOf("/");
		if(slashPos == -1 || !threadSession.getUserLogin().equals(clearText.substring(slashPos + 1))) {
			return errorWithKey("auth_err_passwdstr");
		}
		
		String transPwd = clearText.substring(0, slashPos); // 转换后的密码
		userService.resetPassword(userIdParam, transPwd);
		
		
		return Constants.RESPONSE_SUCCESS;
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
    
	protected String successWithData(Object data) {
    	Map<String, Object> rtObj = new HashMap<String, Object>();
    	rtObj.put(Constants.KEY_CODE, Constants.CODE_SUCCESS);
    	rtObj.put(Constants.KEY_DATA, data);
    	
    	return JsonUtils.toJson(rtObj);
    }

	/************************ RPC  ************************/
	
	@Override
	public boolean isLoggedIn(Long userId, String accessToken, String authStr) {
		Session session = null;
		try {
			session = appAuthService.requestAuth(userId, accessToken, authStr);
		} catch(AuthServiceException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		
		return session != null;
	}

	@Override
	public User getUser(Long userId) {
		return userService.findUserById(userId);
	}

	@Override
	public List<User> getUserList(List<Long> userIdList) {
		Assert.notEmpty(userIdList, "Param [userIdList] is null or empty");
		
		return userService.findUsersByIdList(userIdList);
	}
	
	@Override
	public Map<Long, User> getUsersAsMap(Long... userIds) {
		List<Long> userIdList = Arrays.asList(userIds);
		
		return getUsersAsMap(userIdList);
	}
	
	@Override
	public Map<Long, User> getUsersAsMap(List<Long> userIdList) {
		Assert.notEmpty(userIdList, "Param [userIdList] is null or empty");
		
		List<User> userList = userService.findUsersByIdList(userIdList);
		Map<Long, User> userMap = new HashMap<>();
		for(User user : userList) {
			userMap.put(user.getId(), user);
		}
		
		return userMap;
	}

}

























