import java.util.Comparator;

import org.json.simple.JSONObject;

public class TrackIDComparator implements Comparator<JSONObject> {
	
	@Override
	public int compare(JSONObject o1, JSONObject o2) {
		
		String song1_track_id = (String)o1.get("trackId");
		String song2_track_id = (String)o2.get("trackId");
		
		// sort by two track_id		
		return song1_track_id.compareTo(song2_track_id);
	}
}
