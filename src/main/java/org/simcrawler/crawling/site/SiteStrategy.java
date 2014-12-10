/**
 *
 */
package org.simcrawler.crawling.site;

import org.simcrawler.crawling.CrawlSite;
import org.simcrawler.crawling.CrawlingStrategy;
import org.simcrawler.crawling.page.PageStrategy;

/**
 * @author jnphilipp
 * @version 0.0.1
 * @scince 2014-12-02
 */
public interface SiteStrategy extends CrawlingStrategy {
	public CrawlSite getCrawlSite();

	public String getSite(String url);

	public void setPageStrategy(PageStrategy pageStrategy);
}