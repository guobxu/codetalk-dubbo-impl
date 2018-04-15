package me.codetalk.apps.flow.article.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import me.codetalk.apps.flow.article.entity.UserArticle;

public interface UserArticleMapper {

	/**
	 * 根据用户ID 和 状态列表查找
	 * 
	 * @param count
	 * @return
	 */
	List<Long> selectLatest(@Param("userId") Long userId, @Param("statusList") List<Integer> statusList, 
							@Param("begin") Integer begin, @Param("count") Integer count);
	
	/**
	 * 
	 * @param userId
	 * @param statusList
	 * @return
	 */
	List<Long> selectInStatus(@Param("userId") Long userId, @Param("statusList") List<Integer> statusList);
	
	UserArticle selectUserArticle(@Param("userId") Long userId, @Param("articleId") Long articleId);
	
	void insertUserArticle(UserArticle userArticle);
	
}
