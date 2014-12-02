package org.simcrawler.crawling.page;

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
	private static final File dbFile = new File(Helpers.getUserDir() + "/data/backLink");
	private Map<String, Integer> backLinkCount;
	private Map<String, Integer> backLinkSiteCount;
	private SiteStrategy siteStrategy;
	private int batchSize;

	public BackLinkStrategy(SiteStrategy siteStrategy, int batchSize) {
		this.mapdb = DBMaker.newFileDB(dbFile).mmapFileEnable().closeOnJvmShutdown().cacheSize(200000000).make();
		this.backLinkCount = this.mapdb.getHashMap("BackLinkCountMapping");
		this.backLinkSiteCount = this.mapdb.getHashMap("BackLinkSiteCountMapping");
		this.siteStrategy = siteStrategy;
		this.batchSize = batchSize;
	}

	@Override
	public void close() {
		this.mapdb.close();
		//this.mapdb.delete("BackLinkCountMapping");
		dbFile.delete();
	}

	@Override
	public String crawl(String site, Set<String> pages) {
		if ( pages == null )
			return null;
		//sort
		List<String> sorted = new LinkedList<>(pages);
		Integer bsc = backLinkSiteCount.get(site);
		if ( (bsc == null ? 0 : bsc) >= this.batchSize ) {
			backLinkSiteCount.put(site, 0);//reset
			Collections.sort(sorted, new Comparator<String>() {
				@Override
				public int compare(String arg0, String arg1) {
					Integer t = backLinkCount.get(arg0);
					int barg0 = t == null ? 0 : t;
					t = backLinkCount.get(arg1);
					int barg1 = t == null ? 0 : t;
					return Integer.compare(barg0, barg1);
				}
			});
		}
		//crawl
		Queue<String> queue = new LinkedList<>(sorted);
		String crawledURL = queue.poll();
		String[] newURLs = siteStrategy.getCrawlSite().getLinks(crawledURL);
		//update db
		for ( String url : newURLs ) {
			Integer blc = backLinkCount.get(url);
			bsc = backLinkSiteCount.get(url);
			backLinkCount.put(url, (blc == null ? 0 : blc) + 1);
			backLinkSiteCount.put(site, (bsc == null ? 0 : bsc) + 1);
			queue.add(url);
		}
		mapdb.commit();

		return crawledURL;
	}
}
