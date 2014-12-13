package org.simcrawler.crawling.page.opic;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.simcrawler.crawling.page.AbstractPageCrawlingStrategy;
import org.simcrawler.crawling.site.SiteCrawlingStrategy;

/**
 * OPIC strategy.
 *
 * @author proewer, jnphilipp
 * @since 2014-12-02
 */
public class OPICStrategy extends AbstractPageCrawlingStrategy {
	private double cache;
	private double g;
	private Map<String, Double> opicHistory;
	private Map<String, Integer> batchSizeSiteCount;
	private Map<String, Double> siteMaxScore;

	public OPICStrategy(SiteCrawlingStrategy siteStrategy, int batchSize) {
		super(siteStrategy, batchSize);
	}

	@Override
	public Queue<String> crawl(String site, Queue<String> queue) {
		//sort queue
		Integer bsc = this.batchSizeSiteCount.get(site);
		if ( (bsc == null ? 0 : bsc) >= this.getBatchSize() ) {
			List<String> sorted = new LinkedList<>(queue);

			synchronized ( this.opicHistory ) {
				Collections.sort(sorted, new Comparator<String>() {
					@Override
					public int compare(String arg0, String arg1) {
						double a0 = ((opicHistory.containsKey(arg0) ? opicHistory.get(arg0) : 0) + cache) / (g + 1.0d);
						double a1 = ((opicHistory.containsKey(arg1) ? opicHistory.get(arg1) : 0) + cache) / (g + 1.0d);
						return Double.compare(a1, a0);
					}
				});
			}

			queue = new LinkedList<>(sorted);
			this.siteMaxScore.put(site, this.opicHistory.containsKey(queue.peek()) ? this.opicHistory.get(queue.peek()) : 0);
		}

		//update history
		String page = queue.peek();
		this.opicHistory.put(page, (this.opicHistory.containsKey(page) ? this.opicHistory.get(page) : 0) + this.cache);
		String[] links = this.getSiteCrawlingStrategy().getCrawlSite().getLinks(page);
		double i = links.length == 0 ? 0 : this.opicHistory.get(page) / links.length;
		for ( String link : links )
			this.opicHistory.put(link, (this.opicHistory.containsKey(link) ? this.opicHistory.get(link) : 0) + i);
		this.g += this.opicHistory.get(page);

		//update db
		if ( !this.batchSizeSiteCount.containsKey(site) )
			this.batchSizeSiteCount.put(site, 0);
		for ( String url : this.getSiteCrawlingStrategy().getCrawlSite().getLinks(queue.peek()) )
			if ( url.startsWith(site) )
				this.batchSizeSiteCount.put(site, this.batchSizeSiteCount.get(site) + 1);

		return queue;
	}

	@Override
	public double getMaxPage(String site) {
		return this.siteMaxScore.containsKey(site) ? this.siteMaxScore.get(site) : 0;
	}

	@Override
	public void init(Collection<String> seeds) {
		this.opicHistory = Collections.synchronizedMap(new LinkedHashMap<String, Double>());
		this.batchSizeSiteCount = Collections.synchronizedMap(new LinkedHashMap<String, Integer>());
		this.siteMaxScore = Collections.synchronizedMap(new LinkedHashMap<String, Double>());

		this.cache = 1.0d / seeds.size();
		this.g = 0.0d;
	}

	@Override
	public void update(Collection<String> seeds) {
		this.cache = 1.0d / ((1.0d / this.cache) + seeds.size());
	}

	@Override
	public void close() {
	}
}
