package de.kutzi.javautils.concurrent;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

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
public class BlockingThreadPoolExecutorTest {

	private static final int numberOfTasks = 9876;

    private final AtomicLong counter = new AtomicLong();
    private final Set<Long> results = Collections.synchronizedSet( new HashSet<Long>() );
	private BlockingThreadPoolExecutor executor;
	
	
    public BlockingThreadPoolExecutorTest() {
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
    public void testExecutesAllTasks() throws InterruptedException {
        int numberOfThreads = 10;
        
        executor = new BlockingThreadPoolExecutor( numberOfThreads );

        long start = System.nanoTime();
        Thread adder = new Thread( new TaskAdderRunnable() );
        adder.start();
        adder.join();
        
        executor.shutdown();
        assertTrue( executor.awaitTermination( 10, TimeUnit.SECONDS ), "executor didn't finish within 10 seconds" );
        long end = System.nanoTime();
        
        long secs = TimeUnit.SECONDS.convert(end - start, TimeUnit.NANOSECONDS);
        System.out.println( "took " + secs + " seconds");
        assertEquals(numberOfTasks, results.size());
    }

    
    private class TaskAdderRunnable implements Runnable {
    	@Override
        public void run() {
            for( int i=0; i < numberOfTasks; i++ ) {
                Runnable task = new MyRunnable();
                try {
					executor.executeOrBlock(task);
				} catch (InterruptedException e) {
					fail( e.toString() );
				}
            }
    	}
    }
    
    private class MyRunnable implements Runnable {

        @Override
        public void run() {
            results.add( Long.valueOf( counter.getAndIncrement() ));
            try {
				TimeUnit.MILLISECONDS.sleep( 20 );
			} catch (InterruptedException e) {
				fail( e.toString() );
			}
            //System.out.println(executor.getPoolSize());
            //System.out.println( executor.getActiveCount() );
        }

    }

}