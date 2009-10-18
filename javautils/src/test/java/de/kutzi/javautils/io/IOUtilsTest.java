package de.kutzi.javautils.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.Test;


public class IOUtilsTest {
	@Test
	public void testCopyFile() throws IOException {
		File tmp = File.createTempFile("IOUtilsTest", "tmp");
		tmp.deleteOnExit();
		
		FileWriter fw = new FileWriter(tmp);
		fw.write("jvgdskgfdhfüääöääößdsfdsáéà");
		fw.close();
		
		File tmp2 = File.createTempFile("IOUtilsTest", "tmp");
		if (!tmp2.delete()) {
			Assert.fail("Couldn't delete temp file: " + tmp2);
		}
		IOUtils.copyFile(tmp, tmp2);
		Assert.assertEquals(tmp2.length(), tmp.length());
	}
	
	@Test
	public void testCopyFileWithoutAttributes() throws IOException, InterruptedException {
		File tmp = File.createTempFile("IOUtilsTest", "tmp");
		tmp.deleteOnExit();
		
		FileWriter fw = new FileWriter(tmp);
		fw.write("jvgdskgfdhfüääöääößdsfdsáéà");
		fw.close();
		
		TimeUnit.SECONDS.sleep(1);
		
		File tmp2 = File.createTempFile("IOUtilsTest", "tmp");
		if (!tmp2.delete()) {
			Assert.fail("Couldn't delete temp file: " + tmp2);
		}
		IOUtils.copyFile(tmp, tmp2, false);
		Assert.assertFalse(tmp2.lastModified() == tmp.lastModified());
	}
	
	@Test
	public void testClose() {
		MockCloseable closeable = new MockCloseable();
		IOUtils.close(closeable);
		Assert.assertTrue(closeable.closeCalled);
	}
	
	private static class MockCloseable implements Closeable {
		
		boolean closeCalled = false;
		
		@Override
        public void close() throws IOException {
			closeCalled = true;
	        throw new IOException("Mock");
        }
	}
}
