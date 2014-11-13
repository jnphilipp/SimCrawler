package org.simcrawler;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentNavigableMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.simcrawler.crawling.CrawlingStrategy;
import org.simcrawler.crawling.bfs.BFSStrategy;
import org.simcrawler.io.FileReader;
import org.simcrawler.io.ReadCSVLine;
import org.simcrawler.util.Helpers;

/**
 *
 * @author jnphilipp, proewer
 * @version 0.0.1
 */
public class App {
	public static void main(String[] args) throws IOException {
		System.out.println("SimCrawler");

		int k = 0, maxSteps = -1;
		List<String> seedURLs = null;
		String webGraphFile = null;
		String qualityMappingFile = null;
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
						qualityMappingFile = it.next();
						break;
					case "-wg":
						webGraphFile = it.next();
						break;
					case "-sf":
						seedURLs = Arrays.asList(FileReader.readLines(it.next()));
						break;
					case "-sq":
						stepQualityFile = it.next();
						break;
					case "-ms":
					case "--max_steps":
						maxSteps = Integer.parseInt(it.next());
						break;
				}
			}
		}
		else {
			System.out.println("usage: simcrawler -k <k> -qm <quallity mapping> -wg <web graph> -sf <seed urls> -sq <step quality> -ms <max steps>"
					+ "\n\t-k  								 : urls per crawling step"
					+ "\n\t-qm\n"
					+ "\n\t--quallity_mapping  : quality mapping input file"
					+ "\n\t-wg 								 : web graph input file"
					+ "\n\t-sf 								 : seed url input file"
					+ "\n\t-sq 								 : step quality output file"
					+ "\n\t-ms\n"
					+ "\n\t--max_steps         : maximum number of steps (optinal)");
			System.exit(0);
		}

		DB mapdb = DBMaker.newFileDB(new File(Helpers.getUserDir() + "/data/mapdb")).closeOnJvmShutdown().make();

		System.out.println("Loading quality mapping file ...");
		final ConcurrentNavigableMap<String, Integer> qualityMap = mapdb.getTreeMap("qualityMapping");
		FileReader.readCSV(qualityMappingFile, " ", new ReadCSVLine() {
			@Override
			public void processLine(String[] columns) {
				qualityMap.put(columns[0], Integer.parseInt(columns[1]));
			}
		});
		mapdb.commit();
		System.out.println(qualityMap);

		System.out.println("Loading web graph file ...");
		final ConcurrentNavigableMap<String, Set<String>> webGraph = mapdb.getTreeMap("webGraph");
		FileReader.readCSV(webGraphFile, "\t", new ReadCSVLine() {
			@Override
			public void processLine(String[] columns) {
				if ( !webGraph.containsKey(columns[0]) )
					webGraph.put(columns[0], new LinkedHashSet<String>());
				webGraph.get(columns[0]).add(columns[1]);
			}
		});
		mapdb.commit();
		System.out.println(webGraph);

		CrawlingStrategy crawlingStrategy = new BFSStrategy();
		crawlingStrategy.setK(k);
		crawlingStrategy.setQuality(qualityMap);
		crawlingStrategy.setWebGraph(webGraph);
		crawlingStrategy.start(seedURLs, stepQualityFile, maxSteps);
	}
}