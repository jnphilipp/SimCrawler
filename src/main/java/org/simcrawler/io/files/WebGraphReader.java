package org.simcrawler.io.files;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simcrawler.io.FileReader;

/**
 * 
 * @author paul
 * @version 0.0.1
 */
public class WebGraphReader {

	/**
	 * load the given WEB_GRAPH file into the simulation
	 * @param file name
	 * @return web graph mapping
	 * @throws IOException
	 */
	public static Map<String,Set<String>> read(String file) throws IOException{
		Map<String,Set<String>> webGraph = new LinkedHashMap<>();
		List<String[]> result = new ArrayList<>();
		FileReader.readCSV(file, result, "\t");
		for(int i=0;i<result.size();i++){
			if(!webGraph.containsKey(result.get(i)[0])){
				webGraph.put(result.get(i)[0], new LinkedHashSet<String>());
			}
			webGraph.get(result.get(i)[0]).add(result.get(i)[1]);
		}
				
		return webGraph;
	}
}
