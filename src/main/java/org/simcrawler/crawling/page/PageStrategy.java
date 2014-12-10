package org.simcrawler.crawling.page;

import java.util.Queue;

/**
 *
 * @author proewer, jnphilipp
 * @since 2014-12-02
 */
public interface PageStrategy {
	/**
	 * Reorders the given queue, so that the site to crawl is at the top.
	 * @param site site
	 * @param queue queue with pages of the given site
	 * @return reordert queue
	 */
	public Queue<String> crawl(String site, Queue<String> queue);

	public double getMaxPage(String site);
}