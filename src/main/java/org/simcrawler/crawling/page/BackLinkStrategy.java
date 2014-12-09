package org.simcrawler.crawling.page;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.simcrawler.crawling.site.SiteStrategy;

/**
 *
 * @author proewer
 * @since 2014-12-02
 */
public class BackLinkStrategy implements PageStrategy {

	//private DB mapdb;
	//private static final File dbFile = new File(Helpers.getUserDir() + "/data/backLink");
	private Map<String, Integer> backLinkCount;
	private Map<String, Integer> batchSizeSiteCount;
	private SiteStrategy siteStrategy;
	private int batchSize;

	public BackLinkStrategy(SiteStrategy siteStrategy, int batchSize) {
		/*this.mapdb = DBMaker.newFileDB(dbFile).mmapFileEnable().closeOnJvmShutdown().cacheSize(200000000).make();
		this.backLinkCount = this.mapdb.getHashMap("BackLinkCountMapping");
		this.backLinkSiteCount = this.mapdb.getHashMap("BackLinkSiteCountMapping");*/
		this.backLinkCount = Collections.synchronizedMap(new LinkedHashMap<String, Integer>());
		this.batchSizeSiteCount = Collections.synchronizedMap(new LinkedHashMap<String, Integer>());
		this.siteStrategy = siteStrategy;
		this.batchSize = batchSize;
	}

	@Override
	public void close() {
		//this.mapdb.close();
		//this.mapdb.delete("BackLinkCountMapping");
		//dbFile.delete();
	}

	@Override
	public Queue<String> crawl(String site, Queue<String> queue, Set<String> seen) {
		//sort
		List<String> sorted = new LinkedList<>(queue);
		Integer bsc = this.batchSizeSiteCount.get(site);
		if ( (bsc == null ? 0 : bsc) >= this.batchSize ) {
			this.batchSizeSiteCount.put(site, 0);//reset
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
		queue = new LinkedList<>(sorted);
		String crawledURL = queue.peek();
		//pages = Collections.synchronizedSet(new LinkedHashSet<>(queue));
		String[] newURLs = this.siteStrategy.getCrawlSite().getLinks(crawledURL);
		//update db

		bsc = this.batchSizeSiteCount.get(site);
		this.batchSizeSiteCount.put(site, (bsc == null ? 0 : bsc) + 1);
		for ( String url : newURLs ) {
			if ( seen.contains(url) )
				this.backLinkCount.remove(url);
			else {
				Integer blc = this.backLinkCount.get(url);
				this.backLinkCount.put(url, (blc == null ? 0 : blc) + 1);
			}
		}

		return queue;
	}
}
