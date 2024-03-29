package org.simcrawler.crawling;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.simcrawler.io.FileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Absctract class for crawling strategies.
 *
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

	/**
	 * Writes to the step quality file.
	 *
	 * @param stepQualityFile step quality file
	 * @param good number of good URLs
	 * @param crawled number of crawled URLs
	 */
	protected void writeStepQuality(String stepQualityFile, int good, int crawled) {
		try {
			FileWriter.write(stepQualityFile, true, String.format("%s/%s=%s\n", good, crawled, (good / (float) crawled)));
		}
		catch ( IOException e ) {
			logger.error("Error while writing to step quality file.", e);
		}
	}

	protected Set<String> getURLsToAdd(Set<String> seen, Set<String> newURLs) {
		Set<String> toAdd = new LinkedHashSet<>();
		for ( String link : newURLs )
			if ( !seen.contains(link) )
				toAdd.add(link);
		seen.addAll(newURLs);
		return toAdd;
	}

	/**
	 * Sums the return values of the futures.
	 *
	 * @param futures futures
	 * @return sum of good sites
	 */
	protected int sum(Set<Future<Integer>> futures) {
		int result = 0;
		for ( Future<Integer> future : futures )
			try {
				result += future.get();
			}
			catch ( InterruptedException | ExecutionException e ) {
				logger.error("Error while summing futures.", e);
			}
		return result;
	}
}
