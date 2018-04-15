package me.codetalk.webminer.mapper;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import me.codetalk.webminer.pojo.SitePage;
import me.codetalk.webminer.pojo.SitePageVO;

public interface SitePageMapper {

	public void insertPages(@Param("pageList") List<SitePage> pageList);
	
	public void updatePageStatus(SitePage page);
	
	public List<String> selectUrlIn(@Param("urlList") List<String> urlList);
	
	public List<SitePageVO> selectErrPagesAfter(@Param("tsAfter") Timestamp tsAfter);
	
}
