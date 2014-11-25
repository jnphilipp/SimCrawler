package org.simcrawler.crawling.bfs;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
		quality = new HashMap<>();
		quality.put("a", 0);
		quality.put("b", 1);
		quality.put("c", 1);
		quality.put("d", 0);
		quality.put("e", 0);
		quality.put("f", 0);
		quality.put("g", 0);
		quality.put("h", 0);
		quality.put("i", 0);
		quality.put("j", 0);
		quality.put("k", 1);
		quality.put("l", 0);
		quality.put("m", 0);
		quality.put("n", 0);
		quality.put("o", 1);
		quality.put("p", 0);
		quality.put("q", 0);
		quality.put("r", 1);
		quality.put("s", 1);
		quality.put("t", 0);
		quality.put("u", 0);
		quality.put("v", 0);
		quality.put("w", 1);
		quality.put("x", 1);
		quality.put("y", 0);
		quality.put("z", 1);

		graph = new HashMap<>();
		graph.put("a", new HashSet<>(Arrays.asList(new String[] { "c", "d", "h" })));
		graph.put("b", new HashSet<>(Arrays.asList(new String[] { "a", "j", "z" })));
		graph.put("c", new HashSet<>(Arrays.asList(new String[] { "b", "d", "l", "m", "o" })));
		graph.put("d", new HashSet<>(Arrays.asList(new String[] { "b", "d", "f", "q" })));
		graph.put("e", new HashSet<>(Arrays.asList(new String[] { "t", "m", "c" })));
		graph.put("f", new HashSet<>(Arrays.asList(new String[] { "y", "o" })));
		graph.put("g", new HashSet<>(Arrays.asList(new String[] { "x", "l", "l", "d" })));
		graph.put("h", new HashSet<>(Arrays.asList(new String[] { "w", "d" })));
		graph.put("i", new HashSet<>(Arrays.asList(new String[] { "l", "e" })));
		graph.put("j", new HashSet<>(Arrays.asList(new String[] { "f", "k" })));
		graph.put("k", new HashSet<>(Arrays.asList(new String[] { "d", "l" })));
		graph.put("l", new HashSet<>(Arrays.asList(new String[] { "s", "z" })));
		graph.put("m", new HashSet<>(Arrays.asList(new String[] { "c", "i" })));
		graph.put("n", new HashSet<>(Arrays.asList(new String[] { "p", "j" })));
		graph.put("o", new HashSet<>(Arrays.asList(new String[] { "b", "a" })));
		graph.put("p", new HashSet<>(Arrays.asList(new String[] { "c", "u" })));
		graph.put("q", new HashSet<>(Arrays.asList(new String[] { "u", "d", "m" })));
		graph.put("r", new HashSet<>(Arrays.asList(new String[] { "r", "c" })));
		graph.put("s", new HashSet<>(Arrays.asList(new String[] { "h", "v" })));
		graph.put("t", new HashSet<>(Arrays.asList(new String[] { "w", "v" })));
		graph.put("u", new HashSet<>(Arrays.asList(new String[] { "q", "q" })));
		graph.put("v", new HashSet<>(Arrays.asList(new String[] { "o", "i" })));
		graph.put("w", new HashSet<>(Arrays.asList(new String[] { "k", "g", "e", "l" })));
		graph.put("x", new HashSet<>(Arrays.asList(new String[] { "l", "l" })));
		graph.put("y", new HashSet<>(Arrays.asList(new String[] { "s", "s" })));
		graph.put("z", new HashSet<>(Arrays.asList(new String[] { "m", "d", "s" })));
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
		Set<String> seed = new HashSet<>(Arrays.asList(new String[] { "a", "c", "q", "f" }));

		BFSStrategy bfs = new BFSStrategy();
		String out = Helpers.getUserDir() + "/target/test.out";
		bfs.setK(1);
		bfs.setQualityMap(quality);
		bfs.setWebGraph(graph);
		bfs.start(seed, out);

		System.out.println(Helpers.join(FileReader.readLines(out), "\n"));
		assertTrue(new File(out).exists());
	}

	/**
	 * Test of start method, of class BFSStrategy.
	 * 
	 * @throws java.io.IOException
	 */
	@Test
	public void testStartWithMaxSteps() throws IOException {
		Set<String> seed = new HashSet<>(Arrays.asList(new String[] { "y", "m", "p", "g" }));

		BFSStrategy bfs = new BFSStrategy();
		String out = Helpers.getUserDir() + "/target/test_steps.out";
		bfs.setK(1);
		bfs.setQualityMap(quality);
		bfs.setWebGraph(graph);
		bfs.start(seed, out, 10);

		System.out.println(Helpers.join(FileReader.readLines(out), "\n"));
		assertTrue(new File(out).exists());
	}
}