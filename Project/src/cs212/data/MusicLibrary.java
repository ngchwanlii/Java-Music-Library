package cs212.data;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import cs212.util.comparator.*;
import database.DBHelper;


public class MusicLibrary {
	
	// instance variable
	private Path inputPath; 
	private Path outputPath;
	private TreeMap<String, TreeSet<Song>> artistMusicLibrary;
	private TreeMap<String, TreeSet<Song>> titleMusicLibrary;
	private TreeMap<String, ArrayList<Song>> tagMusicLibrary;	
	private TreeMap<String, Song> trackIDMusicLibrary;	
	private TreeSet<String> sortedArtistNameSet;
	
	/** for case insensitive search **/
	private Map<String, String> artistMap;
	private Map<String, String> titleMap;
	private Map<String, String> tagMap;
	
	
	// MusicLibrary constructor - for web search (multi-thread)
	public MusicLibrary(String musiclibrary_database){
		
		// Note: no output file created for web search
		// the output of search result will be handled by servlet that returning an html result page
		
		this.inputPath = Paths.get(musiclibrary_database);		 
		this.artistMusicLibrary = new TreeMap<String, TreeSet<Song>>();
		this.titleMusicLibrary = new TreeMap<String, TreeSet<Song>>(); 
		this.tagMusicLibrary = new TreeMap<String, ArrayList<Song>>();
		this.trackIDMusicLibrary = new TreeMap<String, Song>();
		
		
		
		/** Advance Feature **/ 
		this.sortedArtistNameSet = new TreeSet<String>();
		
		/** Case insensitve search **/
		artistMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		titleMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		tagMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		
	} 
	
	
	// MusicLibrary constructor - normal version / with search function test
	public MusicLibrary(String inputStringPath, String outStringPath){
				
		this.inputPath = Paths.get(inputStringPath);
		this.outputPath = Paths.get(outStringPath);
	
		// instantiate all type of MusicLibrary for artist, title and tag		
		this.artistMusicLibrary = new TreeMap<String, TreeSet<Song>>();
		this.titleMusicLibrary = new TreeMap<String, TreeSet<Song>>(); 
		this.tagMusicLibrary = new TreeMap<String, ArrayList<Song>>();		
		this.trackIDMusicLibrary = new TreeMap<String, Song>();
		
		/** Advance Feature **/ 
		this.sortedArtistNameSet = new TreeSet<String>();
		artistMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		titleMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		tagMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		
			
	}
	
	// searchByArtist method - return similarSongJSON based on query search in JSONArray
	public JSONArray searchByArtist(String query) {
		
		
		// ignore case and extract out the specific query
		query = artistMap.get(query);
		
		
		// thread-safe local variable
		// initialize resultList
		JSONArray resultList = new JSONArray();
		
		// if key is not contain in this map, return an empty resultList 
		if(!artistMusicLibrary.containsKey(query)){
			
			return resultList;
		}
				
		// list of songs by given artist
		TreeSet<Song> songs = artistMusicLibrary.get(query);
		
		
		
		// for each song in songs
		for(Song s : songs){
				
			// list of songs similar to song			 
			ArrayList<String> similarSongTrackID = s.getSimilars();
			
			// for each similarSong by track_ID
			if(!similarSongTrackID.isEmpty()){
				for(String trackID : similarSongTrackID){
					
					// get song by tracking id found in trackIDMusicLibrary
					if(trackIDMusicLibrary.containsKey(trackID)){
						Song song = trackIDMusicLibrary.get(trackID);
						// convert this songObj to songJSONObj
						JSONObject songJSONObj = convertToSongJSONObject(song);
						// if this resultList does not contain this songJSONObj, add it to resultList
						if(!resultList.contains(songJSONObj)){
							resultList.add(songJSONObj);
						}
					}					
				}								
			}					
		}
		return resultList;
	}
	
	// searchByArtist method - return similarSongJSON based on query search in JSONArray
	public JSONArray searchByTitle(String query) {
	
		// case insensitve search
		query = titleMap.get(query);
		
		
		// initialize resultList
		JSONArray resultList = new JSONArray();
		
		// if key is not contain in this map, return an empty resultList 		
		if(!titleMusicLibrary.containsKey(query)){
			
			return resultList;
		}
				
		// list of songs by given artist
		TreeSet<Song> songs = titleMusicLibrary.get(query);
		
		// for each song in songs
		for(Song s : songs){
				
			// list of songs similar to song			 
			ArrayList<String> similarSongTrackID = s.getSimilars();
			
			// for each similarSong by track_ID
			if(!similarSongTrackID.isEmpty()){
				for(String trackID : similarSongTrackID){
					
					// get song by tracking id found in trackIDMusicLibrary
					if(trackIDMusicLibrary.containsKey(trackID)){
						Song song = trackIDMusicLibrary.get(trackID);
						// convert this songObj to songJSONObj
						JSONObject songJSONObj = convertToSongJSONObject(song);
						// if this resultList does not contain this songJSONObj, add it to resultList
						if(!resultList.contains(songJSONObj)){
							resultList.add(songJSONObj);
						}
						
					}
						
				}								
			}
			
			
		}
		return resultList;
	}
	
	// searchByArtist method - return similarSongJSON based on query search in JSONArray
	public JSONArray searchByTag(String query) {
		
		// case insensitve search
		query = tagMap.get(query);
		
		// initialize resultList
		JSONArray resultList = new JSONArray();
		
		
		// if key is not contain in this map, return an empty resultList 		
		if(!tagMusicLibrary.containsKey(query)){
			
			return resultList;
		}
				
		// list of songs by given artist
		ArrayList<Song> songs = tagMusicLibrary.get(query);
		
		// for each song in songs
		for(Song song : songs){
				
			if(trackIDMusicLibrary.containsKey(song.getTrackID())){
				// convert this songObj to songJSONObj
				JSONObject songJSONObj = convertToSongJSONObject(song);
				// if this resultList does not contain this songJSONObj, add it to resultList
				if(!resultList.contains(songJSONObj)){
					resultList.add(songJSONObj);
				}				
			}			
		}
		
		return resultList;
	}
		
		
		
	
	
	// thread-safe - share function that could be use for searchByArtist/searchByTitle/searchByTag 
	private JSONObject convertToSongJSONObject(Song song){
		
		// thread-safe local variable that use for returning back to caller
		// caller is also a new local variable from its method		
		JSONObject songJSON = new JSONObject();
	
		songJSON.put("artist", song.getArtistName());
		songJSON.put("trackId", song.getTrackID());
		songJSON.put("title", song.getTitle());
		
		return songJSON;
		
		
	}
	
	// addSong method
	public void addSong(Song song){

		
		// artist					
		String artistName = song.getArtistName();
		// title
		String title = song.getTitle();
		// tag
		ArrayList<String> tagList = song.getTags();
		
		// track_id		
		String trackID = song.getTrackID();
		
		
		/** Advance Feature [Display artist name alpbaetically] **/
		this.sortedArtistNameSet.add(artistName);
		
		this.trackIDMusicLibrary.put(trackID, song);
		
		
		
		// if this is a new artist, create TreeSet to store these song
		if(!artistMusicLibrary.containsKey(artistName)){
			this.artistMusicLibrary.put(artistName, new TreeSet<Song>(new ArtistComparator()));
			
			// case insensitive search
			artistMap.put(artistName, artistName);
				
			
		}
		
		// else add to the created artistName data structure
		this.artistMusicLibrary.get(artistName).add(song);
		
		// if this is a new title, create TreeSet to store these song
		if(!titleMusicLibrary.containsKey(title)){
			
			this.titleMusicLibrary.put(title, new TreeSet<Song>(new TitleComparator()));
			
			// TODO: case insensitive search
			titleMap.put(title, title);
		}
		
		// else add to the created title data structure
		this.titleMusicLibrary.get(title).add(song);
		
		
		//  add tag to tagMusicLibrary
		for(int i = 0; i < tagList.size(); i++){
			
			String tag = tagList.get(i);
			
			// if this is a new tag, create TreeSet to store these song
			if(!tagMusicLibrary.containsKey(tag)){
				// natural ordering, don't have to create Tag Comparator
				this.tagMusicLibrary.put(tag, new ArrayList<Song>());
				
				// TODO: case insensitive search
				tagMap.put(tag, tag);
				
				
			}
			
			// no duplicate song can be added in orderType tag
			if(!this.tagMusicLibrary.get(tag).contains(song)) { 					
				this.tagMusicLibrary.get(tag).add(song);
			}
		
		}
		
		
		
		
	}
	
	// getTreeSet a list of sorted artistname alphabetically
	// make this thread safe, TreeSet is not thread safe but the element inside is a String type, string element inside is thread-safe 
	// caller who invoke this method need to ACQUIRE READ LOCK 	
	public TreeSet<String> getSortedArtistName(){
		
		// create a new TreeSet object for returning
		TreeSet<String> result = new TreeSet<String>();
		
		for(String str : this.sortedArtistNameSet){
			result.add(str);
		}
		
		return result;
		
	}
	
		
	// writeToTextFile method
	public void writeToTextFile(String order) throws IllegalArgumentException{
		
		TreeMap<String, TreeSet<Song>> tmpLibrary = null;
		TreeMap<String, ArrayList<Song>> tagTmpLibrary = null;
		
		
		// if inputPath is valid (exists), and outputPath parent directory
		if(inputPath.toFile().exists() && outputPath.toFile().getParentFile().isDirectory()){
			// write out path
			try(PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputPath, Charset.forName("UTF-8")))){

				
				// initialize library based on orderType
				if(order.equals("artist")){
					tmpLibrary = this.artistMusicLibrary;					
				}
				else if(order.equals("title")){
					tmpLibrary = this.titleMusicLibrary;
				}
				else if(order.equals("tag")){
					tagTmpLibrary = this.tagMusicLibrary;
				}
				
								
				// ready to print to textFile (for both artist or title)
				if(order.equals("artist") || order.equals("title")){
					for(String key : tmpLibrary.navigableKeySet()){
						
						TreeSet<Song> songs = tmpLibrary.get(key);
						
						for(Song song : songs){									
							// write to textFile
							writer.println(song.getArtistName() + " - " + song.getTitle());						
						}						
					}	
				}
				
				// ready to print to textFile (tag) 
				else if(order.equals("tag")){
					for(String key : tagTmpLibrary.navigableKeySet()){
						
						ArrayList<Song> songs = tagTmpLibrary.get(key);
						
						StringBuilder sb = new StringBuilder();
						sb.append(key + ": ");
						
						for(Song song : songs){
														
							sb.append(song.getTrackID() + " ");																					
						}
						writer.println(sb.toString());				
					}
				}
			}												
			catch (IOException e) {				
				System.out.println(e);
			}			
		}
		else {
			throw new IllegalArgumentException("\nAttempt to write textFile but the inputpath does not exists or outputpath's parent directory is not exists.");
		}
	}
	
	
	
	// return a new JSONArray which contain artistMusicLibrary Info
	public JSONObject getArtistMusicLibrary(){
		
		JSONObject artistMusicLibObj = new JSONObject();
		
		JSONArray artistArray = new JSONArray();
		
		for(String str : this.sortedArtistNameSet){
			artistArray.add(str);
		}
	
		
		artistMusicLibObj.put("artistMusicLibrary", artistArray);
		
		return artistMusicLibObj;
		
	}
	
	// return a new JSONArray which contain titleMusicLibrary Info
	public JSONObject getSongTitleMusicLibrary(){
		
		
		JSONObject titleMusicLibObj = new JSONObject();
		
		JSONArray songsArray = new JSONArray();
		
		for(Map.Entry<String, TreeSet<Song>> entry: this.titleMusicLibrary.entrySet()){
			
			String songTitle = entry.getKey();
			
			TreeSet<Song> songs = entry.getValue();
			
	
			for(Song song : songs){
				
				JSONObject innerObj = new JSONObject();
			
				String artistName = song.getArtistName();
				String songTrackID = song.getTrackID();
				
				// forming most inner jsonObj
				innerObj.put("songTitle", songTitle);	// # this is the song title we want - at key set
				innerObj.put("artistName", artistName);
				innerObj.put("trackID", songTrackID);
				
				// add to songsJSONArray
				songsArray.add(innerObj);
				
			}
		
			
		}
		
		// forming top level titleMusicLibObj
		titleMusicLibObj.put("artistMusicLibrary", songsArray);
		
		return titleMusicLibObj;
	
	}
	
	// get tag music libarry for DBHelper to add into mySQL
	public JSONObject getTagMusicLibrary(){
		
		
		JSONObject tagMusicLibObj = new JSONObject();
		
		JSONArray songsArray = new JSONArray();
		
		for(Map.Entry<String, ArrayList<Song>> entry: this.tagMusicLibrary.entrySet()){
			
			String tag = entry.getKey();
			
			ArrayList<Song> songs = entry.getValue();


			for(Song song : songs){
				
				JSONObject innerObj = new JSONObject();
			
				String songTrackID = song.getTrackID();
				
				// forming most inner jsonObj
				innerObj.put("tag", tag);	// # this is the song title we want - at key set				
				innerObj.put("trackID", songTrackID);
				
				// add to songsJSONArray
				songsArray.add(innerObj);
				
			}		
		}
		
		
		// forming top level titleMusicLibObj
		tagMusicLibObj.put("tagMusicLibrary", songsArray);
		
		return tagMusicLibObj;
	
	}
	
	
	// get trackID music library for DBHelper to add into mySQL
	public JSONObject getTrackIDMusicLibrary(){
		
		
		JSONObject trackIDMusicLibObj = new JSONObject();
		
		JSONArray songsArray = new JSONArray();
		
		for(Map.Entry<String, Song> entry: this.trackIDMusicLibrary.entrySet()){
			
			String tag = entry.getKey();
			
			Song song = entry.getValue();

			JSONObject innerObj = new JSONObject();
		
			String songTrackID = song.getTrackID();
			
			// forming most inner jsonObj
			innerObj.put("trackID", songTrackID);
			innerObj.put("tag", tag);	// # this is the song title we want - at key set				
			
			
			// add to songsJSONArray
			songsArray.add(innerObj);
		
		}
		
		
		// forming top level titleMusicLibObj
		trackIDMusicLibObj.put("trackIDMusicLibrary", songsArray);
		
		return trackIDMusicLibObj;
	
	}
	
	
	
	
}	
