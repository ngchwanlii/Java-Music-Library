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
	private JSONArray queryArray;
	
	
	// SearchQuery constructor
	public SearchQuery(String key, String query, JSONArray queryArray, ThreadSafeMusicLibrary threadSafeML, JSONArray searchResultArray){
		
		this.key = key;
		this.query = query;
		this.queryArray = queryArray;
		this.threadSafeML = threadSafeML;
		this.searchResultArray =  searchResultArray;
	
	}
	
	@Override
	public void run() {
		
		if(key.equals("searchByArtist")){
			
			searchArtist(key, query, queryArray);
			
		}		
		else if(key.equals("searchByTag")){
			
			searchTag(key, query, queryArray);
			
		}
		else if(key.equals("searchByTitle")){
			
			searchTitle(key, query, queryArray);
			
		}
	
		
	}
	// searchArtist method - use for searchResultArray to form all similarSong in a representation of JSONArray
	// detail: search on given query (-artist) -> retrieve list of song by given artist -> 
	// 		   -> each song in songList find it's similarSongList -> each song(in track_id) in similarSongList 
	//		   -> convert to songJSON and add to resultList
	public void searchArtist(String searchType, String query, JSONArray queryArray){
	
		// lock implemented in threadSafeML's class		
		JSONArray similarsSongJSONArray = threadSafeML.searchByArtist(query);
				
		// multiple thread that finished their search task will block here until they acquire the instrinsic lock - queryArray
		// sort inner array based on key - "similars"
		similarsSongJSONArray = sortByTrackID(similarsSongJSONArray, queryArray);
		
		// create new songJSONObject
		JSONObject songJSON = new JSONObject();
	
		// insert each similar songObject into resultList based on the queries' order
		insertByQueryOrder(searchType, query, queryArray, songJSON, similarsSongJSONArray);
		
	}

	// searchTitle method
	public void searchTitle(String searchType, String query, JSONArray queryArray){
		
		// lock implemented in threadSafeML's class		
		JSONArray similarsSongJSONArray = threadSafeML.searchByTitle(query);
				
		// multiple thread that finished their search task will block here until they acquire the instrinsic lock - queryArray
		// sort inner array based on key - "similars"
		similarsSongJSONArray = sortByTrackID(similarsSongJSONArray, queryArray);
		
		// create new songJSONObject
		JSONObject songJSON = new JSONObject();
		
		// insert each similar songObject into resultList based on the queries' order
		insertByQueryOrder(searchType, query, queryArray, songJSON, similarsSongJSONArray);
	
	}
	
	// searchTag method
	public void searchTag(String searchType, String query, JSONArray queryArray){
		
		// lock implemented in threadSafeML's class		
		JSONArray similarsSongJSONArray = threadSafeML.searchByTag(query);
				
		// multiple thread that finished their search task will block here until they acquire the instrinsic lock - queryArray
		// sort inner array based on key - "similars"
		similarsSongJSONArray = sortByTrackID(similarsSongJSONArray, queryArray);
		
		// create new songJSONObject
		JSONObject songJSON = new JSONObject();
		
		// insert each similar songObject into resultList based on the queries' order
		insertByQueryOrder(searchType, query, queryArray, songJSON, similarsSongJSONArray);
	
	}
	
	
	// sortByTrackID method - sort similar song array by track_id
	private JSONArray sortByTrackID(JSONArray similarsSongJSONArray, JSONArray queryArray){
		
		synchronized(queryArray){
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
		
	}

	// insertByQueryOrder - the resultList is inserting songJSONObject based on the queries' order
	private void insertByQueryOrder(String searchType, String query, JSONArray queryArray, JSONObject songJSON, JSONArray similarsSongJSONArray){
		
		// while still has queries processing
		while(queryArray.size() != 0){
			
			synchronized(queryArray){
				if(query.equals(queryArray.get(0))){	
					// if current query is in order, break out this loop
					break;					
				}
				else {
					try {
						// if current query is not in his order, waiting here
						queryArray.wait();
					} 
					catch (InterruptedException e) {						
						e.printStackTrace();
					}
				}
			}
		}
		
		// multi-thread may pass their query order check, example - searchByArtist thread/ searchyTitle thread/ searchByTag thread
		// implement synchronize block here so the thread that own this intrinsic lock can perform their task and call notify
		// if no synchronized block here - will cause IllegalMointorStateException		
		synchronized(queryArray){		
			
			if(searchType.equals("searchByArtist")){				
				songJSON.put("artist", query);					
				songJSON.put("similars", similarsSongJSONArray);	
			}
			else if(searchType.equals("searchByTitle")){
				songJSON.put("similars", similarsSongJSONArray);
				songJSON.put("title", query);
			}
			else if(searchType.equals("searchByTag")){
				songJSON.put("similars", similarsSongJSONArray);
				songJSON.put("tag", query);
			}
			searchResultArray.add(songJSON);	
			
			// remove the 1st element in the queryArray, so it can go in order
			queryArray.remove(0);
			// notify the waiting thread above
			queryArray.notifyAll();			
		}
		
			
	}
	

}
