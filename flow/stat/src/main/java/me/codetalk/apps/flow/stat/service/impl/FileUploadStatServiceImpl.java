package me.codetalk.apps.flow.stat.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.codetalk.apps.flow.stat.cache.IFileUploadStatCache;
import me.codetalk.apps.flow.stat.service.IFileUploadStatService;

@Service("fuStatService")
public class FileUploadStatServiceImpl implements IFileUploadStatService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadStatServiceImpl.class);
	
	@Autowired
	private IFileUploadStatCache fuStatCache;
	
	@Override
	public void incrFileUpload(String date) {
		fuStatCache.incrFileUpload(date);
	}

}
