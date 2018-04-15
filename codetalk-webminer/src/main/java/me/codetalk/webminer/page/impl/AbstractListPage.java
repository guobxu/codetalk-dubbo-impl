package me.codetalk.webminer.page.impl;

import java.io.IOException;
import java.util.Map;

import me.codetalk.webminer.page.ListPage;
import me.codetalk.webminer.page.PageAttr;
import me.codetalk.webminer.page.PageData;

/**
 * 基础实现, 使用根路径查找所有的子页面
 * @author guobxu
 *
 */
public abstract class AbstractListPage extends AbstractPage implements ListPage {

	protected AbstractListPage(String url) {
		super(url);
	}
	
	@Override
	public PageData fetchEntity(Map<String, PageAttr> attrMap) throws IOException {
		throw new UnsupportedOperationException("not implemented!");
	}

	
}
