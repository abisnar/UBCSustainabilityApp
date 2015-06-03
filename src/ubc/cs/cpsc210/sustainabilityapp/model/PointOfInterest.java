package ubc.cs.cpsc210.sustainabilityapp.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Encapsulates the data for a point-of-interest on the tour.
 */
public class PointOfInterest {
	private String displayName;
	private String id;
	private LatLong LatLong;
	private String description;
	private List<Feature> features = new ArrayList<Feature>();
	private String photoUrl;

	/**
	 * Construct a new point-of-interest.
	 * 
	 * <br><br>
	 * <b>Requires:</b> <br> id must be unique across all POIs.<br><br>
	 * <b>Effects:</b> <br> id and displayName are remembered in object.
	 * 
	 * @param id This must be unique across all POI's.
	 * @param displayName Display name for the POI.  
	 */
	public PointOfInterest(String id, String displayName) {
		this.id = id;
		this.displayName = displayName;
	}
	
	@Override 
	public String toString() {
		return getDisplayName();
	}

	/**
	 * Get POI's unique ID.
	 */
	public String getId() {
		return id;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public LatLong getLatLong() {
		return LatLong;
	}

	public void setLatLong(LatLong point) {
		this.LatLong = point;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setPhotoURL (String url){
		this.photoUrl = url;
	}
	
	public String getPhotoURL(){
		return photoUrl;
	}

	/**
	 * Get the sustainability features of this POI.
	 */
	public List<Feature> getFeatures() {
		return this.features;
	}

	/**
	 * Set the sustainability features associated with this POI.
	 */
	public void setFeatures(Feature[] features) {
		this.setFeatures(Arrays.asList(features));
	}

	/**
	 * Set the sustainability features associated with this POI.
	 */
	public void setFeatures(List<Feature> features) {
		this.features = features;
	}
	
	

}
