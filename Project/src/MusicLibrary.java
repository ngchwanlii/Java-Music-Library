import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.simple.JSONArray;

public class MusicLibrary {
	
	private HashMap<String, String> argMap;
	private TreeMap<String, TreeSet<Song>> artistMusicLibrary;
	private TreeMap<String, TreeSet<Song>> titleMusicLibrary;
	private TreeMap<String, ArrayList<Song>> tagMusicLibrary;	
	private String order;	
	private Path inputPath; 
	private Path outputPath;
	
	public MusicLibrary(HashMap<String, String> argMap){
		
		this.argMap = argMap;
		this.inputPath = Paths.get(argMap.get("-input"));
		this.outputPath = Paths.get(argMap.get("-output"));
		this.order = argMap.get("-order");
		
		// instantiate TreeMap for artist, title and tag based on order type
		if(order.equals("artist")){
			this.artistMusicLibrary = new TreeMap<String, TreeSet<Song>>();
		}
		else if(order.equals("title")){
			this.titleMusicLibrary = new TreeMap<String, TreeSet<Song>>(); 
		}
		else if (order.equals("tag")){
			this.tagMusicLibrary = new TreeMap<String, ArrayList<Song>>();
		}
			
		// process and add song
		SongDataProcessor processSongData = new SongDataProcessor(this, argMap);
	}
	
	public void addSong(Song song){
		
		// artist
		if(order.equals("artist")){
			
			String artistName = song.getArtistName();
						
			if(!artistMusicLibrary.containsKey(artistName)){
				this.artistMusicLibrary.put(artistName, new TreeSet<Song>(new ArtistComparator()));
			}
						 
			this.artistMusicLibrary.get(artistName).add(song);
			
		}
		// title
		else if(order.equals("title")){
			
			String title = song.getTitle();
			
			if(!titleMusicLibrary.containsKey(title)){
				this.titleMusicLibrary.put(title, new TreeSet<Song>(new TitleComparator()));
			}
						 
			this.titleMusicLibrary.get(title).add(song);
			
		}
		
		else if(order.equals("tag")){
						
			JSONArray tagArray = song.getTags();
			
			if(!tagArray.isEmpty()) {
				for(int i = 0; i < tagArray.size(); i++){
					
					JSONArray innerArray = (JSONArray) tagArray.get(i);
					
					String tag = (String)innerArray.get(0);
				
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
			
		}
		
	
		
	}
	
		
	// writeToTextFile method
	public void writeToTextFile() throws IllegalArgumentException{
		
		/** MODIFIED after 1st review **/
		// if inputPath is valid (exists), and outputPath parent directory
		if(inputPath.toFile().exists() && outputPath.toFile().getParentFile().isDirectory()){
			// write out path
			try(PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputPath, Charset.forName("UTF-8")))){
				
				if(order.equals("artist")){
					for(String key : artistMusicLibrary.navigableKeySet()){
						
						TreeSet<Song> songs = artistMusicLibrary.get(key);
						
						for(Song song : songs){
														
							writer.println(key + " - " + song.getTitle());																					
						}
						
					}					
				}								
				else if(order.equals("title")){
					for(String key : titleMusicLibrary.navigableKeySet()){
						
						TreeSet<Song> songs = titleMusicLibrary.get(key);
						
						for(Song song : songs){
														
							writer.println(song.getArtistName() + " - " + key);																					
						}
						
					}
					
				}								
				
				else if(order.equals("tag")){					
					for(String key : tagMusicLibrary.navigableKeySet()){
						
						ArrayList<Song> songs = tagMusicLibrary.get(key);
						
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
