package cs212.data;

public class ArtistPlayCountInfo {
	
	private String artist;
	private Integer playcount;
	
	public ArtistPlayCountInfo(String artist, Integer playcount){
		
		this.artist = artist;
		this.playcount = playcount;
		
	}

	public String getArtist() {
		return artist;
	}

	public Integer getPlaycount() {
		return playcount;
	}
	
	
}
