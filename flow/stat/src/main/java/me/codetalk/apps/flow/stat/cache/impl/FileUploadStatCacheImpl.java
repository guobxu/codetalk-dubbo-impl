package me.codetalk.apps.flow.stat.cache.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.codetalk.apps.flow.stat.cache.IFileUploadStatCache;
import me.codetalk.stat.StatSupport;

@Component("fuStatCache")
public class FileUploadStatCacheImpl implements IFileUploadStatCache {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadStatCacheImpl.class);
	
	public static final String STAT_FILE_UPLOAD = "STAT-FILE-UPLOAD";			// 文件上传数量
	
	@Autowired
	private StatSupport statCache;
	
	@Override
	public void incrFileUpload(String date) {
		statCache.incrStatById(date, STAT_FILE_UPLOAD, 1);
	}

}
