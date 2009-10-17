/* Created on 08.02.2008 */
package de.kutzi.javautils.statistics;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class ThroughputGaugeCASImpl implements ThroughputGauge {

    /**
     * Size of the measurement window in the passed {@link TimeUnit}.
     */
    private long windowSize;
    private long granularityNanos;
    private final AtomicReferenceArray<AtomicLong> pings;
    private final AtomicLong nextBucketTime = new AtomicLong();
    private long startTime;
    private TimeUnit timeUnit;
    private long granularity;
    private long totalPings;

    public ThroughputGaugeCASImpl(long windowSize, long granularity,
            TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        this.windowSize = windowSize;
        this.granularity = granularity;
        this.granularityNanos = timeUnit.toNanos(granularity);
        if (windowSize % granularity != 0) {
            throw new IllegalArgumentException(
                    "windowSize is no multiple of granularity");
        }

        this.pings = new AtomicReferenceArray<AtomicLong>((int) (windowSize / granularity) + 1); // one
                                                                     // additional
                                                                     // bucket
                                                                     // for the
                                                                     // 'current'
                                                                     // bucket
        this.startTime = System.nanoTime();
        this.nextBucketTime.set( this.startTime + granularityNanos );
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.kutzi.javautils.statistics.ThroughputGauge#ping()
     */
    public synchronized void ping() {
        advanceBucket();
        this.pings.get(0).incrementAndGet();
        this.totalPings++;
    }

    private void advanceBucket() {
        long now = System.nanoTime();
        long _nextBucketTime = this.nextBucketTime.get();
        if( now <= _nextBucketTime ) {
            return;
        }

        AtomicLong currentBucket = this.pings.get(0);
        if( !this.pings.compareAndSet(0, currentBucket, new AtomicLong(0)) ) {
            // another thread is already updating the pings
            return;
        }
        
        
        long nrOfMoves = (now - this.nextBucketTime.get()) / granularityNanos;
        long newNextBucketTime = _nextBucketTime + nrOfMoves * granularityNanos;
        this.nextBucketTime.compareAndSet( _nextBucketTime, newNextBucketTime );

        // now clean-up the other buckets:
        
        nrOfMoves = nrOfMoves < this.pings.length() ? nrOfMoves : this.pings.length() - 1;

        for( int i=1; i <= nrOfMoves; i++ ) {
            AtomicLong tmp = this.pings.get( i );
            if( ! this.pings.compareAndSet(i, tmp, currentBucket ) ) {
                throw new IllegalStateException();
            }
            currentBucket = tmp;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.kutzi.javautils.statistics.ThroughputGauge#getThroughput()
     */
    public synchronized double getThroughput() {
        /*
         * long now = System.currentTimeMillis(); if( now < this.startTime +
         * this.windowSizeMs ) { System.out.println( "Not enough data, yet!" );
         * return -1; }
         */

        long totalProcessed = getTotalCount(false);
        double throughput = totalProcessed / (this.pings.length() - 1);

        System.out.println("Throughput in the last " + windowSize + " "
                + timeUnit.toString() + " :\n" + throughput + " messages / "
                + granularity + " " + this.timeUnit.toString());
        return throughput;
    }

    private long getTotalCount(boolean includeCurrentBucket) {
        advanceBucket();

        long totalProcessed = 0;
        int start = includeCurrentBucket ? 0 : 1;
        for (int i = start; i < this.pings.length(); i++) { // bucket 0 is left out, because it
            // would falsify the result
            totalProcessed += this.pings.get(i).get();
        }
        return totalProcessed;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.kutzi.javautils.statistics.ThroughputGauge#getTotalCount()
     */
    public synchronized long getTotalCount() {
        return getTotalCount(true);
    }

    public static void main(String[] args) throws InterruptedException {
        ThroughputGauge gauge = new ThroughputGaugeCASImpl(10, 1,
                TimeUnit.SECONDS);
        int pingis = 100000000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < pingis; i++) {
            gauge.ping();
        }
        long end = System.currentTimeMillis();
        System.out.println(pingis + " pings in " + (end - start) + " ms");
        System.out.println((double) (end - start) / pingis + " ms per ping");
        // Thread.sleep(5000);
        gauge.getThroughput();
    }
}
