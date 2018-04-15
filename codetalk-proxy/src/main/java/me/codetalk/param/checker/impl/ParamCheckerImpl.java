package me.codetalk.param.checker.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.codetalk.mesg.KeyedMessages;
import me.codetalk.param.checker.IParamChecker;
import me.codetalk.param.checker.ParamCheckResult;
import me.codetalk.param.type.Param;

@Component("paramChecker")
public class ParamCheckerImpl implements IParamChecker {

	@Autowired
	private KeyedMessages km;
	
	// 映射
	static Map<String, Param[]> URI_PARAM_MAPPING = new HashMap<String, Param[]>();

	@Override
	public boolean needCheck(String uri) {
		return URI_PARAM_MAPPING.containsKey(uri);
	}
	
	@Override
	public ParamCheckResult checkParams(String uri, Map<String, Object> params) {
		Param[] paramArr = URI_PARAM_MAPPING.get(uri);
		if(paramArr == null || paramArr.length == 0) return ParamCheckResult.VALID;
		
		for(Param param : paramArr) {
			Object val = params.get(param.getName());
			if(!param.isValid(val)) {
				String errMsgFmt = km.get("common_err_param");
				return ParamCheckResult.invalidWithMsg(String.format(errMsgFmt, param.getName()));
			}
		}
		
		return ParamCheckResult.VALID;
	}


}

















