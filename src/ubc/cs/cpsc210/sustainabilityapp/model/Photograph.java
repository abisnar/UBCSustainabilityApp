package ubc.cs.cpsc210.sustainabilityapp.model;

public class Photograph extends PointOfInterest {


	// TODO Project Phase Two : you need to complete the implementation of this class 
	// (write the getURL() method)
	
	private String farm;
	private String server;
	private String owner;
	private String secret;
	
	public Photograph(String id, String displayName, String farm, String secret, String owner, String server) {
		super(id, displayName);
		this.farm = farm;
		this.server = server;
		this.owner = owner;
		this.secret = secret;
	}

	/** Returns a String that is consisent with the way Flickr uses the farm, server, owner and secret to
	* generate URLs
	* 
	* <b>Effects:</b><br>Returns a String that is formatted following Flickr's URL formatting guidelines
	*          http://www.flickr.com/services/api/misc.urls.html
	*/

	public String getURL()
	{
		return "http://farm"+farm+".staticflickr.com/" +server+"/"+ this.getId() +"_"+secret+"_m.jpg";
	}
	 
}
