package org.simcrawler.crawling.mpp;

import java.util.Queue;
import java.util.Set;

import org.simcrawler.crawling.AbstractCrawlingStrategy;
import org.simcrawler.crawling.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * max page priority strategy
 * @author proewer
 * @since 14-11-28
 */
public class MPPStrategy extends AbstractCrawlingStrategy {

	private static final Logger logger = LoggerFactory.getLogger(MPPStrategy.class);

	public MPPStrategy() {
		super();
	}

	@Override
	protected int doStep(Set<String> crawled, Queue<URL> queue, String stepQualityFile) {
		// TODO Auto-generated method stub
		return 0;
	}
}
