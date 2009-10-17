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
    
    public static boolean copyFile(File source, File destination) throws IOException {

        if (JavaUtil.isGreaterOrEqual(17, 0)) {
            copyJava7(source, destination);
            return true;
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
                destination.setExecutable(source.canExecute());
                // obviously, source is readable
                return true;
            } finally {
                close(fis, fos);
            }
        }
    }

    @IgnoreJRERequirement
    private static void copyJava7(File source, File destination) throws IOException {
        Path sourcePath = source.toPath();
        Path targetPath = destination.toPath();
        sourcePath.copyTo(targetPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.COPY_ATTRIBUTES);
    }

    public static void close(Closeable... closables) {
        for (Closeable closeable : closables) {
            try {
                closeable.close();
            } catch(IOException e) {
                // ignore
            }
        }
    }
}
