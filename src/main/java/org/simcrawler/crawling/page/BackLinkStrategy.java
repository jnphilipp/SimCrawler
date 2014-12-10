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
 * @author proewer, jnphilipp
 * @since 2014-12-02
 */
public class BackLinkStrategy implements PageStrategy {
	private int batchSize;
	private SiteStrategy siteStrategy;
	private Map<String, Integer> backLinkCount;
	private Map<String, Integer> batchSizeSiteCount;
	private Map<String, Integer> siteMaxBackLinkCount;

	public BackLinkStrategy(SiteStrategy siteStrategy, int batchSize) {
		this.siteStrategy = siteStrategy;
		this.batchSize = batchSize;
		this.backLinkCount = Collections.synchronizedMap(new LinkedHashMap<String, Integer>());
		this.batchSizeSiteCount = Collections.synchronizedMap(new LinkedHashMap<String, Integer>());
		this.siteMaxBackLinkCount = Collections.synchronizedMap(new LinkedHashMap<String, Integer>());
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
					return Integer.compare(backLinkCount.containsKey(arg0) ? backLinkCount.get(arg0) : 0, backLinkCount.containsKey(arg1) ? backLinkCount.get(arg1) : 0);
				}
			});

			this.siteMaxBackLinkCount.put(site, this.backLinkCount.get(sorted.get(0)));
		}

		//crawl
		queue = new LinkedList<>(sorted);

		//update db
		for ( String url : this.siteStrategy.getCrawlSite().getLinks(queue.peek()) ) {
			if ( !this.backLinkCount.containsKey(url) )
				this.backLinkCount.put(url, 0);
			this.backLinkCount.put(url, this.backLinkCount.get(url) + 1);

			String s = this.siteStrategy.getSite(url);
			if ( !this.batchSizeSiteCount.containsKey(s) )
				this.batchSizeSiteCount.put(s, 0);
			this.batchSizeSiteCount.put(s, this.batchSizeSiteCount.get(s) + 1);
		}

		return queue;
	}

	@Override
	public double getMaxPage(String site) {
		return this.siteMaxBackLinkCount.containsKey(site) ? this.siteMaxBackLinkCount.get(site) : 0;
	}
}