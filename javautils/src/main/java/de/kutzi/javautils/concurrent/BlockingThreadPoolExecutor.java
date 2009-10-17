package de.kutzi.javautils.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A blocking {@link ThreadPoolExecutor} as suggested by Brian Goetz.
 * 
 * See
 * <a href="http://today.java.net/pub/a/today/2008/10/23/creating-a-notifying-blocking-thread-pool-executor.html">here</a>
 * (3rd suggested solution)
 */
public class BlockingThreadPoolExecutor
    extends ThreadPoolExecutor { 

    private final Semaphore semaphore;

    /**
     * {@inheritDoc}
     */
    public BlockingThreadPoolExecutor( int poolSize ) {
    	/* Note the subtle impl. details here:
    	 * - using a corePoolSize doesn't work as pool size would never grow!
    	 * - workingQueue must have a size >> 1
    	 *  e.g. 6 was working for me for a poolSize of 10, but setting it to poolsize
    	 *  should satisfy the theoretical limit!?
    	 *  The reason is IMHO that there is a small, but >0 time span between releasing the semaphore
    	 *  and when the corresponding worker thread becomes available for the threadpool again.
    	 */
        super( poolSize, poolSize, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>( poolSize ) );
        this.semaphore = new Semaphore( poolSize );
    }

    /**
     * Not implemented in this class.
     * Please, use the {{@link #executeOrBlock(Runnable)} or
     * {{@link #executeOrBlock(Runnable, long, TimeUnit)} method instead!
     */
    @Override
    public void execute(Runnable task) {
    	throw new UnsupportedOperationException( "Please use one of the executeOrBlock methods!" );
    }

    /**
     * Executes the given task sometime in the future.  The task
     * may execute in a new thread or in an existing pooled thread.
     *
     * If the task cannot be submitted for execution, because this
     * executor has reached its capacity, this call blocks.
     *
     * If the task cannot be submitted for execution, because this
     * executor has been shutdown,
     * the task is handled by the current <tt>RejectedExecutionHandler</tt>.
     *
     * @param task the task to execute
     * @throws InterruptedException if the current thread is interrupted
     * @throws RejectedExecutionException at discretion of
     * <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     * for execution
     * @throws NullPointerException if command is null
     */
    public void executeOrBlock(Runnable task) throws InterruptedException {
        semaphore.acquire();

        try {
            super.execute(task);
        } catch(RuntimeException e) {
            // specifically, handle RejectedExecutionException
            semaphore.release();
            throw e;
        } catch(Error e) {
            semaphore.release();
            throw e;
        }
    }

    /**
     * Like {{@link #executeOrBlock(Runnable)} but waits only up to <tt>timeout</tt>
     * before giving up trying to enqueue the <tt>task</tt>.
     * 
     * @param task the task to execute
     * @throws InterruptedException if the current thread is interrupted
     * @throws RejectedExecutionException at discretion of
     * <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     * for execution
     * @return <tt>true</tt> if the task was successfully enqueued,
     *   <tt>false</tt> if enqueuing failed because of timeout.
     */
    public boolean executeOrBlock(Runnable task, long timeout, TimeUnit unit ) throws InterruptedException{
        boolean acquired = semaphore.tryAcquire( timeout, unit );
        if( !acquired ) {
        	return false;
        }

        try {
            super.execute(task);
            return true;
        } catch(RuntimeException e) {
            // specifically, handle RejectedExecutionException
            semaphore.release();
            throw e;
        } catch(Error e) {
            semaphore.release();
            throw e;
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        semaphore.release();
    }

    /**
     * Unsupported operation
     */
	@Override
	public void setCorePoolSize(int corePoolSize) {
		throw new UnsupportedOperationException( "Changing the pool size during runtime is not" +
		" supported by this executor!" );
	}

	/**
	 * Unsupported operation
	 */
	@Override
	public void setMaximumPoolSize(int poolSize) {
		throw new UnsupportedOperationException( "Changing the pool size during runtime is not" +
				" supported by this executor!" );
        /*final ReentrantLock mainLock = this.mainLock ;
        mainLock.lock();
        try {
        	int currentPoolSize = getCorePoolSize();
    		if( poolSize == currentPoolSize ) {
    			return;
    		}

    		super.setCorePoolSize(poolSize);
    		super.setMaximumPoolSize(poolSize);
    		
    		// should resize the queue, also, but thats not possible!
    		
    		int difference = poolSize - currentPoolSize;
    		if( difference > 0 ) {
    			this.semaphore.release( difference );
    		} else {
    			boolean acquired = false;
    	        do {
    	            try {
    	                semaphore.acquire( -difference );
    	                acquired = true;
    	            } catch (InterruptedException e) {
    	                // wait forever!
    	            }
    	        } while(!acquired);
    		}
        } finally {
            mainLock.unlock();
        }*/
	}

    
}

