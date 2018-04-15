package me.codetalk.auth.service.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import me.codetalk.auth.cache.SignupCache;
import me.codetalk.auth.entity.SignupSession;
import me.codetalk.auth.exception.AuthServiceException;
import me.codetalk.auth.service.SignupService;
import me.codetalk.mesg.KeyedMessages;
import me.codetalk.util.ImageUtils;
import me.codetalk.util.StringUtils;

@Service("signupService")
public class SignupSerivceImpl implements SignupService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SignupSerivceImpl.class);
	
	@Autowired
	private KeyedMessages km;
	
	@Autowired
	private SignupCache signupCache;
	
	@Value("${auth.signup.verify-code-len}")
	private int vcodeLen;
	
	@Value("${auth.signup.verify-code-width}")
	private int vcodeWidth;
	
	@Value("${auth.signup.verify-code-height}")
	private int vcodeHeight;
	
	@Value("${auth.signup.verify-code-bgcolor}")
	private String vcodeBgColor;
	
	@Value("${auth.signup.verify-code-textcolor}")
	private String vcodeTextColor;
	
	@Value("${auth.signup.verify-code-format}")
	private String vcodeFormat;
	
	@Override
	public SignupSession initSession() throws AuthServiceException {
		String vcode = StringUtils.randomNum(vcodeLen);
		String imgdata = null;
		try {
			imgdata = ImageUtils.base64CodeImg(vcode, vcodeWidth, vcodeHeight, vcodeBgColor, vcodeTextColor, vcodeFormat); 
		} catch(IOException ioe) {
			throw new AuthServiceException( km.get("auth_err_signup_genvcode"), ioe );
		}
		
		// session
		SignupSession ssess = new SignupSession();
		String ssid = StringUtils.uuid();
		ssess.setId(ssid);
		ssess.setVcode(vcode);
		ssess.setImgdata(imgdata);
		ssess.setPasswdKey(StringUtils.randomKey32());
		
		// cache
		signupCache.setSessionById(ssess);
		
		return ssess;
	}

	@Override
	public SignupSession findSessionById(String ssid) {
		return signupCache.getSessionById(ssid);
	}

	@Override
	public void deleteSessionById(String ssid) {
		signupCache.deleteSessionById(ssid);
	}

}






















