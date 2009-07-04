package com.kemai.wremja;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.kemai.util.DurationFormat;
import com.kemai.util.SmartTimeFormat;
import com.kemai.util.TimeFormat;
import com.kemai.util.DurationFormat.Style;
import com.kemai.wremja.gui.settings.UserSettings;

/** Utility class for formatting. */
public abstract class FormatUtils {
    
    /** Hide constructor in utility class. */
    private FormatUtils() { }

    // ------------------------------------------------
    // Date Formats
    // ------------------------------------------------
    
    private static final ThreadLocal<DateFormat> timeFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SmartTimeFormat();
        } 
    };
    
    private static final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(TimeFormat.HHMM_FORMAT);

    public static String formatTime(final DateTime date) {
        return timeFormatter.print(date);
    }
    
    public static String formatDate(final DateTime date) {
        return DateFormat.getDateInstance().format(date.toDate());
    }
    
    public static DateTime parseTime(final String time) throws ParseException {
        return new DateTime( timeFormat.get().parse(time) );
    }
    
    /**
     * Returns a {@link DateFormat} instance which is safe to use in the current thread.
     */
    public static DateFormat getTimeFormat() {
        return timeFormat.get();
    }
    
    // ------------------------------------------------
    // Number Formats
    // ------------------------------------------------
    public static NumberFormat getDurationFormat() {
        return durationFormat;
    }
    
    @SuppressWarnings("serial")
    public static NumberFormat getDurationFormatWithH() {
        return new NumberFormat() {

			@Override
            public StringBuffer format(double number, StringBuffer toAppendTo,
                    FieldPosition pos) {
	            StringBuffer res = getDurationFormat().format(number, toAppendTo, pos);
	            res.append('h');
	            return res;
            }

			@Override
            public StringBuffer format(long number, StringBuffer toAppendTo,
                    FieldPosition pos) {
	            return format((double)number, toAppendTo, pos);
            }

			@Override
            public Number parse(String source, ParsePosition parsePosition) {
	            throw new UnsupportedOperationException();
            }
        	
        };
    }

    private static final NumberFormat durationFormat;
    
    static {
    	String format = UserSettings.instance().getDurationFormat();
    	final DurationFormat.Style style;
        if("#0.00".equals(format)) {
            style = Style.HOURS_FRACTIONS;
        } else if("#0:00".equals(format)) {
        	style = Style.HOURS_MINUTES;
        } else {
            throw new IllegalStateException("Unknown format: " + format);
        }
        durationFormat = new DurationFormat(style);
    }
}
