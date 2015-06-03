package ubc.cs.cpsc210.sustainabilityapp;

import java.io.IOException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.SimpleLocationOverlay;

import ubc.cs.cpsc210.sustainabilityapp.model.LatLong;
import ubc.cs.cpsc210.sustainabilityapp.model.POIRegistry;
import ubc.cs.cpsc210.sustainabilityapp.model.Photograph;
import ubc.cs.cpsc210.sustainabilityapp.model.PhotographLocations;
import ubc.cs.cpsc210.sustainabilityapp.model.PointOfInterest;
import ubc.cs.cpsc210.sustainabilityapp.model.SharedPreferencesKeyValueStore;
import ubc.cs.cpsc210.sustainabilityapp.model.TourState;
import ubc.cs.cpsc210.sustainabilityapp.webservices.RouteInfo;
import ubc.cs.cpsc210.sustainabilityapp.webservices.RoutingService;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;
import java.net.URL;

/**
 * Fragment holding the map in the UI.
 */
public class MapDisplayFragment extends Fragment {
	// TODO: Project Phase Two: Finish implementing this class

	/**
	 * Log tag for LogCat messages
	 */
	private final static String LOG_TAG = "MapDisplayFragment";

	/**
	 * Location of the ICCS building
	 */
	private final static GeoPoint ICICS_GEOPOINT = new GeoPoint(49.260887,
			-123.24902);

	/**
	 * Overlay for POI markers.
	 */
	private ItemizedIconOverlay<OverlayItem> poiOverlay;

	/**
	 * Overlay for the route connecting the selected POI's.
	 */
	private PathOverlay tourOverlay;

	/**
	 * Overlay for the route connecting the user's current location to the
	 * nearest selected POI.
	 */
	private PathOverlay routeToTourOverlay;

	/**
	 * Manages and stores selected features and POI's.
	 */
	private TourState tourState;

	/**
	 * Currently selected POI's.
	 */
	private List<PointOfInterest> selectedPOIs;

	/**
	 * Wrapper for a service which calculates routes between POI's, and between
	 * the user's current location and the nearest selected POI.
	 */
	private RoutingService routingService;

	/**
	 * View that shows the map
	 */
	private MapView mapView;


	/**
	 * Route retriever services
	 */
	private RouteRetriever poiRouteRetriever;
	private RouteRetriever toTourRouteRetriever;


	// TODO: Add fields necessary to show the photographs from flickr
	// We've given you one of the fields that you'll need
	private List<Photograph> selectedPhotos = new ArrayList<Photograph>();
	// made by Allan: used to get the POIOverlays
	private PhotographLocations photoLocations;
	// used for producing the actual photos
	private ItemizedIconOverlay<OverlayItem> photoOverlay;
	private ImageView photoView;
	/**
	 * Get routing service, current state of tour and initialize location
	 * services.
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(LOG_TAG, "onActivityCreated");

		routingService = ((UBCSustainabilityAppActivity) getActivity())
				.getRoutingService();

		tourState = new TourState(POIRegistry.getDefault(),
				new SharedPreferencesKeyValueStore(getActivity(),
						TourState.STORE_NAME));

	}

	/**
	 * Set up map view with overlays for points of interest, current location
	 * and tour.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(LOG_TAG, "onCreateView");

		if (mapView == null) {
			mapView = new MapView(getActivity(), null);

			mapView.setTileSource(TileSourceFactory.MAPNIK);
			mapView.setClickable(true);
			mapView.setBuiltInZoomControls(true);

			MapController mapController = mapView.getController();

			if (savedInstanceState == null) {
				// With the Mapnik server, this zoom level results in a map
				// which
				// encompasses most of the UBC campus.
				mapController.setZoom(mapView.getMaxZoomLevel() - 4);

				// Center the map on ICICS.
				mapController.setCenter(ICICS_GEOPOINT);
			} else {
				// restore previous zoom level and map centre
				mapController.setZoom(savedInstanceState.getInt("zoomLevel"));
				int lat = savedInstanceState.getInt("latE6");
				int lon = savedInstanceState.getInt("lonE6");
				GeoPoint cntr = new GeoPoint(lat, lon);
				mapController.setCenter(cntr);
			}

			poiOverlay = createPOIOverlay();
			// made by Allan create the photoGraphOverlays on create
			photoOverlay = createPhotographOverlay();
			tourOverlay = createTourOverlay();
			routeToTourOverlay = createRouteToTourOverlay();

			// Order matters: overlays added later are displayed on top of
			// overlays added earlier.
			mapView.getOverlays().add(tourOverlay);
			mapView.getOverlays().add(routeToTourOverlay);
			mapView.getOverlays().add(poiOverlay);
			//ensure the PhotoOverlays are added on top of POIOverlays
			mapView.getOverlays().add(photoOverlay);

		}

		return mapView;
	}

	/**
	 * When view is destroyed, remove map view from its parent so that it can be
	 * added again when view is re-created. Stop route retriever threads as the
	 * view is about to be destroyed.
	 */
	@Override
	public void onDestroyView() {
		Log.d(LOG_TAG, "onDestroyView");

		// interrupt the RouteRetrieverThreads
		if (poiRouteRetriever != null)
			poiRouteRetriever.interrupt();

		if (toTourRouteRetriever != null)
			toTourRouteRetriever.interrupt();

		((ViewGroup) mapView.getParent()).removeView(mapView);

		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "onDestroy");

		mapView = null;

		super.onDestroy();
	}

	/**
	 * Update the overlays based on the selected POI's and the user's current
	 * location. Request location updates.
	 */
	@Override
	public void onResume() {
		Log.d(LOG_TAG, "onResume");

		selectedPOIs = tourState.getSelectedPOIs();

		updateTour(selectedPOIs);

		super.onResume();
	}

	/**
	 * Cancel location updates.
	 */
	@Override
	public void onPause() {
		Log.d(LOG_TAG, "onPause");

		super.onPause();
	}

	/**
	 * Save map's zoom level and centre.
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (mapView != null) {
			outState.putInt("zoomLevel", mapView.getZoomLevel());
			IGeoPoint cntr = mapView.getMapCenter();
			outState.putInt("latE6", cntr.getLatitudeE6());
			outState.putInt("lonE6", cntr.getLongitudeE6());
			Log.i("MapSave", "Zoom: " + mapView.getZoomLevel());
		}
	}

	/**
	 * Selected POIs has changed so update tour, location and repaint.
	 */
	void update() {

		selectedPOIs = tourState.getSelectedPOIs();
		updateTour(selectedPOIs);
		mapView.invalidate();
	}

	/**
	 * Update the POI markers and the route connecting them.
	 */
	private void updateTour(List<PointOfInterest> pois) {
		
		//ensure POIOverlays are cleared each time updated
		poiOverlay.removeAllItems();

		//plot POIOverlays for each pois on the map
		if (pois != null) {
			for (PointOfInterest poi : pois) {
				plotPOI(poiOverlay, poi);
			}
		}
		//reset the tourOverlay
		tourOverlay.clearPath();
		
		//Requires: a non empty list of POIs with at least 2 POIS
		// add pois by their corresponding latLong to pointsOnTour
		// call helper function findROuteAndUpdateOverlay to update the tour
		if (pois != null && pois.size() > 1) {
			List<LatLong> pointsOnTour = new ArrayList<LatLong>();
			for (PointOfInterest poi : pois) {
				pointsOnTour.add(poi.getLatLong());
			}
			pointsOnTour.add(pois.get(0).getLatLong());

			findRouteAndUpdateOverlay(poiRouteRetriever, tourOverlay,
					pointsOnTour, true);
		}

	}

	/**
	 * Plot a POI on the specified overlay.
	 */
	private void plotPOI(ItemizedIconOverlay<OverlayItem> overlay,
			PointOfInterest poi) {

		GeoPoint point = new GeoPoint(poi.getLatLong().getLatitude(), poi
				.getLatLong().getLongitude());
		OverlayItem overlayItem = new OverlayItem(poi.getDisplayName(),
				poi.getDescription(), point);
		overlay.addItem(overlayItem);

	}

	// made by Allan
	// clears photoOverlays
	private void clearPhotos() {
		// TODO: Implement this method
		photoOverlay.removeAllItems();
	}


	/**
	 * Create the overlay for POI markers.
	 */
	private ItemizedIconOverlay<OverlayItem> createPOIOverlay() {
		ResourceProxy rp = new DefaultResourceProxyImpl(getActivity());

		OnItemGestureListener<OverlayItem> gestureListener = new OnItemGestureListener<OverlayItem>() {
			/**
			 * Display POI's title and description in dialog box when user taps
			 * it.
			 * 
			 * @param index
			 *            index of item tapped
			 * @param oi
			 *            the OverlayItem that was tapped
			 * @return true to indicate that tap event has been handled
			 */
			@Override
			public boolean onItemSingleTapUp(int index, OverlayItem oi) {
				final PointOfInterest poi = selectedPOIs.get(index);
				clearPhotos();

				AlertDialog.Builder dialog = new AlertDialog.Builder(
						getActivity());



				DialogInterface.OnClickListener showPhotos = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						// Retrieved all of the LatLongs associated with each poi
						// instantiated the photoLocations by accessing our POIRegistry
						// called getThreeClostestToLocations from our latlong stored as a list of photos

						LatLong refLatLong = poi.getLatLong();
						photoLocations = new PhotographLocations(POIRegistry.getDefault());

						selectedPhotos = photoLocations.getThreeClosestToLocation(refLatLong);

						// ensure there is no photoOverlays on map
						clearPhotos();

						// traverse the list of photos closest to POI
						// plot the photoOverlay for each photo closest to the POI
						for (Photograph p : selectedPhotos) {
							plotPOI(photoOverlay, p);
						}

						//ensure the map is updated
						update();

						// STOP ADDING CODE HERE
						dialog.cancel();

					}
				};

				DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				};

				ImageView displayView;

				displayView = new ImageView(getActivity(), null,
						android.R.drawable.ic_dialog_alert);

				Bitmap displayBmp = getBitmapFromUrl(poi.getPhotoURL());
				displayView.setImageBitmap(displayBmp);

				dialog.setView(displayView);

				dialog.setPositiveButton("Nearest Photos", showPhotos);
				dialog.setNegativeButton("Cancel", cancel);

				dialog.setTitle(poi.getDisplayName());
				dialog.setMessage(poi.getDescription());


				dialog.show();
				return true;
			}

			@Override
			public boolean onItemLongPress(int index, OverlayItem oi) {
				// do nothing
				return false;
			}
		};

		return new ItemizedIconOverlay<OverlayItem>(
				new ArrayList<OverlayItem>(), getResources().getDrawable(
						R.drawable.map_pin_blue), gestureListener, rp);
	}
	//helper function to get a bitmap image from the image url
	private Bitmap getBitmapFromUrl(String myUrl)
	{
		Bitmap bmp = null;

		try{

			URL url = new URL(myUrl);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setDoInput(true);
			connection.connect();

			InputStream input = connection.getInputStream();
			bmp = BitmapFactory.decodeStream(input);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return bmp;
	}

	/**
	 * Create the overlay for POI markers.
	 */
	private ItemizedIconOverlay<OverlayItem> createPhotographOverlay() {
		ResourceProxy rp = new DefaultResourceProxyImpl(getActivity());

		OnItemGestureListener<OverlayItem> gestureListener = new OnItemGestureListener<OverlayItem>() {
			/**
			 * Display photograph's title and the photo in dialog box when user
			 * taps it.
			 * 
			 * @param index
			 *            index of item tapped
			 * @param oi
			 *            the OverlayItem that was tapped
			 * @return true to indicate that tap event has been handled
			 */
			@Override
			public boolean onItemSingleTapUp(int index, OverlayItem oi) {

				// Tap should be impossible in this situation.
				if (selectedPhotos.isEmpty()) {
					return true;
				}

				final Photograph clicked = selectedPhotos.get(index);
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						getActivity());
				DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				};

				dialog.setNegativeButton("Cancel", cancel);

				// START ADDING CODE HERE
				// TODO: Add code here that displays the photograph as an image
				// on the dialog

				Bitmap bmp;
				String myUrl = clicked.getURL();
				photoView = new ImageView(getActivity(), null,
						android.R.drawable.ic_dialog_alert);
				bmp = getBitmapFromUrl(myUrl);
				photoView.setImageBitmap(bmp);
				dialog.setTitle(clicked.getDisplayName());
				dialog.setMessage("Image from flickr.com");
				dialog.setView(photoView);
				// STOP ADDING CODE HERE

				dialog.show();
				return true;
			}

			@Override
			public boolean onItemLongPress(int index, OverlayItem oi) {
				// do nothing
				return false;
			}
		};

		return new ItemizedIconOverlay<OverlayItem>(
				new ArrayList<OverlayItem>(), getResources().getDrawable(
						R.drawable.map_pin_green), gestureListener, rp);
	}

	/**
	 * Create the overlay for the route connecting the selected POI's.
	 */
	private PathOverlay createTourOverlay() {
		PathOverlay po = new PathOverlay(Color.RED, getActivity());
		Paint pathPaint = new Paint();
		pathPaint.setColor(Color.RED);
		pathPaint.setStrokeWidth(4.0f);
		pathPaint.setStyle(Style.STROKE);
		po.setPaint(pathPaint);
		return po;
	}

	/**
	 * Create the overlay connecting the user's current location to the closest
	 * selected POI.
	 */
	private PathOverlay createRouteToTourOverlay() {
		PathOverlay po = new PathOverlay(Color.BLUE, getActivity());
		Paint pathPaint = new Paint();
		pathPaint.setColor(Color.MAGENTA);
		pathPaint.setStrokeWidth(4.0f);
		pathPaint.setStyle(Style.STROKE);
		po.setPaint(pathPaint);
		return po;
	}

	/**
	 * Halts current route retriever service if it's running. Creates new route
	 * retriever and calls the routing service to obtain a route which connects
	 * the specified list of lat/long points.
	 * 
	 * @param retriever
	 *            current route retriever
	 * @param overlay
	 *            The way overlay which will be updated with the resulting
	 *            route.
	 * @param points
	 *            Points which the route must pass through.
	 * @param useCache
	 *            If set to true, the routing service will return a cached route
	 *            if one is available (and will cache the result if no cached
	 *            route is found).
	 * @return new route retriever instance
	 */
	private RouteRetriever findRouteAndUpdateOverlay(RouteRetriever retriever,
			PathOverlay overlay, List<LatLong> points, boolean useCache) {
		// Retrieve routes in a separate thread, as it can take some time and we
		// do not want to
		// block the UI thread.
		if (retriever != null && retriever.isAlive()) { // thread is still
			// running so interrupt
			// it
			retriever.interrupt();
			overlay.clearPath();
		}

		retriever = new RouteRetriever(overlay, points, useCache);
		retriever.start();
		return retriever;
	}

	/**
	 * Add a route to the specified overlay.
	 */
	private void addRouteToOverlay(PathOverlay overlay, List<LatLong> waypoints) {

		for (LatLong latLong : waypoints) {
			GeoPoint point = new GeoPoint(latLong.getLatitude(),
					latLong.getLongitude());
			tourOverlay.addPoint(point);
		}
	}

	/**
	 * Calls the routing service to obtain a route which connects the specified
	 * list of lat/long points, and updates the overlay provided with the
	 * resulting route.
	 * 
	 * Routes are retrieved in a separate thread, as it can take some time and
	 * we do not want to block the UI thread.
	 */
	private class RouteRetriever extends Thread {
		private PathOverlay overlay;
		private List<LatLong> points;
		private boolean useCache;
		private boolean routeRetrieved;

		public RouteRetriever(PathOverlay overlay, List<LatLong> points,
				boolean useCache) {
			this.overlay = overlay;
			this.points = points;
			this.useCache = useCache;
			this.routeRetrieved = false;
		}

		@Override
		public void run() {

			try {
				if (points.size() > 1) {
					int i = 1;
					final List<LatLong> waypoints = new ArrayList<LatLong>();

					while (i < points.size() && !isInterrupted()) {
						LatLong currPoint = points.get(i - 1);
						LatLong nextPoint = points.get(i);
						RouteInfo info = routingService.getRoute(currPoint,
								nextPoint, useCache);

						if (info != null) {
							waypoints.add(currPoint);
							waypoints.addAll(info.getWaypoints());
							waypoints.add(nextPoint);
						}

						i++;
					}

					if (!isInterrupted()) {
						// Updates to the UI must run on the UI thread.
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								addRouteToOverlay(overlay, waypoints);
								mapView.invalidate();
							}

						});

						routeRetrieved = true;
					}
				}
			} catch (Exception e) {
				Log.e(LOG_TAG, "Error retrieving route from route service");

			} finally {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// display message to user
						if (!routeRetrieved) {
							Toast toast = Toast.makeText(getActivity(),
									R.string.rs_na_label, Toast.LENGTH_SHORT);
							toast.show();
						}
					}
				});
			}
		}
	}
}
