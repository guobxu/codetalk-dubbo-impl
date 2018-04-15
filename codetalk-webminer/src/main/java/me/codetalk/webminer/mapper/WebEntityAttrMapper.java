package me.codetalk.webminer.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import me.codetalk.webminer.pojo.WebEntityAttr;

public interface WebEntityAttrMapper {

	public void insertAttrList(@Param("attrList") List<WebEntityAttr> attrList);
	
}
