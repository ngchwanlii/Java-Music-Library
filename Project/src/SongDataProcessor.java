import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SongDataProcessor {

	private MusicLibrary ml;

	public SongDataProcessor(MusicLibrary musicLibrary, HashMap<String, String> argMap) {

		this.ml = musicLibrary;
		Path inputPath = Paths.get(argMap.get("-input"));
		findFile(inputPath);

	}

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

	public boolean checkFileFormat(Path path) {

		if (path.toString().toLowerCase().trim().endsWith(".json")) {
			return true;
		} else {
			return false;
		}
	}

}
