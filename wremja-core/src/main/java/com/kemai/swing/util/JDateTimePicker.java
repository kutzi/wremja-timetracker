package com.kemai.swing.util;

import java.util.Date;

import org.jdesktop.swingx.JXDatePicker;
import org.joda.time.DateTime;

/**
 * Extension of {@link JXDatePicker} which handles JODA {@link DateTime}s.
 * 
 * @author kutzi
 */
public class JDateTimePicker extends JXDatePicker {

    private static final long serialVersionUID = 1L;

    public JDateTimePicker( DateTime current ) {
        super(current.toDate());
    }
    
    public DateTime getDateTime() {
        Date d = super.getDate();
        return new DateTime(d);
    }

    public void setDateTime( DateTime newDateTime ) {
        super.setDate(newDateTime.toDate());
    }
    
    /**
     * @deprecated please use {@link #getDateTime()}
     */
    @Override
    public Date getDate() {
        return super.getDate();
    }

    /**
     * @deprecated please use {@link #setDateTime(DateTime)}
     */
    @Override
    public void setDate(Date date) {
        super.setDate(date);
    }
    
    
}
