package com.kemai.wremja.model.export;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.model.ProjectActivity;
import com.kemai.wremja.model.ProjectView;
import com.kemai.wremja.model.filter.Filter;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Exports the data into Commma Separated Value (CSV) format.
 * @author remast
 */
public class CsvExporter implements Exporter {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(CsvExporter.class);

    private static final DateTimeFormatter timeFormat = DateTimeFormat.forPattern("hh:mm"); //$NON-NLS-1$

    private static final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("dd.MM.yyyy"); //$NON-NLS-1$

    public static final NumberFormat durationFormat = new DecimalFormat("#0.00"); //$NON-NLS-1$

    private static final char SEPARATOR_CHARACTER = ';';

    /** Header of CSV file containing the column headings. */
    private static final String[] CSV_HEADER;
    static {
        CSV_HEADER = new String[] {
                textBundle.textFor("CsvExporter.ProjectHeading"), //$NON-NLS-1$
                textBundle.textFor("CsvExporter.DateHeading"), //$NON-NLS-1$
                textBundle.textFor("CsvExporter.StartTimeHeading"), //$NON-NLS-1$
                textBundle.textFor("CsvExporter.EndTimeHeading"), //$NON-NLS-1$
                textBundle.textFor("CsvExporter.HoursHeading"), //$NON-NLS-1$
                textBundle.textFor("CsvExporter.DescriptionHeading") //$NON-NLS-1$
        };
    }

    /**
     * Exports the given data as Comma Separated Value (CSV) to the 
     * <code>OutputStream</code> under consideration of the given filter.
     * @param data the data to be exported
     * @param filter the current filter
     * @param outputStream the stream to write to
     * @throws Exception exception during data export
     */
    @Override
    public void export(final ProjectView data, final Filter filter, final OutputStream outputStream) throws Exception {
        List<ProjectActivity> activities = data.getActivities();
        if (filter != null) {
            activities = filter.applyFilters(data.getActivities());
        }

        final CSVWriter writer = new CSVWriter(
                new OutputStreamWriter(outputStream),
                SEPARATOR_CHARACTER
        );

        writer.writeNext(CSV_HEADER);

        for (ProjectActivity activity : activities) {
            writer.writeNext(makeCsvLine(activity));
            writer.flush();
        }

    }

    /**
     * Creates a line of the CSV file from the given activity.
     * @param activity the activity to create line for
     * @return the CSV line for the given activity
     */
    private String[] makeCsvLine(final ProjectActivity activity) {
        if (activity == null) {
            return null;
        }

        final String[] csvLine = new String[6];

        csvLine[0] = activity.getProject().getTitle();
        csvLine[1] = dateFormat.print(activity.getStart());
        csvLine[2] = timeFormat.print(activity.getStart());
        csvLine[3] = timeFormat.print(activity.getEnd());
        csvLine[4] = durationFormat.format(activity.getDuration());

        // Description
        String description = com.kemai.util.StringUtils.stripXmlTags(activity.getDescription());
        description = StringUtils.trim(description);
        csvLine[5] = description;

        return csvLine;
    }
}
