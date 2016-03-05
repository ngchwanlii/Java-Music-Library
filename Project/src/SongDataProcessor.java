import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SongDataProcessor {
	
	// instance variable
	private MusicLibrary ml;

	// SongDataProcessor constructor
	public SongDataProcessor(MusicLibrary musicLibrary, String inputStringPath) {

		this.ml = musicLibrary;
		Path inputPath = Paths.get(inputStringPath);
		findFile(inputPath);

	}

	// traverse and findFiles within the File System
	public void findFile(Path path) {
		
		if (Files.isDirectory(path)) {

			try (DirectoryStream<Path> list = Files.newDirectoryStream(path)) {

				for (Path file : list) {

					findFile(file);

				}

			} 
			catch (IOException e) {
				System.out.println(e);
			}
		}

		else {

			if (checkFileFormat(path)) {
				parseFunction(path);
			}

		}

	}

	// parse and read the object in JSON object
	// add song to MusicLibrary
	public void parseFunction(Path path) {

		JSONParser jsonParser = new JSONParser();

		try (BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {

			String line = reader.readLine();

			while (line != null) {

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
	
	// check if the file format is .json extension
	public boolean checkFileFormat(Path path) {

		if (path.toString().toLowerCase().trim().endsWith(".json")) {
			return true;
		} else {
			return false;
		}
	}

}
