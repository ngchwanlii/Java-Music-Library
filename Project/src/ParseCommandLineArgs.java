import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.regex.Pattern;


public class ParseCommandLineArgs {
	
	// instance variable
	private String[] args;
	private final int MAX_ARGS = 12;	 // added -searchInput, input/queries.json, searchOutput, results/searchResults.json
	private final int MANDATORY_FLAG = 3; // mandatory flag = -input, -output, -order
	private final int MAX_NUM_FLAGS = 6; // added -threads as flag
										 // added -searchInput as flag
										 // added -searchOutput as flag
	private HashMap<String, String> argMap;
	private final String DEFAULT_VALUE = "10";
	

	// ParseCommandLineArgs constructor
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
	
		// allow for -threads or -threads 5 (with numbers of threads)
		// allow for optional flag -searchInput & -searchOutput with associate value
		if(args.length < MAX_ARGS-6 || args.length > MAX_ARGS){
			throw new IllegalArgumentException("\nError: Incorrect command line input argument\n"
												+ "The inputs in command line should have at least 3 flags\n"
												+ "The 3 flags (-input, -output & -order) should associated with 3 appropriate values\n"
												+ "Optional flag (-thread) value can be empty or associate with value.\n"
												+ "Optional flag (-searchInput) value must be associate with a path to json file\n"
												+ "Optional flag (-searchOutput) value must be associate with a path to json file\n");
		}
		
		// check if 3 mandatory required flags is missing, count on some option flags
		for(String arg : args){
			
			// detect flag
			if(arg.startsWith("-")){
				
				if(arg.equals("-input") || arg.equals("-output") || arg.equals("-order") || arg.equals("-threads") || arg.equals("-searchInput") || arg.equals("-searchOutput")){
					countFlags++;
				}				
			}
		}
		
		
		// if there is missing flag throw exception, catch exception and print out error message, then exit gracefully
		if(countFlags < MANDATORY_FLAG || countFlags > MAX_NUM_FLAGS){
			
			if(countFlags < MANDATORY_FLAG) {
				throw new IllegalArgumentException("\nErrors: Missing basic flags. (-input, -output, -order)");
			}
			else {
				throw new IllegalArgumentException("\nErrors: Flags overflow. You've entered extra flags that is not defined.\n"
													+ "The inputs in command line should have at least 3 flags\n"
													+ "The 3 flags (-input, -output & -order) should associated with 3 appropriate values\n"
													+ "Optional flag (-thread) value can be empty or associate with value.\n"
													+ "Optional flag (-searchInput) value must be associate with a path to json file\n"
													+ "Optional flag (-searchOutput) value must be associate with a path to json file\n");
			}
		}
		
		// check if optional flag associate right value
		for(int i = 0; i < args.length; i++){
			
			// detect flag			
			if(isFlag(args[i])){
				
				String flag = args[i];
				String value = null;
				String strNum = null;
				
				// test special case like -input input/lastfm_subset -output results tag -order ( where the flag is at last)
				//  args.length = MAX_ARGS (included -threads 5)
				// which means have (-threads 5)
				if(args.length != MAX_ARGS-1){
					if((i+1) >= args.length){
						throw new IllegalArgumentException("\nThe value associated with flags: " + flag + " does not exists" );
					}	
					// if it is -thread
					if(args[i].equals("-threads")) {
						
						// set the object value of -threads (can be integer/float/string), parse later						
						strNum = args[i+1];
						
					}
					else {
						// for the path of (-input, -output, -order, -searchInput, -searchOutput)
						value = args[i+1];
					}
				}
				// means args.length = 7 = (-threads (without numbers))
				else {
					// for -threads (without value associate, use default value of 10)
					if(args[i].equals("-threads")){
						strNum = null;
					}
					else {
						// for the path of (-input, -output, -order, -searchInput, -searchOutput) 
						value = args[i+1];
					}
				}
			
				
				// check if files exists
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
				case "-threads":
					
					// if no value specified with -threads, use default value 10
					if(strNum == null){
						// default value
						strNum = DEFAULT_VALUE;
						
					}
					else {
						// if it is integer and in range 1-1000 (inclusive)
						if(isInt(strNum) && intInRange(strNum)){
																					
							strNum = strNum;							
														
						}
						else if(isFloat(strNum) || isString(strNum) || !intInRange(strNum)){
							
													
							strNum = DEFAULT_VALUE;
							
						}
						
						
					}
					
					// add into HashMap , key - '-threads', value -> 'integer value represent in String type', -> when create thread pool
					// use Integer.parseInt() to parse this strNum and assigned to integer type.
					argMap.put(flag, strNum);
					break;	
					
				//TODO: added new -searchInput & -searchOutput
				case "-searchInput":
					path = Paths.get(value);
					if(!Files.exists(path)){
						
						// Files.exists will throw 
						throw new IllegalArgumentException("\nThe filepath value associate with flags: \"" + flag + "\" does not match and is in invalid format.");
					}
									
					// else add into HashMap, key -> flags, value -> input value						
					argMap.put(flag, value);					
					break;
				case "-searchOutput":
					path = Paths.get(value);
					if(!path.toFile().getParentFile().isDirectory()){
						throw new IllegalArgumentException("\nThe filepath value associate with flags: \"" + flag + "\" does not match and is in invalid format.");
					}
					argMap.put(flag, value);
					break;
				default:
					break;
				}
			}			
		}		
	}							
	
	// check flag type (special case where user input -5, this -5 is start with "-" but obviously it is not a flag, so we need a checker for flag
	public boolean isFlag(String str){
		
		String flagRegexPattern = "^-[a-zA-Z]+";

		return Pattern.matches(flagRegexPattern, str);
	}
	
	// check if type is Integer
	public boolean isInt(String str){
		
		// Allow for the +sign -> Ex: +5 is consider as integer positive 5
		// does not allow for -sign -> Ex: -5, return false anyway since it is less than 1
		String intRegexPattern = "[+]?[0-9]+";
				
		// if it is not integer, return false
		return Pattern.matches(intRegexPattern, str);		
		
	}
	
	// check if integer is in range
	public boolean intInRange(String str){
		
		int x = Integer.parseInt(str);
		
		// if it is integer BUT less than 1 or greater 1000, return false
		if(x < 1 || x > 1000) {
			return false;
		}
		
		
		// otherwise, it is integer and in range 1~1000 (inclusive) 
		return true;
				
	}
	
	// check if type is floating point
	public boolean isFloat(String str){
		
		String floatRegexPattern = "^[-+]?[0-9]*\\.[0-9]+";
		
		return Pattern.matches(floatRegexPattern, str);
	}
	
	// check if type is String
	public boolean isString(String str){
		
		String strWithQuoteReg = "^\"?.*\"?$";
		return Pattern.matches(strWithQuoteReg, str);
	}
	
	
	
	public HashMap<String, String> getArgsMap(){
		return argMap;
		
	}
	
}