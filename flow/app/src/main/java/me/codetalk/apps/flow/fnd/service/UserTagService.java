package me.codetalk.apps.flow.fnd.service;

import java.util.List;

import me.codetalk.apps.flow.fnd.entity.vo.UserTagVO;

public interface UserTagService {

	public void updateUserTag(Long userId, List<String> tagList);
	
	public void addUserTag(Long userId, String tag);
	
	public List<UserTagVO> findUserTag(Long userId);
	
	public List<String> findUserTagForSearch(Long userId);
	
}
