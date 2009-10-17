package de.kutzi.javautils.statistics;

/**
 * Measures throughput (i.e. number of work items done per time span). 
 */
public interface ThroughputGauge {

	/**
	 * Tells that an item of work has done.
	 */
	public void ping();

	/**
	 * Returns the throughput in the last time span.
	 * 
	 * Look into the concrete implementation for an exact definition of
	 * 'time span'.
	 * 
	 * Note that depending on the type of the implementation, the returned number
	 * may be only an 'approximate'.
	 */
	public double getThroughput();

	/**
	 * Returns the total count in the last time span.
	 */
	public long getTotalCount();

}