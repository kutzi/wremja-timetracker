package com.kemai.util;

import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class TimeFormatTest {
	
	private TimeFormat timeFormat;

	@Before
	public void setUp() {
		timeFormat = new TimeFormat();
	}
	
	@Test
	public void shouldParseValidHoursAndMinutes() throws ParseException {
		Date date = timeFormat.parse("12:23");
		
		assertNotNull(date);
		assertEquals(12, date.getHours());
		assertEquals(23, date.getMinutes());
	}
	
	@Test
	public void shouldParseValidHoursAndPartialMinutes() throws ParseException {
		Date date = timeFormat.parse("12:2");
		
		assertNotNull(date);
		assertEquals(12, date.getHours());
		assertEquals(2, date.getMinutes());
	}
	
	@Test(expected = ParseException.class)
	public void shouldRejectNonDigits() throws ParseException {
		timeFormat.parse("15:1a");
	}
	
	@Test(expected = ParseException.class)
	public void shouldRejectNonDigits2() throws ParseException {
		timeFormat.parse("15:a1");
	}
	
	@Test(expected = ParseException.class)
	public void shouldRejectNonDigits3() throws ParseException {
		timeFormat.parse("1a:11");
	}
}
