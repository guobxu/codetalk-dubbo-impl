package me.codetalk.apps.flow.fnd.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import me.codetalk.apps.flow.fnd.cache.TagCache;
import me.codetalk.apps.flow.fnd.entity.Tag;
import me.codetalk.apps.flow.fnd.mapper.TagMapper;
import me.codetalk.apps.flow.fnd.service.TagService;
import me.codetalk.apps.flow.post.service.PostService;
import me.codetalk.apps.flow.service.AbstractAppService;
import me.codetalk.lock.service.LockService;
import me.codetalk.util.DateUtils;
import me.codetalk.util.JsonUtils;
import me.codetalk.util.StringUtils;

@Service("tagService")
public class TagServiceImpl extends AbstractAppService implements TagService {

	private static final Logger LOGGER = Logger.getLogger(TagServiceImpl.class);
	
	private static final int MAX_TAG_FETCH = 100; // 最多获取100个tag
	
	@Autowired
	private TagMapper tagMapper;
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private LockService lockService;
	
	@Autowired
	private TagCache tagCache;
	
	@Override
	public List<Tag> findTopTagLike(String q, int count) {
		return tagMapper.selectTopTagLike(q.toLowerCase(), count > MAX_TAG_FETCH ? MAX_TAG_FETCH : count);
	}
	
	@Override
	public List<Tag> findTopTags(int count) {
		count = count > MAX_TAG_FETCH ? MAX_TAG_FETCH : count;
		
		List<Tag> tags = tagCache.getTopTags(count);
		if(tags == null) {
			tags = tagMapper.selectTopTag(count);
			tagCache.setTopTags(count, tags);
		}
		
		return tags;
	}
	
	// TODO: 容错处理
//	@Scheduled(cron = "0 */1 * * * ?")
//	@Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2:00
	@Scheduled(cron = "0 0/10 * * * *") // 每隔十分钟, 统计上十分钟的
	public void countTagHits() {
		
//		String cacheKey = "JOB-TAG-COUNT-" + DateUtils.formatToDay(new Date());
//		if(!lockService.lock(cacheKey)) return;

		long timeMillis = DateUtils.timeMillisToMi();
		String cacheKey = "JOB-TAG-COUNT-" + DateUtils.formatToMi(timeMillis);
		if(!lockService.lock(cacheKey)) return;
		
//		Long start = DateUtils.startOfDay(-1), end = DateUtils.endOfDay(-1);
		Long start = timeMillis - 10 * 60 * 1000L, end = timeMillis - 1;
		for(int i = 0, count = 1000; ; i++) {
			int begin = i * count;
			List<String> tagstrList = postService.findPostTagBetween(start, end, begin, count);
			
			if(tagstrList.isEmpty()) return;
			
			// tag: [{t=>xx, s=>0, e=>0}, ...]
			Map<String, Long> tagHitMap = new HashMap<>();
			for(String tagstr : tagstrList) {
				List<Map<String, Object>> tagmapList = (List<Map<String, Object>>)JsonUtils.fromJsonObj(tagstr, List.class);
				for(Map<String, Object> tagmap : tagmapList) {
					String tt = tagmap.get("t").toString();
					Long hits = tagHitMap.get(tt);
					tagHitMap.put(tt, hits == null ? 1 : hits + 1);
				}
			}
			
			tagHitMap.forEach((text, hits) -> {
				Tag tag = tagMapper.selectTag(text);
				if(tag != null) {
					tagMapper.updateTagHits(text, tag.getHits() + hits);
				} else {
					tag = new Tag();
					tag.setText(text);
					tag.setHits(hits);
					tagMapper.insertTag(tag);
				}
			});
		}
		
	}

	@Override
	public List<Tag> topHitTagByDay(int count) {
		return tagCache.topHitTagByDay(count);
	}

	/******************************** Service ********************************/
	static final String URI_TAG_LIST = "/flow/post/tag/list";
	static final String URI_TOP_TAG = "/flow/post/tag/topbyday";
	
	@Override
	public String doService(String uri, Map<String, Object> params) {
		if(URI_TAG_LIST.equals(uri)) {
			return tagList(params);
		} else if(URI_TOP_TAG.equals(uri)) {
			return topTagByDay(params);
		}
		
		return null;
	}
	
	/**
	 * Params:
	 * pf_type=0
		q="xx" // 搜索关键字, 可选
		count=0
	 *
	 * @param params
	 * @return
	 */
	private String tagList(Map<String, Object> params) {
		LOGGER.info("In tagList... Params = " + params);
		
		String q = StringUtils.toString(params.get("q"), true).trim();
		int count = Integer.parseInt(params.get("count").toString());
		
		List<Tag> tags = null;
		if(q.isEmpty()) {
			tags = findTopTags(count);
		} else {
			tags = findTopTagLike(q, count);
		}
		
		return successWithData(tags);
	}
	
	/**
	 * Params:
	 * 参数列表:
	 * "pf_type" = 0
	 * count =0 // 返回tag个数
	 * 
	 * @param params
	 * @return
	 */
	private String topTagByDay(Map<String, Object> params) {
		LOGGER.info("In topTagByDay... Params = " + params);
		
		Integer count = Integer.parseInt((String)params.get("count"));
		
		return successWithData(topHitTagByDay(count));
	}

}
