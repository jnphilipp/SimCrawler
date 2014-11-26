package org.simcrawler.crawling;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.simcrawler.io.FileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Absctract class for crawling strategies.
 * @author jnphilipp
 * @version 0.0.1
 * @scince 2014-11-17
 */
public abstract class AbstractCrawlingStrategy implements CrawlingStrategy {
	private static final Logger logger = LoggerFactory.getLogger(AbstractCrawlingStrategy.class);
	protected int k;
	protected Map<String, Integer> quality;
	protected Map<String, String[]> graph;
	protected ScheduledThreadPoolExecutor executor;

	public AbstractCrawlingStrategy() {
		this.executor = new ScheduledThreadPoolExecutor(2);
	}

	public AbstractCrawlingStrategy(int threadPoolSize) {
		this.executor = new ScheduledThreadPoolExecutor(threadPoolSize);
	}

	/**
	 * Performs the per step actions, take k URLs, crawl and add new URLs.
	 * @param crawled crawled URLs
	 * @param queue queue for URLs
	 * @param stepQualityFile step quality file
	 * @return good URLs crawled in this step
	 */
	protected abstract int doStep(Set<String> crawled, Queue<String> queue, String stepQualityFile);

	@Override
	public Map<String, Integer> getQualityMap() {
		return this.quality;
	}

	@Override
	public Map<String, String[]> getWebGraph() {
		return this.graph;
	}

	@Override
	public void setK(int k) {
		this.k = k;
	}

	@Override
	public void setQualityMap(Map<String, Integer> quality) {
		this.quality = Collections.synchronizedMap(quality);
	}

	@Override
	public void setWebGraph(Map<String, String[]> graph) {
		this.graph = Collections.synchronizedMap(graph);
	}

	@Override
	public void start(Collection<String> urls, String stepQualityFile) {
		this.start(urls, stepQualityFile, -1);
	}

	@Override
	public void start(Collection<String> urls, String stepQualityFile, int maxSteps) {
		Set<String> crawled = new HashSet<>();
		Queue<String> queue = new PriorityQueue<>(urls);
		int good = 0, steps = 1;
		do {
			long time = System.currentTimeMillis();
			int q = queue.size();

			good += this.doStep(crawled, queue, stepQualityFile);
			this.writeStepQuality(stepQualityFile, good, crawled.size());
			System.out.println(String.format("Step %s of %s.\tQueue: %s\tCrawled: %s\tnew urls: %s\ttime: %s sec", steps, maxSteps, q, crawled.size(), Math.abs(queue.size() - q + Math.min(this.k, q)), (System.currentTimeMillis() - time) / 1000.0f));

			steps++;
		} while ( !queue.isEmpty() && (steps <= maxSteps || maxSteps == -1) );
		this.executor.shutdownNow();
	}

	/**
	 * Writes to the step quality file.
	 * @param stepQualityFile step quality file
	 * @param good number of good URLs
	 * @param crawled number of crawled URLs
	 */
	private void writeStepQuality(String stepQualityFile, int good, int crawled) {
		try {
			FileWriter.write(stepQualityFile, true, String.format("%s/%s=%s\n", good, crawled, (good / (float) crawled)));
		}
		catch ( IOException e ) {
			logger.error("Error while writing to step quality file.", e);
		}
	}
}