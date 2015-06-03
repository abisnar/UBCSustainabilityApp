package ubc.cs.cpsc210.sustainabilityapp.webservices;

import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.model.*;
import org.scribe.oauth.*;

import android.text.Html;
import android.util.Log;



public class FlickrService {
	// Project Phase Two : TODO you need to complete the implementation of this class

	//Key:
	//3279e3f7131f45f8befdefa4f6657366

	//Secret:
	//99f1bf026c101adb

	// Replace this with your own api key 
	/*
	 * ApiKey:
	d1a92c230075b153d4d7ce4c54fa9c80

	Secret:
	2bc5107417ccfaf5
	
	http://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=3279e3f7131f45f8befdefa4f6657366&format=json&lat=49.260887&lon=-123.24902&tags=ubccpsc210
	
	*/
	
	private static final String PROTECTED_RESOURCE_URL = "http://api.flickr.com/services/rest/";
	
	private static final String apiKey = "3279e3f7131f45f8befdefa4f6657366";
	private static final String apiSecret = "99f1bf026c101adb";
	
	/** 
	 * Client for making HTTP requests to the API of the service.
	 */
	private HttpClient client;
	private String LOG_TAG = "flicker";
	
	/**
	 * 
	 * <br><br>
	 * <b>Requires:</b><br> A non-empty tag and values latitude and longitudes<br><br>
	 * <b>Effects:</b><br>Returns the body of the response from Flickr
	 * @param tag, latitude, longitude
	 */
	public String search(String tag, double latitude, double longitude) {
		// TODO Project Phase Two : implement this method

		// Hint: You will want to look at the Flickr API to determine which method to call
		//       http://www.flickr.com/services/api/
		// Hint2: You may find it useful to use org.scribe.model.OAuthRequest. You do not have to
		//        use this class
		// Hint3: Make sure to specify that you want the response in json format
		
		//String url = "http://api.flickr.com/service/rest/?method=flickr.photos.search&api_key="+apiKey+"&format=json&lat="+latitude+"&lon="+longitude+"&tags="+tag;
	
		
		// creates an URI request using OAuthRequest
		OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
		request.addQuerystringParameter("method", "flickr.photos.search");
		request.addQuerystringParameter("api_key", apiKey);
		request.addQuerystringParameter("format", "json");
		request.addQuerystringParameter("lat", Double.toString(latitude));
		request.addQuerystringParameter("lon", Double.toString(longitude));
		request.addQuerystringParameter("tags", tag);
		//Sends the request to flicker servers
		Response res = request.send();
		
		
		
		// gets the body from the corresponding request as a JSON object
		return res.getBody();
		
	}

	/**
	 * 
	 * <br><br>
	 * <b>Requires:</b><br> A non-empty photoID<br><br>
	 * <b>Effects:</b><br>Returns the body of the response from Flickr
	 * @param photoID
	 */
	public static String getLocation(String photoID){
		// TODO Project Phase Two : Implement this method

		// Hint: Make sure to specify that you want the response in json format
		//String url = "http://api.flickr.com/service/rest/?method=flickr.photos.geo.getLocation&api_key="+apiKey+"&format=json&photo_id="+photoID;

		// Helper function used to get the LatLongs for associated photos
		OAuthRequest req = new OAuthRequest(Verb.GET,PROTECTED_RESOURCE_URL);
		req.addQuerystringParameter("method", "flickr.photos.geo.getLocation");
		req.addQuerystringParameter("api_key", apiKey);
		req.addQuerystringParameter("format", "json");
		req.addQuerystringParameter("photo_id", photoID);
		
		Response resLocation = req.send();
		
		
		// got the body of the following Request
		return resLocation.getBody();
	}



}
