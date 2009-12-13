package de.kutzi.javautils.misc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.testng.annotations.Test;


@Test
public class MavenVersionTest {

	public void testFromString() {
		String version = "1.0";
		MavenVersion mVersion = MavenVersion.fromString(version);
		
		assertFalse(mVersion.isSnapshot());
		assertArrayEquals(mVersion.getVersions(), new String[] {"1", "0"});
		
		version = "2-SNAPSHOT";
		mVersion = MavenVersion.fromString(version);
		
		assertTrue(mVersion.isSnapshot());
		assertArrayEquals(mVersion.getVersions(), new String[] {"2"});
		
		version = "1.12.35.56.3243545";
		mVersion = MavenVersion.fromString(version);
		
		assertFalse(mVersion.isSnapshot());
		assertArrayEquals(mVersion.getVersions(), new String[] {"1", "12", "35", "56", "3243545"});
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testInvalidVersion() {
		MavenVersion.fromString("1. 2a");
	}
	
	public void testIsGreater() {
		MavenVersion v1 = MavenVersion.fromString("2.0.1");
		
		MavenVersion v2 = MavenVersion.fromString("2.0.0");
		assertTrue(v1.isGreaterThan(v2));
		
		v2 = MavenVersion.fromString("2.0");
		assertTrue(v1.isGreaterThan(v2));
		
		v2 = MavenVersion.fromString("2.0.1-SNAPSHOT");
		assertTrue(v1.isGreaterThan(v2));
		
		v2 = MavenVersion.fromString("2.0.2-SNAPSHOT");
		assertTrue(v2.isGreaterThan(v1));
		
		v2 = MavenVersion.fromString("3");
		assertTrue(v2.isGreaterThan(v1));
	}
}
