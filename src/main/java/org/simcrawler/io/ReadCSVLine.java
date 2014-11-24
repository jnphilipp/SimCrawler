package org.simcrawler.io;

/**
 * @author jnphilipp
 * @version 0.0.1
 * @scince Nov 12, 2014
 */
public interface ReadCSVLine {
	/**
	 * The file reader will call this method for each line with the list of columns read.
	 * @param columns columns in csv file
	 */
	public void processLine(String[] columns);
}