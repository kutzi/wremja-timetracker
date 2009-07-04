package com.kemai.util;

import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

/**
 * A {@link NumberFormat} which formats duration hours (representation as doubles) depending on a {@link Style}.
 * 
 * @author kutzi
 */
public class DurationFormat extends NumberFormat {

    private static final long serialVersionUID = 1L;
    
    public enum Style {
    	HOURS_FRACTIONS, HOURS_MINUTES;
    }

    private final Style style;
    private final char decimalSep;

    public DurationFormat(Style style) {
    	this.style = style;
        this.decimalSep = new DecimalFormatSymbols(Locale.getDefault()).getDecimalSeparator();
    }
    
    @Override
    public StringBuffer format(double number, StringBuffer toAppendTo,
            FieldPosition pos) {
        
        long hours = (long)number;
        double fractions = number - hours;
        
        if(this.style == Style.HOURS_MINUTES) {
            long minutes = Math.round(60 * fractions);
            final String minutesStr;
            if(minutes < 10) {
                minutesStr = "0" + minutes;
            } else {
                minutesStr = String.valueOf(minutes);
            }
            
            toAppendTo.append(hours).append(":").append(minutesStr);
        } else {
            long fractionsF = Math.round(fractions * 100);
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
