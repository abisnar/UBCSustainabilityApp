package ubc.cs.cpsc210.sustainabilityapp.webservices;

import java.util.LinkedList;

import java.util.List;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.util.Log;

import ubc.cs.cpsc210.sustainabilityapp.model.Photograph;
import ubc.cs.cpsc210.sustainabilityapp.model.LatLong;

public class FlickrParser {
	
	
    public static List<Photograph> parse(String response) {
        LinkedList<Photograph> photoIDs = new LinkedList<Photograph>();
        
        // Simple JSON format: parsed the body from search
        JSONObject obj = (JSONObject) JSONValue.parse(getJSONFromResponse(response));
        // produces get photos header
        JSONObject header = (JSONObject) obj.get("photos");
        // retrieve the attributes within photos to get "photo"
        JSONArray photographs = (JSONArray) header.get("photo");
        
        // within each "photo" extracted the following attributes
        for (int i = 0; i < photographs.size(); i++){
        	JSONObject photo = (JSONObject) photographs.get(i);
        	
        	String id = photo.get("id").toString();
        	String owner = photo.get("owner").toString();
        	String secret = photo.get("secret").toString();
        	String displayName = photo.get("title").toString();
        	String server = photo.get("server").toString();
        	String farm = photo.get("farm").toString();
        	
        	// within the for loop create a new photo for each JSON object
        	Photograph photoInstance = new Photograph(id, displayName, farm, secret, owner, server);
        	
        	// call to the helper function getLocation to extract the corresponding LatLongs
        	photoInstance.setLatLong(parseLocation(FlickrService.getLocation(photoInstance.getId())));
        	// to track what we are parsing
        	Log.e("Flickr Parser Locations", FlickrService.getLocation(photoInstance.getId()).toString());
        	// add the new photoInstances to our linkedlist
        	photoIDs.add(photoInstance);
        	
        	
        }
// list of all the Photos in the linkedlist after the loop completes
	Log.e("PHOTO IDs",photoIDs.toString());
    
    return photoIDs;
    }
    // helper function used to get the LatLongs associated with each parsed photo
    private static LatLong parseLocation(String response){
    	String json = getJSONFromResponse(response);
    	
    	
    	// Simple JSON format
    	JSONObject obj = (JSONObject) JSONValue.parse(json);
    	JSONObject header = (JSONObject) obj.get("photo");
    	JSONObject locations = (JSONObject) header.get("location");
    	
    	
    	Double latitude = (Double) locations.get("latitude");
    	Double longitude = (Double) locations.get("longitude");
    	
    	// store the parsed information to a new LatLong
    	LatLong latlong = new LatLong(latitude,longitude);
    	
    	// to track the latitudes and longitudes associated ater the parse to ensure it is working correctly
    	Log.e("Parsed latlong latitude", Double.toString(latlong.getLatitude()));
    	Log.e("Parsed latlong longitude", Double.toString(latlong.getLongitude()));
    	
    	return latlong;
		
    }
    
    private static String getJSONFromResponse(String response){
    	return response.substring(14, response.length() -1);
    }
    
}

