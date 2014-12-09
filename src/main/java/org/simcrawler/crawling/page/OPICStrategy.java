package org.simcrawler.crawling.page;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.simcrawler.crawling.site.SiteStrategy;

/**
 * 
 * @author proewer
 * @since 2014-12-02
 */
public class OPICStrategy implements PageStrategy {

	private Map<String, Double> opicHistory;
	//private int sumScoreHistory;
	private SiteStrategy siteStrategy;
	private int batchSize;
	private boolean sortFlag;
	
	OPICStrategy (SiteStrategy siteStrategy, int batchSize){
		this.siteStrategy = siteStrategy;
		this.batchSize = batchSize;
		//this.sumScoreHistory = 0;
		this.sortFlag = false;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Queue<String> crawl(String site, Queue<String> pages, Set<String> seen) {
		//update history
		for(String page : pages){
			Double score = this.opicHistory.get(page);
			this.opicHistory.put(page, (score==null?0:score)+1.0/pages.size());
		}
		//updateScore();
		List<String> sorted = new LinkedList<>(pages);
		//sort query	
		if(this.sortFlag) {
			Collections.sort(sorted, new Comparator(){
				@Override
				public int compare(Object arg0, Object arg1) {
					return opicHistory.get(arg0).compareTo(opicHistory.get(arg1));
				}});
		}
		//crawl
		Queue<String> queue = new LinkedList<>(sorted);
		String crawledURL = queue.peek();
		String[] newURLs = this.siteStrategy.getCrawlSite().getLinks(crawledURL);
		//update crawled pages counter
		this.batchSize-=newURLs.length;
		if(this.batchSize<=0) {
			this.batchSize*=-1;
			this.sortFlag = true;
		}
		//return crawled URL
		return queue;
	}
	
	/*private void updateScore() {
		for (Map.Entry<String, Double> entry : this.opicHistory.entrySet())
			this.sumScoreHistory+=entry.getValue();
	}*/

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}
}