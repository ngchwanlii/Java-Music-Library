import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.simple.JSONArray;

public class MusicLibrary {
	
	private TreeMap<String, ArrayList<Song>> musicLibrary;	
	private String order;
	
	public MusicLibrary(String order){
				
		this.musicLibrary = new TreeMap<>();
		
		this.order = order;
	}
	
	public void addSong(Song song){
		
		// artist
		if(order.equals("artist")){
			
			String artistName = song.getArtistName();
						
			if(!musicLibrary.containsKey(artistName)){
				this.musicLibrary.put(artistName, new ArrayList<Song>());
			}
						 
			this.musicLibrary.get(artistName).add(song);
			
		}
		// title
		else if(order.equals("title")){
			
			String title = song.getTitle();
			
			if(!musicLibrary.containsKey(title)){
				this.musicLibrary.put(title, new ArrayList<Song>());
			}
						 
			this.musicLibrary.get(title).add(song);
			
		}
		
		else if(order.equals("tag")){
						
			JSONArray tagArray = song.getTags();
			
			if(!tagArray.isEmpty()) {
				for(int i = 0; i < tagArray.size(); i++){
					
					JSONArray innerArray = (JSONArray) tagArray.get(i);
					
					String tag = (String)innerArray.get(0);
				
					if(!musicLibrary.containsKey(tag)){
						// natural ordering, don't have to create Tag Comparator
						this.musicLibrary.put(tag, new ArrayList<Song>());
					}
					
					// no duplicate song can be added in orderType tag
					if(!this.musicLibrary.get(tag).contains(song)) { 					
						this.musicLibrary.get(tag).add(song);
					}				
				}
			}
			
		}
		
	
		
	}
	
	public TreeMap<String, ArrayList<Song>> getMusicLibraryTreeMap() {
		return musicLibrary;
	}
	
	
	
}	
