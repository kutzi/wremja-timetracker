package com.kemai.wremja.model.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.kemai.util.IOUtils;
import com.kemai.wremja.model.ActivityRepository;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;
import com.kemai.wremja.model.ReadableRepository;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Writer for ActivityRepository data files.
 * @author remast
 */
public class ProTrackWriter {

    /** The data to write. */
    private ReadableRepository data;

    /**
     * Create a write for given data.
     * @param data the data
     */
    public ProTrackWriter(final ReadableRepository data) {
        this.data = data;
    }

    /**
     * Write the data to the given file.
     * @param file the file to write to
     * @throws IOException on write error
     */
    public final void write(final File file) throws IOException {
        if (file == null) {
            return;
        }

        synchronized (data) {
            final OutputStream fileOut = new BufferedOutputStream(new FileOutputStream(file));
            try {
                write(fileOut);
                fileOut.flush();
            } finally {
                IOUtils.closeQuietly(fileOut);
            }
        }
    }
    
    /**
     * Write the data to the given {@link OutputStream}.
     * @param outputStream the outputStream to write to
     * @throws IOException on write error
     */
    public final void write(final OutputStream outputStream) throws IOException {
        final XStream xstream = new XStream(new DomDriver(IOConstants.FILE_ENCODING));
        xstream.processAnnotations(
                new Class[] {ActivityRepository.class, Project.class, ProjectActivity.class}
        );
        xstream.autodetectAnnotations(true);

        xstream.setMode(XStream.ID_REFERENCES);
        xstream.registerConverter(new DateTimeConverter());
        xstream.toXML(data, outputStream);
    }
}
