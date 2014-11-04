package org.simcrawler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.simcrawler.crawling.CrawlingStrategy;
import org.simcrawler.crawling.bfs.BFSStrategy;
import org.simcrawler.io.FileReader;
import org.simcrawler.io.files.QualityMappingReader;
import org.simcrawler.io.files.WebGraphReader;

/**
 *
 * @author jnphilipp, proewer
 * @version 0.0.1
 */
public class App {

	public static void main(String[] args) throws IOException {
		Logger.getInstance();
		Logger.info("SimCrawler");
		int k = 0;
		Map<String, Integer> qualityMap = null;
		Map<String, Set<String>> webGraph = null;
		List<String> seedURLs = null;
		String stepQualityFile = null;
		if ( args.length != 0 ) {
			List<String> l = Arrays.asList(args);
			Iterator<String> it = l.iterator();
			while ( it.hasNext() ) {
				switch ( it.next() ) {
					case "-k":
						k = Integer.parseInt(it.next());
					break;
					case "-qm":
					case "--quality_mapping":
						qualityMap = QualityMappingReader.read(it.next());
						break;
					case "-wg":
						webGraph = WebGraphReader.read(it.next());
						break;
					case "-sf":
						seedURLs = Arrays.asList(FileReader.readLines(it.next()));
						break;
					case "-sq":
						stepQualityFile = it.next();
						break;
				}
			}
		}
		else{
			Logger.info("input parameters:"
					+ "\n\t-k : urls per crawling step"
					+ "\n\t-qm : quality mapping input file"
					+ "\n\t-wg : web graph input file"
					+ "\n\t-sf: seed input file"
					+ "\n\t-sq : step quality output file");
			System.exit(0);
		}

		CrawlingStrategy crawlingStrategy = new BFSStrategy();
		crawlingStrategy.setK(k);
		crawlingStrategy.setQuality(qualityMap);
		crawlingStrategy.setWebGraph(webGraph);
		crawlingStrategy.start(seedURLs, stepQualityFile);
	}
}