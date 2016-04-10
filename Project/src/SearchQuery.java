import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SearchQuery implements Runnable {

	
	private String key;
	private String query;
	// share data structure
	private ThreadSafeMusicLibrary threadSafeML;
	private JSONArray searchResultArray; 
	private ReentrantLock lock;
	
	// SearchQuery constructor
	public SearchQuery(String key, String query, ThreadSafeMusicLibrary threadSafeML, JSONArray searchResultArray, ReentrantLock lock){
		
		this.key = key;
		this.query = query;
		this.threadSafeML = threadSafeML;
		this.searchResultArray =  searchResultArray;
		this.lock = lock;
	
		
	}
	
	@Override
	public void run() {
		
		if(key.equals("searchByArtist")){
			
			searchArtist(query, lock);
			
		}
		
		
	}
	
	
	public void searchArtist(String query, ReentrantLock artistLock){
	
		// lock implemented in threadSafeML's class
		JSONArray similarsSongJSONArray = threadSafeML.searchByArtist(query);
		
		artistLock.lockWrite();
		
		/** jay - DEBUG Print **/
		System.out.println("QUERY: " + query);
		System.out.println(similarsSongJSONArray);
		
		JSONObject songJSON = new JSONObject();
		songJSON.put("artist", query);
		songJSON.put("similars", similarsSongJSONArray);
		searchResultArray.add(songJSON);
		artistLock.unlockWrite();
		
		
		
	}
	

}
