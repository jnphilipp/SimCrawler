package org.simcrawler.crawling.page;

import org.simcrawler.crawling.site.SiteCrawlingStrategy;

/**
 * Abstract class for page crawling strategies.
 *
 * @author jnphilipp
 * @version 0.0.1
 * @since 2014-12-12
 */
public abstract class AbstractPageCrawlingStrategy implements PageCrawlingStrategy {
	protected int batchSize;
	protected SiteCrawlingStrategy siteStrategy;

	public AbstractPageCrawlingStrategy(SiteCrawlingStrategy siteStrategy, int batchSize) {
		this.siteStrategy = siteStrategy;
		this.batchSize = batchSize;
	}

	/**
	 * @return the batchSize
	 */
	@Override
	public int getBatchSize() {
		return this.batchSize;
	}

	/**
	 * @return the siteStrategy
	 */
	@Override
	public SiteCrawlingStrategy getSiteCrawlingStrategy() {
		return this.siteStrategy;
	}
}
