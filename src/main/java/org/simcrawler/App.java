package org.simcrawler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.simcrawler.crawling.CrawlingStrategy;
import org.simcrawler.crawling.bfs.BFSStrategy;
import org.simcrawler.crawling.page.bl.BacklinkStrategy;
import org.simcrawler.crawling.page.opic.OPICStrategy;
import org.simcrawler.crawling.page.optimal.OptimalStrategy;
import org.simcrawler.crawling.site.SiteCrawlingStrategy;
import org.simcrawler.crawling.site.mpp.MPPStrategy;
import org.simcrawler.crawling.site.rrs.RRStrategy;
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

	/**
	 * Loads the quality mapping and web graph from the given files.
	 *
	 * @param qualityMappingFile quality mapping file
	 * @param webGraphFile web graph file
	 * @param mapdb map DB
	 * @param qualityMap quality map
	 * @param webGraph web graph
	 * @throws IOException
	 */
	private static void loadFiles(String qualityMappingFile, String webGraphFile, final DB mapdb, final Map<String, Integer> qualityMap, final Map<String, String[]> webGraph) throws IOException {
		System.out.println("Loading quality mapping file ...");
		FileReader.readCSV(qualityMappingFile, " ", new ReadCSVLineWithLineNumber() {
			@Override
			public void close() {
			}

			@Override
			public void processLine(String[] columns, long line) {
				qualityMap.put(columns[0], Integer.parseInt(columns[1]));
				if ( line % 1000000 == 0 )
					mapdb.commit();
			}
		});
		mapdb.commit();

		System.out.println("Loading web graph file ...");
		FileReader.readCSV(webGraphFile, "\t", new ReadCSVLineWithLineNumber() {
			private String key = "";
			private List<String> links = new ArrayList<>();

			@Override
			public void close() {
				webGraph.put(this.key, this.links.toArray(new String[this.links.size()]));
				mapdb.commit();
			}

			@Override
			public void processLine(String[] columns, long line) {
				if ( this.key.isEmpty() ) {
					this.key = columns[0];
					this.links.add(columns[1]);
					return;
				}

				if ( this.key.equals(columns[0]) )
					this.links.add(columns[1]);
				else {
					webGraph.put(this.key, this.links.toArray(new String[this.links.size()]));
					this.key = columns[0];
					this.links.clear();
					this.links.add(columns[1]);
				}

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

		int k = 0, maxSteps = -1, batchSize = 1;
		List<String> seedURLs = null;
		String webGraphFile = null;
		String qualityMappingFile = null;
		String stepQualityFile = null;

		boolean roundRobin = false;
		boolean maxPagePriority = false;
		boolean backlink = false;
		boolean opic = false;
		boolean optimal = false;

		if ( args.length != 0 ) {
			List<String> l = Arrays.asList(args);
			Iterator<String> it = l.iterator();
			while ( it.hasNext() )
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
						System.out.println("Loading seed file...");
						seedURLs = FileReader.readLines(it.next());
						break;
					case "-sq":
					case "--step_quality":
						stepQualityFile = it.next();
						break;
					case "-ms":
					case "--max_steps":
						maxSteps = Integer.parseInt(it.next());
						break;
					case "-rr":
					case "--raound_robin":
						roundRobin = true;
						maxPagePriority = false;
						break;
					case "-mpp":
					case "--max_page_priority":
						maxPagePriority = true;
						roundRobin = false;
						break;
					case "-bl":
					case "--backlink":
						backlink = true;
						opic = false;
						break;
					case "-opic":
						opic = true;
						backlink = false;
						break;
					case "-o":
					case "-optimal":
						optimal = true;
						backlink = false;
						opic = false;
						break;
					case "-b":
					case "--batch_size":
						batchSize = Integer.parseInt(it.next());
						break;
				}
		}
		else
			printUsage();

		if ( (qualityMappingFile == null || webGraphFile == null) && !dbFile.exists() || seedURLs == null )
			printUsage();

		System.out.println("Init DB ...");
		DB mapdb = DBMaker.newFileDB(dbFile).mmapFileEnable().closeOnJvmShutdown().cacheSize(200000000).make();
		Map<String, Integer> qualityMap = mapdb.getHashMap("qualityMapping");
		Map<String, String[]> webGraph = mapdb.getHashMap("webGraph");

		if ( qualityMappingFile != null || webGraphFile != null )
			loadFiles(qualityMappingFile, webGraphFile, mapdb, qualityMap, webGraph);

		System.out.println("Start crawling ...");

		CrawlingStrategy crawlingStrategy;
		if ( roundRobin )
			crawlingStrategy = new RRStrategy(Runtime.getRuntime().availableProcessors());
		else if ( maxPagePriority )
			crawlingStrategy = new MPPStrategy(Runtime.getRuntime().availableProcessors());
		else
			crawlingStrategy = new BFSStrategy(Runtime.getRuntime().availableProcessors());

		crawlingStrategy.setK(k);
		crawlingStrategy.setQualityMap(qualityMap);
		crawlingStrategy.setWebGraph(webGraph);

		if ( crawlingStrategy instanceof SiteCrawlingStrategy )
			if ( backlink )
				((SiteCrawlingStrategy) crawlingStrategy).setPageCrawlingStrategy(new BacklinkStrategy((SiteCrawlingStrategy) crawlingStrategy, batchSize));
			else if ( opic )
				((SiteCrawlingStrategy) crawlingStrategy).setPageCrawlingStrategy(new OPICStrategy((SiteCrawlingStrategy) crawlingStrategy, batchSize));
			else if ( optimal )
				((SiteCrawlingStrategy) crawlingStrategy).setPageCrawlingStrategy(new OptimalStrategy((SiteCrawlingStrategy) crawlingStrategy, batchSize));
			else
				crawlingStrategy = null;

		if ( crawlingStrategy != null )
			crawlingStrategy.start(seedURLs, stepQualityFile, maxSteps);
		else
			System.out.println("Invalid configuration, aborting.");
	}

	/**
	 * Prints usage.
	 */
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
						+ "\n\t-rr"
						+ "\n\t-raound_robin: round robin page crawling strategy"
						+ "\n\t-mpp"
						+ "\n\t-max_page_priority: max page priority page crawling strategy"
						+ "\n\t-bl"
						+ "\n\t-backlink: backlink site crawling strategy"
						+ "\n\t-opic: opic site crawling strategy"
						+ "\n\t-o"
						+ "\n\t-optimal: optimal site crawling strategy"
						+ "\n\t-b"
						+ "\n\t--batch_size: batch size"
						+ "\n\nIf no web graph file and/or quality mapping file is given a mapdb file is expected in ./data.");
		System.exit(0);
	}
}
