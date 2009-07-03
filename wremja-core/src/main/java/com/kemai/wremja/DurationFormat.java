package com.kemai.wremja;

import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

import com.kemai.wremja.gui.settings.UserSettings;

public class DurationFormat extends NumberFormat {

    private static final long serialVersionUID = 1L;
    
    private final boolean hourMinuteFormat;
    private final char decimalSep;

    public DurationFormat() {
        String format = UserSettings.instance().getDurationFormat();
        if("#0.00".equals(format)) {
            hourMinuteFormat = false;
        } else if("#0:00".equals(format)) {
            hourMinuteFormat = true;
        } else {
            throw new IllegalStateException("Unknown format: " + format);
        }
        
        this.decimalSep = new DecimalFormatSymbols(Locale.getDefault()).getDecimalSeparator();
    }
    
    @Override
    public StringBuffer format(double number, StringBuffer toAppendTo,
            FieldPosition pos) {
        
        long hours = (long)number;
        double fractions = number - hours;
        
        if(hourMinuteFormat) {
            long minutes = (long)(60 * fractions);
            final String minutesStr;
            if(minutes < 10) {
                minutesStr = "0" + minutes;
            } else {
                minutesStr = String.valueOf(minutes);
            }
            
            toAppendTo.append(hours).append(":").append(minutesStr);
        } else {
            long fractionsF = (long)(fractions * 100);
            final String fractionsStr;
            if(fractionsF < 10) {
                fractionsStr = "0" + fractionsF;
            } else {
                fractionsStr = String.valueOf(fractionsF);
            }
            
            toAppendTo.append(hours).append(this.decimalSep).append(fractionsStr);
        }
        return toAppendTo;
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
}
