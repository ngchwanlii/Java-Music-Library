import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SongDataProcessor {
		
	private HashMap<String, String> argMap;
	private String orderType;
	private Path inputPath; 
	private Path outputPath;
	private MusicLibrary ml;
	
	public SongDataProcessor(HashMap<String, String> argMap){
		
		this.argMap = argMap;
		this.inputPath = Paths.get(argMap.get("-input"));
		this.outputPath = Paths.get(argMap.get("-output"));
		this.orderType = argMap.get("-order");
		this.ml = new MusicLibrary(orderType);
		
		
		findFile(inputPath);
	
	}
	
	
	public void findFile(Path path){
		
		if(Files.isDirectory(path)){
			
			try (DirectoryStream<Path> list = Files.newDirectoryStream(path)){
				
				for(Path file : list){
					
					findFile(file);
										
				}
							
			}
			catch (IOException e){
				System.out.println(e);
			}							
		}
		
		else {
			
			if(checkFileFormat(path)){
				parseFunction(path);
			}
			
		}
		
	}
	
	public void parseFunction(Path path){
		
		JSONParser jsonParser = new JSONParser();
		
		
		try(BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"))){
			
			String line = reader.readLine();
			
			while(line != null){
				
				JSONObject singleSongObject = (JSONObject) jsonParser.parse(line);
				
				// save as a single song object
				Song song = new Song(singleSongObject);
				
				// use musicLibrary to add song				
				ml.addSong(song);
				
				line = reader.readLine();
			}						
		} 
		catch (IOException e) {			
			System.out.println(e);
		} 
		catch (ParseException e) {
			
			System.out.println(e);
		}
		
	}
	
	
	
	public boolean checkFileFormat(Path path){
		
		if(path.toString().toLowerCase().trim().endsWith(".json")) {
			return true;
		}
		else { 
			return false;
		}
	
	}
	
	public void writeToTextFile(){
		
		
		try {
			if(orderType.equals("artist")) {
				
				// set output file name based on orderType "artist" result folder
				if(inputPath.toString().endsWith("lastfm_subset")){
					// if for example, the output in user_input args is : -output results, it will write and create songsByArtistSubset.txt that contains the sorted order type of song data 
					if(!outputPath.toString().endsWith("songsByArtistSubset.txt")){
						outputPath = Paths.get(outputPath.toString(), "songsByArtistSubset.txt");
					}
					else {
						// for libraryTest, args is written with full path to destination file and file name. -output results/songsByArtistSubset.txt
						outputPath = Paths.get(outputPath.toString());
					}
				}
				else if(inputPath.toString().endsWith("lastfm_simple")){
					if(!outputPath.toString().endsWith("songsByArtistSimple.txt")){
						outputPath = Paths.get(outputPath.toString(), "songsByArtistSimple.txt");
					}
					else {
						outputPath = Paths.get(outputPath.toString());
					}
				}
				else if(inputPath.toString().endsWith("lastfm_txtfile")){
					if(!outputPath.toString().endsWith("songsByArtistWithTxtFile.txt")){
						outputPath = Paths.get(outputPath.toString(), "songsByArtistWithTxtFile.txt");
					}
					else {
						outputPath = Paths.get(outputPath.toString());
					}
				}
				
			}
			else if(orderType.equals("title")){
				
				// set output file name based on orderType "title" result folder
				if(inputPath.toString().endsWith("lastfm_subset")){
					if(!outputPath.toString().endsWith("songsByTitleSubset.txt")){
						outputPath = Paths.get(outputPath.toString(), "songsByTitleSubset.txt");
					}
					else{
						outputPath = Paths.get(outputPath.toString());
					}
				}
				else if(inputPath.toString().endsWith("lastfm_simple")){
					if(!outputPath.toString().endsWith("songsByTitleSimple.txt")){
						outputPath = Paths.get(outputPath.toString(), "songsByTitleSimple.txt");
					}
					else {
						outputPath = Paths.get(outputPath.toString());
					}
				}
				else if(inputPath.toString().endsWith("lastfm_txtfile")){
					if(!outputPath.toString().endsWith("songsByTitleWithTxtFile.txt")){
						outputPath = Paths.get(outputPath.toString(), "songsByTitleWithTxtFile.txt");
					}
					else{
						outputPath = Paths.get(outputPath.toString());
					}
				}
												
			}
			else if(orderType.equals("tag")){
				
//				 set output file name based on orderType "tag" result folder
				if(inputPath.toString().endsWith("lastfm_subset")){
					if(!outputPath.toString().endsWith("songsByTagSubset.txt")){
						outputPath = Paths.get(outputPath.toString(), "songsByTagSubset.txt");
					}
					else{
						outputPath = Paths.get(outputPath.toString());
					}
				}
				else if(inputPath.toString().endsWith("lastfm_simple")){
					if(!outputPath.toString().endsWith("songsByTagSimple.txt")){
						outputPath = Paths.get(outputPath.toString(), "songsByTagSimple.txt");
					}
					else{
						outputPath = Paths.get(outputPath.toString());
					}
				}
				else if(inputPath.toString().endsWith("lastfm_txtfile")){
					if(!outputPath.toString().endsWith("songsByTagWithTxtFile.txt")){
						outputPath = Paths.get(outputPath.toString(), "songsByTagWithTxtFile.txt");
					}
					else{
						outputPath = Paths.get(outputPath.toString());
					}
				}
				
			}
			
		}
		catch (InvalidPathException e){
			System.out.println(e);
		}
		// write out path
		try(PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputPath, Charset.forName("UTF-8")))){
			
			
			if(orderType.equals("artist") || orderType.equals("title")){
				for(String key : ml.getMusicLibraryTreeMap().navigableKeySet()){
									
					ArrayList<Song> songs = ml.getMusicLibraryTreeMap().get(key);
					
					// Collection sort based on "order-type" before saving music data to a file
					if(orderType.equals("artist")){
						Collections.sort(songs, new ArtistComparator());
					}
					else if(orderType.equals("title")){
						Collections.sort(songs, new TitleComparator());
					}
										
					for(Song song : songs ){
																	
						if(orderType.equals("artist")){
							writer.println(key + " - " + song.getTitle());
						}
						else if(orderType.equals("title")){
							writer.println(song.getArtistName() + " - " + key);
						}																	
					}							
				}
			}						
			else if(orderType.equals("tag")){
				
				for(String key : ml.getMusicLibraryTreeMap().navigableKeySet()){
					
					ArrayList<Song> songs = ml.getMusicLibraryTreeMap().get(key);
					
					
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

}
