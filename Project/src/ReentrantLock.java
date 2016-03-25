import java.util.HashMap;

public class ReentrantLock {

	// instance variable
	private HashMap<Thread, Integer> readThread;
	private HashMap<Thread, Integer> writeThread;

	// ReentrantLock constructor
	public ReentrantLock() {
		this.readThread = new HashMap<Thread, Integer>();		
		this.writeThread = new HashMap<Thread, Integer>();
		
	}

	
	// check if current thread hold a read lock or not
	// true - if the invoking thread holds a read lock
	// false - otherwise
	public synchronized boolean hasRead() {
		
		// consider if other thread try to invoke hasRead, but don't have a readLock (null one), return false
		// since we use HashMap, if this thread is not inside HashMap, mean they don't has read access
		Thread callingThread = Thread.currentThread();
		if(readThread.containsKey(callingThread)){
			return true;
		}
		
		// else
		return false;
		
		
	}

	// check if current thread hold a write lock
	// true - if the invoking thread holds a write lock
	// false - otherwise
	public synchronized boolean hasWrite() {
		
		// consider if other thread try to invoke hasWrite, but don't hold a writeLock, return false the write method can only access by one thread each time)
		// since we use HashMap, if this thread is not inside HashMap, mean they don't has write access
		Thread callingThread = Thread.currentThread();
		if(writeThread.containsKey(callingThread)){
			return true;
		}
		
		return false;
		
	}

	// tryLockRead() method - try to acquire a read lock
	// true if successfully acquire the readLock
	// false otherwise
	public synchronized boolean tryLockRead() {
		
		Thread callingThread = Thread.currentThread();
		boolean sameThreadHoldWriteLock = writeThread.containsKey(callingThread);
		
		// if 	T1 do:
		// 		write  -> read, this read can get a tryLockRead()
		// (the thread hold the writeLock do read operation is allowed)
	
		// if 	T2 do:
		//		read -> but T1 has write, it cannot gain the tryLockRead() 
		if(!sameThreadHoldWriteLock && writeThread.size() > 0)
			return false;
				
		// else update the acquire readLock and return true
		// initialize writeThread, set integer to 0
		if(readThread.get(callingThread) == null){
			readThread.put(callingThread, 0);
		}
		
		// update readLockCount number
		int readLock = readThread.get(callingThread) + 1;				
		readThread.put(callingThread, readLock);		
		return true;
	}

	
	// tryLockWrite() - attempt to acquire write lock
	// true - if successfully acquire
	// false - otherwise
	public synchronized boolean tryLockWrite() {
		
		Thread callingThread = Thread.currentThread();
		boolean sameThreadHoldWrite = writeThread.containsKey(callingThread);
		
		// we can try to acquire a writeLock if there is no writeThread && readThread, except if the thread itself already hold a writeLock, so multiple writeLock is allow for same thread as long as not after read-write process
		// modified
		if( (writeThread.size() > 0 || readThread.size() > 0) && !sameThreadHoldWrite)
			return false;
		
		// else update the acquire writeLock and return true
		// initialize writeThread, set integer to 0
		if(writeThread.get(callingThread) == null){
			writeThread.put(callingThread, 0);
		}
		
		// update writeLockCount number
		int writeLock = writeThread.get(callingThread) + 1;				
		writeThread.put(callingThread, writeLock);
		return true;
	}

	// Blocking method that will return only when the read lock has been acquired.  
	public synchronized void lockRead() {
				
		// read - write , cannot!
		// a thread holding a write lock may also acquire the read lock (the same and only the one write thread), but not other thread!
		// writeThead can only hold 1 key at any time, only 1 thread can access writeLock each time, rest of thread that attempt to access has to wait
		// if the other thread (say's T2) which previously waiting at here (T1 write, T2 read, but there are not same thread, so T2 can't read and need to wait at this point), the unlockWrite() will notify this T2 and it is ready to read next time.
		// the only exception is a thread which hold a writeLock, can acquire the readLock, but not another side
		// 1. T1 threads -> write/read -> OKAY
		// 2. T1 threads -> read/write -> CANNOT ALLOW THIS!
		// 3. T2 threads -> reading... ops T1 has writeLock, T2 should wait
		// 4. writeThread.size() > 0, here it is to detect if the "another waited" thread should awake. If the writeLock has unlocked, 
		// if it is unlocked, notify the T2 threads which is waiting/monitor this, since writeThread.size() = 0 > 0 (false), 
		// T2 thread can acquire readLock again
		
		// Modified, reuse tryLockRead()
		// if it fail to get a tryLockRead(), this will return !false = true, and the thread will wait
		while(!tryLockRead()){  // added writeThread.size() > 0 03/01/2016
 			try {
				wait();
			} catch (InterruptedException e) {			
				e.printStackTrace();
				
			}
		}
		
		// else, tryLockRead has already update the thread
		// do nothing here
		
		
	}

	// Releases the read lock held by the calling thread. Other threads may continue to hold a read lock.	 
	public synchronized void unlockRead() {
		
		Thread callingThread = Thread.currentThread();
		
		// if other thread intend to unlockRead() method but don't hold a readLock, do nothing
		if(readThread.get(callingThread) != null){
			int releaseReadLock = readThread.get(callingThread) - 1;
			
			if(releaseReadLock == 0){
				// fully released the writeLock, remove the thread in writeThread
				readThread.remove(callingThread);
				// awake waiting thread
				notifyAll();
			}
			else {
				// update the released read lock				
				readThread.put(callingThread, releaseReadLock);
			}
			
		}
		
		
	}

	// Blocking method that will return only when the write lock has been acquired.
	public synchronized void lockWrite() {
		
		// only one thread may hold the write lock at any time
		// a thread holding a read lock may not upgrade to the write lock
		
		// Modified logic - if I can't acquire a writeLock, this will !false = true, the thread wait
		while(!tryLockWrite()){
			try {
				wait();
			} catch (InterruptedException e) {				
				e.printStackTrace();
				
			}
		}
				
	
	}

	// Releases the write lock held by the calling thread. The calling thread may continue to hold a read lock.
	public synchronized void unlockWrite() {
		
		Thread callingThread = Thread.currentThread();
	
		// if other thread intend to unlockWrite() method but don't hold a writeLock, do nothing
		if(writeThread.get(callingThread) != null){
			int releaseWriteLock = writeThread.get(callingThread) - 1;
			
			if(releaseWriteLock == 0){
				// fully released the writeLock, remove the thread in writeThread
				writeThread.remove(callingThread);
				notifyAll();
			}
			else {
				// update the released write lock
				writeThread.put(callingThread, releaseWriteLock);
			}
			
		}
		
		
	}
}