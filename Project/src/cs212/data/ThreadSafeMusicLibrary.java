package cs212.data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import cs212.util.concurrent.ReentrantLock;

public class ThreadSafeMusicLibrary extends MusicLibrary {

	// instance variable
	private ReentrantLock lock;
	
	// ThreadSasfeMusicLibrary's constructor - for web search
	public ThreadSafeMusicLibrary(String musicLibrary_database){
		
		super(musicLibrary_database);
		this.lock = new ReentrantLock();

	}
	

	// ThreadSafeMusicLibrary's constructor - normal / multi-thread / with search function 
	public ThreadSafeMusicLibrary(String inputStringPath, String outStringPath) {
		super(inputStringPath, outStringPath);
		this.lock = new ReentrantLock(); 
	}
	
	@Override
	// searchByArtist method
	public JSONArray searchByArtist(String query){
		
		// should change to readLock
		// use try finally block to unlock		
		lock.lockRead();
		try{
			return super.searchByArtist(query);
		}
		finally {
			lock.unlockRead();
		}
			
	}
	
	@Override
	// searchByTitle method
	public JSONArray searchByTitle(String query){
		
		// TODO: should change to readLock
		// use try finally block to unlock		
		lock.lockRead();
		try{
			return super.searchByTitle(query);	
		}
		finally {
			lock.unlockRead();
		}
		
		
	}
	
	@Override
	// searchByTag method
	public JSONArray searchByTag(String query){
		
		// should change to readLock
		// use try finally block to unlock
		lock.lockRead();
		try {
			return super.searchByTag(query);
		}
		finally {
			lock.unlockRead();
		}
		
		
	}

	
	@Override
	// addSong is a write operation, (each song that has to parse need to be update to the data structure)
	public void addSong(Song song){
		
		// need to acquire writeLock first to write 
		lock.lockWrite();	
		// calling to super class - addSong's method
		super.addSong(song);
		// unlock writeLock after finish writing.
		lock.unlockWrite();
			
	} 
	
	@Override	
	// writeToTextFile is a read operation (nothing will be update to the data structure)
	public void writeToTextFile(String order) throws IllegalArgumentException{ 
		
		// need to acquire readLock first to read
		lock.lockRead();		
		// calling to super class - writeToTextFile's method
		super.writeToTextFile(order);
		// unlock readLock after finish writing.
		lock.unlockRead();
	
	}
	
}
