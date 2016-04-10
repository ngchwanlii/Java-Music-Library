import java.util.ArrayList;

import org.json.simple.JSONArray;

public class Test {
	
	public static void main(String[] args) {
		
		JSONArray arr1;
		ArrayList<JSONArray> record = new ArrayList<JSONArray>();
		
		
		// point to reference changed the content of newArray
//		JSONArray newArr = new JSONArray();
		
		for(int i = 0 ; i < 2; i++){
			
			// point to reference changed the content of newArray
//			arr1 = returnArr(i,newArr);
			arr1 = returnArr(i);
			record.add(arr1);
			
		}
		
		
		System.out.println(record);
		
		
		
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
