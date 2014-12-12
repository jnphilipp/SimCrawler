package org.simcrawler.crawling.site.mpp;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.simcrawler.crawling.site.AbstractSiteCrawlingStrategy;

/**
 * Max page priority crawling strategy.
 *
 * @author jnphilipp
 * @version 0.0.1
 * @scince 2014-12-02
 */
public class MPPStrategy extends AbstractSiteCrawlingStrategy {
	public MPPStrategy() {
		super();
	}

	public MPPStrategy(int threadPoolSize) {
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

		this.pageStrategy.init();
		do {
			long time = System.currentTimeMillis();
			Set<Future<Integer>> futures = new LinkedHashSet<>();
			final Set<String> newURLs = Collections.synchronizedSet(new LinkedHashSet<String>());

			for ( int i = 0; i < this.k; i++ ) {
				final String site = queue.poll();
				if ( sites.get(site).size() != 0 ) {
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

					crawled++;
					queueSize--;
				}
				else
					i--;
				queue.add(site);
			}

			good += this.sum(futures);
			int addedURLs = queueSize;
			for ( String page : this.getURLsToAdd(seen, newURLs) ) {
				queueSize++;

				String site = this.getSite(page);
				Queue<String> q = sites.get(site);
				if ( q == null )
					q = new LinkedList<>();
				q.add(page);
				sites.put(site, q);
			}
			addedURLs = queueSize - addedURLs;

			if ( steps % this.pageStrategy.getBatchSize() == 0 ) {
				List<String> sorted = new LinkedList<>(queue);
				Collections.sort(sorted, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return Double.compare(pageStrategy.getMaxPage(o2), pageStrategy.getMaxPage(o1));
					}
				});
				queue.clear();
				queue.addAll(sorted);
			}

			this.writeStepQuality(stepQualityFile, good, crawled);
			System.out.println(String.format("Step %s of %s.\tQueue: %s\tCrawled: %s\tnew URLs: %s\ttime: %s sec", steps, maxSteps, queueSize, crawled, addedURLs, (System.currentTimeMillis() - time) / 1000.0f));
			steps++;
		}
		while ( !queue.isEmpty() && (steps <= maxSteps || maxSteps == -1) );

		this.pageStrategy.close();
		this.executor.shutdownNow();
	}
}
