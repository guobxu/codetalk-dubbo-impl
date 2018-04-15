package me.codetalk.mesg;

import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import me.codetalk.util.StringUtils;

/**
 * 
 * @author guobxu
 *
 */
@Component
public class KeyedMessages {

    @Autowired
    private MessageSource messageSource;

    public String get(String code) {
        return messageSource.getMessage(code, null, Locale.CHINESE);
    }

    public String getWithParams(String code, Map<String, String> params) {
        String mesg = get(code);

        if(params != null) {
            for(String key : params.keySet()) {
                mesg = StringUtils.replaceNoRegex(mesg, "{" + key + "}", params.get(key));
            }
        }

        return mesg;
    }
    
    public String getWithParams(String code, String...params) {
        String mesg = get(code);

        for(int i = 0, len = params.length / 2; i < len; i++) {
        	mesg = StringUtils.replaceNoRegex(mesg, "{" + params[2*i] + "}", params[2*i + 1]);
        }
        
        return mesg;
    }
    
}
