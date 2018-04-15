package me.codetalk.apps.flow.post.service;

import java.util.List;

public interface PostSearchService {

	/**
	 * 
	 * @param tags
	 * @param beginDate	包含, 允许null
	 * @param endDate 不包含, 允许null
	 * @param page
	 * @param size
	 * @return
	 */
	public List<Long> searchByTags(List<String> tags, Long beginDate, Long endDate, Integer page, Integer size);

	public List<Long> searchByKeyword(String q, Long beginDate, Long endDate, Integer page, Integer size);
	
	public List<Long> searchByKeywordAndTags(String q, List<String> tags, Long beginDate, Long endDate, Integer page, Integer size);
	
	/**
	 * 
	 * @param users	用户ID列表	不允许空
	 * @param tags	标签列表		不允许空
	 * @param beginDate	包含, 允许null
	 * @param endDate 不包含, 允许null
	 * @param page
	 * @param size
	 * @return
	 */
	public List<Long> searchByUserAndTag(List<String> users, List<String> tags, Long beginDate, Long endDate, Integer page, Integer size);
	
}
