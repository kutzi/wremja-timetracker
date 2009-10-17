package de.kutzi.javautils.concurrent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Demonstrates that under rare conditions the last task in a {@link NotifyingBlockingThreadPoolExecutor}
 * may 'starve' i.e. never been executed.
 */
public class NotifyingBlockingThreadPoolExecutorErrorTest {

    private final AtomicLong counter = new AtomicLong();
    private final Set<Long> results = Collections.synchronizedSet( new HashSet<Long>() );
	private NotifyingBlockingThreadPoolExecutor executor;

	
	private static final CountDownLatch taskRunnerLatch = new CountDownLatch(1);
	
    public NotifyingBlockingThreadPoolExecutorErrorTest() {
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

        System.out.println( System.getProperty("java.vm.name") + " " + System.getProperty("java.runtime.version"));
        int numberOfThreads = 1;
        int workQueueSize = 2;
        executor = new NotifyingBlockingThreadPoolExecutor(
                numberOfThreads, workQueueSize, 100, TimeUnit.MILLISECONDS );

        for( int i=0; i < numberOfThreads + workQueueSize; i++ ) {
            Runnable task = new MyRunnable();
            executor.execute(task);
        }
        // all worker threads should be busy now and work queue should be full now
        
        // block BlockThenRunPolicy in ThreadPool
        NotifyingBlockingThreadPoolExecutor.semaphoreForTesting.acquire();
        
        // add another task: total number of tasks must be now numberOfThreads + workQueueSize + 1
        new Thread( new LastTaskSubmitter() ).start();
        
        // sleep: let LastTaskSubmitter continue until it blocks
        Thread.sleep(1000);
        
        // let tasks continue => empty work queue 
        taskRunnerLatch.countDown();
        
        // wait until all tasks but the last are finished
        while( results.size() < numberOfThreads + workQueueSize ) {
        	Thread.sleep( 1000 );
        }
        
        // let BlockThenRunPolicy continue: last task is now added to workqueue
        NotifyingBlockingThreadPoolExecutor.semaphoreForTesting.release();

        // let executor some time to pickup the last task
        Thread.sleep(2000);
        
        assertEquals(numberOfThreads + workQueueSize + 1, results.size());
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
        	try {
				taskRunnerLatch.await();
			} catch (InterruptedException e) {
				fail( e.toString() );
			}
            results.add( Long.valueOf( counter.getAndIncrement() ));
        }
    }

    private class LastTaskSubmitter implements Runnable {

		@Override
		public void run() {
			try {
				Runnable task = new MyRunnable();
		        executor.execute(task);
			} catch (Exception e) {
				fail( e.toString() );
			}
		}
    	
    }
    
}