package me.codetalk.param.checker;

import java.util.Map;

public interface IParamChecker {

	/**
	 * 检查POST请求参数
	 * @param uri
	 * @param params
	 * @return
	 */
	public ParamCheckResult checkParams(String uri, Map<String, Object> params);

	/**
	 * 判断该uri是否需要参数检查
	 * 
	 * @param uri
	 * @return
	 */
	public boolean needCheck(String uri);
	
}
