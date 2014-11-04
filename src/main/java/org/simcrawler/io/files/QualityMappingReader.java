package org.simcrawler.io.files;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.simcrawler.io.FileReader;

/**
 * 
 * @author proewer
 * @version 0.0.1
 */
public class QualityMappingReader {

	/**
	 * load the given QUALITY_MAPPING file into the simulation
	 * @param file name
	 * @return quality mapping
	 * @throws IOException
	 */
	public static Map<String,Integer> read(String file) throws IOException{
		Map<String,Integer> qualityMap = new LinkedHashMap<>();
		List<String[]> result = new ArrayList<>();
		FileReader.readCSV(file, result, "\t");
		for(int i=0;i<result.size();i++){
			qualityMap.put(result.get(i)[0], Integer.parseInt(result.get(i)[1]));
		}
		return qualityMap;
	}
}
