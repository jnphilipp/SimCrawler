package org.simcrawler.crawling;

import java.util.Set;

/**
 * 
 * @author paul
 * @since 2014-11-25
 */
public interface CrawlSite {
	/**
	 * Checks the quality of the given URL.
	 * @param url URL
	 * @return quality value
	 */
	public int evaluate(String url);

	/**
	 * Returns all links in the graph for the given URL.
	 * @param url URL
	 * @return links
	 */
	public Set<String> getLinks(String url);
}