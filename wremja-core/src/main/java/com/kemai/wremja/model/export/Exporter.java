package com.kemai.wremja.model.export;

import java.io.OutputStream;

import com.kemai.wremja.model.ReadableRepository;
import com.kemai.wremja.model.filter.Filter;

/**
 * Interface for all data exporters (e.g. MS Excel, CSV).
 * @author kutzi
 * @author remast
 */
public interface Exporter {
    
    /**
     * Exports the given data to the <code>OutputStream</code> under consideration
     * of the given filter.
     * @param data the data to be exported
     * @param filter the current filter
     * @param outputStream the stream to write to
     * @throws Exception exception during data export
     */
    public void export(final ReadableRepository data, final Filter filter, final OutputStream outputStream) throws Exception;

}
