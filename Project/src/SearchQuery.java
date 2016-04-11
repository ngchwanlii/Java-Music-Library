import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
		else if(key.equals("searchByTag")){
			
			searchTag(query, lock);
			
		}
		else if(key.equals("searchByTitle")){
			
			searchTitle(query, lock);
			
		}
	
		
	}
	
	
	// sortByTrackID method - sort similar song array by track_id
	public synchronized JSONArray sortByTrackID(JSONArray similarsSongJSONArray ){
		
		// sort JSONArray based on trackID
		JSONArray sortJSONArray = new JSONArray();
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		
		for(int i = 0; i < similarsSongJSONArray.size(); i++){
			jsonList.add((JSONObject) similarsSongJSONArray.get(i));
		}
		Collections.sort(jsonList, new TrackIDComparator());
		
		for(int i = 0; i < similarsSongJSONArray.size(); i++){
			sortJSONArray.add(jsonList.get(i));			
		}
		return sortJSONArray;
		
	}
	
	// searchArtist method - use for searchResultArray to form all similarSong in a representation of JSONArray
	// detail: search on given query (-artist) -> retrieve list of song by given artist -> 
	// 		   -> each song in songList find it's similarSongList -> each song(in track_id) in similarSongList 
	//		   -> convert to songJSON and add to resultList
	public void searchArtist(String query, ReentrantLock artistLock){
	
		// lock implemented in threadSafeML's class
		
		JSONArray similarsSongJSONArray = threadSafeML.searchByArtist(query);

		
		// multiple thread that finished their search task will block here until they acquire the lock 
		
		// acquiring lock
		artistLock.lockWrite();	
		similarsSongJSONArray = sortByTrackID(similarsSongJSONArray);
		/** jay - DEBUG Print **/
//		System.out.println("QUERY: " + query);
//		System.out.println(similarsSongJSONArray);
		
		JSONObject songJSON = new JSONObject();
		songJSON.put("artist", query);
		songJSON.put("similars", similarsSongJSONArray);
		searchResultArray.add(songJSON);
		artistLock.unlockWrite();
	
	}
	
	// searchTitle method
	public void searchTitle(String query, ReentrantLock titleLock){
		
		// lock implemented in threadSafeML's class
		
		JSONArray similarsSongJSONArray = threadSafeML.searchByTitle(query);
		
		
		// multiple thread that finished their search task will block here until they acquire the lock 
		
		// acquiring lock
		titleLock.lockWrite();
		similarsSongJSONArray = sortByTrackID(similarsSongJSONArray);
		/** jay - DEBUG Print **/
//		System.out.println("QUERY: " + query);
//		System.out.println(similarsSongJSONArray);
		
		JSONObject songJSON = new JSONObject();		
		songJSON.put("similars", similarsSongJSONArray);
		songJSON.put("title", query);
		searchResultArray.add(songJSON);		
		titleLock.unlockWrite();
	
	}
	
	
	// searchTitle method
	public void searchTag(String query, ReentrantLock tagLock){
		
		// lock implemented in threadSafeML's class
		JSONArray similarsSongJSONArray = threadSafeML.searchByTag(query);
		
		
		// multiple thread that finished their search task will block here until they acquire the lock
		 		
		// acquiring lock
		tagLock.lockWrite();
		similarsSongJSONArray = sortByTrackID(similarsSongJSONArray);
		/** jay - DEBUG Print **/
//		System.out.println("QUERY: " + query);
//		System.out.println(similarsSongJSONArray);
		
		JSONObject songJSON = new JSONObject();		
		songJSON.put("similars", similarsSongJSONArray);
		songJSON.put("tag", query);
		searchResultArray.add(songJSON);		
		tagLock.unlockWrite();
	
	}
	
	
	
	
	
	

}
