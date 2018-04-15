package me.codetalk.webminer.page;

import java.io.IOException;
import java.util.List;

/**
 * 列表页面
 * @author guobxu
 *
 */
public interface ListPage extends Page {

	/**
	 * 获取所有Sub Pages
	 * 
	 * @param attr		- sub page el & attr
	 * @return
	 * @throws IOException 网络IO异常
	 */
	public List<Page> fetchPages(PageAttr attr) throws IOException ;
	
}

