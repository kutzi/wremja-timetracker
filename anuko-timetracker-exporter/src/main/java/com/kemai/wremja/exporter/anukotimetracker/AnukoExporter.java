package com.kemai.wremja.exporter.anukotimetracker;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;


import com.kemai.wremja.exporter.anukotimetracker.model.AnukoActivity;
import com.kemai.wremja.exporter.anukotimetracker.util.AnukoAccess;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;
import com.kemai.wremja.model.ProjectView;
import com.kemai.wremja.model.export.Exporter;
import com.kemai.wremja.model.filter.Filter;

public class AnukoExporter implements Exporter {

    private Map<Project, AnukoActivity> mappings;
    private AnukoAccess anukoAccess;

    @Deprecated
    public AnukoExporter() {
    }
    
    public AnukoExporter( String url, String login, String password,
            Map<Project, AnukoActivity> mappings ) {
        this.mappings = mappings;
        
        this.anukoAccess = new AnukoAccess(url, login, password);
    }
    
    public void export(ProjectView data, Filter filter,
            OutputStream outputStream) throws Exception {
        
        List<ProjectActivity> activities = filter.applyFilters(data.getActivities());
        anukoAccess.submitActivities( activities, this.mappings );
    }

}
