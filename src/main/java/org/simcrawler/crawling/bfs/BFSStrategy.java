package org.simcrawler.crawling.bfs;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.simcrawler.crawling.AbstractCrawlingStrategy;
import org.simcrawler.io.FileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jnphilipp
 * @version 0.0.1
 */
public class BFSStrategy extends AbstractCrawlingStrategy {
	private static final Logger logger = LoggerFactory.getLogger(BFSStrategy.class);

	private Set<String> crawl(String url) {
		this.good += this.quality.containsKey(url) ? this.quality.get(url) : 0;
		return this.graph.containsKey(url) ? this.graph.get(url) : new LinkedHashSet<String>();
	}

	@Override
	protected void doStep(Set<String> crawled, Queue<String> queue, String stepQualityFile) {
		for ( int i = 0; i < this.k; i++ ) {
			if ( queue.peek() == null ) {
				logger.info("Empty queue while retrieving, aborting.");
				break;
			}

			crawled.add(queue.peek());
			logger.debug("good: " + this.good);
			queue.addAll(CollectionUtils.subtract(CollectionUtils.subtract(this.crawl(queue.poll()), crawled), queue));
		}

		try {
			FileWriter.write(stepQualityFile, true, String.format("%s/%s=%s\n", this.good, crawled.size(), (this.good / (float) crawled.size())));
		}
		catch ( IOException e ) {
			logger.error("Error while writing to step quality file.", e);
		}
	}
}