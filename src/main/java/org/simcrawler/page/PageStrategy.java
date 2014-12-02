package org.simcrawler.page;

import java.util.Queue;

public interface PageStrategy {
	
	public Queue<String> crawl(Queue<String> queue);
	
	public void close();
}
