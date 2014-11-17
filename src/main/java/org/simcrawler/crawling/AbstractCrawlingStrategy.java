package org.simcrawler.crawling;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * @author jnphilipp
 * @version 0.0.1
 * @scince 2014-11-17
 */
public abstract class AbstractCrawlingStrategy implements CrawlingStrategy {
	protected int good = 0;
	protected int k;
	protected Map<String, Integer> quality;
	protected Map<String, Set<String>> graph;

	protected abstract void doStep(Set<String> crawled, Queue<String> queue, String stepQualityFile);

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

		int steps = maxSteps;
		while ( !queue.isEmpty() && steps != -1 && steps > 0 ) {
			long time = System.currentTimeMillis();
			int q = queue.size();

			System.out.println(String.format("Step %s of %s.\nQueue: %s\nCrawled: %s", maxSteps - steps + 1, maxSteps, queue.size(), crawled.size()));
			this.doStep(crawled, queue, stepQualityFile);
			System.out.println(String.format("new urls: %s\ntime: %s sec", Math.abs(queue.size() - q + this.k), (System.currentTimeMillis() - time) / 1000.0f));

			if ( steps != -1 )
				steps--;
		}
	}
}