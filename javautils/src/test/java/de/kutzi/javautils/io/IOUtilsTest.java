package de.kutzi.javautils.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
	public void testClose() {
		MockCloseable closeable = new MockCloseable();
		IOUtils.close(closeable);
		Assert.assertTrue(closeable.closeCalled);
		
		closeable = new MockCloseable();
		MockCloseable closeable2 = new MockCloseable();
		IOUtils.close(closeable, closeable2);
		Assert.assertTrue(closeable.closeCalled);
		Assert.assertTrue(closeable2.closeCalled);
		
		Closeable nullCloseable = null;
		IOUtils.close(nullCloseable);
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
