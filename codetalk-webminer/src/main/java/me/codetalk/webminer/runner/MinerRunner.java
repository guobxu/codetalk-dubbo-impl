package me.codetalk.webminer.runner;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import me.codetalk.messaging.IMessageSender;
import me.codetalk.messaging.MessagingUtil;
import me.codetalk.webminer.MinerConstants;
import me.codetalk.webminer.mapper.SiteListMapper;
import me.codetalk.webminer.mapper.SitePageMapper;
import me.codetalk.webminer.mapper.WebEntityAttrMapper;
import me.codetalk.webminer.mapper.WebEntityMapper;
import me.codetalk.webminer.page.ListPage;
import me.codetalk.webminer.page.Page;
import me.codetalk.webminer.page.PageAttr;
import me.codetalk.webminer.page.PageData;
import me.codetalk.webminer.page.impl.HttpClientHtmlListPage;
import me.codetalk.webminer.page.impl.HttpClientHtmlPage;
import me.codetalk.webminer.page.impl.JsonListPage;
import me.codetalk.webminer.page.impl.JsoupHtmlListPage;
import me.codetalk.webminer.page.impl.JsoupHtmlPage;
import me.codetalk.webminer.pojo.SiteEntityAttr;
import me.codetalk.webminer.pojo.SiteListVO;
import me.codetalk.webminer.pojo.SitePage;
import me.codetalk.webminer.pojo.SitePageVO;
import me.codetalk.webminer.pojo.WebEntity;
import me.codetalk.webminer.pojo.WebEntityAttr;
import me.codetalk.webminer.pojo.WebEntityVO;

@Component
public class MinerRunner implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(MinerRunner.class);
	
//	@Autowired
//	private IKafkaMesgService messagingService;

	@Autowired
	private IMessageSender mesgSender;
	
	@Autowired
	private SiteListMapper slmapper;
	
	@Autowired
	private SitePageMapper spmapper;
	
	@Autowired
	private WebEntityMapper wemapper;
	
	@Autowired
	private WebEntityAttrMapper weamapper;
	
	private final long sleepInit = 60 * 1000L;	// 休眠, 避免429 - too many requests
	
	private final Map<Integer, Long> siteSleep = new ConcurrentHashMap<>();
	
	private Random rand = new Random();
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
	
	// site running status
	private Map<Integer, Boolean> siteStatus = new HashMap<>();
	
	// count down
	private CountDownLatch siteLatch = null;
	
	// 处理3天内的错误
	private static final long ERR_PAGE_WITHIN = 3L * 24 * 60 * 60 * 1000; // 
	
	@Override
	public synchronized void run(String... args) throws Exception {
		// 1. select active lists
		List<SiteListVO> lists = slmapper.selectActiveLists();
		if(lists.isEmpty()) return;
		
		// map: siteId => List<SiteListVO>
		Map<Integer, List<SiteListVO>> siteListMap = new HashMap<>();
		for(SiteListVO list : lists) {
			Integer siteId = list.getSiteId();
			
			List<SiteListVO> subLists = siteListMap.get(siteId);
			if(subLists == null) {
				subLists = new ArrayList<>();
				siteListMap.put(siteId, subLists);
			}
			subLists.add(list);
		}
		
		// map: siteId => iterator
		Map<Integer, Iterator<SiteListVO>> siteIterMap = new HashMap<>();
		siteListMap.forEach((siteId, siteList) -> {
			siteIterMap.put(siteId, siteList.iterator());
			
			siteStatus.put(siteId, Boolean.FALSE);
			siteSleep.put(siteId, sleepInit);
		});
		
		// 1.1 executor & latch
		int threads = siteListMap.size();
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		siteLatch = new CountDownLatch(threads);
		
		while(true) {
			siteIterMap.forEach((siteId, listIter) -> {
				if(siteStatus.get(siteId)) {
					return; 
				}
				
				if(!listIter.hasNext()) {
					siteLatch.countDown(); // site has no more list, then count down
					
					return;
				}
					
				executor.submit(() -> {
					siteStatus.put(siteId, Boolean.TRUE);
					
					processList(listIter.next());
				});
			});
			
			if(siteLatch.getCount() == 0) {
				executor.shutdown();
				
				return; // all done
			}
			
			wait();
		}

	}
	
	private void processList(SiteListVO list) {
		while(true) {
			// 2. fetch sub pages
			
			// 2.1 make sure list still enabled
			Integer listEnabled = slmapper.selectListEnabled(list.getId());
			if(listEnabled == 0) {
				listDoneNotify(list);
				
				return;
			}
			
			// 2.2 make sure not exceed max page
			if(list.exceedMaxPage()) {
				slmapper.updateListDisabled(list.getId(), "Max page reached!");
				listDoneNotify(list);
				
				return;
			}
			
			String url = list.getDerivedUrl(); // derived url
			Integer siteId = list.getSiteId();
			
			LOGGER.info("===============> Start to process list url: " + url);
			
			Integer type = list.getType();
			ListPage listPage = null;
			if(type == MinerConstants.LIST_TYPE_HTML) {
				listPage = new JsoupHtmlListPage(url);
			} else if(type == MinerConstants.LIST_TYPE_JSON) {
				listPage = new JsonListPage(url);
			} else if(type == MinerConstants.LIST_TYPE_HTML_HTTPCLIENT) {
				listPage = new HttpClientHtmlListPage(url);
			} else {
				throw new IllegalArgumentException("Err list type");
			}
			
			PageAttr pa = new PageAttr(list.getDerivedEl(), list.getPagesAttr());
			List<Page> pages = null;
			try {
				pages = listPage.fetchPages(pa);
				
				try {
					Thread.sleep(rand.nextInt(30) * 1000);
				} catch(InterruptedException iex) {
					LOGGER.error(iex.getMessage(), iex);
				}
			} catch(IOException ioe) {
				LOGGER.error(ioe.getMessage(), ioe);
				LOGGER.info("===============> Error fetch sub pages of url: " + url);
				
				if(getSiteSleep(siteId) > sleepInit * 4) {	// disable for too much wait - add 1106
					setSiteSleep(siteId, sleepInit);
					
					slmapper.updateListDisabled(list.getId(), "Error fetch sub page after 3 times retry, list disabled!");
					listDoneNotify(list);
					
					return;
				}
				
				// retry later
				try {
					long sleepMillis = getSiteSleep(siteId);
					Thread.sleep(sleepMillis);
					
					setSiteSleep(siteId, sleepMillis * 2);
				} catch(InterruptedException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
				
				continue;
			}
			
			setSiteSleep(siteId, sleepInit);
			
			if(pages.isEmpty()) {
				slmapper.updateListDisabled(list.getId(), "No sub page found!");
				listDoneNotify(list);
				
				return;
			}
			
			processPages(pages, siteId, list.getSiteName(), list.getType(), list.getEntityTypeId(), list.getEntityType(), list.getEntityAttrs());
			
			if(list.getPageParam() == null) {	// 非分页, 直接返回
				slmapper.updateListDisabled(list.getId(), "List page finished!");
				listDoneNotify(list);
				
				return;
			}  else {
				slmapper.incrLastPage(list.getId());
				list.setLastPage(list.getLastPage() + 1);
			}
		}
		
	}
	
	private long getSiteSleep(Integer siteId) {
		return siteSleep.get(siteId);
	}
	
	private void setSiteSleep(Integer siteId, Long sleepMillis) {
		siteSleep.put(siteId, sleepMillis);
	}
	
	private synchronized void listDoneNotify(SiteListVO list) {
		siteStatus.put(list.getSiteId(), Boolean.FALSE);
//		sitesLock.notify();
		notify();
	}
	
	private void processPages(List<Page> pages, Integer siteId, String siteName, Integer listType, Integer entityTypeId, String entityType, List<SiteEntityAttr> entityAttrs) {
		// 3. insert site pages
		// 3-1. filter 
		// create mapping: url -> page
		Map<String, Page> urlPageMap = new HashMap<>(); // url->Page mapping
		
		List<String> urlList = new ArrayList<String>();
		for(Page page : pages) {
			urlList.add(page.getUrl());
			
			urlPageMap.put(page.getUrl(), page);
		}

		List<String> existingUrls = spmapper.selectUrlIn(urlList);
		
		List<SitePage> sitePages = new ArrayList<SitePage>();
		for(Page page : pages) {
			if(existingUrls.contains(page.getUrl())) continue;
			
			SitePage sp = new SitePage();
			sp.setId(UUID.randomUUID().toString());
			sp.setUrl(page.getUrl());
			sp.setSiteId(siteId);
			sp.setEntityTypeId(entityTypeId);
			
			if(listType == MinerConstants.LIST_TYPE_HTML || listType == MinerConstants.LIST_TYPE_JSON) {
				sp.setType(MinerConstants.PAGE_TYPE_HTML);
			} else if(listType == MinerConstants.LIST_TYPE_HTML_HTTPCLIENT || listType == MinerConstants.LIST_TYPE_JSON_HTTPCLIENT) {
				sp.setType(MinerConstants.PAGE_TYPE_HTML_HTTPCLIENT);
			}
			
			sitePages.add(sp);
		}
		
		// 4. fetch page
		if(sitePages.isEmpty()) return;
		
		spmapper.insertPages(sitePages);
		
		// 4.1 conver attr map
		Map<String, PageAttr> attrMap = convertToAttrMap(entityAttrs);
		
		for(SitePage sp : sitePages) {
			String pageUrl = sp.getUrl();
			LOGGER.info("===============> Start to process page url: " + pageUrl);
			
			Page page = urlPageMap.get(pageUrl);
			try {
				PageData we = page.fetchEntity(attrMap);
				
				LOGGER.info("===============> Fetch page successfully!");
				
				// sleep a while
				try {
					Thread.sleep(rand.nextInt(45) * 1000);
				} catch(InterruptedException iex) {
					LOGGER.error(iex.getMessage(), iex);
				}
				
				LOGGER.info("===============> End of sleep!");
				
				// reset sleep millis
				setSiteSleep(siteId, sleepInit);
				
				LOGGER.info("===============> Begin to process web entity!");
				
				processWebEntity(we, siteId, siteName, entityTypeId, entityType);
				
				// 4.3 update page status
				sp.setStatus(MinerConstants.PAGE_STATUS_FETCHED);
				spmapper.updatePageStatus(sp);
			} catch(Exception ex) {
				LOGGER.error(ex.getMessage(), ex);
				LOGGER.error("===============> Error process page, skip url: " + pageUrl);
				
				sp.setStatus(MinerConstants.PAGE_STATUS_ERR);
				sp.setErrorMsg(ex.getMessage());
				spmapper.updatePageStatus(sp);
				
				// retry later
				if(ex instanceof IOException) {
					try {
						long sleepMillis = getSiteSleep(siteId);
						Thread.sleep(sleepMillis);
						
						setSiteSleep(siteId, sleepMillis * 2);
					} catch(InterruptedException iex) {
						LOGGER.error(iex.getMessage(), iex);
					}
				}
			}
			
		}
	}
	
	private void processWebEntity(PageData we, Integer siteId, String siteName, Integer entityTypeId, String entityType) {
		// 4.2 convert & insert web entity
		WebEntity entity = new WebEntity();
		entity.setEntityTypeId(entityTypeId);
		entity.setPageUrl(we.getUrl());
		entity.setSiteId(siteId);
		
		wemapper.insertEntity(entity);
		
		List<WebEntityAttr> attrs = new ArrayList<WebEntityAttr>();
		we.getAttrs().forEach((k, v) -> {
			WebEntityAttr attr = new WebEntityAttr();
			attr.setKey(k);
			attr.setVal(v);
			attr.setEntityId(entity.getId());
			
			attrs.add(attr);
		});
		
		weamapper.insertAttrList(attrs);
		
		// 4.2 send message to kafka
		WebEntityVO entityVO = new WebEntityVO();
		entityVO.setAttrs(attrs);
		entityVO.setId(entity.getId());
		entityVO.setEntityType(entityType);
		entityVO.setEntityTypeId(entityTypeId);
		entityVO.setPageUrl(we.getUrl());
		entityVO.setSite(siteName);
		entityVO.setSiteHome(we.getSite());
		entityVO.setSiteId(siteId);
		entityVO.setCreateDate(new Timestamp(System.currentTimeMillis()));
		
		String target = getTarget(entityVO);
		mesgSender.sendMessage(target, MessagingUtil.convertAsMesgObj(target, entityVO));
	}
	
	private Map<String, PageAttr> convertToAttrMap(List<SiteEntityAttr> entityAttrs) {
		// 4.1 conver attr map
		Map<String, PageAttr> attrMap = new HashMap<String, PageAttr>();
		for(SiteEntityAttr attr : entityAttrs) {
			PageAttr pageAttr = new PageAttr(attr.getEl(), attr.getName(), attr.getType());
			attrMap.put(attr.getKey(), pageAttr);
		}
		
		return attrMap;
	}
	
	// 每隔15分钟执行一次
	@Scheduled(fixedDelay = 1 * 60 * 60 * 1000)
	public void processErrPages() {
		Timestamp tsAfter = new Timestamp( System.currentTimeMillis() - ERR_PAGE_WITHIN );
		
		LOGGER.info("===============================> In processErrPages, after = " + sdf.format(tsAfter));
		
		List<SitePageVO> errPages = spmapper.selectErrPagesAfter(tsAfter);
		
		LOGGER.info("===============================> Found err pages, size = " + errPages.size());
		
		for(SitePageVO sp : errPages) {
			String pageUrl = sp.getUrl();
			LOGGER.info("===============> Start to process page url: " + pageUrl);
			
			Page page = null;
			if(sp.getType() == MinerConstants.PAGE_TYPE_HTML) {
				page = new JsoupHtmlPage(pageUrl);
			} else if(sp.getType() == MinerConstants.PAGE_TYPE_HTML_HTTPCLIENT) {
				page = new HttpClientHtmlPage(pageUrl);
			} else {
				LOGGER.error("===============> Err page type, return.");
				continue;
			}
			
			Map<String, PageAttr> attrMap = convertToAttrMap(sp.getEntityAttrs());
			try {
				PageData we = page.fetchEntity(attrMap);
				
				// sleep a while
				try {
					Thread.sleep(rand.nextInt(30) * 1000);
				} catch(InterruptedException iex) {
					LOGGER.error(iex.getMessage(), iex);
				}
				
				processWebEntity(we, sp.getSiteId(), sp.getSiteName(), sp.getEntityTypeId(), sp.getEntityType());
				
				sp.setStatus(MinerConstants.PAGE_STATUS_FETCHED);
				spmapper.updatePageStatus(sp);
			} catch(Exception ex) {
				LOGGER.error(ex.getMessage(), ex);
				LOGGER.error("===============> Error process page, skip url: " + pageUrl);
				
				sp.setStatus(MinerConstants.PAGE_STATUS_ERR);
				sp.setErrorMsg(ex.getMessage());
				spmapper.updatePageStatus(sp);
			}
			
		}
	}
	
	private String getTopic(WebEntityVO entity) {
		return MinerConstants.KAFKA_TOPIC_PREFIX + entity.getSite() + "-" + entity.getEntityType();
	}
	
	private String getTarget(WebEntityVO entity) {
		return MinerConstants.TARGET_WEBMINER_PREFIX + entity.getSite() + "-" + entity.getEntityType();
	}
	
	
}


























