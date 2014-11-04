package org.simcrawler.crawling.bfs;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.simcrawler.Logger;
import org.simcrawler.crawling.CrawlingStrategy;
import org.simcrawler.io.FileWriter;

/**
 *
 * @author jnphilipp
 * @version 0.0.1
 */
public class BFSStrategy implements CrawlingStrategy {
	private int good = 0;
	private int k;
	private Map<String, Integer> quality;
	private Map<String, Set<String>> graph;

	@Override
	public void setK(int k) {
		this.k = k;
	}

	@Override
	public Map<String, Set<String>> getWebGraph() {
		return this.graph;
	}

	@Override
	public void setWebGraph(Map<String, Set<String>> graph) {
		this.graph = graph;
	}

	@Override
	public Map<String, Integer> getQuality() {
		return this.quality;
	}

	@Override
	public void setQuality(Map<String, Integer> quality) {
		this.quality = quality;
	}

	@Override
	public void start(Collection<String> urls, String stepQualityFile) {
		Set<String> crawled = new LinkedHashSet<>();
		Queue<String> queue = new PriorityQueue<>(urls);
		this.good = 0;

		while ( !queue.isEmpty() ) {
			this.doStep(crawled, queue, stepQualityFile);
		}
	}

	private void doStep(Set<String> crawled, Queue<String> queue, String stepQualityFile) {
		for ( int i = 0; i < this.k; i++ ) {
			if ( queue.peek() == null ) {
				Logger.error(BFSStrategy.class, "Empty queue aborting.");
				break;
			}

			crawled.add(queue.peek());
			Logger.info(BFSStrategy.class, "good: " + this.good);
			queue.addAll(CollectionUtils.subtract(CollectionUtils.subtract(this.crawl(queue.poll()), crawled), queue));
		}

		try {
			FileWriter.write(stepQualityFile, true, String.format("%s/%s=%s\n", this.good, crawled.size(), (this.good/(float)crawled.size())));
		}
		catch ( IOException e ) {
			Logger.error(BFSStrategy.class, "Error while writing to step quality file.", e.toString());
		}
	}

	private Set<String> crawl(String url) {
		Logger.info(BFSStrategy.class, url, "good: " + good);
		this.good += this.quality.get(url);
		return this.graph.get(url);
	}
}