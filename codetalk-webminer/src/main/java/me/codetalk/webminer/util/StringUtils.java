package me.codetalk.webminer.util;

import me.codetalk.webminer.MinerConstants;

public final class StringUtils {

	public static String[] extractUrl(String url) {
		String[] rt = new String[2];
		
		int pos = -1;
		if(url.startsWith(MinerConstants.PROTO_HTTP)) {
			pos = url.indexOf("/", MinerConstants.PROTO_HTTP.length());
		} else if(url.startsWith(MinerConstants.PROTO_HTTPS)) {
			pos = url.indexOf("/", MinerConstants.PROTO_HTTPS.length());
		} else {
			pos = url.indexOf("/");
		}
		
		rt[0] = url.substring(0, pos);
		rt[1] = url.substring(pos);
		
		return rt;
	}
	
}



















