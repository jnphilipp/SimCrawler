package org.simcrawler.crawling.site;

import org.simcrawler.crawling.CrawlSite;
import org.simcrawler.crawling.CrawlingStrategy;
import org.simcrawler.crawling.page.PageCrawlingStrategy;

/**
 * Site crawling strategy.
 *
 * @author jnphilipp
 * @version 0.0.1
 * @scince 2014-12-02
 */
public interface SiteCrawlingStrategy extends CrawlingStrategy {
	/**
	 * Returns the crawl site.
	 *
	 * @return crawl site
	 */
	public CrawlSite getCrawlSite();

	/**
	 * Gets the site URL for the given URL.
	 *
	 * @param url URL
	 * @return site URL
	 */
	public String getSite(String url);

	/**
	 * Sets the page crawling strategy.
	 *
	 * @param pageCrawlingStrategy page crawling strategy
	 */
	public void setPageCrawlingStrategy(PageCrawlingStrategy pageCrawlingStrategy);
}
