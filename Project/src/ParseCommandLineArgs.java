import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;


public class ParseCommandLineArgs {
	
	private String[] args;
	private final int MAX_ARGS = 6;
	private final int MAX_NUM_FLAGS = 3;
	private HashMap<String, String> argMap;
	

	// Constructor
	public ParseCommandLineArgs(String[] args) throws IllegalArgumentException{
		
		this.args = args;
		argMap = new HashMap<String, String>();
		
		// if checkArgument() has problem, it will throws IllegalArgumentException here
		checkArgument();
	}
	
	// if checkArgument has exception, handled by propagating exception
	public void checkArgument() throws IllegalArgumentException{
				
		int countFlags = 0;
		Path path;
		
		// initial check arguments
		
		// test in general the input arguments meet the expected one
		if(args.length != MAX_ARGS){
			throw new IllegalArgumentException("\nError: Incorrect command line input argument\n"
												+ "The inputs in command line should have 3 flags, associated with 3 appropriate values");
		}
		
		// check if 3 required flags is missing
		for(String arg : args){
			
			// detect flag
			if(arg.startsWith("-")){
				
				if(arg.equals("-input")){
					countFlags++;
				}
				else if(arg.equals("-output")){
					countFlags++;
				}
				else if(arg.equals("-order")){
					countFlags++;
				}
			
			}
		}
		
		// if there is missing flag throw exception, catch exception and print out error message, then exit gracefully
		if(countFlags != MAX_NUM_FLAGS){
			throw new IllegalArgumentException("\nErrors: Missing flags.");
		}
		
		// check if optional flag associate right value
		for(int i = 0; i < args.length; i++){
			
			// detect flag
			if(args[i].startsWith("-")){
				
				String flag = args[i];
				
				// test special case like -input input/lastfm_subset -output results tag -order ( where the flag is at last)
				if((i+1) >= args.length){
					throw new IllegalArgumentException("\nThe value associated with flags: " + flag + " does not exists" );
				}
				
				String value = args[i+1];
				
				// check if files exits
				switch(flag){
				
				case "-input":
					path = Paths.get(value);
					if(!Files.exists(path)){
						
						// Files.exists will throw 
						throw new IllegalArgumentException("\nThe filepath value associate with flags: \"" + flag + "\" does not match and is in invalid format.");
					}
					
											
					// else add into HashMap, key -> flags, value -> input value						
					argMap.put(flag, value);					
					break;
				
				case "-output":
					path = Paths.get(value);
					
					// written output file must be in "results" directories
					// if user input's args : -output results, it will still write to file with specific filename based on sort order type
					// if user input's args: -output results/songsByArtistSubset (full path that include the txt file name)for example, it will still write to file based on sort order type  
					if(!path.toFile().getParentFile().isDirectory()){
						throw new IllegalArgumentException("\nThe filepath value associate with flags: \"" + flag + "\" does not match and is in invalid format.");
					}
											
					// else add into HashMap, key -> flags, value -> input value						
					argMap.put(flag, value);						
					break;
				case "-order":
					
					if(value.equals("artist") || value.equals("title") || value.equals("tag")){
						argMap.put(flag, value);
					}
					else {
						throw new IllegalArgumentException("\nErrors with value associate with flags: " + flag + " \"" + value + "\"" + "\nThe value associate with flags must be either (artist, title or tag).");
					}
					break;	
				default:
					break;
				}
			}			
		}		
	}							

	
	public HashMap<String, String> getArgsMap(){
		return argMap;
		
	}
	
}