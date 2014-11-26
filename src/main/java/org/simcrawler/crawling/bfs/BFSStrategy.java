package org.simcrawler.crawling.bfs;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.simcrawler.crawling.AbstractCrawlingStrategy;
import org.simcrawler.crawling.CrawlSiteImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Breadth-first search crawling strategy.
 * @author jnphilipp
 * @version 0.0.1
 */
public class BFSStrategy extends AbstractCrawlingStrategy {
	private static final Logger logger = LoggerFactory.getLogger(BFSStrategy.class);

	public BFSStrategy() {
		super();
	}

	public BFSStrategy(int threadPoolSize) {
		super(threadPoolSize);
	}

	private void addNewURLs(Queue<String> queue, Set<String> crawled, Set<String> newURLs) {
		for ( String link : newURLs )
			if ( !crawled.contains(link) && !queue.contains(link) )
				queue.add(link);
	}

	@Override
	protected int doStep(Set<String> crawled, Queue<String> queue, String stepQualityFile) {
		Set<Future<Integer>> futures = new HashSet<>();
		final Set<String> newURLs = Collections.synchronizedSet(new HashSet<String>());
		for ( int i = 0; i < this.k; i++ ) {
			if ( queue.peek() == null ) {
				logger.info("Empty queue while retrieving, ending step.");
				break;
			}

			crawled.add(queue.peek());
			final CrawlSiteImpl crawler = new CrawlSiteImpl(this);
			final String url = queue.poll();
			futures.add(this.executor.submit(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					newURLs.addAll(Arrays.asList(crawler.getLinks(url)));
					return crawler.evaluate(url);
				}
			}));
		}

		int good = this.sum(futures);
		this.addNewURLs(queue, crawled, newURLs);
		return good;
	}

	/**
	 * Sums the return values of the futures.
	 * @param crawled crawled sites
	 * @param queue queue
	 * @param futures futures
	 * @return sum of good sites
	 */
	private int sum(Set<Future<Integer>> futures) {
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