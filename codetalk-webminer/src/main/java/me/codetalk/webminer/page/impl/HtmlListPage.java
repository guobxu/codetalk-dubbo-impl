package me.codetalk.webminer.page.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import me.codetalk.webminer.page.Page;
import me.codetalk.webminer.page.PageAttr;

/**
 * HTML列表页面
 * @author guobxu
 *
 */
public abstract class HtmlListPage extends AbstractListPage {

	public HtmlListPage(String url) {
		super(url);
	}
	
	protected abstract Document getDocument(String url) throws IOException;
	
	protected abstract HtmlPage getSubPage(String pageUrl);
	
	public List<Page> fetchPages(PageAttr attr) throws IOException {
		List<Page> pages = new ArrayList<Page>();
		
		Document doc = getDocument(url);
		Elements subPages = doc.select(attr.getEl());
		subPages.forEach(el -> {
			Page subPage = getSubPage(el.absUrl(attr.getName()));
			pages.add(subPage);
		});
		
		return pages;
	}

}
