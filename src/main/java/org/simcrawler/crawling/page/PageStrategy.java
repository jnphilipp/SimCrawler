package org.simcrawler.crawling.page;

import java.util.Queue;
import java.util.Set;

/**
 *
 * @author proewer, jnphilipp
 * @since 2014-12-02
 */
public interface PageStrategy {

	public void close();

	public Queue<String> crawl(String site, Queue<String> queue, Set<String> seen);
}