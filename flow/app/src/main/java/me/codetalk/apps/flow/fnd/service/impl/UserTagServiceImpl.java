package me.codetalk.apps.flow.fnd.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.codetalk.Constants;
import me.codetalk.apps.flow.fnd.entity.UserTag;
import me.codetalk.apps.flow.fnd.entity.vo.UserTagVO;
import me.codetalk.apps.flow.fnd.mapper.UserTagMapper;
import me.codetalk.apps.flow.fnd.service.UserTagService;
import me.codetalk.apps.flow.service.AbstractAppService;
import me.codetalk.auth.aop.ThreadSession;
import me.codetalk.cache.service.CacheService;

@Service("userTagService")
public class UserTagServiceImpl extends AbstractAppService implements UserTagService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserTagServiceImpl.class);
	
	private static final String CACHE_USER_TAG = "USER-TAG-";
	
	@Autowired
	private UserTagMapper userTagMapper;
	
	@Autowired
	private CacheService cacheService; 
	
	@Autowired
	private ThreadSession threadSession;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateUserTag(Long userId, List<String> tagList) {
		List<UserTag> userTagList = new ArrayList<>();
		
		Long createDate = System.currentTimeMillis();
		for(String tag : tagList) {
			UserTag userTag = new UserTag();
			userTag.setUserId(userId);
			userTag.setTag(tag);
			userTag.setCreateDate(createDate);
			
			userTagList.add(userTag);
		}
		
		userTagMapper.deleteUserTag(userId);
		userTagMapper.insertUserTag(userTagList);
		
		// cache
		String cacheKey = CACHE_USER_TAG + userId;
		cacheService.delete(cacheKey);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addUserTag(Long userId, String tag) {
		UserTag userTag = new UserTag();
		userTag.setUserId(userId);
		userTag.setTag(tag);
		userTag.setCreateDate(System.currentTimeMillis());
		
		userTagMapper.insertUserTag(Arrays.asList(userTag));
		
		// cache
		String cacheKey = CACHE_USER_TAG + userId;
		cacheService.delete(cacheKey);
	}

	@Override
	public List<UserTagVO> findUserTag(Long userId) {
		String cacheKey = CACHE_USER_TAG + userId;
		List<UserTagVO> userTags = (List<UserTagVO>)cacheService.get(cacheKey); 
		if(userTags == null) {
			userTags = userTagMapper.selectUserTag(userId);
			cacheService.set(cacheKey, userTags, CacheService.DEFAULT_CACHE_EXPIRE);
		}

		return userTags;
	}

	@Override
	public List<String> findUserTagForSearch(Long userId) {
		List<String> tagList = new ArrayList<String>();
		
		for(UserTagVO userTag : findUserTag(userId)) {
			tagList.add(userTag.getTag());
		}
		
		return tagList;
	}

	/****************************** Service ******************************/
	static final String URI_USERTAG = "/flow/post/usertag";
	static final String URI_USERTAG_UPDATE = "/flow/post/usertag/update";
	static final String URI_USERTAG_ADD = "/flow/post/usertag/add";
	
	@Override
	@Transactional
	public String doService(String uri, Map<String, Object> params) {
		if(URI_USERTAG.equals(uri)) {
			return userTag(params);
		} else if(URI_USERTAG_UPDATE.equals(uri)) {
			return userTagUpdate(params);
		} else if(URI_USERTAG_ADD.equals(uri)) {
			return userTagAdd(params);
		}
		
		return null;
	}
	
	/**
	 * Params:
	 * "pf_type"=0
		"user_id" = 0L // 用户ID
		"access_token" = "xx" // token 
		"auth_str": "xx"
	 *
	 * @param params
	 * @return
	 */
	private String userTag(Map<String, Object> params) {
		LOGGER.info("In userTag... Params = " + params);
		
		if(!threadSession.authorized()) {
			return errorWithCodeKey(Constants.CODE_ERROR_RELOGIN, "auth_err_session_notfound");
		}
		
		List<UserTagVO> userTags = findUserTag(threadSession.getUserId());
		
		return successWithData(userTags);
	}
	
	/**
	 * Params:
	 * {
		"pf_type":0 
		"user_id": 0L // 用户ID
		"access_token": "xx" // token
		"auth_str": "xx"
		"tag_text": "xx"
		}
	 *
	 * @param params
	 * @return
	 */
	private String userTagAdd(Map<String, Object> params) {
		LOGGER.info("In userTagAdd... Params = " + params);
		
		if(!threadSession.authorized()) {
			return errorWithCodeKey(Constants.CODE_ERROR_RELOGIN, "auth_err_session_notfound");
		}
		
		Long userId = threadSession.getUserId();
		String tag = params.get("tag_text").toString();
		addUserTag(userId, tag);
		
		return Constants.RESPONSE_SUCCESS;
	}
	
	/**
	 * {
		"pf_type":0 
		"user_id": 0L // 用户ID
		"access_token": "xx" // token
		"auth_str": "xx"
		"tag_list":["tag1", "tag2"...] // 标签ID列表
		}
	 *
	 * @param params
	 * @return
	 */
	private String userTagUpdate(Map<String, Object> params) {
		LOGGER.info("In userTagUpdate... Params = " + params);
		
		if(!threadSession.authorized()) {
			return errorWithCodeKey(Constants.CODE_ERROR_RELOGIN, "auth_err_session_notfound");
		}
		
		Long userId = threadSession.getUserId();
		List<String> tagList = (List<String>)params.get("tag_list");
		if(tagList == null || tagList.isEmpty()) {
			return errorWithKey("post_err_usertag_empty");
		}
		
		updateUserTag(userId, tagList);
		
		return Constants.RESPONSE_SUCCESS;
	}

}
