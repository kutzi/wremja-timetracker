package de.kutzi.javautils.statistics;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test(groups="stress")
public class ThroughputGaugeTest {

	private static final int NUM_THREADS = 200;
	private static final int NUM_TASKS_PER_THREAD = 100000;
	
	private ThroughputGaugeSynchronizedImpl throughputGauge;
	private CountDownLatch startLatch;
	private final AtomicLong taskCount = new AtomicLong(0);

	@BeforeTest
	public void setup() {
		this.throughputGauge = new ThroughputGaugeSynchronizedImpl( 300, 1, TimeUnit.SECONDS );
		this.startLatch = new CountDownLatch( NUM_THREADS );
		this.taskCount.set(0);
	}

	public void stressTest() throws InterruptedException {
		Thread[] threads = new Thread[NUM_THREADS];
		for( int i=0; i < NUM_THREADS; i++) {
			threads[i] = new WorkerThread();
			threads[i].start();
		}

		startLatch.countDown();
		startLatch.await();
		long start = System.nanoTime();
		
		for( int i=0; i < NUM_THREADS; i++) {
			threads[i].join();
		}
		long end = System.nanoTime();
		long duration = end - start;

		System.out.println( "Took " + TimeUnit.NANOSECONDS.toMillis( duration ) + " ms" );
		Assert.assertEquals(taskCount.get(), throughputGauge.getTotalCount());
		System.out.println("Time per ping: " + duration / taskCount.get() + " ns");
		
		long startTime = this.throughputGauge.getStartTime();
		long nextBucketTime = this.throughputGauge.getNextBucketTime();
		long period = nextBucketTime - startTime;
		long remainder = period % TimeUnit.SECONDS.toNanos(1);
		Assert.assertEquals(remainder, 0,
				"nextBucketTime must only be increased by full multiples of granularity!");
	}

	private class WorkerThread extends Thread {

		@Override
		public void run() {
			taskCount.getAndAdd(NUM_TASKS_PER_THREAD);
			startLatch.countDown();
			try {
				startLatch.await();
			} catch (InterruptedException e) {
				Assert.fail( e.toString() );
			}
			
			for( int i=0; i < NUM_TASKS_PER_THREAD; i++ ) {
				throughputGauge.ping();
			}
			
		}
		
	}
}
