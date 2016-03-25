import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

//TODO: add documentation  FIXED

public class Song {
	
	// instance variable
	private String artistName;
	private String title;
	private String trackID;
	private ArrayList<String> similarsList;
	private ArrayList<String> tagList;
	
	// Song constructor
	public Song(JSONObject song){
		
		
		this.artistName = (String)song.get("artist");
		this.title = (String)song.get("title");
		this.trackID = (String)song.get("track_id");
		this.similarsList = new ArrayList<String>();
		this.tagList = new ArrayList<String>();
					
		// add different tag within each song into tagList
		JSONArray tagArray = (JSONArray) song.get("tags");
						
		if(!tagArray.isEmpty()) {
			for(int i = 0; i < tagArray.size(); i++){
				
				JSONArray innerArray = (JSONArray) tagArray.get(i);
				
				String tag = (String)innerArray.get(0);
								
				// no duplicate tag added for each song				
				if(!tagList.contains(tag)){
					this.tagList.add(tag);					
				}			
			}
		}
		
		
		// add different similars tag within each song into similarsList
		JSONArray similarArray = (JSONArray) song.get("similars");
		
		if(!similarArray.isEmpty()){
			for(int i = 0; i < similarArray.size(); i++){
				
				JSONArray innerArray = (JSONArray) similarArray.get(i);
				
				String similar = (String)innerArray.get(0);
								
				// no duplicate tag added for each song 
				if(!similarsList.contains(similar)){
					this.similarsList.add(similar);
				}			
			}
		}
		
		
	}

	
	// TODO: This is not thread-safe: need to modified this 
	public String getArtistName() {
		return artistName;
	}

	public String getTitle() {
		return title;
	}

	public String getTrackID() {
		return trackID;
	}

	public ArrayList<String> getSimilars() {
		return similarsList;
	}

	public ArrayList<String> getTags() {
		return tagList;
	}
	
}
