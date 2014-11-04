package org.simcrawler;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
		int k;
		Map qualityMap, webGraph;
		File seedFile, stepQuality;
		if ( args.length != 0 ) {
			List<String> l = Arrays.asList(args);
			Iterator<String> it = l.iterator();
			while ( it.hasNext() ) {
				switch ( it.next() ) {
					case "--k":k = Integer.parseInt(it.next());
					case "--qm":qualityMap=QualityMappingReader.read(it.next());break;
					case "--wg":webGraph=WebGraphReader.read(it.next());break;
					case "--sf":seedFile = new File(it.next());break;
					case "--sq":stepQuality = new File(it.next());break;
				}
			}
		}
		else{
			Logger.info("input parameters:"
					+ "\n\t--k : urls per crawling step"
					+ "\n\t--qm : quality mapping input file"
					+ "\n\t--wg : web graph input file"
					+ "\n\t-- sf: seed input file"
					+ "\n\t--sq : step quality output file");
		}
	}
}