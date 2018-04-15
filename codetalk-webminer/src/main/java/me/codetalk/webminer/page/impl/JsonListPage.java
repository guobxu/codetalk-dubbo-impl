package me.codetalk.webminer.page.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import me.codetalk.webminer.page.Page;
import me.codetalk.webminer.page.PageAttr;
import me.codetalk.webminer.util.StringUtils;

/**
 * JSON格式列表页面
 * 
 * @author guobxu
 *
 */
public class JsonListPage extends AbstractListPage {

	public JsonListPage(String url) {
		super(url);
	}

	@Override
	public List<Page> fetchPages(PageAttr attr) throws IOException { // Assumption: key[JSONObject] -> ... ->  key[JSONObject] -> key[JSONArray]
		List<Page> pages = new ArrayList<Page>();

		try(InputStream input = new URL(url).openStream()) {
			String json = IOUtils.toString(input);
			
			JSONObject jsonObj = JSON.parseObject(json);
			
			JSONObject tmpObj = null;
			JSONArray jsonArr = null;
			String[] keys = attr.getEl().split("\\s+");
			for(int i = 0, len = keys.length; i < len; i++) {
				String key = keys[i];
				
				if(i == len - 1) {
					jsonArr = (JSONArray) ( i == 0 ? jsonObj.getJSONArray(key) : tmpObj.getJSONArray(key) );
				} else {
					tmpObj = ( tmpObj == null ? jsonObj.getJSONObject(key) : tmpObj.getJSONObject(key) );
				}
			}
			
			if(jsonArr != null) {
				String[] siteRes = StringUtils.extractUrl(url);
				
				jsonArr.forEach( obj -> {
					JSONObject jobj = (JSONObject)obj;
					
					String pageUrl = jobj.getString(attr.getName());
					Page subPage = new JsoupHtmlPage(siteRes[0] +
							( pageUrl.startsWith("/") ? "" : "/" ) +
							pageUrl);
					pages.add(subPage);
				});
			}
		}
		
		return pages;
	}
	

}