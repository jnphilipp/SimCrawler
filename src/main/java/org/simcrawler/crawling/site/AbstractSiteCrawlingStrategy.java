package org.simcrawler.crawling.site;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.simcrawler.crawling.AbstractCrawlingStrategy;
import org.simcrawler.crawling.CrawlSite;
import org.simcrawler.crawling.CrawlSiteImpl;
import org.simcrawler.crawling.page.PageStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for crawling strategies.
 *
 * @author jnphilipp
 * @version 0.0.1
 * @scince 2014-11-17
 */
public abstract class AbstractSiteCrawlingStrategy extends AbstractCrawlingStrategy implements SiteStrategy {
	private static final Logger logger = LoggerFactory.getLogger(AbstractSiteCrawlingStrategy.class);
	protected CrawlSite crawlSite;
	protected PageStrategy pageStrategy;

	public AbstractSiteCrawlingStrategy() {
		super();
		this.crawlSite = new CrawlSiteImpl(this);
	}

	public AbstractSiteCrawlingStrategy(int threadPoolSize) {
		super(threadPoolSize);
		this.crawlSite = new CrawlSiteImpl(this);
	}

	protected Map<String, Queue<String>> fillSites(Collection<String> urls) {
		Map<String, Queue<String>> sites = new LinkedHashMap<>();
		for ( String url : urls ) {
			String site = this.getSite(url);
			if ( !sites.containsKey(site) )
				sites.put(site, new LinkedList<String>());
			sites.get(site).add(url);
		}

		return sites;
	}

	@Override
	public CrawlSite getCrawlSite() {
		return this.crawlSite;
	}

	@Override
	public String getSite(String url) {
		Matcher m = Pattern.compile("(http://.+?\\.[^/]+)/").matcher(url);
		return m.find() ? m.group(1) : "";
	}

	@Override
	public void setPageStrategy(PageStrategy pageStrategy) {
		this.pageStrategy = pageStrategy;
	}
}
