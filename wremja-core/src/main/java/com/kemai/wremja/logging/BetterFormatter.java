package com.kemai.wremja.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * 'Better' formatter for java.util.logging 
 */
public class BetterFormatter extends Formatter {

    private final Date dat = new Date();
    private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String lineSeparator = System.getProperty("line.separator");

    /**
     * Format the given LogRecord.
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    @Override
    public synchronized String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        // Minimize memory allocations here.
        dat.setTime(record.getMillis());
        
        sb.append(formatter.format(dat));
        sb.append(" ");
	    sb.append(record.getLoggerName());
	    sb.append(" ");
	    sb.append(record.getLevel().getName());
	    sb.append(": ");
	    String message = unlocalizedFormat(record);
	    sb.append(message);
	    sb.append(lineSeparator);
    	if (record.getThrown() != null) {
	        StringWriter sw = new StringWriter();
	        PrintWriter pw = new PrintWriter(sw);
	        record.getThrown().printStackTrace(pw);
	        pw.close();
	        sb.append(sw.toString());
    	}
    	return sb.toString();
    }
    
    private String unlocalizedFormat( LogRecord record) {
        String format = record.getMessage();
        // Do the formatting.
        try {
            Object parameters[] = record.getParameters();
            if (parameters == null || parameters.length == 0) {
            // No parameters.  Just return format string.
            return format;
            }
            // Is is a java.text style format?
                // Ideally we could match with
                // Pattern.compile("\\{\\d").matcher(format).find())
                // However the cost is 14% higher, so we cheaply check for
                // 1 of the first 4 parameters
                if (format.indexOf("{0") >= 0 || format.indexOf("{1") >=0 ||
                            format.indexOf("{2") >=0|| format.indexOf("{3") >=0) {
                return java.text.MessageFormat.format(format, parameters);
            }
            return format;
        } catch (Exception ex) {
            // Formatting failed: use default format string.
            return format;
        }
    }
}
