/**
 *
 */
package org.simcrawler.crawling.page;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.simcrawler.crawling.page.bl.BacklinkStrategy;
import org.simcrawler.crawling.site.AbstractSiteCrawlingStrategy;
import org.simcrawler.crawling.site.SiteCrawlingStrategy;

import static org.junit.Assert.assertEquals;

/**
 * @author jnphilipp
 * @version 0.0.1
 * @scince Dec 2, 2014
 */
public class BacklinkStrategyTest {

	private static Map<String, Integer> quality;

	private static Map<String, String[]> graph;

	@BeforeClass
	public static void setUpClass() {
		quality = new LinkedHashMap<>();
		quality.put("http://a.de/5", 0);
		quality.put("http://a.de/7", 1);
		quality.put("http://a.de/2", 0);
		quality.put("http://b.de/9", 1);
		quality.put("http://b.de/52", 0);
		quality.put("http://b.de/47", 1);
		quality.put("http://c.de/32", 1);
		quality.put("http://c.de/6", 0);
		quality.put("http://c.de/1", 0);
		quality.put("http://c.de/5", 1);
		quality.put("http://d.de/97", 0);
		quality.put("http://d.de/7", 0);
		quality.put("http://d.de/3", 1);
		quality.put("http://d.de/6", 1);
		quality.put("http://e.de/6", 0);
		quality.put("http://e.de/3", 0);
		quality.put("http://f.de/9", 0);
		quality.put("http://f.de/4", 1);
		quality.put("http://f.de/3", 0);
		quality.put("http://g.de/8", 1);
		quality.put("http://g.de/6", 1);
		quality.put("http://g.de/7", 1);
		quality.put("http://g.de/2", 0);
		quality.put("http://h.de/3", 1);
		quality.put("http://h.de/6", 0);
		quality.put("http://h.de/5", 0);
		quality.put("http://h.de/3", 1);
		quality.put("http://i.de/5", 0);
		quality.put("http://j.de/7", 0);
		quality.put("http://k.de/3", 1);
		quality.put("http://l.de/6", 0);
		quality.put("http://m.de/9", 0);
		quality.put("http://n.de/1", 0);
		quality.put("http://o.de/9", 1);
		quality.put("http://p.de/5", 0);
		quality.put("http://q.de/7", 0);
		quality.put("http://r.de/9", 1);
		quality.put("http://s.de/9", 1);
		quality.put("http://t.de/1", 0);
		quality.put("http://u.de/56", 0);
		quality.put("http://v.de/6", 0);
		quality.put("http://w.de/89", 1);
		quality.put("http://x.de/9", 1);
		quality.put("http://y.de/8", 0);
		quality.put("http://z.de/56", 1);

		graph = new LinkedHashMap<>();
		graph.put("http://a.de/5", new String[] {"http://a.de/7", "http://a.de/2", "http://c.de/32", "http://d.de/7", "http://h.de/5"});
		graph.put("http://a.de/7", new String[] {"http://a.de/5", "http://d.de/97", "http://h.de/6"});
		graph.put("http://a.de/2", new String[] {"http://a.de/5", "http://q.de/7", "http://h.de/3"});
	}

	@Test
	public void test() {
		SiteCrawlingStrategy siteStrategy = new AbstractSiteCrawlingStrategy() {

			@Override
			public void start(Collection<String> urls, String stepQualityFile) {
			}

			@Override
			public void start(Collection<String> urls, String stepQualityFile, int maxSteps) {
			}
		};

		BacklinkStrategy bls = new BacklinkStrategy(siteStrategy, 100);

		siteStrategy.setQualityMap(quality);
		siteStrategy.setWebGraph(graph);
		siteStrategy.setPageCrawlingStrategy(bls);
		List<String> seeds = Arrays.asList(new String[] {"http://a.de/5"});
		bls.init(seeds);

		Queue<String> queue = new LinkedList<>(seeds);
		queue = bls.crawl("http://a.de", queue, 0);
		String page = queue.poll();
		assertEquals("http://a.de/5", page);
		queue = new LinkedList<>(Arrays.asList(graph.get(page.toString())));
		bls.update(queue);
		queue = bls.crawl("http://a.de", queue, 1);
		page = queue.poll();
		assertEquals("http://a.de/7", page);

		bls.close();
	}
}
