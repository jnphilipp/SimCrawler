package org.simcrawler.crawling.page.optimal;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.simcrawler.crawling.page.AbstractPageCrawlingStrategy;
import org.simcrawler.crawling.page.bl.BacklinkStrategy;
import org.simcrawler.crawling.page.opic.OPICStrategy;
import org.simcrawler.crawling.site.SiteCrawlingStrategy;

/**
 *
 * @author jnphilipp
 * @since 2015-01-08
 */
public class OptimalStrategy extends AbstractPageCrawlingStrategy {
	private BacklinkStrategy bl;
	private OPICStrategy opic;
	private Map<String, Integer[]> ratio;
	private Map<String, Double> siteMaxScore;

	public OptimalStrategy(SiteCrawlingStrategy siteStrategy, int batchSize) {
		super(siteStrategy, batchSize);
		this.bl = new BacklinkStrategy(siteStrategy, Integer.MAX_VALUE);
		this.opic = new OPICStrategy(siteStrategy, Integer.MAX_VALUE);
	}

	@Override
	public void close() {}

	@Override
	public Queue<String> crawl(String site, Queue<String> queue, int crawled) {
		this.bl.crawl(site, queue, crawled);
		this.opic.crawl(site, queue, crawled);

		if ( !this.ratio.containsKey(site) )
			this.ratio.put(site, new Integer[]{0,0});

		Integer bsc = this.batchSizeSiteCount.get(site);
		synchronized ( this ) {
			if ( (bsc == null ? 0 : bsc) >= this.getBatchSize() ) {
				List<String> pages = new LinkedList<>(queue);
				final Map<String, Double> o = new LinkedHashMap<>();

				Integer[] r = this.ratio.get(site);
				double v = 0.0d;
				if ( r[0] == r[1] && r[1] == 0 )
					v = Math.random();
				else
					v = (double)r[0] / (double)r[1];

				for ( String page : pages )
					o.put(page, Math.log((this.opic.getOpicHistory().containsKey(page) ? this.opic.getOpicHistory().get(page) : 0) * this.fBL(page, crawled) * v));

				Collections.sort(pages, new Comparator<String>() {
					@Override
					public int compare(String arg0, String arg1) {
						return Double.compare(o.get(arg1), o.get(arg0));
					}
				});

				queue = new LinkedList<>(pages);
				this.siteMaxScore.put(site, o.get(queue.peek()));
			}
		}

		Integer[] ratio = this.ratio.get(site);
		ratio[0] += this.siteStrategy.getCrawlSite().evaluate(queue.peek());
		ratio[1]++;
		this.ratio.put(site, ratio);

		if ( !this.batchSizeSiteCount.containsKey(site) )
			this.batchSizeSiteCount.put(site, 0);
		for ( String url : this.getSiteCrawlingStrategy().getCrawlSite().getLinks(queue.peek()) )
			if ( url.startsWith(site) )
				this.batchSizeSiteCount.put(site, this.batchSizeSiteCount.get(site) + 1);

		return queue;
	}

	private double fBL(String page, int crawled) {
		return (double)(this.bl.getBacklinkCount().containsKey(page) ? this.bl.getBacklinkCount().get(page) : 0) / (double)crawled;
	}

	@Override
	public double getMaxPage(String site) {
		return this.siteMaxScore.containsKey(site) ? this.siteMaxScore.get(site) : 0;
	}

	@Override
	public void init(Collection<String> seeds) {
		this.bl.init(seeds);
		this.opic.init(seeds);
		this.batchSizeSiteCount = Collections.synchronizedMap(new LinkedHashMap<String, Integer>());
		this.siteMaxScore = Collections.synchronizedMap(new LinkedHashMap<String, Double>());
		this.ratio = Collections.synchronizedMap(new LinkedHashMap<String, Integer[]>());

		for ( String page : seeds )
			this.ratio.put(this.siteStrategy.getSite(page), new Integer[]{0,0});
	}

	@Override
	public void update(Collection<String> newURLs) {
		this.bl.update(newURLs);
		this.opic.update(newURLs);
	}	
}