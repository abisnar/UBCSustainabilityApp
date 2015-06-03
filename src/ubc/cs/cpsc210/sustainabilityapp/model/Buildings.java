package ubc.cs.cpsc210.sustainabilityapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides access to just the buildings stored in the registry. Looks up information
 * about buildings from the registry upon demand.
 */
public class Buildings {

	// Use the registry to access buildings
	private POIRegistry registry;

	/**
	 * Constructor
	 * 
	 * <br><br>
	 * <b>Requires:</b><br> An initialized registry of points of interest.<br><br>
	 * <b>Modifies:</b><br>(Like all constructors creates this!)<br><br>
	 * <b>Effects:</b><br>An initialized Buildings object
	 * @param registry
	 */
	public Buildings(POIRegistry registry) {
		this.registry = registry;
	}

	/**
	 * Return the buildings in walking tour order (the order they were loaded into the
	 * registry)
	 * 
	 * <br><br>
	 * <b>Effects:</b><br>Returns a possibly empty list of buildings in walking tour order.<br><br>
	 * 
	 * @return A possibly empty list of buildings in walking tour order
	 */
	public List<PointOfInterest> getBuildingsByLocation() {
		List<PointOfInterest> buildingsByLocation = new ArrayList<PointOfInterest>();

		// Points come back from registry by location.
		List<PointOfInterest> pointsByLocation = registry.getPointsByLocation();
		// Pick out buildings
		for (PointOfInterest poi : pointsByLocation) {
			if (poi instanceof Building)
				buildingsByLocation.add((Building) poi);
		}
		return buildingsByLocation;
	}

	/**
	 * Return a list of buildings with the specified feature.
	 * 
	 * <br><br>
	 * <b>Requires:</b><br> The feature of interest.<br><br>
	 * <b>Effects:</b><br> Returns a possibly empty list of buildings.<br><br>
	 * 
	 * @param feature The feature of interest
	 * @return Returns a possibly empty list of buildings
	 */
	public List<PointOfInterest> getBuildingsWithFeature(Feature feature) {
		List<PointOfInterest> buildingsWithFeature = new ArrayList<PointOfInterest>();
		for (PointOfInterest poi: registry.getPointsByLocation()) {
			if (poi instanceof Building) {
				Building aBuilding = (Building) poi;
				if (aBuilding.getFeatures().contains(feature))
					buildingsWithFeature.add(aBuilding);
			}
		}
		return buildingsWithFeature;
	}
	
	/**
	 * Return a list of buildings sorted in alphabetical (case insensitive) order by id
	 * 
	 * <br><br>
	 * <b>Effects</b>:<br> Return a list of buildings sorted in alphabetical (case insensitive) order by id<br><br>
	 * 
	 * @return Return a list of buildings sorted in alphabetical (case insensitive) order by id
	 */
	public List<PointOfInterest> getBuildingsAlphabetically() {
		List<PointOfInterest> buildingsAlphabetically = new ArrayList<PointOfInterest>();
		for (PointOfInterest poi: registry.getPointsAlphabetical()) {
			if (poi instanceof Building)
				buildingsAlphabetically.add((Building) poi);
		}
		return buildingsAlphabetically;
	}
	
}
