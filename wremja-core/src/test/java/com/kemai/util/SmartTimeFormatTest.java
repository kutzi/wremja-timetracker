package com.kemai.util;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;



public class SmartTimeFormatTest {

    @Test
    public void testNormalize() throws ParseException {
        Assert.assertArrayEquals( new int [] {11, 15}, SmartTimeFormat.parseToHourAndMinutes("11,25"));
        
        Assert.assertArrayEquals( new int [] {10, 45}, SmartTimeFormat.parseToHourAndMinutes("10;75"));
        
        Assert.assertArrayEquals( new int[] {9,0}, SmartTimeFormat.parseToHourAndMinutes("9 ") );
    }
}
