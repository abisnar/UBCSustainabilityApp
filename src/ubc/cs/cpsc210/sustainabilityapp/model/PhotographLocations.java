package ubc.cs.cpsc210.sustainabilityapp.model;

import java.util.ArrayList;
import java.util.List;

public class PhotographLocations {

	private POIRegistry registry;

	public PhotographLocations(POIRegistry registry) {
		this.registry = registry;
	}

	public List<Photograph> getAllFlickrPhotoLocations() {
		List<Photograph> allFlickrPhotoLocations = new ArrayList<Photograph>();
		for (PointOfInterest poi : registry.getPointsByLocation()) {
			if (poi instanceof Photograph)
				allFlickrPhotoLocations.add((Photograph) poi);
		}
		return allFlickrPhotoLocations;
	}

	public List<Photograph> getThreeClosestToLocation(LatLong location) {
		List<Photograph> closeFlickrPhotoLocations = new ArrayList<Photograph>();
		List<Photograph> allFlickrPhotoLocations = getAllFlickrPhotoLocations();

		for (int i = 0; i < 3; i++) {
			PointOfInterest poi = findClosestPOI(location,
					allFlickrPhotoLocations);
			if (poi != null) {
				closeFlickrPhotoLocations.add((Photograph) poi);
				allFlickrPhotoLocations.remove(poi);
			}
		}
		return closeFlickrPhotoLocations;

	}
	
	private PointOfInterest findClosestPOI(LatLong location,
			List<Photograph> pois) {
		PointOfInterest closest = null;
		double minDistValue = Double.MAX_VALUE;

		for (PointOfInterest poi : pois) {
			double distValue = location.distanceTo(poi.getLatLong());
			if (distValue < minDistValue) {
				minDistValue = distValue;
				closest = poi;
			}
		}

		return closest;
	}

	
}
