package org.simcrawler.io;

/**
 * @author jnphilipp
 * @version 0.0.1
 * @scince Nov 12, 2014
 */
public interface ReadCSVLineWithLineNumber {
	public void processLine(String[] columns, int line);
}