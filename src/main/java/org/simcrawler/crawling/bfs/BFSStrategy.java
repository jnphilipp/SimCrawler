package org.simcrawler.crawling.bfs;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.simcrawler.crawling.AbstractCrawlingStrategy;
import org.simcrawler.crawling.CrawlSiteImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Breadth-first search crawling strategy.
 *
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

	@Override
	public void start(Collection<String> urls, String stepQualityFile) {
		this.start(urls, stepQualityFile, -1);
	}

	@Override
	public void start(Collection<String> seeds, String stepQualityFile, int maxSteps) {
		Set<String> seen = new LinkedHashSet<>(seeds);
		Queue<String> queue = new LinkedList<>(seeds);
		int good = 0, crawled = 0, steps = 1;
		do {
			long time = System.currentTimeMillis();
			int q = queue.size();

			Set<Future<Integer>> futures = new LinkedHashSet<>();
			final CrawlSiteImpl crawler = new CrawlSiteImpl(this);
			final Set<String> newURLs = Collections.synchronizedSet(new LinkedHashSet<String>());
			for ( int i = 0; i < this.k; i++ ) {
				if ( queue.peek() == null ) {
					logger.info("Empty queue while retrieving, ending step.");
					break;
				}

				final String url = queue.poll();
				crawled++;
				futures.add(this.executor.submit(new Callable<Integer>() {
					@Override
					public Integer call() throws Exception {
						newURLs.addAll(Arrays.asList(crawler.getLinks(url)));
						return crawler.evaluate(url);
					}
				}));
			}

			good += this.sum(futures);
			queue.addAll(this.getURLsToAdd(seen, newURLs));

			this.writeStepQuality(stepQualityFile, good, crawled);
			System.out.println(String.format("Step %s of %s.\tQueue: %s\tCrawled: %s\tnew URLs: %s\ttime: %s sec", steps, maxSteps, queue.size(), crawled, Math.abs(queue.size() - q + Math.min(this.k, q)), (System.currentTimeMillis() - time) / 1000.0f));

			steps++;
		}
		while ( !queue.isEmpty() && (steps <= maxSteps || maxSteps == -1) );
		this.executor.shutdownNow();
	}
}
