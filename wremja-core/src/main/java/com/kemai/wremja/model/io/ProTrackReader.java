package com.kemai.wremja.model.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.kemai.wremja.logging.Logger;
import com.kemai.wremja.model.ActivityRepository;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import de.kutzi.javautils.io.IOUtils;

/**
 * Reader for {@link ActivityRepository} data files.
 * @author remast
 * @author kutzi
 */
public class ProTrackReader {

    private static final Logger LOG = Logger.getLogger(ProTrackReader.class);

    private static final XStream xstream;
    static {
        xstream = new XStream(new DomDriver(IOConstants.FILE_ENCODING));
        xstream.setMode(XStream.ID_REFERENCES);
        xstream.registerConverter(new DateTimeConverter());
        xstream.processAnnotations(
                new Class[] {ActivityRepository.class, Project.class, ProjectActivity.class}
        );
    }
    
    /**
     * Actually read the data from file.
     * @throws IOException
     */
    public ActivityRepository read(final File file) throws IOException {
        final InputStream fis = new BufferedInputStream(new FileInputStream(file));
        try {
            return read(fis);
        } catch(IOException e) {
            throw new IOException("The file " + (file != null ? file.toString() : "<null>") + " does not contain valid Baralga data.", e);
        } finally {
            IOUtils.close(fis);
        }
    }

    /**
     * Read the data from an {@link InputStream}.
     * @throws IOException
     */
    public ActivityRepository read(final InputStream in) throws IOException {
        Object o = null;
        try {
            o = xstream.fromXML(in);
            return (ActivityRepository) o;
        } catch (Exception e) {
            LOG.error(e, e);
            throw new IOException("The file input stream does not contain valid Wremja data.", e);
        }
    }
}
