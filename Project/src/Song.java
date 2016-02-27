import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Song {
	
	private String artistName;
	private String title;
	private String trackID;
	private JSONArray similars;
	private JSONArray tag;
	
	public Song(JSONObject song){
		
		this.artistName = (String)song.get("artist");
		this.title = (String)song.get("title");
		this.trackID = (String)song.get("track_id");
		this.similars = (JSONArray)song.get("similars");		
		this.tag = (JSONArray) song.get("tags");
		
	}

	public String getArtistName() {
		return artistName;
	}

	public String getTitle() {
		return title;
	}

	public String getTrackID() {
		return trackID;
	}

	public JSONArray getSimilars() {
		return similars;
	}

	public JSONArray getTags() {
		return tag;
	}
	
}
