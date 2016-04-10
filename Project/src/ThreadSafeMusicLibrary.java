import org.json.simple.JSONArray;

public class ThreadSafeMusicLibrary extends MusicLibrary {

	// instance variable
	private ReentrantLock lock;
	private Object artistLock;
	private Object titleLock;
	private Object tagLock;
	
	// ThreadSafeMusicLibrary's constructor - with search function
	public ThreadSafeMusicLibrary(String inputStringPath, String outputStringPath, String searchInputPath, String searchOutputPath){
		
		super(inputStringPath, outputStringPath, searchInputPath, searchOutputPath);
		this.lock = new ReentrantLock();
		this.artistLock = new Object();
		this.titleLock = new Object();
		this.tagLock = new Object();
		
	}
	
	
	// ThreadSafeMusicLibrary's constructor  
	public ThreadSafeMusicLibrary(String inputStringPath, String outStringPath) {
		super(inputStringPath, outStringPath);
		this.lock = new ReentrantLock(); 
	}
	
	@Override
	public JSONArray searchByArtist(String query){
		
		synchronized(artistLock){
			
			return super.searchByArtist(query);
			
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
