package cs212.data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		JSONArray jsonArray = new JSONArray();
		
		JSONObject jsonObj = new JSONObject();
		
		
		jsonObj.put("artist", "Jay");
		
		jsonObj.put("song", "ALOHA");
		jsonObj.put("trackID", "T182379");
		
		jsonArray.add(jsonObj);
		
		
		JSONObject jsonObj2 = new JSONObject();
		
		jsonObj2.put("artist", "Vivian");
		
		jsonObj2.put("song", "VIVA");
		jsonObj2.put("trackID", "T1208308012");
		
		jsonArray.add(jsonObj2);
		
		
		System.out.println(jsonArray.toJSONString());
		
//		JSONObject ret = new JSONObject();
		
		for(int i = 0; i < jsonArray.size(); i++){
			
			JSONObject ret = (JSONObject) jsonArray.get(i);
			
			String artist = (String) ret.get("artist");
			String song = (String)ret.get("song");
			String trackID = (String)ret.get("trackID");
			
			System.out.println(artist);
			System.out.println(song);
			System.out.println(trackID);
		}
		
		
		
		
	}

}
