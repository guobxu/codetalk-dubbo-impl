package me.codetalk.auth.proxy;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import me.codetalk.auth.service.AuthService;
import me.codetalk.proxy.AbstractServiceProxy;

@RestController
public class AuthServiceProxy extends AbstractServiceProxy {

	public static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceProxy.class);
	
	@Autowired
	private AuthService authService;
	
    /********************************************   Auth   ********************************************/ 
    @RequestMapping(value = "/auth/**", method = RequestMethod.POST)
    public String doPost(@RequestBody Map<String, Object> params, HttpServletRequest request) {
    	LOGGER.info("In doPost...");
    	
    	extractRequestHeader(request, params);
    	
        try {
            return authService.doService(request.getRequestURI(), params);
        } catch(Exception ex) {
        	LOGGER.error(ex.getMessage(), ex);
    		
    		return errorWithKey("auth_exception_msg");
    	}
    }
    
    @RequestMapping(value = "/auth/**", method = RequestMethod.GET)
    public String doGet(HttpServletRequest request) {
    	LOGGER.info("In doGet...");
    	
    	Map<String, Object> params = normalizeGetParams(request.getParameterMap());
    	extractRequestHeader(request, params);
    	
        try {
            return authService.doService(request.getRequestURI(), params);
        } catch(Exception ex) {
        	LOGGER.error(ex.getMessage(), ex);
    		
    		return errorWithKey("auth_exception_msg");
    	}
    }
    
    
}
































