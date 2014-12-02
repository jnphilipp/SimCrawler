package org.simcrawler.page;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.simcrawler.crawling.site.SiteStrategy;
import org.simcrawler.util.Helpers;

/**
 * 
 * @author proewer
 * @since 2014-12-02
 */
public class BackLinkStrategy implements PageStrategy {

	private DB mapdb;
	private static final File dbFileLinkCount = new File(Helpers.getUserDir()+"/data/backLinkCountDB");
	private static final File dbFileSiteCount = new File(Helpers.getUserDir()+"/data/backLinkSiteCountDB");
	private Map<String,Integer> backLinkCount;
	private Map<String,Integer> backLinkSiteCount;
	private SiteStrategy siteStr; 
	private int batchSize;
	
	public BackLinkStrategy(SiteStrategy siteStr, int batchSize){ 
		this.mapdb = DBMaker.newFileDB(dbFileLinkCount).mmapFileEnable().closeOnJvmShutdown().cacheSize(200000000).deleteFilesAfterClose().make();
		this.backLinkCount = mapdb.getHashMap("BackLinkCountMapping");
		this.mapdb = DBMaker.newFileDB(dbFileSiteCount).mmapFileEnable().closeOnJvmShutdown().cacheSize(200000000).deleteFilesAfterClose().make();
		this.backLinkSiteCount = mapdb.getHashMap("BackLinkSiteCountMapping");
		this.siteStr = siteStr;
		this.batchSize = batchSize;
	}
	
	@Override
	public String crawl(String site, Set<String> pages) {
		//sort
		List<String> sorted = new LinkedList<>(pages);
		if(backLinkSiteCount.get(site)>=this.batchSize) {
			backLinkSiteCount.put(site, 0);//reset
			Collections.sort(sorted, new Comparator<String>(){
	
				@Override
				public int compare(String arg0, String arg1) {
					int barg0 = backLinkCount.get(arg0);
					int barg1 = backLinkCount.get(arg1);
					return Integer.compare(barg0, barg1);
				}});
		}
		//crawl
		Queue<String> queue = new LinkedList<>(sorted);
		String crawledURL = queue.poll();
		String[] newURLs = siteStr.getCrawlSite().getLinks(crawledURL);
		//update db
		for(String url:newURLs){
			if(!backLinkCount.containsKey(url)) {
				backLinkCount.put(url, backLinkCount.get(url)+1);
				backLinkSiteCount.put(site, backLinkSiteCount.get(site)+1);
			}
			queue.add(url);
		}
		mapdb.commit();
		
		return crawledURL;
	}

	@Override
	public void close() {
		this.mapdb.close();
	}
}
