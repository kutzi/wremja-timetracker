package de.kutzi.javautils.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.jvnet.animal_sniffer.IgnoreJRERequirement;

import de.kutzi.javautils.system.JavaUtil;

public class IOUtils {
    private IOUtils() {
        throw new AssertionError("No instances allowed!");
    }
    
    /**
     * Copies the file contents from source to destination.
     * Also, copies file attributes as far as possible (i.e. supported by the underlying
     * Java version) - this means that at least last-modified timestamp is kept.
     */
    public static void copyFile(File source, File destination) throws IOException {
    	copyFile(source, destination, true);
    }
    
    /**
     * Copies the file contents from source to destination.
     * 
     * If <code>copyAttributes</code> is true, also copies file attributes as far as possible (i.e. supported by the underlying
     * Java version) - this means that at least last-modified timestamp is kept.
     * 
     * Attention: if <code>copyAttributes</code> is false, it does NOT necessarily mean that attributes
     * are not supported. The behaviour seems to be platform dependent.
     */
    public static void copyFile(File source, File destination, boolean copyAttributes) throws IOException {

        if (JavaUtil.isGreaterOrEqual(17, 0)) {
            copyJava7(source, destination, copyAttributes);
        } else {
            // Java 6
            FileInputStream fis = null;
            FileOutputStream fos = null;
            try {
                FileChannel sourceChannel = new FileInputStream(source).getChannel();
                FileChannel destChannel = new FileOutputStream(destination).getChannel();
                long size = sourceChannel.size();
                long transferedBytes = 0;
                while (transferedBytes < size) {
                    transferedBytes += destChannel.transferFrom(sourceChannel, 0, size - transferedBytes);
                }
                destination.setWritable(source.canWrite());
                // obviously, source is readable
                destination.setExecutable(source.canExecute());
                if (!destination.setLastModified(source.lastModified())) {
                	throw new IOException("Couldn't update last-modified of '" + destination
                			+ "' to " + source.lastModified());
                }
            } finally {
                close(fis, fos);
            }
        }
    }

    @IgnoreJRERequirement
    private static void copyJava7(File source, File destination, boolean copyAttributes) throws IOException {
        Path sourcePath = source.toPath();
        Path targetPath = destination.toPath();
        if (copyAttributes) {
        	sourcePath.copyTo(targetPath, StandardCopyOption.COPY_ATTRIBUTES);
        } else {
        	sourcePath.copyTo(targetPath);
        }
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch(IOException e) {
                // ignore
            }
        }
    }
    
    public static void close(Closeable... closables) {
        for (Closeable closeable : closables) {
        	close(closeable);
        }
    }
}
