package org.simcrawler.crawling.bfs;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.simcrawler.crawling.CrawlingStrategy;
import org.simcrawler.io.FileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jnphilipp
 * @version 0.0.1
 */
public class BFSStrategy implements CrawlingStrategy {
	private static final Logger LOGGER = LoggerFactory.getLogger(BFSStrategy.class);
	private int good = 0;
	private int k;
	private Map<String, Integer> quality;
	private Map<String, Set<String>> graph;

	private Set<String> crawl(String url) {
		this.good += this.quality.containsKey(url) ? this.quality.get(url) : 0;
		return this.graph.containsKey(url) ? this.graph.get(url) : new LinkedHashSet<String>();
	}

	private void doStep(Set<String> crawled, Queue<String> queue, String stepQualityFile) {
		for ( int i = 0; i < this.k; i++ ) {
			if ( queue.peek() == null ) {
				LOGGER.info("Empty queue while retrieving, aborting.");
				break;
			}

			crawled.add(queue.peek());
			LOGGER.debug("good: " + this.good);
			queue.addAll(CollectionUtils.subtract(CollectionUtils.subtract(this.crawl(queue.poll()), crawled), queue));
		}

		try {
			FileWriter.write(stepQualityFile, true, String.format("%s/%s=%s\n", this.good, crawled.size(), (this.good / (float) crawled.size())));
		}
		catch ( IOException e ) {
			LOGGER.error("Error while writing to step quality file.", e);
		}
	}

	@Override
	public Map<String, Integer> getQuality() {
		return this.quality;
	}

	@Override
	public Map<String, Set<String>> getWebGraph() {
		return this.graph;
	}

	@Override
	public void setK(int k) {
		this.k = k;
	}

	@Override
	public void setQuality(Map<String, Integer> quality) {
		this.quality = quality;
	}

	@Override
	public void setWebGraph(Map<String, Set<String>> graph) {
		this.graph = graph;
	}

	@Override
	public void start(Collection<String> urls, String stepQualityFile) {
		this.start(urls, stepQualityFile, -1);
	}

	@Override
	public void start(Collection<String> urls, String stepQualityFile, int maxSteps) {
		Set<String> crawled = new LinkedHashSet<>();
		Queue<String> queue = new PriorityQueue<>(urls);
		this.good = 0;

		while ( !queue.isEmpty() && maxSteps != -1 && maxSteps > 0 ) {
			System.out.println("steps: " + maxSteps);
			this.doStep(crawled, queue, stepQualityFile);
			if ( maxSteps != -1 )
				maxSteps--;
		}
	}
}