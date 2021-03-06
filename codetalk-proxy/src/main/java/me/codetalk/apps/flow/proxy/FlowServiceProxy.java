package me.codetalk.apps.flow.proxy;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import me.codetalk.apps.service.AppService;
import me.codetalk.proxy.AbstractServiceProxy;

/**
 * Created by guobxu on 2017/4/1.
 */
@RestController("flowAppProxy")
public class FlowServiceProxy extends AbstractServiceProxy {

	public static final Logger LOGGER = LoggerFactory.getLogger(FlowServiceProxy.class);
	
	@Autowired
	@Qualifier("flowAppService")
    private AppService appService;
	
    /********************************************   Auth   ********************************************/ 
    @RequestMapping(value = { "/flow/**" }, method = RequestMethod.POST)
    public String doPost(@RequestBody Map<String, Object> params, HttpServletRequest request) {
    	LOGGER.info("In doPost...");
    	
    	extractRequestHeader(request, params);
    	
        try {
            return appService.doService(request.getRequestURI(), params);
        } catch(Exception ex) {
        	LOGGER.error(ex.getMessage(), ex);
    		
    		return errorWithKey("app_exception_msg");
    	}
    }
    
    @RequestMapping(value = { "/flow/**" }, method = RequestMethod.GET)
    public String doGet(HttpServletRequest request) {
    	LOGGER.info("In doGet...");
    	
    	Map<String, Object> params = normalizeGetParams(request.getParameterMap());
    	extractRequestHeader(request, params);
    	
        try {
        	return appService.doService(request.getRequestURI(), params);
        } catch(Exception ex) {
        	LOGGER.error(ex.getMessage(), ex);
    		
    		return errorWithKey("app_exception_msg");
    	}
    }
    
}



















