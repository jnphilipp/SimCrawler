package org.simcrawler.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;

/**
 *
 * @author jnphilipp
 * @version 0.0.2
 */
public class FileReader {
	/**
	 * Reads the given file and returns the content.
	 * @param file path to file
	 * @return content
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 */
	public static String read(String file) throws FileNotFoundException, IOException {
		Reader reader = null;
		String content = "";

		try {
			reader = new BufferedReader(new java.io.FileReader(file));

			while ( true ) {
				int c = reader.read();
				if ( c == -1 )
					break;

				content += (char) c;
			}
		}
		finally {
			if ( reader != null )
				reader.close();
		}

		return content;
	}

	/**
	 * Reads the given file as CSV and call for each line the given method.
	 * @param file file
	 * @param cement cement
	 * @param method method for per line action
	 * @throws IOException
	 */
	public static void readCSV(File file, String cement, ReadCSVLine method) throws IOException {
		Reader reader = null;
		try {
			reader = new BufferedReader(new java.io.FileReader(file));

			while ( true ) {
				String c = ((BufferedReader) reader).readLine();
				if ( c == null )
					break;

				method.processLine(c.split(cement));
			}
			method.close();
		}
		finally {
			if ( reader != null )
				reader.close();
		}
	}

	/**
	 * Reads the given file as CSV and call for each line the given method.
	 * @param file file
	 * @param cement cement
	 * @param method method for per line action
	 * @throws IOException
	 */
	public static void readCSV(File file, String cement, ReadCSVLineWithLineNumber method) throws IOException {
		Reader reader = null;
		try {
			reader = new BufferedReader(new java.io.FileReader(file));

			long line = 1;
			while ( true ) {
				String c = ((BufferedReader) reader).readLine();
				if ( c == null )
					break;

				method.processLine(c.split(cement), line++);
			}
			method.close();
		}
		finally {
			if ( reader != null )
				reader.close();
		}
	}

	/**
	 * Reads the given file as CSV.
	 * @param file file
	 * @param csv CSV output
	 * @param cement cement
	 * @throws java.io.IOException
	 */
	public static void readCSV(String file, Collection<String[]> csv, String cement) throws IOException {
		String[] lines = readLines(file);
		for ( String line : lines )
			csv.add(line.split(cement));
	}

	/**
	 * Reads the given file as CSV and call for each line the given method.
	 * @param file file
	 * @param cement cement
	 * @param method method for per line action
	 * @throws IOException
	 */
	public static void readCSV(String file, String cement, ReadCSVLine method) throws IOException {
		readCSV(new File(file), cement, method);
	}

	/**
	 * Reads the given file as CSV and call for each line the given method.
	 * @param file file
	 * @param cement cement
	 * @param method method for per line action
	 * @throws IOException
	 */
	public static void readCSV(String file, String cement, ReadCSVLineWithLineNumber method) throws IOException {
		readCSV(new File(file), cement, method);
	}

	/**
	 * Reads the given file line wise.
	 * @param file path to file
	 * @return lines
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 */
	public static String[] readLines(String file) throws FileNotFoundException, IOException {
		String content = read(file);

		if ( content.isEmpty() )
			return new String[0];

		return content.split(System.lineSeparator());
	}
}