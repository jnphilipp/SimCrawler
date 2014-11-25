package org.simcrawler.crawling;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author paul
 * @since 2014-11-25
 */
public class CrawlSiteImpl implements CrawlSite {
	private CrawlingStrategy strategy;

	public CrawlSiteImpl(CrawlingStrategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * Checks the quality of the given URL.
	 * @param url URL
	 * @return quality value
	 */
	public int evaluate(String url) {
		Integer e = this.strategy.getQualityMap().get(url);
		return e == null ? 0 : e;
	}

	/**
	 * Returns all links in the graph for the given URL.
	 * @param url URL
	 * @return links
	 */
	public Set<String> getLinks(String url) {
		Set<String> links = this.strategy.getWebGraph().get(url);
		return links == null ? new HashSet<String>() : links;
	}
}