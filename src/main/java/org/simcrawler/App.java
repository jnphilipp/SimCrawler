package org.simcrawler;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.simcrawler.crawling.CrawlingStrategy;
import org.simcrawler.crawling.bfs.BFSStrategy;
import org.simcrawler.io.FileReader;
import org.simcrawler.io.ReadCSVLineWithLineNumber;
import org.simcrawler.util.Helpers;

/**
 *
 * @author jnphilipp, proewer
 * @version 0.0.1
 */
public class App {
	private static final File dbFile = new File(Helpers.getUserDir() + "/data/mapdb");

	private static void loadFiles(String qualityMappingFile, String webGraphFile, final DB mapdb, final Map<String, Integer> qualityMap, final Map<String, Set<String>> webGraph) throws IOException {
		System.out.println("Loading quality mapping file ...");
		FileReader.readCSV(qualityMappingFile, " ", new ReadCSVLineWithLineNumber() {
			@Override
			public void processLine(String[] columns, int line) {
				qualityMap.put(columns[0], Integer.parseInt(columns[1]));
				if ( line % 1000000 == 0 )
					mapdb.commit();
			}
		});
		mapdb.commit();

		System.out.println("Loading web graph file ...");
		FileReader.readCSV(webGraphFile, "\t", new ReadCSVLineWithLineNumber() {
			@Override
			public void processLine(String[] columns, int line) {
				if ( !webGraph.containsKey(columns[0]) )
					webGraph.put(columns[0], new LinkedHashSet<String>());
				webGraph.get(columns[0]).add(columns[1]);

				if ( line % 1000000 == 0 )
					mapdb.commit();
			}
		});
		mapdb.commit();

		System.out.println("Finished loading files.");
	}

	public static void main(String[] args) throws IOException {
		System.out.println("SimCrawler");

		if ( !new File(dbFile.getParent()).exists() )
			new File(dbFile.getParent()).mkdir();

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
					case "--web_graph":
						webGraphFile = it.next();
						break;
					case "-sf":
					case "--seed_file":
						seedURLs = Arrays.asList(FileReader.readLines(it.next()));
						break;
					case "-sq":
					case "--step_quality":
						stepQualityFile = it.next();
						break;
					case "-ms":
					case "--max_steps":
						maxSteps = Integer.parseInt(it.next());
						break;
				}
			}
		}
		else
			printUsage();

		if ( (qualityMappingFile == null || webGraphFile == null) && !dbFile.exists() || seedURLs == null )
			printUsage();

		DB mapdb = DBMaker.newFileDB(dbFile).mmapFileEnable().closeOnJvmShutdown().make();
		Map<String, Integer> qualityMap = mapdb.getHashMap("qualityMapping");
		Map<String, Set<String>> webGraph = mapdb.getHashMap("webGraph");

		if ( qualityMappingFile != null || webGraphFile != null ) {
			loadFiles(qualityMappingFile, webGraphFile, mapdb, qualityMap, webGraph);
		}

		System.out.println("Start crawling ...");
		CrawlingStrategy crawlingStrategy = new BFSStrategy();
		crawlingStrategy.setK(k);
		crawlingStrategy.setQuality(qualityMap);
		crawlingStrategy.setWebGraph(webGraph);
		crawlingStrategy.start(seedURLs, stepQualityFile, maxSteps);
	}

	private static void printUsage() {
		System.out.println("usage: simcrawler -k <k> -qm <quality mapping> -wg <web graph> -sf <seed urls> -sq <step quality> -ms <max steps>"
				+ "\n\t-k\t\t\t: urls per crawling step"
				+ "\n\t-qm"
				+ "\n\t--quality_mapping\t: quality mapping input file"
				+ "\n\t-wg"
				+ "\n\t--web_graph\t\t: web graph input file"
				+ "\n\t-sf"
				+ "\n\t--seed_file\t\t: seed url input file"
				+ "\n\t-sq"
				+ "\n\t--step_quality\t\t: step quality output file"
				+ "\n\t-ms"
				+ "\n\t--max_steps\t\t: maximum number of steps (optinal)"
				+ "\n\nIf no web graph file and/or quality mapping file is given a mapdb file is expected in ./data.");
		System.exit(0);
	}
}