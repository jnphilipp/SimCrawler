/**
 *
 */
package org.simcrawler.crawling.site;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author jnphilipp
 * @version 0.0.1
 * @scince Dec 2, 2014
 */
public class AbstractSiteCrawlingStrategyTest {
	private static AbstractSiteCrawlingStrategy strategy;

	@BeforeClass
	public static void beforeClass() {
		strategy = new AbstractSiteCrawlingStrategy() {
			@Override
			public void start(Collection<String> urls, String stepQualityFile) {}

			@Override
			public void start(Collection<String> urls, String stepQualityFile, int maxSteps) {}
		};
	}

	@Test
	public void testGetSite() {
		assertEquals("http://a.de", strategy.getSite("http://a.de/5"));
	}
}