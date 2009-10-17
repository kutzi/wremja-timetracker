/* Created on 08.02.2008 */
package de.kutzi.javautils.statistics;

import java.util.concurrent.TimeUnit;

public class ThroughputGaugeSynchronizedImpl implements ThroughputGauge {

  private long windowSize;
  //private long windowSizeNanos;
  private long granularityNanos;
  private long[] pings;
  private long nextBucketTime;
  private int index = 0;
  private long startTime;
  private TimeUnit timeUnit;
  private long granularity;
  private long totalPings;

  public ThroughputGaugeSynchronizedImpl( long windowSize, long granularity, TimeUnit timeUnit ) {
    this.timeUnit = timeUnit;
    this.windowSize = windowSize;
    //this.windowSizeNanos = timeUnit.toNanos(windowSize);
    this.granularity = granularity;
    this.granularityNanos = timeUnit.toNanos(granularity);
    if( windowSize % granularity != 0 ) {
      throw new IllegalArgumentException( "windowSize is no multiple of granularity" );
    }
    
    this.pings = new long[ (int)(windowSize / granularity) + 1 ]; // one additional bucket for the 'current' bucket
    this.startTime = System.nanoTime();
    this.nextBucketTime = this.startTime + granularityNanos;
  }

  /* (non-Javadoc)
   * @see de.kutzi.javautils.statistics.ThroughputGauge#ping()
   */
  public synchronized void ping() {
    advanceBucket();
    this.pings[index]++;
    this.totalPings++;
  }
  
  /**
   * Note: must be called from a synchronized context!
   */
  private void advanceBucket() {
    long now = System.nanoTime();
    int i = 0;
    long _nextBucketTime = this.nextBucketTime;
    while( now > _nextBucketTime && i < this.pings.length ) {
      index++;
      //System.out.println("switched bucket to " + index);
      if( index >= pings.length ) {
        index = 0;
      }
      this.pings[index] = 0;
      i++;
      _nextBucketTime += granularityNanos;
    }
    
    this.nextBucketTime = _nextBucketTime; // FIXME: handle 'overruns' of index!

    //this.nextBucketTime = now + granularityNanos; // FIXME: take old nextBucketTime instead of now!
  }

  /* (non-Javadoc)
   * @see de.kutzi.javautils.statistics.ThroughputGauge#getThroughput()
   */
  public synchronized double getThroughput() {
    /*long now = System.currentTimeMillis();
    if( now < this.startTime + this.windowSizeMs ) {
      System.out.println( "Not enough data, yet!" );
      return -1;
    }*/
      
    long totalProcessed = getTotalCount( false );
    double throughput = (double)totalProcessed / (double)(this.pings.length - 1);

    System.out.println( "Throughput in the last " + windowSize + " " + timeUnit.toString() + " :\n" +
        throughput + " messages / " + granularity + " " + this.timeUnit.toString() );
    return throughput;
  }

  private long getTotalCount( boolean includeCurrentBucket ) {
      advanceBucket();
      long totalProcessed = 0;
      for (int i = 0; i < this.pings.length; i++) {
          if( i != this.index ) { // current bucket is left out, because it would falsify the result
              totalProcessed += this.pings[i];
          } else if( includeCurrentBucket ) {
        	  totalProcessed += this.pings[i];
          }
      }
      return totalProcessed;
  }

  /* (non-Javadoc)
 * @see de.kutzi.javautils.statistics.ThroughputGauge#getTotalCount()
 */
  public synchronized long getTotalCount() {
	  return getTotalCount( true );
  }


  public static void main(String[] args) throws InterruptedException {
    ThroughputGauge gauge = new ThroughputGaugeSynchronizedImpl( 10, 1, TimeUnit.SECONDS );
    int pingis = 100000000;
    long start = System.currentTimeMillis();
    for(int i=0; i < pingis; i++ ) {
      gauge.ping();
    }
    long end = System.currentTimeMillis();
    System.out.println( pingis + " pings in " + (end - start) + " ms" );
    System.out.println( (double)(end-start)/pingis + " ms per ping" );
    //Thread.sleep(5000);
    gauge.getThroughput();
  }

  /**
   * For testing
   */
  long getStartTime() {
      return this.startTime;
  }

  /**
   * For testing
   */
  long getNextBucketTime() {
      return this.nextBucketTime;
  }
}
