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
	public Queue<String> crawl(String site, Queue<String> queue, Set<String> seen) {
		//update history
		for ( String page : queue ) {
			Double score = this.opicHistory.get(page);
			this.opicHistory.put(page, (score == null ? 0 : score) + 1.0 / queue.size());
		}

		//sort query
		List<String> sorted = new LinkedList<>(queue);
		Integer bsc = this.batchSizeSiteCount.get(site);
		if ( (bsc == null ? 0 : bsc) >= this.batchSize ) {
			Collections.sort(sorted, new Comparator<String>() {
				@Override
				public int compare(String arg0, String arg1) {
					Double darg0 = opicHistory.get(arg0);
					Double darg1 = opicHistory.get(arg1);
					darg0 = darg0 == null ? 0 : darg0;
					darg1 = darg1 == null ? 0 : darg1;
					return darg0.compareTo(darg1);
				}
			});

			this.siteMaxScore.put(site, this.opicHistory.get(sorted.get(0)));
		}

		//crawl
		queue = new LinkedList<>(sorted);
		bsc = this.batchSizeSiteCount.get(site);
		this.batchSizeSiteCount.put(site, (bsc == null ? 0 : bsc) + 1);
		return queue;
	}

	@Override
	public double getMaxPage(String site) {
		Double i = this.siteMaxScore.get(site);
		return i == null ? 0 : i;
	}
}