package de.kutzi.javautils.concurrent;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 *
 * @author kutzi
 */
@Test(groups="stress")
public class NBThreadPoolExecutorMultithreadedTest {

    private final AtomicLong counter = new AtomicLong();
    private final Set<Long> results = Collections.synchronizedSet( new HashSet<Long>() );
	private NotifyingBlockingThreadPoolExecutor executor;
	private static int numberOfPoolThreads = 1;
	private static int numberOfFillerThreads = 500;
	private static int numberOfTasksPerThread = 123;

    public NBThreadPoolExecutorMultithreadedTest() {
    }

    @BeforeTest
    public void setUp() {
        counter.set(0);
        results.clear();
    }

    @AfterTest
    public void tearDown() {
    }

    /**
     * Test of execute method, of class BlockingThreadPoolExecutor.
     * @throws InterruptedException 
     */
    @Test
    public void testExecutesAllTasks() throws InterruptedException {
        
        
        executor = new NotifyingBlockingThreadPoolExecutor(
                numberOfPoolThreads, numberOfPoolThreads * 2, 10, TimeUnit.SECONDS );

        long start = System.nanoTime();
        
        Thread[] fillers = new Thread[ numberOfFillerThreads ];
        for( int i=0; i < numberOfFillerThreads; i++ ) {
        	fillers[i] = new Thread( new FillerRunnable() );
        	fillers[i].start();
        }
        
        for( int i=0; i < numberOfFillerThreads; i++ ) {
        	fillers[i].join();
        }

        executor.shutdown();
        assertTrue( executor.awaitTermination( 10, TimeUnit.SECONDS ), "executor didn't finish within 10 seconds" );
        
        long end = System.nanoTime();
        long secs = TimeUnit.SECONDS.convert(end - start, TimeUnit.NANOSECONDS);
        System.out.println( "took " + secs + " seconds");
        
        assertEquals(numberOfTasksPerThread * numberOfFillerThreads, results.size());
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            results.add( Long.valueOf( counter.getAndIncrement() ));
            try {
				TimeUnit.MILLISECONDS.sleep( 0 );
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            //System.out.println(executor.getPoolSize());
            //System.out.println( executor.getActiveCount() );
        }
    }

    private class FillerRunnable implements Runnable {
    	@Override
        public void run() {
    		for( int i=0; i < numberOfTasksPerThread; i++ ) {
                Runnable task = new MyRunnable();
                executor.execute(task);
            }
    	}
    }
    
}