package me.codetalk.webminer.page;

import java.io.IOException;
import java.util.Map;

/**
 * 单个页面
 * @author guobxu
 *
 */
public interface Page {

	// 页面URL
	public String getUrl();
	
	/**
	 * 获取entity
	 * 
	 * @param attrMap attr -> attr path
	 * @return
	 * @throws IOException 网络IO异常
	 */
	public PageData fetchEntity(Map<String, PageAttr> attrMap) throws IOException;
	
}
