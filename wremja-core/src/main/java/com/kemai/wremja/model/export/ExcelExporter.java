package com.kemai.wremja.model.export;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import jxl.CellView;
import jxl.JXLException;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.NumberFormats;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.logging.Logger;
import com.kemai.wremja.model.ProjectActivity;
import com.kemai.wremja.model.ReadableRepository;
import com.kemai.wremja.model.filter.Filter;
import com.kemai.wremja.model.report.AccumulatedActivitiesReport;
import com.kemai.wremja.model.report.AccumulatedProjectActivity;

/**
 * Exports data to Microsoft Excel format.
 * @author remast
 */
public class ExcelExporter implements Exporter {

    private static final Logger LOG = Logger.getLogger(ExcelExporter.class);
    
    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ExcelExporter.class);

    public static final DateTimeFormatter MONTH_FORMAT = DateTimeFormat.forPattern("MM"); //$NON-NLS-1$

    public static final DateTimeFormatter YEAR_FORMAT = DateTimeFormat.forPattern("yyyy"); //$NON-NLS-1$

    private static final WritableCellFormat headingFormat;
    static {
        WritableFont arial16 = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD, true,
                UnderlineStyle.NO_UNDERLINE, Colour.DARK_BLUE);
        headingFormat = new WritableCellFormat(arial16);
        try {
            headingFormat.setBackground(Colour.GRAY_25);
        } catch (WriteException e) {
            LOG.error( "Error creating headingFormat", e );
        }
    }
    
    private static final WritableCellFormat floatFormat = new WritableCellFormat(NumberFormats.FLOAT); 
    private static final WritableCellFormat timeFormat = new WritableCellFormat(new DateFormat("hh:mm"));//$NON-NLS-1$
    private static final WritableCellFormat dateFormat = new WritableCellFormat(new DateFormat("DD.MM.yyyy")); //$NON-NLS-1$

    public ExcelExporter() { }

    /**
     * Exports the given data as Microsoft Excel to the 
     * <code>OutputStream</code> under consideration of the given filter.
     * @param data the data to be exported
     * @param filter the current filter
     * @param outputStream the stream to write to
     * @throws Exception exception during data export
     */
    @Override
    public void export(final ReadableRepository data, final Filter filter, final OutputStream outputStream) throws Exception {
            final WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
            createFilteredReport(workbook, data, filter);
            
            final WritableSheet sheet = workbook.createSheet(textBundle.textFor("ExcelExporter.SheetTitleActivityRecords"), 1); //$NON-NLS-1$
            
            int row = 0;
            int col = 0;
            
            sheet.addCell(new Label(col++, row, textBundle.textFor("ExcelExporter.ProjectHeading"), headingFormat)); //$NON-NLS-1$
            sheet.addCell(new Label(col++, row, textBundle.textFor("ExcelExporter.DateHeading"), headingFormat)); //$NON-NLS-1$
            sheet.addCell(new Label(col++, row, textBundle.textFor("ExcelExporter.StartTimeHeading"), headingFormat)); //$NON-NLS-1$
            sheet.addCell(new Label(col++, row, textBundle.textFor("ExcelExporter.EndTimeHeading"), headingFormat)); //$NON-NLS-1$
            sheet.addCell(new Label(col++, row, textBundle.textFor("ExcelExporter.HoursHeading"), headingFormat)); //$NON-NLS-1$
            sheet.addCell(new Label(col++, row, textBundle.textFor("ExcelExporter.DescriptionHeading"), headingFormat)); //$NON-NLS-1$
            
            col = 0;
            row++;

            Collection<ProjectActivity> activities = data.getActivities();
            if (filter != null)  {
                activities = filter.applyFilters(data.getActivities());
            }
            
            for (ProjectActivity actitivity : activities) {
                sheet.addCell(new Label(col++, row, actitivity.getProject().getTitle()));
                sheet.addCell(makeDateCell(col++, row, actitivity.getStart().toDate()));
                sheet.addCell(makeTimeCell(col++, row, actitivity.getStart().toDate()));
                
                WritableCell c = makeTimeCell(col++, row, actitivity.getEnd().toDate());
                sheet.addCell(c);
                sheet.addCell(makeNumberCell(col++, row, actitivity.getDuration()));

                // Description
                String description = com.kemai.util.StringUtils.stripXmlTags(actitivity.getDescription());
                description = StringUtils.trim(description);
                sheet.addCell(new Label(col++, row, description));
                
                col = 0;
                row++;
            }
            // reset col
            col = 0;
            
            // Format Cells
            CellView v = new CellView();
            v.setAutosize(true);
            sheet.setColumnView(col++, v);
            sheet.setColumnView(col++, v);
            sheet.setColumnView(col++, v);
            sheet.setColumnView(col++, v);
            sheet.setColumnView(col++, v);
            sheet.setColumnView(col++, v);
            
            workbook.write();
            workbook.close();
    }
    
    private void createFilteredReport(final WritableWorkbook workbook, final ReadableRepository data, final Filter filter) throws JXLException {
        String year = "";
        if (filter != null && filter.getYear() != null) {
            year = YEAR_FORMAT.print(filter.getYear());
        }
        
        String month = "";
        if (filter != null && filter.getMonth() != null) {
            org.joda.time.DateTime monthDt = new org.joda.time.DateTime().withMonthOfYear(filter.getMonth());
            month = MONTH_FORMAT.print( monthDt );
        }
        
        String reportName = textBundle.textFor("ExcelExporter.SheetTitleStart"); //$NON-NLS-1$
        if (StringUtils.isNotBlank(year)) {
            reportName += year;
        }
        if (StringUtils.isNotBlank(month)) {
            reportName += "-" + month;
        }
        
        final WritableSheet sheet = workbook.createSheet(reportName, 0);

        final AccumulatedActivitiesReport report = new AccumulatedActivitiesReport(data);
        report.setFilter(filter);

        int row = 0;
        int col = 0;
        
        sheet.addCell(new Label(col++, row, textBundle.textFor("ExcelExporter.DateHeading"), headingFormat)); //$NON-NLS-1$
        sheet.addCell(new Label(col++, row, textBundle.textFor("ExcelExporter.ProjectHeading"), headingFormat)); //$NON-NLS-1$
        sheet.addCell(new Label(col++, row, textBundle.textFor("ExcelExporter.TimeHeading"), headingFormat)); //$NON-NLS-1$

        row++;
        col = 0;

        final List<AccumulatedProjectActivity> accumulatedActivitiesByDay = report.getAccumulatedActivitiesByDay();
        for (AccumulatedProjectActivity activity : accumulatedActivitiesByDay) {
            sheet.addCell(makeDateCell(col++, row, activity.getDay()));
            sheet.addCell(new Label(col++, row, activity.getProject().getTitle()));
            sheet.addCell(makeNumberCell(col++, row, activity.getTime()));

            row++;
            col = 0;
        }

        col = 0;

        // Format Cells
        final CellView v = new CellView();
        v.setAutosize(true);
        sheet.setColumnView(col++, v);
        sheet.setColumnView(col++, v);
        sheet.setColumnView(col++, v);
    }

    private static jxl.write.Number makeNumberCell(final int col, final int row, final double number) {
        return new jxl.write.Number(col, row, number, floatFormat); 
    }

    private static WritableCell makeTimeCell(final int col, final int row, final Date date) {
        return new DateTime(col, row, date, timeFormat); 
    }

    private static DateTime makeDateCell(final int i, final int j, final Date date) {
        return new DateTime(i, j, date, dateFormat); 
    }

}
