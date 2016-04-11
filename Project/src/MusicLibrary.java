import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MusicLibrary {
	
	// instance variable
	private TreeMap<String, TreeSet<Song>> artistMusicLibrary;
	private TreeMap<String, TreeSet<Song>> titleMusicLibrary;
	private TreeMap<String, ArrayList<Song>> tagMusicLibrary;	
	private TreeMap<String, Song> trackIDMusicLibrary;
	private Path inputPath; 
	private Path outputPath;
	private Path searchInPath;
	private Path searchOutPath;
	
	// MusicLibrary constructor - with search function 
	public MusicLibrary(String inputStringPath, String outStringPath, String searchIn, String searchOut){
		
		this.inputPath = Paths.get(inputStringPath);
		this.outputPath = Paths.get(outStringPath);
		this.searchInPath = Paths.get(searchIn);
		this.searchOutPath = Paths.get(searchOut);
	
		// instantiate all type of MusicLibrary for artist, title and tag		
		this.artistMusicLibrary = new TreeMap<String, TreeSet<Song>>();
		this.titleMusicLibrary = new TreeMap<String, TreeSet<Song>>(); 
		this.tagMusicLibrary = new TreeMap<String, ArrayList<Song>>();
		this.trackIDMusicLibrary = new TreeMap<String, Song>();
			
	}
	
	// MusicLibrary constructor
	public MusicLibrary(String inputStringPath, String outStringPath){
				
		this.inputPath = Paths.get(inputStringPath);
		this.outputPath = Paths.get(outStringPath);
	
		// instantiate all type of MusicLibrary for artist, title and tag		
		this.artistMusicLibrary = new TreeMap<String, TreeSet<Song>>();
		this.titleMusicLibrary = new TreeMap<String, TreeSet<Song>>(); 
		this.tagMusicLibrary = new TreeMap<String, ArrayList<Song>>();		
		this.trackIDMusicLibrary = new TreeMap<String, Song>();
		
			
	}
	
	// searchByArtist method - return similarSongJSON based on query search in JSONArray
	public JSONArray searchByArtist(String query) {
		
		
		// initialize resultList
		JSONArray resultList = new JSONArray();
		
		/** NOTE: if key is not contain in this map, return an empty resultList **/		
		if(!artistMusicLibrary.containsKey(query)){
			
			return resultList;
		}
				
		// list of songs by given artist
		TreeSet<Song> songs = artistMusicLibrary.get(query);
		
		// for each song in songs
		for(Song s : songs){
				
			// list of songs similar to song			 
			ArrayList<String> similarSongTrackID = s.getSimilars();
			
			/** DEBUG print - confirmed Busta - DJ Quik in trackID **/
			/** NEED DEBUG **/
//			System.out.println(trackIDMusicLibrary.containsKey("TRAAMJY128F92F5919"));
			
			// for each similarSong by track_ID
			if(!similarSongTrackID.isEmpty()){
				for(String trackID : similarSongTrackID){
					
					// get song by tracking id found in trackIDMusicLibrary
					if(trackIDMusicLibrary.containsKey(trackID)){
						Song song = trackIDMusicLibrary.get(trackID);
						// convert this songObj to songJSONObj
						JSONObject songJSONObj = convertToSongJSONObject(song);
						resultList.add(songJSONObj);
					}
						
				}								
			}
			
			
		}
		return resultList;
	}
	
	// searchByArtist method - return similarSongJSON based on query search in JSONArray
	public JSONArray searchByTitle(String query) {
		
		// initialize resultList
		JSONArray resultList = new JSONArray();
		
		/** NOTE: if key is not contain in this map, return an empty resultList **/		
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
						resultList.add(songJSONObj);
					}
						
				}								
			}
			
			
		}
		return resultList;
	}
		
		
		
	
	
	// thread-safe - share function that could be use for searchByArtist/searchByTitle/searchByTag 
	private synchronized JSONObject convertToSongJSONObject(Song song){
		
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
		/** jay - added trackID music library for tracking id **/
		String trackID = song.getTrackID();
	
	
		/** jay - DEBUG PRINT **/	
//		System.out.println("trackID: " + trackID + " song: " + song.getArtistName());
		
		this.trackIDMusicLibrary.put(trackID, song);
		
		
		
		// if this is a new artist, create TreeSet to store these song
		if(!artistMusicLibrary.containsKey(artistName)){
			this.artistMusicLibrary.put(artistName, new TreeSet<Song>(new ArtistComparator()));
		}
		
		// else add to the created artistName data structure
		this.artistMusicLibrary.get(artistName).add(song);
		
		// if this is a new title, create TreeSet to store these song
		if(!titleMusicLibrary.containsKey(title)){
			this.titleMusicLibrary.put(title, new TreeSet<Song>(new TitleComparator()));
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
			}
			
			// no duplicate song can be added in orderType tag
			if(!this.tagMusicLibrary.get(tag).contains(song)) { 					
				this.tagMusicLibrary.get(tag).add(song);
			}
		
		}
		
		
		
		
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

	
	
}	