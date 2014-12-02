package org.simcrawler.crawling.site.rrs;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.simcrawler.crawling.site.AbstractSiteCrawlingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Round Robin crawling strategy.
 * @author jnphilipp
 * @version 0.0.1
 * @scince 2014-12-02
 */
public class RRStrategy extends AbstractSiteCrawlingStrategy {
	private static final Logger logger = LoggerFactory.getLogger(RRStrategy.class);

	public RRStrategy() {
		super();
	}

	public RRStrategy(int threadPoolSize) {
		super(threadPoolSize);
	}

	private void addNewURLs(Map<String, Set<String>> sites, Set<String> seen, Set<String> newURLs) {
		Set<String> toAdd = new LinkedHashSet<>();
		for ( String link : newURLs )
			if ( !seen.contains(link) )
				toAdd.add(link);
		seen.addAll(newURLs);

		for ( String page : toAdd ) {
			String site = this.getSite(page);
			if ( !sites.containsKey(site) )
				sites.put(site, new LinkedHashSet<String>());
			sites.get(site).add(page);
		}
	}

	private Map<String, Set<String>> fillSites(Collection<String> urls) {
		Map<String, Set<String>> sites = new LinkedHashMap<>();
		for ( String url : urls ) {
			String site = this.getSite(url);
			if ( !sites.containsKey(site) )
				sites.put(site, new LinkedHashSet<String>());
			sites.get(site).add(url);
		}

		return sites;
	}

	@Override
	public void start(Collection<String> urls, String stepQualityFile) {
		this.start(urls, stepQualityFile, -1);
	}

	@Override
	public void start(Collection<String> urls, String stepQualityFile, int maxSteps) {
		Set<String> seen = new LinkedHashSet<>(urls);
		final Map<String, Set<String>> sites = this.fillSites(urls);
		final Queue<String> queue = new LinkedList<>(sites.keySet());
		int good = 0, crawled = 0, steps = 1;

		do {
			long time = System.currentTimeMillis();
			Set<Future<Integer>> futures = new LinkedHashSet<>();
			final Set<String> newURLs = Collections.synchronizedSet(new LinkedHashSet<String>());

			for ( int i = 0; i < this.k; i++ ) {
				crawled++;
				futures.add(this.executor.submit(new Callable<Integer>() {
					@Override
					public Integer call() throws Exception {
						String site = queue.poll();
						String page = pageStrategy.crawl(site, sites.get(site));
						queue.add(site);

						if ( page == null )
							return 0;

						newURLs.addAll(Arrays.asList(crawlSite.getLinks(page)));
						return crawlSite.evaluate(page);
					}
				}));
			}

			good += this.sum(futures);
			this.addNewURLs(sites, seen, newURLs);

			this.writeStepQuality(stepQualityFile, good, crawled);
			System.out.println(String.format("Step %s of %s.\tQueue: %s\tCrawled: %s\ttime: %s sec", steps, maxSteps, queue.size(), crawled, (System.currentTimeMillis() - time) / 1000.0f));
			steps++;
		} while ( !queue.isEmpty() && (steps <= maxSteps || maxSteps == -1) );
		this.executor.shutdownNow();
		this.pageStrategy.close();
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