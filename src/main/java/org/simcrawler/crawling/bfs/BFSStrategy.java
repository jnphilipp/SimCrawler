package org.simcrawler.crawling.bfs;

import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.simcrawler.crawling.AbstractCrawlingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Breadth-first search crawling strategy.
 * @author jnphilipp
 * @version 0.0.1
 */
public class BFSStrategy extends AbstractCrawlingStrategy {
	private static final Logger logger = LoggerFactory.getLogger(BFSStrategy.class);

	@Override
	protected int doStep(Set<String> crawled, Queue<String> queue, String stepQualityFile) {
		int good = 0;

		for ( int i = 0; i < this.k; i++ ) {
			if ( queue.peek() == null ) {
				logger.info("Empty queue while retrieving, aborting.");
				break;
			}

			crawled.add(queue.peek());
			good += this.evaluate(queue.peek());
			queue.addAll(CollectionUtils.subtract(CollectionUtils.subtract(this.getLinks(queue.poll()), crawled), queue));
		}

		return good;
	}

	/**
	 * Checks the quality of the given URL.
	 * @param url URL
	 * @return quality value
	 */
	private int evaluate(String url) {
		return this.quality.containsKey(url) ? this.quality.get(url) : 0;
	}

	/**
	 * Returns all links in the graph for the given URL.
	 * @param url URL
	 * @return links
	 */
	private Set<String> getLinks(String url) {
		return this.graph.containsKey(url) ? this.graph.get(url) : new LinkedHashSet<String>();
	}
}