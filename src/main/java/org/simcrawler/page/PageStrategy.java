package org.simcrawler.page;

import java.util.Set;

/**
 * 
 * @author proewer
 * @since 2014-12-02
 */
public interface PageStrategy {
	
	public String crawl(String site, Set<String> pages);
	
	public void close();
}