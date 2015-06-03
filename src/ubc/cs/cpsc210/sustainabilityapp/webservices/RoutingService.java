package ubc.cs.cpsc210.sustainabilityapp.webservices;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import ubc.cs.cpsc210.sustainabilityapp.model.LatLong;

/**
 * Wrapper around a service which calculates routes between geographic locations.  This class may 
 * be called concurrently from multiple threads -- it is thread-safe.
 * 
 * Currently, this class wraps the www.yournavigation.org API 
 * (<a href="http://wiki.openstreetmap.org/wiki/YOURS#Routing_API">http://wiki.openstreetmap.org/wiki/YOURS#Routing_API</a>).  
 */
public class RoutingService {
	// Project Phase Two : TODO you need to complete the implementation of this class
	
	private final static String LOG_TAG = "RoutingService";
	
	/**
	 * Caches routes retrieved by their endpoints.  Access to this map must be synchronized on the
	 * map.
	 */
	private Map<RouteEndpoints, RouteInfo> routeCache = new HashMap<RouteEndpoints, RouteInfo>();
	
	/** 
	 * Client for making HTTP requests to the API of the service.
	 */
	private HttpClient client;
	private String BASE_ROUTING_URL = "//yours.cs.ubc.ca/yours/api/1.0/gosmore.php";
	
	
	public RoutingService() {
        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will
        // be accessing the HttpClient.
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        
        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
        client = new DefaultHttpClient(cm, params);
	}
	
	public void shutdown() {
		if (client != null) {
			client.getConnectionManager().shutdown();
		}
	}
	
	/**
	 * Calculate route for given start point and end point.  An internet connection must be available.
	 * See {@link getRouteFromService} for further information on route generation.
	 * 
	 * @param start The start point of the route.
	 * @param end The end point of the route.
	 * @param useCache Indicates whether the service should return a cached route, if one exists.  
	 *                 If this flag is set to true, and a cached route is not available, then the new 
	 *                 route obtained from the server will be cached.
	 * @return Information on the route calculated, including the waypoints.
	 * @throws IOException If an error occurs while retrieving the route from the server.
	 */
	public RouteInfo getRoute(LatLong start, LatLong end, boolean useCache) throws IOException {
		 RouteEndpoints endpoints = new RouteEndpoints(start,end);
         RouteInfo info = null;
         if (useCache) {
                 info = getCachedRoute(endpoints);
                 if (info!=null) {
                         return info;
                 }
         }
         info = getRouteFromService(endpoints);
         if (useCache&&info!=null) {
                 addRouteToCache(endpoints,info);
         }
         return info;
	}
	
	/**
	 * Calculate route for given endpoints.  Currently, we use the www.yournavigation.org API
	 * (<a href="http://wiki.openstreetmap.org/wiki/YOURS#Routing_API">http://wiki.openstreetmap.org/wiki/YOURS#Routing_API</a>), 
	 * with result format set to geojson, vehicle set to foot and route type set to shortest (rather than fastest).  Using route 
	 * type of fastest can result in different routes between the same two points, depending on the 
	 * direction traveled.
	 * 
	 * Subclasses can override this method to connect to alternate routing services.
	 * 
	 * @param endpoints Endpoints of the route.
	 * @return Information on the route calculated, including waypoints.
	 * @throws IOException If an error occurs while retrieving the route from the server.
	 */
	private RouteInfo getRouteFromService(RouteEndpoints endpoints) throws IOException {
		 RouteInfo result = null;
         synchronized(client) {
                 try {   
                	 	 String uriStem = BASE_ROUTING_URL;
                         uriStem += "?format=geojson&v=foot&fast=0";
                         uriStem += "&flat="+(endpoints.getStart().getLatitude());
                         uriStem += "&flon="+(endpoints.getStart().getLongitude());
                         uriStem += "&tlat="+(endpoints.getEnd().getLatitude());
                         uriStem += "&tlon="+(endpoints.getEnd().getLongitude());
                         //Log.e("URI: ",uriStem);
                         URI uri = new URI("http", uriStem, null);

                         HttpGet getRequest = new HttpGet(uri);
                         getRequest.addHeader("X-Yours-client","UBC CPSC 210");

                         ResponseHandler<String> responseHandler = new BasicResponseHandler();
                         String response = client.execute(getRequest,responseHandler);

                         JSONObject obj = (JSONObject)new JSONTokener(response).nextValue();

                         List<LatLong> waypoints = new ArrayList<LatLong>();

                         JSONArray coordinates = obj.getJSONArray("coordinates");

                         for (int i = 0; i < coordinates.length(); i++) {
                                 JSONArray element = coordinates.getJSONArray(i);
                                 LatLong wp = new LatLong(element.getDouble(1),element.getDouble(0));
                                 waypoints.add(wp);
                         }
                         result = new RouteInfo(waypoints);
                 } catch (IOException e) { 
                         throw e;
                 } catch (Exception e) {
                         
                         Log.e(LOG_TAG, "Unable to retrieve route , unexpected exception encountered!", e);
                         throw new IOException("Unable to retrieve route from www.yournavigation.org: " + e);
                 }
         }
         return result;
 }
	
	private RouteInfo getCachedRoute(RouteEndpoints endpoints) {
		synchronized (routeCache) {
			return routeCache.get(endpoints);
		}
	}
	
	private void addRouteToCache(RouteEndpoints endpoints, RouteInfo routeInfo) {
		synchronized (routeCache) {
			routeCache.put(endpoints, routeInfo);
		}
	}
}
