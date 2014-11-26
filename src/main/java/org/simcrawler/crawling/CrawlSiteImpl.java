package org.simcrawler.crawling;


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
	@Override
	public int evaluate(String url) {
		Integer e = this.strategy.getQualityMap().get(url);
		return e == null ? 0 : e;
	}

	/**
	 * Returns all links in the graph for the given URL.
	 * @param url URL
	 * @return links
	 */
	@Override
	public String[] getLinks(String url) {
		String[] links = this.strategy.getWebGraph().get(url);
		return links == null ? new String[0] : links;
	}
}