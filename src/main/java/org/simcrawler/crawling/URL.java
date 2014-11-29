package org.simcrawler.crawling;

import java.util.Collection;
import java.util.HashSet;

/**
 *
 * @author proewer
 * @since 14-11-28
 */
public class URL {

	private String url;
	private int quality;
	private int score;

	public URL(String url) {
		this.url = url;
	}

	public URL(String url, int quality, int score) {
		this.url = url;
		this.quality = quality;
		this.score = score;
	}

	public static Collection<URL> fromCollection(Collection<String> urls) {
		Collection<URL> results = new HashSet<>();
		for ( String url : urls )
			results.add(new URL(url));
		return results;
	}

	@Override
	public boolean equals(Object obj) {
		if ( obj != null ) {
			if ( obj instanceof URL ) {
				if ( this.url.equals(((URL) obj).getUrl()) )
					return true;
			}
			else if ( obj instanceof String ) {
				if ( this.url.equals(obj) )
					return true;
			}
		}
		return false;
	}

	public int getQuality() {
		return quality;
	}

	public int getScore() {
		return score;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return this.url;
	}
}
