package me.codetalk.apps.flow.article.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.codetalk.apps.flow.AppConstants;
import me.codetalk.apps.flow.article.cache.UserArticleCache;
import me.codetalk.apps.flow.article.mapper.UserArticleMapper;
import me.codetalk.apps.flow.article.service.UserArticleService;
import me.codetalk.apps.flow.fnd.service.UserTagService;
import me.codetalk.apps.flow.service.AbstractAppService;
import me.codetalk.auth.aop.ThreadSession;
import me.codetalk.messaging.IMessageSender;
import me.codetalk.util.JsonUtils;
import me.codetalk.util.MapUtils;
import me.codetalk.util.StringUtils;
import me.codetalk.webdb.service.WebdbService;

@Service("userArticleService")
public class UserArticleServiceImpl extends AbstractAppService implements UserArticleService {

	static final int SEARCH_COUNT_MULTI_BY = 20; 	// 如果每次获取5篇, 则搜索 5 * 20 = 100 篇 
	
	@Autowired
	private UserArticleCache userArticleCache;
	
	@Autowired
	private WebdbService webdbService;
	
	@Autowired
	private UserTagService userTagService;
	
	@Autowired
	private UserArticleMapper userArticleMapper;
	
	@Autowired
	private IMessageSender mesgSender;
	
	@Autowired
	private ThreadSession threadSession;
	
	@Override
	public List<Map<String, Object>> getUserArticles(Long userId, Integer count) {
		int size = SEARCH_COUNT_MULTI_BY * count;
		
		if(userArticleCache.count(userId) == 0) { // 不存在或者数量为0
			List<String> tagList = userTagService.findUserTagForSearch(userId);
			
			// 最近完成 或者 不感兴趣
			List<Long> excludeArticles = userArticleMapper.selectInStatus(userId, Arrays.asList(AppConstants.ARTICLE_NOT_FAVOR, AppConstants.ARTICLE_READ));
			
			// Assumption: tagList.size() > 0
			String articleJsonStr = webdbService.searchArticleByTags(tagList, excludeArticles, 0, size);
			List<Map<String, Object>> amapList = JsonUtils.fromJsonArray(articleJsonStr, Map.class);
			
			userArticleCache.setUserArticle(userId, amapList);
		}
		
		return userArticleCache.getUserArticle(userId, count);
	}

	@Override
	public void removeUserArticles(Long userId, List<Long> articleIdList) {
//		Long articleInSet = userArticleCache.count(userId);
//		if(articleInSet < articleIdList.size() * 2) return;
		
		userArticleCache.removeUserArticles(userId, articleIdList);
		
		Map<String, Object> mapData = MapUtils.toMap(new String[]{"user_id", "article_list"}, new Object[]{userId, articleIdList});
		mesgSender.sendMessage(AppConstants.MESG_ARTICLE_RM, mapData);
	}
	
	static class ArticleRmInfo {
		
		Long userId;
		List<Long> articleIdList;
		List<Long> rmStat;
		
		ArticleRmInfo(Long userId, List<Long> articleIdList, List<Long> rmStat) {
			this.userId = userId;
			this.articleIdList = articleIdList;
			this.rmStat = rmStat;
		}
		
		public Long getUserId() {
			return userId;
		}
		
		public List<Long> getArticleIdList() {
			return articleIdList;
		}
		
		public List<Long> getRmStat() {
			return rmStat;
		}
		
	}

	/******************************* Service *******************************/
	
	static final String URI_ARTICLE_SUGGEST = "/flow/article/suggest";
	
	@Override
	public String doService(String uri, Map<String, Object> params) {
		if(URI_ARTICLE_SUGGEST.equals(uri)) {
			return articleSuggest(params);
		}
		
		return null;
	}

	/**
	 * Params:
	 * rm_articles="1,2,3" // 作为后续推荐
	 * begin=0 // 起始
	 * count=0 // 条数, 最多10条
	 *
	 * @param params
	 * @return
	 */
	private String articleSuggest(Map<String, Object> params) {
		Integer begin = Integer.parseInt((String)params.get("begin")),
				count = Integer.parseInt((String)params.get("count"));
		
		String rmArticleStr = (String)params.get("rm_articles");
		List<Long> excludeArticles = null;
		if(!StringUtils.isBlank(rmArticleStr)) {
			excludeArticles = new ArrayList<>();
			
			List<String> tmpList = StringUtils.splitNoRegex(rmArticleStr, ",");
			for(String tmp : tmpList) {
				excludeArticles.add(Long.parseLong(tmp));
			}
		}
		
		List<Map<String, Object>> articleList = null;
		if(threadSession.authorized()) {
			Long userId = threadSession.getUserId();
			
			if(excludeArticles != null) {
				removeUserArticles(userId, excludeArticles);
			}
			
			articleList = getUserArticles(userId, count);
		} else {
			articleList = JsonUtils.fromJsonArray(webdbService.findLatestArticles(count), Map.class);
			
		}
		
		Map<String, Object> rtData = MapUtils.toMap(new String[]{"article_list", "article_left"}, 
				new Object[] {articleList, 0});
		return successWithData(rtData);
	}
	
}
