package com.kemai.util;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

import com.kemai.util.DurationFormat.Style;


public class DurationFormatTest {

	@Test
	public void testHoursFractions() {
		Locale.setDefault(Locale.ENGLISH);
		
		DurationFormat df = new DurationFormat(Style.HOURS_FRACTIONS);
		
		assertEquals("0.00", df.format(0.0));
		assertEquals("0.00", df.format(0L));
		
		assertEquals("2.11", df.format(2.11));
		assertEquals("7.03", df.format(7.03));
		assertEquals("7.30", df.format(7.3));
		
		Locale.setDefault(Locale.GERMANY);
		
		df = new DurationFormat(Style.HOURS_FRACTIONS);
		assertEquals("33,33", df.format(33.33));
	}
	
	@Test
	public void testHoursMinutes() {
		DurationFormat df = new DurationFormat(Style.HOURS_MINUTES);
		
		assertEquals("0:00", df.format(0.0));
		assertEquals("0:00", df.format(0L));
		
		assertEquals("1:06", df.format(1.1));
		assertEquals("3:30", df.format(3.5));
		
		assertEquals("0:20", df.format(0.333333));
	}
}
