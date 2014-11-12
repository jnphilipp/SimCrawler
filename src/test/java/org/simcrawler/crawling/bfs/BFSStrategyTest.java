package org.simcrawler.crawling.bfs;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.simcrawler.io.FileReader;
import org.simcrawler.util.Helpers;

/**
 *
 * @author jnphilipp
 * @version 0.0.1
 */
public class BFSStrategyTest {
	@BeforeClass
	public static void setUpClass() {
		quality = new LinkedHashMap<>();
		quality.put("a", 0);
		quality.put("b", 1);
		quality.put("c", 1);
		// quality.put("d", 0);

		graph = new LinkedHashMap<>();
		graph.put("a", new LinkedHashSet<>(Arrays.asList(new String[] { "c", "d" })));
		graph.put("b", new LinkedHashSet<>(Arrays.asList(new String[] { "a" })));
		graph.put("c", new LinkedHashSet<>(Arrays.asList(new String[] { "b", "d" })));
		graph.put("d", new LinkedHashSet<>(Arrays.asList(new String[] { "b" })));
	}

	private static Map<String, Integer> quality;

	private static Map<String, Set<String>> graph;

	/**
	 * Test of start method, of class BFSStrategy.
	 * 
	 * @throws java.io.IOException
	 */
	@Test
	public void testStart() throws IOException {
		Set<String> seed = new LinkedHashSet<>(Arrays.asList(new String[] { "a", "c" }));

		BFSStrategy bfs = new BFSStrategy();
		bfs.setK(1);
		bfs.setQuality(quality);
		bfs.setWebGraph(graph);
		bfs.start(seed, Helpers.getUserDir() + "/target/test.out");

		System.out.println(Helpers.join(FileReader.readLines(Helpers.getUserDir() + "/target/test.out"), "\n"));
		assertTrue(new File(Helpers.getUserDir() + "/target/test.out").exists());
	}
}