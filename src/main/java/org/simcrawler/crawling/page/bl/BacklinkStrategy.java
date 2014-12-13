package org.simcrawler.crawling.page.bl;

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
 * Backlink page strategy.
 *
 * @author proewer, jnphilipp
 * @since 2014-12-02
 */
public class BacklinkStrategy extends AbstractPageCrawlingStrategy {
	private Map<String, Integer> backlinkCount;
	private Map<String, Integer> batchSizeSiteCount;
	private Map<String, Integer> siteMaxBacklinkCount;

	public BacklinkStrategy(SiteCrawlingStrategy siteStrategy, int batchSize) {
		super(siteStrategy, batchSize);
	}

	@Override
	public Queue<String> crawl(String site, Queue<String> queue) {
		//sort
		Integer bsc = this.batchSizeSiteCount.get(site);
		synchronized ( this.backlinkCount ) {
			List<String> sorted = new LinkedList<>(queue);

			if ( (bsc == null ? 0 : bsc) >= this.batchSize ) {
				this.batchSizeSiteCount.put(site, 0);//reset
				Collections.sort(sorted, new Comparator<String>() {
					@Override
					public int compare(String arg0, String arg1) {
						return Integer.compare(backlinkCount.containsKey(arg1) ? backlinkCount.get(arg1) : 0, backlinkCount.containsKey(arg0) ? backlinkCount.get(arg0) : 0);
					}
				});

				queue = new LinkedList<>(sorted);
				this.siteMaxBacklinkCount.put(site, this.backlinkCount.containsKey(queue.peek()) ? this.backlinkCount.get(queue.peek()) : 0);
			}
		}

		//update db
		for ( String url : this.siteStrategy.getCrawlSite().getLinks(queue.peek()) ) {
			if ( !this.backlinkCount.containsKey(url) )
				this.backlinkCount.put(url, 0);
			this.backlinkCount.put(url, this.backlinkCount.get(url) + 1);

			String s = this.siteStrategy.getSite(url);
			if ( !this.batchSizeSiteCount.containsKey(s) )
				this.batchSizeSiteCount.put(s, 0);
			this.batchSizeSiteCount.put(s, this.batchSizeSiteCount.get(s) + 1);
		}

		return queue;
	}

	@Override
	public double getMaxPage(String site) {
		return this.siteMaxBacklinkCount.containsKey(site) ? this.siteMaxBacklinkCount.get(site) : 0;
	}

	@Override
	public void init(Collection<String> seeds) {
		this.backlinkCount = Collections.synchronizedMap(new LinkedHashMap<String, Integer>());
		this.batchSizeSiteCount = Collections.synchronizedMap(new LinkedHashMap<String, Integer>());
		this.siteMaxBacklinkCount = Collections.synchronizedMap(new LinkedHashMap<String, Integer>());
	}

	@Override
	public void update(Collection<String> seeds) {
	}

	@Override
	public void close() {
	}
}
