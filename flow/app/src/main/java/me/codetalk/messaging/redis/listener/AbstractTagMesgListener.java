package me.codetalk.messaging.redis.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.codetalk.apps.flow.fnd.entity.TagPosition;
import me.codetalk.util.StringUtils;

public abstract class AbstractTagMesgListener extends AbstractMessageListener {

	// HASH TAG PTRN
	protected static final Pattern POST_HASHTAG_PTRN = Pattern.compile("#[^#\\s]{1,}");
	
	protected List<TagPosition> findTagPositions(String content) {
		Matcher m = POST_HASHTAG_PTRN.matcher(content);
		List<TagPosition> posList = new ArrayList<>();
		while(m.find()) {
			int start = m.start(), end = m.end();
			String rawTag = m.group();
			
			TagPosition tag = processRawTag(content, rawTag, start, end);
			if(tag != null) posList.add(tag);
		}
		
		return posList;
	}
	
	/**
	 * 规则:
	 * 1. #开头, 后面一个或者多个非(# 或 空白 或 标点)字符
	 * 2. #处在行首 / 前面为空格 / 前面为标点
	 * 3. 以行尾 / 标点结束
	 * 
	 * @param s
	 * @param rawTag
	 * @param start
	 * @param end
	 * @return null -> no tag
	 */
	protected TagPosition processRawTag(String s, String rawTag, int start, int end) {
		if(start != 0) {
			char pre = s.charAt(start - 1);
			if(!Character.isWhitespace(pre) && !StringUtils.isPuncChar(pre)) {
				return null;
			}
		}
		
		int i = 1, j = 0; // tag len
		for(; i < rawTag.length(); i++) {
			char c = rawTag.charAt(i);
			if(StringUtils.isPuncChar(c)) {
				break;
			}
		}
		
		j = i - 1;
		if(j == 0) return null;
		
		// create tag
		TagPosition tag = new TagPosition();
		tag.setText(rawTag.substring(1, j + 1));
		tag.setStart(start);
		tag.setEnd(start + j + 1);
		
		return tag;
	}
	
}
