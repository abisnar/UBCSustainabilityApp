package ubc.cs.cpsc210.sustainabilityapp.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages and stores the tour state: the set of selected POI's and sustainability features.
 */
public class TourState {

	/** 
	 * Storage key for selected POI's.
	 */
	private final static String SELECTED_POIS = "SelectedPOIs";
	
	/**
	 * Storage key for selected features.
	 */
	private final static String SELECTED_FEATURES = "SelectedFeatures";
	
	/**
	 * Registry storing all available POI's.
	 */
	private POIRegistry registry;
	
	/**
	 * Persistent key-value store used to maintain selected POI's and features.
	 */
	private KeyValueStore store;
	
	/**
	 * Name of key-value storage instance.
	 */
	public final static String STORE_NAME = "TourState";

	public TourState(POIRegistry registry, KeyValueStore store) {
		this.registry = registry;
		this.store = store;
	}
	
	/**
	 * Get currently selected POI's from storage, in walking-tour order.
	 * 
	 * @return Currently-selected POI's in walking-tour order.  If the storage field for selected 
	 *         POI's is uninitialized, then all of the available POI's are returned, in walking-tour order.
	 */
	public List<PointOfInterest> getSelectedPOIs() {
		List<PointOfInterest> result = null;
		List<String> pointIds = store.getStringList(SELECTED_POIS);
		if (pointIds == null) {
			result = registry.getPointsByLocation();
		} else {
			result = new ArrayList<PointOfInterest>();
			// Ensure that POI's are returned in walking-tour order.
			for (PointOfInterest point: registry.getPointsByLocation()) {
				if (pointIds.contains(point.getId())) {
					result.add(point);
				}
			}
		}
		return result;
	}

	/**
	 * Get currently selected features from storage.
	 * 
	 * @ return Currently-selected features.  If the storage field for selected POI's is uninitialized, then
	 *          all of the available features are returned.
	 */
	public List<Feature> getSelectedFeatures() {
		List<Feature> result = null;
		
		List<String> featureIds = store.getStringList(SELECTED_FEATURES);
		if (featureIds == null) {
			result = Arrays.asList(Feature.values());
		} else {
			result = new ArrayList<Feature>();
			for (String featureId: featureIds) {
				try {
					result.add(Feature.valueOf(featureId));
				} catch (Exception e) {}
			}
		}
		return result;
	}

	/**
	 * Set the selected POI's.  The set of selected features is also updated.  The rule is that
	 * if all the POI's that contain a particular feature are in selectedPOIs, then that feature is 
	 * set as selected.  Otherwise, the feature is deselected.
	 */
	public void setSelectedPOIs(List<PointOfInterest> selectedPOIs) {
		updateSelectedPOIsField(selectedPOIs);
		
		List<Feature> selectedFeatures = new ArrayList<Feature>();
		for (Feature feature: Feature.values()) {
			List<PointOfInterest> pointsWithFeature = registry.getPointsWithFeature(feature);
			if (pointsWithFeature != null && pointsWithFeature.size() > 0 & selectedPOIs.containsAll(pointsWithFeature)) {
				selectedFeatures.add(feature);
			}
		}
		
		updateSelectedFeaturesField(selectedFeatures);
	}
	
	/**
	 * Set the selected features.  The set of selected POI's is also updated.  The rule is that
	 * if a POI contains a feature which is present in selectedFeatures, then it becomes selected.  Otherwise,
	 * the POI is deselected.
	 */
	public void setSelectedFeatures(List<Feature> selectedFeatures) {
		updateSelectedFeaturesField(selectedFeatures);
		
		Set<PointOfInterest> selectedPOIs = new HashSet<PointOfInterest>();
		
		for (Feature feature: selectedFeatures) {
			selectedPOIs.addAll(registry.getPointsWithFeature(feature));
		}
		
		updateSelectedPOIsField(new ArrayList<PointOfInterest>(selectedPOIs));
	}
	
	/**
	 * Update the storage field for selected POI's.
	 */
	private void updateSelectedPOIsField(List<PointOfInterest> selectedPOIs) {
		List<String> pointIds = new ArrayList<String>();
		for (PointOfInterest point: selectedPOIs) {
			pointIds.add(point.getId());
		}
		store.putStringList(SELECTED_POIS, pointIds);
	}

	/**
	 * Update the storage field for selected features.
	 */
	private void updateSelectedFeaturesField(List<Feature> selectedFeatures) {
		List<String> featureIds = new ArrayList<String>();
		for (Feature feature: selectedFeatures) {
			featureIds.add(feature.name());
		}
		store.putStringList(SELECTED_FEATURES, featureIds);
	}
}
