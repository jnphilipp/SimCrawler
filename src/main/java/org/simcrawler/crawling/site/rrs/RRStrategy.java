package org.simcrawler.crawling.site.rrs;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.simcrawler.crawling.site.AbstractSiteCrawlingStrategy;

/**
 * Round Robin crawling strategy.
 * @author jnphilipp
 * @version 0.0.1
 * @scince 2014-12-02
 */
public class RRStrategy extends AbstractSiteCrawlingStrategy {
	public RRStrategy() {
		super();
	}

	public RRStrategy(int threadPoolSize) {
		super(threadPoolSize);
	}

	@Override
	public void start(Collection<String> urls, String stepQualityFile) {
		this.start(urls, stepQualityFile, -1);
	}

	@Override
	public void start(Collection<String> urls, String stepQualityFile, int maxSteps) {
		final Set<String> seen = Collections.synchronizedSet(new LinkedHashSet<>(urls));
		final Map<String, Queue<String>> sites = this.fillSites(urls);
		final Queue<String> queue = new LinkedList<>(sites.keySet());
		int good = 0, crawled = 0, queueSize = urls.size(), steps = 1;

		do {
			long time = System.currentTimeMillis();
			Set<Future<Integer>> futures = new LinkedHashSet<>();
			final Set<String> newURLs = Collections.synchronizedSet(new LinkedHashSet<String>());

			for ( int i = 0; i < this.k; i++ ) {
				crawled++;
				queueSize--;
				final String site = queue.poll();
				if ( sites.get(site).size() != 0 )
					futures.add(this.executor.submit(new Callable<Integer>() {
						@Override
						public Integer call() throws Exception {
							Queue<String> q = pageStrategy.crawl(site, sites.get(site));

							if ( q == null )
								return 0;

							String page = q.poll();
							sites.put(site, q);

							newURLs.addAll(Arrays.asList(crawlSite.getLinks(page)));
							return crawlSite.evaluate(page);
						}
					}));
				queue.add(site);
			}

			good += this.sum(futures);
			for ( String page : this.getURLsToAdd(seen, newURLs) ) {
				queueSize++;

				String site = this.getSite(page);
				Queue<String> q = sites.get(site);
				if ( q == null )
					q = new LinkedList<>();
				q.add(page);
				sites.put(site, q);
			}

			this.writeStepQuality(stepQualityFile, good, crawled);
			System.out.println(String.format("Step %s of %s.\tQueue: %s\tCrawled: %s\ttime: %s sec", steps, maxSteps, queueSize, crawled, (System.currentTimeMillis() - time) / 1000.0f));
			steps++;
		} while ( !queue.isEmpty() && (steps <= maxSteps || maxSteps == -1) );
		this.executor.shutdownNow();
	}
}