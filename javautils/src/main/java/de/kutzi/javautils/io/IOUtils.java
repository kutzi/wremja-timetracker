package de.kutzi.javautils.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;


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
        Path sourcePath = source.toPath();
        Path targetPath = destination.toPath();
        if (copyAttributes) {
        	Files.copy(sourcePath, targetPath, StandardCopyOption.COPY_ATTRIBUTES);
        } else {
        	Files.copy(sourcePath, targetPath);
        }
    }

    /**
     * @deprecated please use Java 7 try-with-resources pattern!
     */
    @Deprecated
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch(IOException e) {
                // ignore
            }
        }
    }
    
    /**
     * @deprecated please use Java 7 try-with-resources pattern!
     */
    @Deprecated
    public static void close(Closeable... closables) {
        for (Closeable closeable : closables) {
        	close(closeable);
        }
    }
}
