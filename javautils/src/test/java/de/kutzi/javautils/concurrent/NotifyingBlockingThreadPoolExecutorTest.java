package de.kutzi.javautils.concurrent;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 *
 * @author kutzi
 */
@Test(groups="stress")
public class NotifyingBlockingThreadPoolExecutorTest {

    private final AtomicLong counter = new AtomicLong();
    private final Set<Long> results = Collections.synchronizedSet( new HashSet<Long>() );
	private NotifyingBlockingThreadPoolExecutor executor;

    public NotifyingBlockingThreadPoolExecutorTest() {
    }

    @BeforeTest
    public void setUp() {
        counter.set(0);
        results.clear();
    }

    /**
     * Test of execute method, of class BlockingThreadPoolExecutor.
     * @throws InterruptedException 
     */
    public void testExecutesAllTasks() throws InterruptedException {
        int numberOfThreads = 1;
        int numberOfTasks = 1000;
        executor = new NotifyingBlockingThreadPoolExecutor(
                numberOfThreads, numberOfThreads * 2, 10, TimeUnit.SECONDS );

        //Thread awaiter1 = new Thread( new AwaiterRunnable() );
        //awaiter1.start();
        //Thread awaiter2 = new Thread( new AwaiterRunnable() );
        //awaiter2.start();

        long start = System.nanoTime();
        for( int i=0; i < numberOfTasks; i++ ) {
            Runnable task = new MyRunnable();
            executor.execute(task);
        }

        executor.shutdown();
        assertTrue( executor.awaitTermination( 10, TimeUnit.SECONDS ), "executor didn't finish within 10 seconds" );
        
        long end = System.nanoTime();
        long secs = TimeUnit.SECONDS.convert(end - start, TimeUnit.NANOSECONDS);
        System.out.println( "took " + secs + " seconds");
        
        assertEquals(numberOfTasks, results.size());

        // Illustrates an issue in the timed await.
        // 2nd awaiter may never wake up
        //System.out.println("waiting for awaiter1");
        //awaiter1.join();
        //System.out.println("waiting for awaiter2");
        //awaiter2.join();
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

    private class AwaiterRunnable implements Runnable {

		@Override
		public void run() {
			try {
				boolean finished = false;
				//executor.await();
				while( !finished ) {
					executor.await(10, TimeUnit.MINUTES);
					finished = true;
				}
			} catch (Exception e) {
				fail( e.toString() );
			}
		}
    	
    }
    
}