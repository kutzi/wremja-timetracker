/**
 * 
 */
package de.kutzi.javautils.misc;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Utility for Maven-style versions/version strings.
 *
 * @author kutzi
 */
public class MavenVersion implements Comparable<MavenVersion> {
	private final String[] versions;
	private final boolean snapshot;
	
	private static final Pattern WHITESPACE = Pattern.compile("\\s");

	public MavenVersion(String[] versions, boolean snapshot) {
		this.versions = versions;
		this.snapshot = snapshot;
		for (String v : this.versions) {
			if (WHITESPACE.matcher(v).find()) {
				throw new IllegalArgumentException("Illegal whitespace in '" + v + "'");
			}
		}
	}
	
	public String[] getVersions() {
    	return versions;
    }

	public boolean isSnapshot() {
    	return snapshot;
    }
	
	public boolean isGreaterThan(MavenVersion other) {
		return compareTo(other) > 0;
	}

	@Override
    public int compareTo(MavenVersion other) {
		int size = Math.min(this.versions.length, other.versions.length);
		for (int i=0; i < size; i++) {
			int compare = this.versions[i].compareTo(other.versions[i]);
			if (compare > 0) {
				return 1;
			} else if (compare < 0) {
				return -1;
			}
		}
		
		// all version found so far are the same
		if (this.versions.length > other.versions.length) {
			return 1;
		} else if (this.versions.length < other.versions.length) {
			return -1;
		}
		
		if (!this.snapshot && other.snapshot) {
			return 1;
		} else if (this.snapshot && !other.snapshot) {
			return -1;
		}
		return 0;
    }
	
	/**
	 * Parses a {@link MavenVersion} from a string.
	 * 
	 * @throws IllegalArgumentException if version contains whitespace
	 */
	public static MavenVersion fromString(String version) {
		boolean snapshot = false;
		if (version.endsWith("-SNAPSHOT")) {
			snapshot = true;
			version = version.substring(0, version.length() - "-SNAPSHOT".length());
		}
		
		if (version.length() == 0) {
			throw new IllegalArgumentException("empty version");
		}
		
		String[] versions = version.split("\\.");
		return new MavenVersion(versions, snapshot);
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (snapshot ? 1231 : 1237);
        result = prime * result + Arrays.hashCode(versions);
        return result;
    }

	@Override
    public boolean equals(Object obj) {
        if (this == obj)
	        return true;
        if (obj == null)
	        return false;
        if (getClass() != obj.getClass())
	        return false;
        MavenVersion other = (MavenVersion) obj;
        if (snapshot != other.snapshot)
	        return false;
        if (!Arrays.equals(versions, other.versions))
	        return false;
        return true;
    }
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		for (String version : versions) {
			if (buf.length() > 0) {
				buf.append('.');
			}
			buf.append(version);
		}
		
		if (isSnapshot()) {
			buf.append("-SNAPSHOT");
		}
		return buf.toString();
	}
}