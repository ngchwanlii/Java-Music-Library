import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ProcessingFile implements Runnable {

	// instance variable
	private Path path;
	// shared data structure - threadSafeML (it's share data structure because multiple threads that invoke addSong's method
	// will update the data structure and add song into the TreeMap)
	private ThreadSafeMusicLibrary threadSafeML;
	 
	
	// ProcessingFile constructor
	public ProcessingFile(Path path, ThreadSafeMusicLibrary threadSafeML){
		
		this.path = path;
		this.threadSafeML = threadSafeML;
	}
	
	@Override
	public void run() {
		
		// PoolWorkerThread - workerThread will run this task if task has been assigned into queue
		parseFunction(path);
		
	}
	
		
	// parse and read the object in JSON object
	// add song to ThreadSafeMusicLibrary	
	public void parseFunction(Path path) {
				
		JSONParser jsonParser = new JSONParser();

		try (BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
			
			JSONObject singleSongObject = (JSONObject) jsonParser.parse(reader);

			// save as a single song object
			Song song = new Song(singleSongObject);

			// invoke in threadSafeMusicLibrary and invoke the inherited addSong method by calling super.addSong() from super class - musicLibrary to add song			
			threadSafeML.addSong(song);
			
		} 
		catch (IOException e) {
			System.out.println(e);
		} 
		catch (ParseException e) {

			System.out.println(e);
		}

	}
	
	
}

	

	


