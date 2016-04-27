import java.util.LinkedList;

/**  ThreadPool class - 
 * Procedure: 
 * 1. assign task into work-queue -> 
 * 2. when there is task in queue, PoolWorkerThread will run the task
 * if it is empty, PoolWorkerThread will wait for the task to be added into queue
 * 
 * Design:
 * 0. instantiate numbers of threads based on value stored in the HashMap: key [-threads] <--> value [numThreads] 
 * 1. implement work queue (put task into queue)
 * 2. execute() method
 * 3. shutdown() method
 * 4. awaitTermination
 * 5. Inner private class PoolWorker() implements Runnable - (worker threads will run the task when it is in queue and queue is not empty) 
 */
 
public class ThreadPool {
	
	// instance variable
	private final int numThreads;
	private final PoolWorkerThread[] threads;
	private final LinkedList<Runnable> taskQueue;
	
	//Extra Self-Concept Test: 
	// Q if I modified this as volatile, should I use synchronize block below (synchronize shutDown())? 
	// Ans: don't need synchronized block if this variable is volatile
	private volatile boolean shutDownActive = false; 
	
	// ThreadPool constructor
	public ThreadPool(int numThreads){
		
		this.numThreads = numThreads;
		taskQueue = new LinkedList<Runnable>();
		threads = new PoolWorkerThread[numThreads];
		
		// call start method for each worker threads
		// if taskQueue is empty, worker thread will standby and wait for new task arrive
		// if there is queue, worker thread pick up and execute the task
		for(int i = 0; i < numThreads; i++){
			// instantiate each worker thread
			threads[i] = new PoolWorkerThread();
			// invoke the run method in inner PoolWorkerThread class 
			threads[i].start();
		}
				
		
	}
	
	// execute method - assigned new task into queue
	public void execute(Runnable r){
		
		// if shutDownStatus is not active - not true (if not invoked shutDown() method)- continue to add task into queue
		// else, stop adding task to the queue
		if(!shutDownActive){
			synchronized(taskQueue){
				taskQueue.add(r);
				// use notify here in this case, since the execution of task is per thread per task
				// use notify here because it cause fewer context switch
				taskQueue.notify();
			}
		}
	}
	
	// shutdown method - when invoking shutdown, no more task can be added into queue
	// previously added task can still be executed by worker threads
	public void shutDown(){
		this.shutDownActive = true;
		
		// notify all waiting thread to quit
		// need to use synchronized have to acquire taskQueue lock to notifyAll()
		
		// Details: The awakened threads will not be able to proceed until the current thread relinquishes the lock on this object. 
		// The awakened threads will compete in the usual manner with any other threads that might be actively competing to synchronize on this object;
		// the thread that WIN the race can re 
						 	
		// This method should only be called by a thread that is the owner of this object's monitor. A thread becomes the owner of the object's monitor in one of three ways:
		// 1. By executing a synchronized instance method of that object.
		// 2. By executing the body of a synchronized statement that synchronizes on the object.    <---- synchronized(taskQueue)
		// 3. For objects of type Class, by executing a synchronized static method of that class.
		// Only one thread at a time can own an object's monitor.

		// Throws: IllegalMonitorStateException - if the current thread is not the owner of this object's monitor.
		synchronized(taskQueue){
			taskQueue.notifyAll();
			
		} 
		
	}
	
	// awaitTermination method - block all thread and wait for the worker thread's execution to finish
	public void awaitTermination(){
		
		// if shutDown() methods have been invoked previously
		if(shutDownActive){
			
			// wait for all running threads to join here
			for(PoolWorkerThread thread: threads){
				try{
					thread.join();
				}
				catch (InterruptedException e){
					e.printStackTrace();
				}
			}
			
		}
		else {
			System.out.println("Should invoke shutDown() method before awaitTermination()");
		}
		
	}
	
	
	// inner PoolWorkerThreads class
	// use PoolWorkerThreads extends Threads() here, since PoolWorkerThread class has been designed soley for one purpose - 
	// worker threads run the task, therefore extends Threads().
	// in general, it is better to implements Runnable, this allow the flexibility to add many other functionality to the class, (implements other interface)
	// while by extending Thread, you can only inherits the behavior of the class, by changing the behavior, you need to override the method of the class.
	private class PoolWorkerThread extends Thread{
		

		// the run method for PoolWorkerThread
		public void run(){
			
			Runnable r;
					 
			while(true){
				
				// intrinsic lock on - taskQueue -> execute each task for each thread per time, no 2 other threads can execute task simultaneously
				synchronized(taskQueue){
					
					// if taskQueue is empty, worker thread have to wait (when calling wait, it give up the ownership of the lock and wait here)
					// therefore when invoking execute method in Driver class, the thread can acquire the "taskQueue intrinsic lock", add task into queue, and 
					// notify/awake this waiting threads			
					while(taskQueue.isEmpty() && !shutDownActive){
						try {
							
							// if taskQueue empty, wait here
							// cond 1: notify(), one of the thread will be awake, use when execute assign new task into queue, only 1 thread can acquire lock each time. Other thread have to wait
							// cond 2: got notifyAll(), this is call when shutDown is invoked, all previously waiting thread need to wake up and check the condition. 
							// (This is in race condition, the thread need to re-acquire the lock after .notify() / .notifyAll() is invoked)							
							// if taskQueue is empty and shutdownActive, it should break out the loop instead of waiting here
							// if taskQueue is empty BUT shutDown is not active, it should wait here until shutDown method is invoked							
							taskQueue.wait();
						}
						catch (InterruptedException e){
							// when thread interrupted while waiting, it will throw InterruptedException						
							e.printStackTrace();							
						}
					}
					
					// if there is no task and shutDownActive = true
					if(taskQueue.isEmpty() && shutDownActive){
						// break out of loops
						break;
					}
					
					// check 
					// 1. if thread is interrupted AND queue is empty, this thread can quit the outermost loop, without entering the if statement
					// 2. if thread is interrupted BUT queue is not empty, the worker need to finish the task before it leave the outermost loop					
					// remove the first element in the LinkedList queue
					r = (Runnable) taskQueue.removeFirst();											
				}
				
				// need to catch RuntimeException - avoid pool leak threads
				try {
					// worker threads pick up the task from the queue, and run the specific task
					// will only run when taskQueue has job, if taskQueue is 'empty' will be waiting in a while loop above
					r.run();
					
				}
				catch (RuntimeException e){
					e.printStackTrace();
				}						
			}
				
		}
		
	} 
	
}
