package com.kemai.wremja.model.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.jfree.util.Log;

import com.kemai.util.IOUtils;
import com.kemai.wremja.gui.model.io.DataBackup;
import com.kemai.wremja.model.ActivityRepository;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;
import com.kemai.wremja.model.ReadableRepository;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Writer for ActivityRepository data files.
 * @author remast
 * @author kutzi
 */
public class ProTrackWriter {

	private static final Object SAVE_LOCK = new Object();
	
	private final XStream xstream;
	
	private static final ProTrackWriter INSTANCE = new ProTrackWriter();
	
	public static ProTrackWriter instance() {
		return INSTANCE;
	}

    /**
     * Creates a writer.
     */
    private ProTrackWriter() {
        this.xstream = new XStream(new DomDriver(IOConstants.FILE_ENCODING));
        this.xstream.processAnnotations(
                new Class[] {ActivityRepository.class, Project.class, ProjectActivity.class}
        );
        this.xstream.autodetectAnnotations(true);

        this.xstream.setMode(XStream.ID_REFERENCES);
        this.xstream.registerConverter(new DateTimeConverter());
    }

    /**
     * Writes the data to the given file.
     * 
     * @param data the data to write
     * @param file the file to write to
     * @throws IOException on write error
     */
    public final void write(ReadableRepository data, File file) throws IOException {
        if (file == null) {
            return;
        }
        
        synchronized (SAVE_LOCK) {
        	File tmpFile = new File(file.getParentFile(), file.getName() + ".tmp");
            final OutputStream fileOut = new BufferedOutputStream(new FileOutputStream(tmpFile));
            try {
                write(data, fileOut);
                fileOut.close();
                
                DataBackup.toBackup(file);
                if( !tmpFile.renameTo(file)) {
                	Log.error("Couldn't rename tmp file to " + file.getAbsolutePath());
                }
            } finally {
                IOUtils.closeQuietly(fileOut);
            }
        }
    }
    
    /**
     * Writes the data to the given {@link OutputStream}.
     * 
     * @param data the data to write
     * @param outputStream the outputStream to write to
     * @throws IOException on write error
     */
    public final void write(ReadableRepository data, OutputStream outputStream) throws IOException {

        Writer w = new OutputStreamWriter(outputStream, IOConstants.FILE_ENCODING);
        xstream.toXML(data, w);
    }
}
