package cs212.server;

public class FacebookClient {
	
	
	private String ID; 
	private String SECRET_KEY;
	
	
	
	public FacebookClient(String ID, String secretKey){
		
		this.ID = ID;
		this.SECRET_KEY = secretKey;
	}
	
	public String getID(){
		return ID;
	}
	public String getSecretKey(){
		return SECRET_KEY;
	}
	
	
	
}



//"228334240883505";
//"00ea36f208739bab42c765f4115f5042"