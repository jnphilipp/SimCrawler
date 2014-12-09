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
public class OPICStrategy implements PageStrategy {

	private Map<String, Double> opicHistory;
	//private int sumScoreHistory;
	private Map<String, Integer> batchSizeSiteCount;
	private SiteStrategy siteStrategy;
	private int batchSize;

	public OPICStrategy(SiteStrategy siteStrategy, int batchSize) {
		this.siteStrategy = siteStrategy;
		this.batchSize = batchSize;
		//this.sumScoreHistory = 0;
		this.batchSizeSiteCount = Collections.synchronizedMap(new LinkedHashMap<String, Integer>());
		this.opicHistory = Collections.synchronizedMap(new LinkedHashMap<String, Double>());
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}

	/*private void updateScore() {
		for (Map.Entry<String, Double> entry : this.opicHistory.entrySet())
			this.sumScoreHistory+=entry.getValue();
	}*/

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Queue<String> crawl(String site, Queue<String> queue, Set<String> seen) {

		//update history
		for ( String page : queue ) {
			Double score = this.opicHistory.get(page);
			this.opicHistory.put(page, (score == null ? 0 : score) + 1.0 / queue.size());
		}
		//updateScore();
		List<String> sorted = new LinkedList<>(queue);
		//sort query
		Integer bsc = this.batchSizeSiteCount.get(site);
		if ( (bsc == null ? 0 : bsc) >= this.batchSize ) {
			Collections.sort(sorted, new Comparator() {
				@Override
				public int compare(Object arg0, Object arg1) {
					Double darg0 = opicHistory.get(arg0);
					Double darg1 = opicHistory.get(arg1);
					darg0 = darg0 == null ? 0 : darg0;
					darg1 = darg1 == null ? 0 : darg1;
					return darg0.compareTo(darg1);
				}
			});
		}
		//crawl
		queue = new LinkedList<>(sorted);
		//String crawledURL = queue.peek();
		//String[] newURLs = this.siteStrategy.getCrawlSite().getLinks(crawledURL);
		//update crawled pages counter
		bsc = this.batchSizeSiteCount.get(site);
		this.batchSizeSiteCount.put(site, (bsc == null ? 0 : bsc) + 1);
		/*this.batchSize -= newURLs.length;
		if ( this.batchSize <= 0 ) {
			this.batchSize *= -1;
			this.sortFlag = true;
		}*/
		//return crawled URL
		return queue;
	}
}