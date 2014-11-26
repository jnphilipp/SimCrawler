package org.simcrawler.io;

/**
 * @author jnphilipp
 * @version 0.0.1
 * @scince Nov 12, 2014
 */
public interface ReadCSVLineWithLineNumber {
	/**
	 * Gets called after the last line is read.
	 */
	public void close();

	/**
	 * The file reader will call this method for each line with the list of columns read abd the current line number.
	 * @param columns csv columns
	 * @param line line number
	 */
	public void processLine(String[] columns, long line);
}