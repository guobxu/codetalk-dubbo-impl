package me.codetalk.auth.aop;

import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import me.codetalk.Constants;
import me.codetalk.apps.flow.auth.service.AppAuthService;
import me.codetalk.auth.aop.annotation.EnableThreadSession;
import me.codetalk.auth.entity.Session;
import me.codetalk.auth.exception.AuthServiceException;

@Aspect
@Configuration
public class ThreadSessionAop {

	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadSessionAop.class);
	
	@Autowired
	private ThreadSession threadSession;
	
	@Autowired
	private AppAuthService appAuthService;
	
	@Around(value="@annotation(annot)")
	public Object enableThreadSession(ProceedingJoinPoint pjp, EnableThreadSession annot) throws Throwable {
		Object[] args = pjp.getArgs();
		Map<String, Object> params = (Map<String, Object>)args[1];
		
		Object uidObj = params.get(Constants.PARAM_USER_ID), 
				tokenObj = params.get(Constants.PARAM_ACCESS_TOKEN),
				authStrObj = params.get(Constants.PARAM_AUTH_STR);
		if(uidObj != null && tokenObj != null && authStrObj != null) {
			Session session = null;
			try {
				session = appAuthService.requestAuth((Long)uidObj, (String)tokenObj, (String)authStrObj);
			} catch(AuthServiceException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
			
			if(session != null) {
				LOGGER.info("Request Auth success. User Id = " + uidObj);
				threadSession.set(session);
			} else {
				LOGGER.info("Request Auth failure. User Id = " + uidObj);
			}
		}
		
		Object rt = null;
		try {
			rt = pjp.proceed(args);
		} finally {
			threadSession.clear();
		}
		
		return rt;
	}
	
}
