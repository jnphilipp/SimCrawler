package org.simcrawler.crawling.page;

import org.simcrawler.crawling.site.SiteStrategy;

/**
 *
 * @author jnphilipp
 * @version 0.0.1
 * @since 2014-12-12
 */
public abstract class AbstractPageCrawlingStrategy implements PageStrategy {
	protected int batchSize;
	protected SiteStrategy siteStrategy;

	public AbstractPageCrawlingStrategy(SiteStrategy siteStrategy, int batchSize) {
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
	public SiteStrategy getSiteStrategy() {
		return this.siteStrategy;
	}
}
