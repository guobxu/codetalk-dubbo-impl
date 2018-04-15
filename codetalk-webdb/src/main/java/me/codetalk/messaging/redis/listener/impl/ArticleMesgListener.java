package me.codetalk.messaging.redis.listener.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;

import me.codetalk.cache.service.ICacheService;
import me.codetalk.messaging.MesgObj;
import me.codetalk.messaging.redis.listener.AbstractMessageListener;
import me.codetalk.util.CollectionUtils;
import me.codetalk.util.StringUtils;
import me.codetalk.webdb.Constants;
import me.codetalk.webdb.entity.Article;
import me.codetalk.webdb.service.ArticleService;

@Component("articleMesgListener")
public class ArticleMesgListener extends AbstractMessageListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(QuestMesgListener.class);
	
	@Autowired
	private ICacheService cacheService;
	
	@Autowired
	private ArticleService articleService;
	
	public void onMessage(Message message, byte[] pattern) {
		MesgObj mesgobj = mesgToObj(message);
		
		String cacheKey = this.getClass().getName() + "-" + mesgobj.getId();
		if(!cacheService.setNX(cacheKey, "X")) return;
		cacheService.expire(cacheKey, LOCK_EXPIRE_SECONDS);
		
		String type = mesgobj.getType();
		
		if(Constants.CHN_ARTICLE_DZONE.equals(type)) {
			dzArticleMigrate(mesgobj);
		} else if(Constants.CHN_ARTICLE_JCG.equals(type)) {
			jcgArticleMigrate(mesgobj);
		} else if(Constants.CHN_ARTICLE_BAELDUNG.equals(type)) {
			baelArticleMigrate(mesgobj);
		} else if(Constants.CHN_ARTICLE_MKYONG.equals(type)) {
			mkyArticleMigrate(mesgobj);
		} else if(Constants.CHN_ARTICLE_INFOQ.equals(type)) {
			infoqArticleMigrate(mesgobj);
		} else if(Constants.CHN_ARTICLE_TNS.equals(type)) {
			tnsArticleMigrate(mesgobj);
		} else if(Constants.CHN_ARTICLE_JVNS.equals(type)) {
			jvnsArticleMigrate(mesgobj);
		} else if(Constants.CHN_ARTICLE_MARTIN.equals(type)) {
			mfArticleMigrate(mesgobj);
		} else if(Constants.CHN_ARTICLE_TUTSPLUS.equals(type)) {
			tutsplusArticleMigrate(mesgobj);
		}
	}
	
	public void dzArticleMigrate(MesgObj mesgobj) {
		LOGGER.info("In dzArticleMigrate...Receive mesg data = " + mesgobj);
		
		Map<String, Object> data = (Map<String, Object>)mesgobj.getData();
		
		// attr list -> map
		List attrs = (List)data.get("attrs");
		Map<String, String> attrsMap = new HashMap<>();
		for(Object attrObj : attrs) {
			Map<String, Object> attrMap = (Map<String, Object>)attrObj;
			
			String key = (String)attrMap.get("key"), val = (String)attrMap.get("val");
			if("article_content".equals(key) && StringUtils.isNull(val)) return;
			
			attrsMap.put(key, val);
		}
		
		Article article = new Article();
		article.setUuid(mesgobj.getId());
		String pageUrl = (String)data.get("pageUrl");
		article.setUrl(pageUrl);
		article.setSite((String)data.get("site"));
		article.setTitle(attrsMap.get("article_title"));
		article.setSummary(attrsMap.get("article_summary"));
		
		String articleContent = attrsMap.get("article_content");
		String siteHome = (String)data.get("siteHome");
		articleContent = completeImgUrl(articleContent, siteHome, pageUrl);
		
		articleContent = articleContent.replaceAll("style=\".*?\"", "");
		articleContent = articleContent.replaceAll("class=\".*?\"", "");
		article.setContent(articleContent);
		
		String tagstr = StringUtils.toString(attrsMap.get("article_tags"), true).trim();
		if(StringUtils.isNull(tagstr)) {
			article.setTags("");
		} else {
			String[] tagarr = tagstr.split(" *, *");
			for(int i = 0; i < tagarr.length; i++) {
				tagarr[i] = tagarr[i].replaceAll(" ", "-");
			}
			article.setTags(CollectionUtils.concat(tagarr, " "));
		}
		
		articleService.createArticle(article);
	}
	
	public void jcgArticleMigrate(MesgObj mesgobj) {
		LOGGER.info("In jcgArticleMigrate...Receive mesg data = " + mesgobj);
		
		Map<String, Object> data = (Map<String, Object>)mesgobj.getData();
		
		// attr list -> map
		Map<String, String> attrsMap = attrsToMap((List)data.get("attrs"));
		
		Article article = new Article();
		article.setUuid(mesgobj.getId());
		article.setUrl((String)data.get("pageUrl"));
		article.setSite((String)data.get("site"));
		article.setTitle(attrsMap.get("article_title"));
		
		String articleContent = attrsMap.get("article_content");
		articleContent = articleContent.replaceAll("’", "'");
		Document doc = Jsoup.parse(articleContent);
		articleContent = removeHtmlEls(doc, new String[]{
				"div.e3lan-post",
				"form#mc4wp-form-1",
				"twitterwidget",
				"div.attribution",
				"script",
		});
		articleContent = articleContent.replaceAll("style=\".*?\"", "");
		articleContent = articleContent.replaceAll("class=\".*?\"", "");
		article.setContent(articleContent);
		
		// extractTextSummary
		int summLen = 300;
		String summary = attrsMap.get("article_summary");
		article.setSummary( summary == null ? extractTextSummary(doc, "body", summLen) 
				: ( summary.length() > summLen ? summary.substring(0, summLen) : summary ) );
		
		String tagstr = StringUtils.toString(attrsMap.get("article_tags"), true).trim();
		if(StringUtils.isNull(tagstr)) {
			article.setTags("");
		} else {
			article.setTags(extractCommonArticleTag(tagstr));
		}
		
		articleService.createArticle(article);
	}
	
	public void baelArticleMigrate(MesgObj mesgobj) {
		LOGGER.info("In baelArticleMigrate...Receive mesg data = " + mesgobj);
		
		Map<String, Object> data = (Map<String, Object>)mesgobj.getData();
		
		// attr list -> map
		Map<String, String> attrsMap = attrsToMap((List)data.get("attrs"));
		
		Article article = new Article();
		article.setUuid(mesgobj.getId());
		article.setUrl((String)data.get("pageUrl"));
		article.setSite((String)data.get("site"));
		article.setTitle(attrsMap.get("article_title"));
		
		String articleContent = attrsMap.get("article_content");
		Document doc = Jsoup.parse(articleContent);
		articleContent = removeHtmlEls(doc, new String[]{
				"p.lead",
				"div.short_box.short_start",
				"div.short_box.short_end",
				"span#tho-end-content",
				"span#tve_leads_end_content",
				"div.tve-leads-post-footer"
		});
		articleContent = articleContent.replaceAll("style=\".*?\"", "");
		articleContent = articleContent.replaceAll("class=\".*?\"", "");
		articleContent = articleContent.replaceAll("’", "'");
		article.setContent(articleContent);
		
		// summary 
		String summary = null;
		
		Element ovEl = doc.select("h2#overview").first();
		if(ovEl != null) {
			Element sumEl = ovEl.nextElementSibling();
			if(sumEl != null) summary = sumEl.html();
		}
		
		if(summary != null) article.setSummary(summary);
		
		// tags
		String tagstr = StringUtils.toString(attrsMap.get("article_tags"), true).trim();
		if(StringUtils.isNull(tagstr)) {
			article.setTags("");
		} else {
			article.setTags(extractCommonArticleTag(tagstr));
		}
		
		articleService.createArticle(article);
	}
	
	public void mkyArticleMigrate(MesgObj mesgobj) {
		LOGGER.info("In mkyArticleMigrate...Receive mesg data = " + mesgobj);
		
		Map<String, Object> data = (Map<String, Object>)mesgobj.getData();
		
		// attr list -> map
		List attrs = (List)data.get("attrs");
		Map<String, String> attrsMap = new HashMap<>();
		for(Object attrObj : attrs) {
			Map<String, Object> attrMap = (Map<String, Object>)attrObj;
			
			String key = (String)attrMap.get("key"), val = (String)attrMap.get("val");
			if("article_content".equals(key) && StringUtils.isNull(val)) return;
			
			attrsMap.put(key, val);
		}
		
		Article article = new Article();
		article.setUuid(mesgobj.getId());
		article.setUrl((String)data.get("pageUrl"));
		article.setSite((String)data.get("site"));
		article.setTitle(attrsMap.get("article_title"));
		
		String articleContent = attrsMap.get("article_content");
		Document doc = Jsoup.parse(articleContent);
		articleContent = removeHtmlEls(doc, new String[]{
				"div.ads-in-post",
				"ins.adsbygoogle",
				"script",
		});
		articleContent = articleContent.replaceAll("(<.*?)style=\".*?\"(.*?>)", "$1$2");
		articleContent = articleContent.replaceAll("(<.*?)class=\".*?\"(.*?>)", "$1$2");
//		articleContent = articleContent.replaceAll("’", "'");
		article.setContent(articleContent);
		
		// extractTextSummary
		int summLen = 300;
		String summary = attrsMap.get("article_summary");
		article.setSummary( summary == null ? extractTextSummary(doc, "body", summLen) 
				: ( summary.length() > summLen ? summary.substring(0, summLen) : summary ) );
		
		// tags
		String tagstr = StringUtils.toString(attrsMap.get("article_tags"), true).trim();
		if(StringUtils.isNull(tagstr)) {
			article.setTags("");
		} else {
			article.setTags(extractCommonArticleTag(tagstr));
		}
		
		articleService.createArticle(article);
	}
	
	public void infoqArticleMigrate(MesgObj mesgobj) {
		LOGGER.info("In infoqArticleMigrate...Receive mesg data = " + mesgobj);
		
		Map<String, Object> data = (Map<String, Object>)mesgobj.getData();
		
		// attr list -> map
		Map<String, String> attrsMap = attrsToMap((List)data.get("attrs"));
		
		Article article = new Article();
		article.setUuid(mesgobj.getId());
		article.setUrl((String)data.get("pageUrl"));
		article.setSite((String)data.get("site"));
		article.setTitle(attrsMap.get("article_title"));
		
		String articleContent = attrsMap.get("article_content");
		Document doc = Jsoup.parse(articleContent);
		articleContent = removeElAndAfter(doc, "div.clear");
		articleContent = articleContent.replaceAll("(<.*?)style=\".*?\"(.*?>)", "$1$2");
		articleContent = articleContent.replaceAll("(<.*?)class=\".*?\"(.*?>)", "$1$2");
//		articleContent = articleContent.replaceAll("’", "'");
		article.setContent(articleContent);
		
		String summary = attrsMap.get("article_summary");
		article.setSummary( summary == null ? null : ( summary.length() > 300 ? summary.substring(0, 300) : summary ) );
		
		// tags
		String tagstr = StringUtils.toString(attrsMap.get("article_tags"), true).trim();
		if(StringUtils.isNull(tagstr)) {
			article.setTags("");
		} else {
			article.setTags(extractInfoqArticleTag(tagstr));
		}
		
		articleService.createArticle(article);
	}
	
	public void tnsArticleMigrate(MesgObj mesgobj) {
		LOGGER.info("In tnsArticleMigrate...Receive mesg data = " + mesgobj);
		
		Map<String, Object> data = (Map<String, Object>)mesgobj.getData();
		
		// attr list -> map
		Map<String, String> attrsMap = attrsToMap((List)data.get("attrs"));
		
		Article article = new Article();
		article.setUuid(mesgobj.getId());
		article.setUrl((String)data.get("pageUrl"));
		article.setSite((String)data.get("site"));
		String title = attrsMap.get("article_title");
		article.setTitle( title == null ? "" : title.replaceAll("’", "'") );
		
		String articleContent = attrsMap.get("article_content");
		Document doc = Jsoup.parse(articleContent);
		articleContent = removeHtmlEls(doc, new String[]{
				"p.attribution",
		});
		articleContent = articleContent.replaceAll("(<.*?)style=\".*?\"(.*?>)", "$1$2");
		articleContent = articleContent.replaceAll("(<.*?)class=\".*?\"(.*?>)", "$1$2");
		articleContent = articleContent.replaceAll("’", "'");
		article.setContent(articleContent);
		
		String summary = attrsMap.get("article_summary");
		article.setSummary( summary == null ? null : ( summary.length() > 300 ? summary.substring(0, 300) : summary ) );
		
		// tags
		String tagstr = StringUtils.toString(attrsMap.get("article_tags"), true).trim();
		if(StringUtils.isNull(tagstr)) {
			article.setTags("");
		} else {
			article.setTags(extractCommonArticleTag(tagstr));
		}
		
		articleService.createArticle(article);
	}
	
	public void jvnsArticleMigrate(MesgObj mesgobj) {
		LOGGER.info("In jvnsArticleMigrate...Receive mesg data = " + mesgobj);
		
		Map<String, Object> data = (Map<String, Object>)mesgobj.getData();
		
		// attr list -> map
		Map<String, String> attrsMap = attrsToMap((List)data.get("attrs"));
		
		Article article = new Article();
		article.setUuid(mesgobj.getId());
		article.setUrl((String)data.get("pageUrl"));
		article.setSite((String)data.get("site"));
		String title = attrsMap.get("article_title");
		article.setTitle( title == null ? "" : title.replaceAll("’", "'") );
		
		String articleContent = attrsMap.get("article_content");
		articleContent = articleContent.replaceAll("(<.*?)style=\".*?\"(.*?>)", "$1$2");
		articleContent = articleContent.replaceAll("(<.*?)class=\".*?\"(.*?>)", "$1$2");
		articleContent = articleContent.replaceAll("’", "'");
		article.setContent(articleContent);
		
		String summary = attrsMap.get("article_summary");
		article.setSummary( summary == null ? null : ( summary.length() > 300 ? summary.substring(0, 300) : summary ) );
		
		// tags
		String tagstr = StringUtils.toString(attrsMap.get("article_tags"), true).trim();
		if(StringUtils.isNull(tagstr)) {
			article.setTags("");
		} else {
			article.setTags(extractCommonArticleTag(tagstr));
		}
		
		articleService.createArticle(article);
	}
	
	public void mfArticleMigrate(MesgObj mesgobj) {
		LOGGER.info("In mfArticleMigrate...Receive mesg data = " + mesgobj);
		
		Map<String, Object> data = (Map<String, Object>)mesgobj.getData();
		
		// attr list -> map
		Map<String, String> attrsMap = attrsToMap((List)data.get("attrs"));
		
		Article article = new Article();
		article.setUuid(mesgobj.getId());
		
		String pageUrl = (String)data.get("pageUrl");
		article.setUrl(pageUrl);
		
		article.setSite((String)data.get("site"));
		String title = attrsMap.get("article_title");
		article.setTitle( title == null ? "" : title.replaceAll("’", "'") );
		
		String siteHome = (String)data.get("siteHome");
		String articleContent = attrsMap.get("article_content");
		articleContent = completeImgUrl(articleContent, siteHome, pageUrl);
		
		Document doc = Jsoup.parse(articleContent);
		articleContent = removeHtmlEls(doc, new String[]{
				"body > h1",
				"p.intent",
				"div.frontMatter",
		});
		articleContent = articleContent.replaceAll("(<.*?)style=\".*?\"(.*?>)", "$1$2");
		articleContent = articleContent.replaceAll("(<.*?)class=\".*?\"(.*?>)", "$1$2");
		articleContent = articleContent.replaceAll("’", "'");
		article.setContent(articleContent);
		
		String summary = attrsMap.get("article_summary");
		article.setSummary( summary == null ? null : ( summary.length() > 300 ? summary.substring(0, 300) : summary ) );
		
		articleService.createArticle(article);
	}
	
	public void tutsplusArticleMigrate(MesgObj mesgobj) {
		LOGGER.info("In tutsplusArticleMigrate...Receive mesg data = " + mesgobj);
		
		Map<String, Object> data = (Map<String, Object>)mesgobj.getData();
		
		// attr list -> map
		Map<String, String> attrsMap = attrsToMap((List)data.get("attrs"));
		
		Article article = new Article();
		article.setUuid(mesgobj.getId());
		
		String pageUrl = (String)data.get("pageUrl");
		article.setUrl(pageUrl);
		
		article.setSite((String)data.get("site"));
		String title = attrsMap.get("article_title");
		article.setTitle( title == null ? "" : title.replaceAll("’", "'") );
		
		String articleContent = attrsMap.get("article_content");
		Document doc = Jsoup.parse(articleContent);
		Elements postrefs = doc.select("ul.roundup-block__contents li.roundup-block__content");
		if(postrefs.size() > 0) {
			for(Element postref : postrefs) {
				String href = postref.select("a.roundup-block__content-link").attr("href"),
						title1 = postref.select("div.roundup-block__content-title").html(),
						title2 = postref.select("a.roundup-block__content-link h1").html();
				
				Element parent = postref.parent();
				postref.remove();
				parent.append("<li><a href='" + href + "'>" + (title1 != null && title1.trim().length() > 0 ? title1 : title2) + "</a></li>");
			}
		}
		
		articleContent = removeHtmlEls(doc, new String[]{
				"div.post__inarticle-ad-template",
		});
		
		articleContent = articleContent.replaceAll("(<.*?)style=\".*?\"(.*?>)", "$1$2");
		articleContent = articleContent.replaceAll("(<.*?)class=\".*?\"(.*?>)", "$1$2");
		articleContent = articleContent.replaceAll("’", "'");
		article.setContent(articleContent);
		
		String summary = attrsMap.get("article_summary");
		article.setSummary( summary == null ? null : ( summary.length() > 300 ? summary.substring(0, 300) : summary ) );
		
		// tags
		String tagstr = StringUtils.toString(attrsMap.get("article_tags"), true).trim();
		if(StringUtils.isNull(tagstr)) {
			article.setTags("");
		} else {
			article.setTags(extractTutsArticleTag(tagstr));
		}
		
		articleService.createArticle(article);
	}
	
	private String completeImgUrl(String articleContent, String siteHome, String pageUrl) {
		Pattern ptrn = Pattern.compile("<img .*src *= *['\"](.*?)['\"]");
		Matcher m = ptrn.matcher(articleContent);
		
		if(siteHome.endsWith("/")) siteHome = siteHome.substring(0, siteHome.length() - 1);
		String parentUrl = pageUrl.substring(0, pageUrl.lastIndexOf("/"));
		
		Set<String> imgSet = new HashSet<>();
		String rt = articleContent;
		while(m.find()) {
			String imgsrc = m.group(1).trim();
			if(imgsrc.startsWith("http://") || imgsrc.startsWith("https://")) continue;
			
			if(imgSet.contains(imgsrc)) continue;
			imgSet.add(imgsrc);
			
			if(imgsrc.startsWith("/")) { // 绝对路径
				rt = rt.replaceAll(imgsrc, siteHome + imgsrc);
			} else { // 相对路径
				rt = rt.replaceAll(imgsrc, parentUrl + "/" + imgsrc);
			}
		}

		return rt;
	}

	private String extractTutsArticleTag(String rawstr) {
		Pattern ptrn = Pattern.compile("<a .*?><span .*?>(.*?)</span></a>");
		Matcher m = ptrn.matcher(rawstr);
		
		StringBuffer tagbuf = new StringBuffer();
		while(m.find()) {
			String tag = m.group(1).trim().replaceAll(" *&amp; *", "&");
			tag = tag.replaceAll(" +", "-");
			tagbuf.append(tagbuf.length() > 0 ? " " + tag : tag);
		}
		
		return tagbuf.toString();
	}
	
	private String extractCommonArticleTag(String rawstr) {
		Pattern ptrn = Pattern.compile("<a .*?>(.*?)</a>");
		Matcher m = ptrn.matcher(rawstr);
		
		StringBuffer tagbuf = new StringBuffer();
		while(m.find()) {
			String tag = m.group(1).trim().replaceAll(" *&amp; *", "&");
			tag = tag.replaceAll(" +", "-");
			tagbuf.append(tagbuf.length() > 0 ? " " + tag : tag);
		}
		
		return tagbuf.toString();
	}

	private String extractInfoqArticleTag(String rawstr) {
		Pattern ptrn = Pattern.compile("<.*?>(.*?)</.*?>");
		Matcher m = ptrn.matcher(rawstr);
		
		StringBuffer tagbuf = new StringBuffer();
		while(m.find()) {
			String tag = m.group(1).trim().replaceAll(" *&amp; *", "&");
			tag = tag.replaceAll(" +", "-");
			tagbuf.append(tagbuf.length() > 0 ? " " + tag : tag);
		}
		
		return tagbuf.toString();
	}
	
	private String removeHtmlEls(Document doc, String[] els) {
		for(String el : els) {
			Elements elements = doc.select(el);
			elements.remove();
		}
		
		return doc.select("body").html();
	}
	
	// 删除elStr节点, 以及后面的sibling节点
	private String removeElAndAfter(Document doc, String elStr) {
		Element el = doc.select(elStr).first();
		
		if(el != null) {
			Node sibling = null;
			while( (sibling = el.nextSibling()) != null ) {
				sibling.remove();
			}
			
			el.remove();
		}
		
		return doc.select("body").html();
	}
	
	private String extractTextSummary(Document doc, String el, int len) {
		Elements els = doc.select(el);
		if(els.isEmpty()) return null;
		
		String text = els.text();
		return text.length() > len ? text.substring(0, len) : text;
	}
	
	private Map<String, String> attrsToMap(List attrs) {
		Map<String, String> attrsMap = new HashMap<>();
		for(Object attrObj : attrs) {
			Map<String, Object> attrMap = (Map<String, Object>)attrObj;
			
			String key = (String)attrMap.get("key"), val = (String)attrMap.get("val");
			attrsMap.put(key, val);
		}
		
		return attrsMap;
	}
	
}
