package me.codetalk.webdb.service.impl;

import java.sql.Timestamp;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.codetalk.messaging.MesgObj;
import me.codetalk.util.JsonUtils;
import me.codetalk.util.CollectionUtils;
import me.codetalk.util.StringUtils;
import me.codetalk.webdb.elastic.DocArticle;
import me.codetalk.webdb.elastic.repos.ArticleRepository;
import me.codetalk.webdb.entity.Article;
import me.codetalk.webdb.mapper.ArticleMapper;
import me.codetalk.webdb.service.ArticleService;

@Service("articleService")
public class ArticleServiceImpl implements ArticleService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ArticleServiceImpl.class);
	
	@Autowired
	private ArticleMapper articleMapper;
	
	@Autowired
	private ArticleRepository articleRepo;
	
	@Override
	@Transactional
	public void createArticle(Article article) {
		createArticle(article, true);
	}
	
	@Override
	@Transactional
	public void createArticle(Article article, boolean doIndex) {
		LOGGER.info("=======================> Create article, uuid=" + article.getUuid());
		
		String url = article.getUrl();
		if(articleMapper.selectOneByUrl(url) != null) {
			LOGGER.info("Article with [url = " + url + "] already exists!");
			
			return;
		}
		
		article.setIndexed(1);
		article.setCreateDate(System.currentTimeMillis());
		articleMapper.insertArticle(article);

		if(doIndex) articleRepo.save(article2Doc(article));
	}
	

	
	// KAFKA
	
//	@KafkaListener(topics = "webminer-dzone-article", groupId = "webdb-article-dzone-article-migrate")
	public void dzArticleMigrate(String msgstr) {
		LOGGER.info("In dzArticleMigrate...Receive mesg data = " + msgstr);
		
		MesgObj mesg = (MesgObj)JsonUtils.fromJsonObj(msgstr, MesgObj.class);
		Map<String, Object> data = (Map<String, Object>)mesg.getData();
		
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
		article.setUuid(mesg.getId());
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
		
		createArticle(article);
	}
	
//	@KafkaListener(topics = "webminer-javacodegeeks-article", groupId = "webdb-article-javacodegeeks-article-migrate")
	public void jcgArticleMigrate(String msgstr) {
		LOGGER.info("In jcgArticleMigrate...Receive mesg data = " + msgstr);
		
		MesgObj mesg = (MesgObj)JsonUtils.fromJsonObj(msgstr, MesgObj.class);
		Map<String, Object> data = (Map<String, Object>)mesg.getData();
		
		// attr list -> map
		Map<String, String> attrsMap = attrsToMap((List)data.get("attrs"));
		
		Article article = new Article();
		article.setUuid(mesg.getId());
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
		
		createArticle(article);
	}
	
//	@KafkaListener(topics = "webminer-baeldung-article", groupId = "webdb-article-baeldung-article-migrate")
	public void baelArticleMigrate(String msgstr) {
		LOGGER.info("In baelArticleMigrate...Receive mesg data = " + msgstr);
		
		MesgObj mesg = (MesgObj)JsonUtils.fromJsonObj(msgstr, MesgObj.class);
		Map<String, Object> data = (Map<String, Object>)mesg.getData();
		
		// attr list -> map
		Map<String, String> attrsMap = attrsToMap((List)data.get("attrs"));
		
		Article article = new Article();
		article.setUuid(mesg.getId());
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
		
		createArticle(article);
	}
	
//	@KafkaListener(topics = "webminer-mkyong-article", groupId = "webdb-article-mkyong-article-migrate")
	public void mkyArticleMigrate(String msgstr) {
		LOGGER.info("In mkyArticleMigrate...Receive mesg data = " + msgstr);
		
		MesgObj mesg = (MesgObj)JsonUtils.fromJsonObj(msgstr, MesgObj.class);
		Map<String, Object> data = (Map<String, Object>)mesg.getData();
		
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
		article.setUuid(mesg.getId());
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
		
		createArticle(article);
	}
	
//	@KafkaListener(topics = "webminer-infoq-article", groupId = "webdb-article-infoq-article-migrate")
	public void infoqArticleMigrate(String msgstr) {
		LOGGER.info("In infoqArticleMigrate...Receive mesg data = " + msgstr);
		
		MesgObj mesg = (MesgObj)JsonUtils.fromJsonObj(msgstr, MesgObj.class);
		Map<String, Object> data = (Map<String, Object>)mesg.getData();
		
		// attr list -> map
		Map<String, String> attrsMap = attrsToMap((List)data.get("attrs"));
		
		Article article = new Article();
		article.setUuid(mesg.getId());
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
		
		createArticle(article);
	}
	
//	@KafkaListener(topics = "webminer-thenewstack-article", groupId = "webdb-article-thenewstack-article-migrate")
	public void tnsArticleMigrate(String msgstr) {
		LOGGER.info("In tnsArticleMigrate...Receive mesg data = " + msgstr);
		
		MesgObj mesg = (MesgObj)JsonUtils.fromJsonObj(msgstr, MesgObj.class);
		Map<String, Object> data = (Map<String, Object>)mesg.getData();
		
		// attr list -> map
		Map<String, String> attrsMap = attrsToMap((List)data.get("attrs"));
		
		Article article = new Article();
		article.setUuid(mesg.getId());
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
		
		createArticle(article);
	}
	
//	@KafkaListener(topics = "webminer-jvns-article", groupId = "webdb-article-jvns-article-migrate")
	public void jvnsArticleMigrate(String msgstr) {
		LOGGER.info("In jvnsArticleMigrate...Receive mesg data = " + msgstr);
		
		MesgObj mesg = (MesgObj)JsonUtils.fromJsonObj(msgstr, MesgObj.class);
		Map<String, Object> data = (Map<String, Object>)mesg.getData();
		
		// attr list -> map
		Map<String, String> attrsMap = attrsToMap((List)data.get("attrs"));
		
		Article article = new Article();
		article.setUuid(mesg.getId());
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
		
		createArticle(article);
	}
	
//	@KafkaListener(topics = "webminer-martinfowler-article", groupId = "webdb-article-martinfowler-article-migrate")
	public void mfArticleMigrate(String msgstr) {
		LOGGER.info("In mfArticleMigrate...Receive mesg data = " + msgstr);
		
		MesgObj mesg = (MesgObj)JsonUtils.fromJsonObj(msgstr, MesgObj.class);
		Map<String, Object> data = (Map<String, Object>)mesg.getData();
		
		// attr list -> map
		Map<String, String> attrsMap = attrsToMap((List)data.get("attrs"));
		
		Article article = new Article();
		article.setUuid(mesg.getId());
		
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
		
		createArticle(article);
	}
	
//	@KafkaListener(topics = "webminer-tutsplus-article", groupId = "webdb-article-tutsplus-article-migrate")
	public void tutsplusArticleMigrate(String msgstr) {
		LOGGER.info("In tutsplusArticleMigrate...Receive mesg data = " + msgstr);
		
		MesgObj mesg = (MesgObj)JsonUtils.fromJsonObj(msgstr, MesgObj.class);
		Map<String, Object> data = (Map<String, Object>)mesg.getData();
		
		// attr list -> map
		Map<String, String> attrsMap = attrsToMap((List)data.get("attrs"));
		
		Article article = new Article();
		article.setUuid(mesg.getId());
		
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
		
		createArticle(article);
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
	
	private DocArticle article2Doc(Article article) {
		DocArticle da = new DocArticle();
		da.setArticleId(article.getId());
		da.setUuid(article.getUuid());
		da.setTitle(article.getTitle());
		da.setSite(article.getSite());
		da.setUrl(article.getUrl());
		da.setSummary(article.getSummary());
		da.setContent(article.getContent());

		// tags
		Set<String> tagSet = new HashSet<>();
		String tags = article.getTags();
		if(!StringUtils.isBlank(tags)) {
			List<String> tagList = StringUtils.splitNoRegex(tags, " ");
			for(String tag : tagList) {
				tagSet.add(tag.toLowerCase());
			}
		}
		da.setTags(tagSet);
		
		da.setCreateDate(article.getCreateDate());
		
		return da;
	}

	@Override
	public List<Article> findLatest(Integer count) {
		return articleMapper.selectLatest(0, count);
	}



	
}
