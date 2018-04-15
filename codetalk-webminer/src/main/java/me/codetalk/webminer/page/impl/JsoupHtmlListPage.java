package me.codetalk.webminer.page.impl;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JsoupHtmlListPage extends HtmlListPage {

	public JsoupHtmlListPage(String url) {
		super(url);
	}

	@Override
	protected Document getDocument(String url) throws IOException {
		return Jsoup.connect(url).get();
	}

	@Override
	protected HtmlPage getSubPage(String pageUrl) {
		return new JsoupHtmlPage(pageUrl);
	}

}
