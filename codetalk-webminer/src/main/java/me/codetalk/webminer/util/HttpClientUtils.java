package me.codetalk.webminer.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import me.codetalk.webminer.MinerConstants;

public class HttpClientUtils {

	private static final HttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(); 
	
	private static HttpClient httpClient = HttpClients.custom()
													  .setConnectionManager(connManager)
													  .setConnectionManagerShared(true)
													  .build();
	
	public static Document getDocument(String url) throws IOException {
		
//		RequestConfig.Builder config = RequestConfig.custom();
//		config.setConnectionRequestTimeout(6000);
//		config.setSocketTimeout(6000);
		
		HttpGet get = new HttpGet(url);
		get.setHeader("User-Agent", MinerConstants.HTTP_USER_AGENT);

		HttpResponse response = httpClient.execute(get);
		try( InputStream in = response.getEntity().getContent() ) {
			String html = IOUtils.toString(in);
//			LOGGER.info("Html returned: " + html);
			
        	Document doc = Jsoup.parse(html);
        	doc.setBaseUri(StringUtils.extractUrl(url)[0]);
        	
        	return doc;
        }
	}
	
}
