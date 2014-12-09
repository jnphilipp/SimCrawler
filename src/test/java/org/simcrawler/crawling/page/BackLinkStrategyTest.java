/**
 *
 */
package org.simcrawler.crawling.page;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.simcrawler.crawling.site.AbstractSiteCrawlingStrategy;
import org.simcrawler.crawling.site.SiteStrategy;

/**
 * @author jnphilipp
 * @version 0.0.1
 * @scince Dec 2, 2014
 */
public class BackLinkStrategyTest {
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
		graph.put("http://a.de/5", new String[] { "http://a.de/7", "http://a.de/2", "http://c.de/32", "http://d.de/7", "http://h.de/5" });
		graph.put("http://a.de/7", new String[] { "http://a.de/5", "http://d.de/97", "http://h.de/6" });
		graph.put("http://a.de/2", new String[] { "http://a.de/5", "http://q.de/7", "http://h.de/3" });
		/*graph.put("http://b", new String[] { "a", "j", "z" });
		graph.put("http://c", new String[] { "b", "d", "l", "m", "o" });
		graph.put("http://d", new String[] { "b", "d", "f", "q" });
		graph.put("http://e", new String[] { "t", "m", "c" });
		graph.put("http://f", new String[] { "y", "o" });
		graph.put("http://g", new String[] { "x", "l", "l", "d" });
		graph.put("http://h", new String[] { "w", "d" });
		graph.put("http://i", new String[] { "l", "e" });
		graph.put("http://j", new String[] { "f", "k" });
		graph.put("http://k", new String[] { "d", "l" });
		graph.put("http://l", new String[] { "s", "z" });
		graph.put("http://m", new String[] { "c", "i" });
		graph.put("http://n", new String[] { "p", "j" });
		graph.put("http://o", new String[] { "b", "a" });
		graph.put("http://p", new String[] { "c", "u" });
		graph.put("http://q", new String[] { "u", "d", "m" });
		graph.put("http://r", new String[] { "r", "c" });
		graph.put("http://s", new String[] { "h", "v" });
		graph.put("http://t", new String[] { "w", "v" });
		graph.put("http://u", new String[] { "q", "q" });
		graph.put("http://v", new String[] { "o", "i" });
		graph.put("http://w", new String[] { "k", "g", "e", "l" });
		graph.put("http://x", new String[] { "l", "l" });
		graph.put("http://y", new String[] { "s", "s" });
		graph.put("http://z", new String[] { "m", "d", "s" });*/
	}

	@Test
	public void test() {
		SiteStrategy siteStrategy = new AbstractSiteCrawlingStrategy() {

			@Override
			public void start(Collection<String> urls, String stepQualityFile) {}

			@Override
			public void start(Collection<String> urls, String stepQualityFile, int maxSteps) {}

		};

		BackLinkStrategy bls = new BackLinkStrategy(siteStrategy, 100);

		siteStrategy.setQualityMap(quality);
		siteStrategy.setWebGraph(graph);
		siteStrategy.setPageStrategy(bls);

		Set<String> seen = new LinkedHashSet<>();

		Queue<String> queue = new LinkedList<>(Arrays.asList(new String[] { "http://a.de/5" }));
		queue = bls.crawl("http://a.de", queue, seen);
		String page = queue.poll();
		assertEquals("http://a.de/5", page);
		seen.add(page);
		queue = new LinkedList<>(Arrays.asList(graph.get(page.toString())));
		queue = bls.crawl("http://a.de", queue, seen);
		page = queue.poll();
		seen.add(page);
		assertEquals("http://a.de/7", page);
	}
}