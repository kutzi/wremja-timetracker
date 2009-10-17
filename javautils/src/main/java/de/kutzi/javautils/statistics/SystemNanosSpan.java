package de.kutzi.javautils.statistics;

import java.util.concurrent.TimeUnit;

public class SystemNanosSpan {
	public static void main(String[] args) {
		long max = Long.MAX_VALUE;
		System.out.println( TimeUnit.DAYS.convert(max, TimeUnit.NANOSECONDS) );
	}
}
