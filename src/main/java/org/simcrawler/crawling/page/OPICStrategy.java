package org.simcrawler.crawling.page;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.simcrawler.crawling.site.SiteStrategy;

/**
 *
 * @author proewer, jnphilipp
 * @since 2014-12-02
 */
public class OPICStrategy implements PageStrategy {
	private int batchSize;
	private SiteStrategy siteStrategy;
	private Map<String, Double> opicHistory;
	private Map<String, Integer> batchSizeSiteCount;
	private Map<String, Double> siteMaxScore;

	public OPICStrategy(SiteStrategy siteStrategy, int batchSize) {
		this.siteStrategy = siteStrategy;
		this.batchSize = batchSize;
		this.opicHistory = Collections.synchronizedMap(new LinkedHashMap<String, Double>());
		this.batchSizeSiteCount = Collections.synchronizedMap(new LinkedHashMap<String, Integer>());
		this.siteMaxScore = Collections.synchronizedMap(new LinkedHashMap<String, Double>());
	}

	@Override
	public Queue<String> crawl(String site, Queue<String> queue) {
		//update history
		double score = 1.0f / this.siteStrategy.getCrawlSite().getLinks(queue.peek()).length;
		for ( String page : queue )
			this.opicHistory.put(page, (this.opicHistory.containsKey(page) ? this.opicHistory.get(page) : 0) + score);

		//sort query
		List<String> sorted = new LinkedList<>(queue);
		Integer bsc = this.batchSizeSiteCount.get(site);
		if ( (bsc == null ? 0 : bsc) >= this.batchSize ) {
			Collections.sort(sorted, new Comparator<String>() {
				@Override
				public int compare(String arg0, String arg1) {
					return Double.compare(opicHistory.containsKey(arg1) ? opicHistory.get(arg1) : 0, opicHistory.containsKey(arg0) ? opicHistory.get(arg0) : 0);
				}
			});

			this.siteMaxScore.put(site, this.opicHistory.get(sorted.get(0)));
		}

		//crawl
		queue = new LinkedList<>(sorted);

		//update db
		if ( !this.batchSizeSiteCount.containsKey(site) )
			this.batchSizeSiteCount.put(site, 0);
		for ( String url : this.siteStrategy.getCrawlSite().getLinks(queue.peek()) ) {
			if ( url.startsWith(site) )
				this.batchSizeSiteCount.put(site, this.batchSizeSiteCount.get(site) + 1);
		}

		return queue;
	}

	@Override
	public double getMaxPage(String site) {
		return this.siteMaxScore.containsKey(site) ? this.siteMaxScore.get(site) : 0;
	}
}