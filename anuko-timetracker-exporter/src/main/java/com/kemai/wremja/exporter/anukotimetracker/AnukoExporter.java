package com.kemai.wremja.exporter.anukotimetracker;

import java.io.OutputStream;
import java.util.List;

import com.kemai.wremja.exporter.anukotimetracker.model.Mapping;
import com.kemai.wremja.exporter.anukotimetracker.util.AnukoAccess;
import com.kemai.wremja.model.ProjectActivity;
import com.kemai.wremja.model.ReadableRepository;
import com.kemai.wremja.model.export.Exporter;
import com.kemai.wremja.model.filter.Filter;

public class AnukoExporter implements Exporter {

    private Mapping mappings;
    private AnukoAccess anukoAccess;

    public AnukoExporter( String url, String login, String password,
            Mapping mappings ) {
        this.mappings = mappings;
        
        this.anukoAccess = new AnukoAccess(url, login, password);
    }
    
    public void export(ReadableRepository data, Filter filter,
            OutputStream outputStream) throws Exception {
        
        List<ProjectActivity> activities = filter.applyFilters(data.getActivities());
        anukoAccess.submitActivities( activities, this.mappings );
    }

}
