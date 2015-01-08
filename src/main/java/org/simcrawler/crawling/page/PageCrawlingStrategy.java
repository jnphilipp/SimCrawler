package org.simcrawler.crawling.page;

import java.util.Collection;
import java.util.Queue;
import org.simcrawler.crawling.site.SiteCrawlingStrategy;

/**
 * Page crawling strategy.
 *
 * @author proewer, jnphilipp
 * @since 2014-12-02
 */
public interface PageCrawlingStrategy {
	/**
	 * Reorders the given queue, so that the site to crawl is at the top.
	 *
	 * @param site site
	 * @param queue queue with pages of the given site
	 * @param crawled total number of crawled pages
	 * @return reordered queue
	 */
	public Queue<String> crawl(String site, Queue<String> queue, int crawled);

	/**
	 * Returns the highest ranking of the pages for the given site.
	 *
	 * @param site site
	 * @return highest ranking for the pages
	 */
	public double getMaxPage(String site);

	/**
	 * Returns the batch size.
	 *
	 * @return batch size
	 */
	public int getBatchSize();

	/**
	 * Returns the site crawling strategy.
	 *
	 * @return site crawling strategy
	 */
	public SiteCrawlingStrategy getSiteCrawlingStrategy();

	/**
	 * Initialize method.
	 *
	 * @param seeds seed URLs
	 */
	public void init(Collection<String> seeds);

	/**
	 * Update method.
	 *
	 * @param newURLs new URLs
	 */
	public void update(Collection<String> newURLs);

	/**
	 * Close method.
	 */
	public void close();
}
