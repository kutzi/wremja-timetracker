package de.kutzi.javautils.concurrent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kutzi
 */
public class BlockingThreadPoolExecutorTest {

	private static final int numberOfTasks = 9876;

    private final AtomicLong counter = new AtomicLong();
    private final Set<Long> results = Collections.synchronizedSet( new HashSet<Long>() );
	private BlockingThreadPoolExecutor executor;
	
	
    public BlockingThreadPoolExecutorTest() {
    }

    @Before
    public void setUp() {
        counter.set(0);
        results.clear();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of execute method, of class BlockingThreadPoolExecutor.
     * @throws InterruptedException 
     */
    @Test
    public void testExecutesAllTasks() throws InterruptedException {
        int numberOfThreads = 10;
        
        executor = new BlockingThreadPoolExecutor( numberOfThreads );

        long start = System.nanoTime();
        Thread adder = new Thread( new TaskAdderRunnable() );
        adder.start();
        adder.join();
        
        executor.shutdown();
        assertTrue( "executor didn't finish within 10 seconds",
        		executor.awaitTermination( 10, TimeUnit.SECONDS ) );
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