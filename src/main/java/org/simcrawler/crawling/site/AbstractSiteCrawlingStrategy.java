package org.simcrawler.crawling.site;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.simcrawler.crawling.CrawlSite;
import org.simcrawler.crawling.CrawlSiteImpl;
import org.simcrawler.crawling.page.PageStrategy;
import org.simcrawler.io.FileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Absctract class for crawling strategies.
 * @author jnphilipp
 * @version 0.0.1
 * @scince 2014-11-17
 */
public abstract class AbstractSiteCrawlingStrategy implements SiteStrategy {
	private static final Logger logger = LoggerFactory.getLogger(AbstractSiteCrawlingStrategy.class);
	protected int k;
	protected Map<String, Integer> quality;
	protected Map<String, String[]> graph;
	protected ScheduledThreadPoolExecutor executor;
	protected CrawlSite crawlSite;
	protected PageStrategy pageStrategy;

	public AbstractSiteCrawlingStrategy() {
		this.executor = new ScheduledThreadPoolExecutor(2);
		this.crawlSite = new CrawlSiteImpl(this);
	}

	public AbstractSiteCrawlingStrategy(int threadPoolSize) {
		this.executor = new ScheduledThreadPoolExecutor(threadPoolSize);
		this.crawlSite = new CrawlSiteImpl(this);
	}

	protected Map<String, Queue<String>> fillSites(Collection<String> urls) {
		Map<String, Queue<String>> sites = new LinkedHashMap<>();
		for ( String url : urls ) {
			String site = this.getSite(url);
			if ( !sites.containsKey(site) )
				sites.put(site, new LinkedList<String>());
			sites.get(site).add(url);
		}

		return sites;
	}

	@Override
	public CrawlSite getCrawlSite() {
		return this.crawlSite;
	}

	@Override
	public Map<String, Integer> getQualityMap() {
		return this.quality;
	}

	@Override
	public String getSite(String url) {
		Matcher m = Pattern.compile("(http://.+?\\.[^/]+)/").matcher(url);
		return m.find() ? m.group(1) : "";
	}

	protected Set<String> getURLsToAdd(Set<String> seen, Set<String> newURLs) {
		Set<String> toAdd = new LinkedHashSet<>();
		for ( String link : newURLs )
			if ( !seen.contains(link) )
				toAdd.add(link);
		seen.addAll(newURLs);
		return toAdd;
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
	public void setPageStrategy(PageStrategy pageStrategy) {
		this.pageStrategy = pageStrategy;
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
	 * Sums the return values of the futures.
	 * @param crawled crawled sites
	 * @param queue queue
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

	/**
	 * Writes to the step quality file.
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
}