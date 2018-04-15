package me.codetalk.webminer.page.impl;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JsoupHtmlPage extends HtmlPage {

	public JsoupHtmlPage(String url) {
		super(url);
	}

	@Override
	protected Document getDocument() throws IOException {
		return Jsoup.connect(url).get();
	}

}
