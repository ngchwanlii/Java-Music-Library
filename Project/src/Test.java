import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Test {
	
	public static void main(String[] args) {
		
		
		Path actualOutput = Paths.get("results/searchResultsMy.json");
		Path expectedOutput = Paths.get("output/searchResultsPro.json");
		 
		
		try {
			
			JSONParser parser = new JSONParser();
		
			JSONObject actualObj = (JSONObject) parser.parse(Files.newBufferedReader(actualOutput));
			JSONObject expectedObj = (JSONObject) parser.parse(Files.newBufferedReader(expectedOutput));
		
			
			System.out.println(actualObj.size());
			System.out.println(expectedObj.size());
			
		} 
		catch (IOException | ParseException e) {
			
			e.printStackTrace();
		}
		
		
		
		
		
		
//		HashSet<String> checkInput = new HashSet<String>();
//		
//		checkInput.add("searchByArtist");
//		checkInput.add("searchByTitle");
//		checkInput.add("searchByTag");
//		checkInput.add("searchByArtist");
//		
//		
//		Iterator it = checkInput.iterator();
//		
//		while(it.hasNext()){
//			
//			String key = (String)it.next();
//			
//			System.out.println(key);
//			
//			/* EXAMPLE of wrong implementation - it.next() being invoked multiple time so it run out of hashset 
//			if(it.next().equals("searchByArtist")){
//				System.out.println("hi artist");
//			}
//			else if(it.next().equals("searchByTag")){
//				System.out.println("hi tag");
//			}
//			else if(it.next().equals("searchByTitle")){
//				System.out.println("hi title");
//			}
//			*/
//	
//			
//		}
		
		
//		System.out.println(checkInput);
//		
//		System.out.println(checkInput.contains("searchByArtist"));
		
		
		
		/**** breaking line ************/
		
//		ArrayList<String> arr = new ArrayList<String>();
//		
//		arr.add("Jay");
//		arr.add("Ng");
//		
//		for(String s : arr){
//			System.out.println(s);
//		}
		
		
		
//		JSONArray arr1;
//		ArrayList<JSONArray> record = new ArrayList<JSONArray>();
//		
//		
//		// point to reference changed the content of newArray
////		JSONArray newArr = new JSONArray();
//		
//		for(int i = 0 ; i < 2; i++){
//			
//			// point to reference changed the content of newArray
////			arr1 = returnArr(i,newArr);
//			arr1 = returnArr(i);
//			record.add(arr1);
//			
//		}
//		
//		
//		System.out.println(record);
		
		
		
	}
	
	
	
	// @param int i, JSONArray newArr 
	public static JSONArray returnArr(int i){
		
		JSONArray newArr = new JSONArray();
		
		if(i == 1){
			
			newArr.add("Vivian");
			
			
		}
		else{
			
			newArr.add("Jay");
			
		}
		
		
		return newArr;
		
	}
}
