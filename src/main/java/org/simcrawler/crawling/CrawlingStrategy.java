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
	 * Returns the quality mapping.
	 * @return quality mapping
	 */
	public Map<String, Integer> getQuality();

	/**
	 * Returns web graph.
	 * @return web graph
	 */
	public Map<String, Set<String>> getWebGraph();

	/**
	 * Sets the k.
	 * @param k k to set
	 */
	public void setK(int k);

	/**
	 * Sets the quality mapping.
	 * @param quality quality mapping to set
	 */
	public void setQuality(Map<String, Integer> quality);

	/**
	 * Sets the web graph.
	 * @param graph web graph to set
	 */
	public void setWebGraph(Map<String, Set<String>> graph);

	/**
	 * Starts the crawling strategy.
	 * @param urls seed URLs
	 * @param stepQualityFile file for output step quality
	 */
	public void start(Collection<String> urls, String stepQualityFile);

	/**
	 * Starts the crawling strategy.
	 * @param urls seed URLs
	 * @param stepQualityFile file for output step quality
	 * @param maxSteps maximum number of steps
	 */
	public void start(Collection<String> urls, String stepQualityFile, int maxSteps);
}