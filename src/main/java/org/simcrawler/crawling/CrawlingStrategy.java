package org.simcrawler.crawling;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jnphilipp
 * @version 0.0.1
 */
public interface CrawlingStrategy {
	/**
	 * Sets the k.
	 * @param k k to set
	 */
	public void setK(int k);

	/**
	 * Returns web graph.
	 * @return web graph
	 */
	public Map<String, Set<String>> getWebGraph();

	/**
	 * Sets the web graph.
	 * @param graph web graph to set
	 */
	public void setWebGraph(Map<String, Set<String>> graph);

	/**
	 * Returns the quality mapping.
	 * @return quality mapping
	 */
	public Map<String, Integer> getQuality();

	/**
	 * Sets the quality mapping.
	 * @param quality quality mapping to set
	 */
	public void setQuality(Map<String, Integer> quality);

	/**
	 * Starts the crawling strategy.
	 * @param urls seed URLs
	 * @param stepQualityFile file for output step quality
	 */
	public void start(Collection<String> urls, String stepQualityFile);
}