package ubc.cs.cpsc210.sustainabilityapp.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import android.util.Log;

import ubc.cs.cpsc210.sustainabilityapp.webservices.FlickrParser;
import ubc.cs.cpsc210.sustainabilityapp.webservices.FlickrService;


public class POIRegistry {
	
	/**
	 * Static default instance.
	 */
	private static POIRegistry defaultInstance = createDefaultInstance();
	
	/**
	 * Maps from POI ID to corresponding POI.
	 */
	private Map<String, PointOfInterest> registeredPoints = new HashMap<String, PointOfInterest>();
	
	/**
	 * Stores POI's according to their geographical location.  This defines the ordering of the POI's
	 * on the walking tour.  
	 */
	private List<PointOfInterest> pointsByLocation = new ArrayList<PointOfInterest>();

	
	public static POIRegistry getDefault() {
		return defaultInstance;
	}
	
	/**
	 * Creates the default POIRegistry instance, which contains a set of
	 * hard-coded UBC sustainability POI's.
	 */
	private static POIRegistry createDefaultInstance() {
			POIRegistry result = new POIRegistry();
			
			Building point;

			// Ordering is important here -- this defines the ordering of the points on the walking tour.
			point = new Building("LAW", "Law Building");
			point.setAddress("1822 East Mall, Vancouver, BC");
			point.setLatLong(new LatLong(49.269041,-123.25319));
			point.setDescription("LEED Gold certification. Renovating the old building would have cost four-fifths the amount to " +
					"construct a new building. In contrast to the Chemistry Centre, UBC decided to construct a new law building " +
					"instead of renovating -- each project is evaluated individually to determine what is best " +
					"environmentally, economically and socially. The Faculty of Law outgrew the old building so now " +
					"they will enjoy state-of-the-art teaching and research spaces that promote legal discussions and " +
					"student engagement.");
			point.setFeatures(new Feature[]{Feature.LEED_CERTIFICATION});
			point.setPhotoURL("http://farm9.staticflickr.com/8510/8601903932_e04939659b.jpg");
			result.addPoint(point);

			point = new Building("CK_CHOI", "CK Choi Building");
			point.setAddress("1855 West Mall, Vancouver, BC");
			point.setLatLong(new LatLong(49.26786,-123.2582));
			point.setDescription("Representative of the first green buildings in North America, along with the " +
					"Liu Centre for Global Issues. No certification because this was the one buildings to start " +
					"the green building movement in North America. Made out of 50% recycled or reused materials, " +
					"including bricks that are over 300 years old! All sewage treated on site with composting " +
					"toilets and a wetland inspired by NASA.");
			point.setFeatures(new Feature[]{Feature.LOW_IMPACT_MATERIALS, Feature.WASTEWATER_TREATMENT});
			point.setPhotoURL("http://farm9.staticflickr.com/8386/8600805361_6f21f2f19a.jpg");
			result.addPoint(point);
			
			point = new Building("CHEM", "Chemistry Centre");
			point.setAddress("2036 Main Mall, Vancouver, BC");
			point.setLatLong(new LatLong(49.266179,-123.253805));
			point.setDescription("By reusing 100% of the exterior shell and 60% of the interior elements " +
					"the renewal project diverted 80% of the construction waste into recycling and away from " +
					"the landfill. High-efficiency lighting and heat recovery inside the building reduces energy " +
					"use by 21% compared to the original building.");
			point.setFeatures(new Feature[] {Feature.LOW_IMPACT_MATERIALS});
			point.setPhotoURL("http://farm9.staticflickr.com/8250/8601902864_d4ef233684.jpg");
			result.addPoint(point);

			point = new Building("CIRS", "CIRS");
			point.setAddress("2260 West Mall, Vancouver, BC");
			point.setLatLong(new LatLong(49.261763,-123.253411));
			point.setDescription("CIRS is a state-of-the-art 'living laboratory' where researchers and " +
					"industry partners can explore current and future " +
					"building systems and technologies, using the building itself as their lab. " +
					"The building sports a range of features, including a rain water harvest system, a deciduous facade, " +
					"geothermal heating and cooling, wastewater treatment, a green roof, and more. Also check out nearby " +
					"Sustainability Street, a demonstration urban landscape that collects rainwater from the street's watershed and stores it " +
					"in an underground well for use in CIRS.");
			point.setFeatures(new Feature[]{Feature.GEOTHERMAL, Feature.LOW_IMPACT_MATERIALS, Feature.RAINWATER_RECOVERY, 
					Feature.SOLAR_ENERGY, Feature.WASTEWATER_TREATMENT, Feature.LEED_CERTIFICATION});
			point.setPhotoURL("http://farm9.staticflickr.com/8389/8600800103_8fa224a8a1.jpg");
			result.addPoint(point);

			point = new Building("BRDC", "Bioenergy Research");
			point.setAddress("Agronomy Road and Lower Mall");
			point.setLatLong(new LatLong(49.260231,-123.253766));
			point.setDescription("Creating energy from carbon neutral sources is critical to ensure environmental " +
					"sustainability. UBC will use Vancouver's wood waste and turn it into synthetic gas, as " +
					"opposed to gas from the Earth's crust, to run a generator that will provide enough steam " +
					"to heat 25% of the campus or sufficient electricity to power the 1,600 bed Marine Drive " +
					"Student Residence. This generator will be housed inside the first Cross Laminated Timber " +
					"building in North America.");
			point.setFeatures(new Feature[] {Feature.BIOFUEL});
			point.setPhotoURL("http://farm9.staticflickr.com/8513/8601899404_44cbe0d5ae.jpg");
			result.addPoint(point);

			point = new Building("UBC_FARM", "UBC Farm");
			point.setAddress("6186 South Campus Road, Vancouver, BC");
			point.setLatLong(new LatLong(49.25143,-123.238181));
			point.setDescription("The UBC Farm is a 24-hectare learning and research farm located at the " +
					"south end of the main campus. The farm is student-driven and " +
					"integrated with the wider community. As the only working farmland within the city of Vancouver, " +
					"the UBC Farm is an urban agrarian gem, featuring a landscape of unique beauty.");
			point.setFeatures(new Feature[]{Feature.SUSTAINABLE_AGRICULTURE});
			point.setPhotoURL("http://farm9.staticflickr.com/8118/8602090648_8b2b34ddb1.jpg");
			result.addPoint(point);
			
			point = new Building("LSC", "Life Sciences Centre");
			point.setAddress("2350 Health Sciences Mall, Vancouver, BC");
			point.setLatLong(new LatLong(49.26239,-123.246244));
			point.setDescription("Canada's largest LEED Gold building. Over 3,500 people learn and do " +
					"cutting-edge medical research every day. The building does not disrupt its surrounding " +
					"watershed, nor does it pollute the night sky with light.");
			point.setFeatures(new Feature[] {Feature.LEED_CERTIFICATION});
			point.setPhotoURL("http://farm9.staticflickr.com/8110/8601906774_f859c25cc7.jpg");
			result.addPoint(point);
			
			point = new Building("BEATY_BIO", "Beaty Biodiversity Centre");
			point.setAddress("2212 Main Mall, Vancouver, BC");
			point.setLatLong(new LatLong(49.26353,-123.25153));
			point.setDescription("The building has innovative sustainability features such as a green roof " +
					"and water channel that supports aquatic plants and insects while helping reduce storm water surges."+
					"This is where Allan sprained his knee and ankle right after this shot!");
			point.setFeatures(new Feature[] {Feature.LEED_CERTIFICATION});
			point.setPhotoURL("http://farm9.staticflickr.com/8369/8600802137_5cec61ab9b.jpg");
			result.addPoint(point);
			
			



		FlickrService flickrService = new FlickrService();
		// Ask for results near ICICS
		String flickrResult = flickrService.search("ubccpsc210", 49.260887, -123.24902);
		if (flickrResult != null && flickrResult.length() > 0) {
			List<Photograph> photos = FlickrParser.parse(flickrResult);
			Log.e("Photos Result", photos.toString());
			if (photos != null && photos.size() > 0) {
				for (Photograph photo : photos)
					result.addPoint(photo);
			}
		}

		

		return result;
	}
	
	/**
	 * Given a POI ID, looks up the corresponding POI.
	 * 
	 * @param id Unique ID of the POI. 
	 * @return The corresponding POI, or null if no matching POI is found.
	 */
	public PointOfInterest lookupPoint(String id) {
		return registeredPoints.get(id);
	}

	/**
	 * Get POI's in alphabetical order, sorted by display name.
	 */
	public List<PointOfInterest> getPointsAlphabetical() {
		List<PointOfInterest> result = new ArrayList<PointOfInterest>(pointsByLocation);
		Collections.sort(result, new Comparator<PointOfInterest>() {

			@Override
			public int compare(PointOfInterest object1, PointOfInterest object2) {
				return object1.getDisplayName().compareTo(object2.getDisplayName());
			}
			
		});
		return result;
	}
	
	/**
	 * Get POI's in walking tour order.
	 */
	public List<PointOfInterest> getPointsByLocation() {
		return pointsByLocation;
	}

	/**
	 * Get POI's which display a specified sustainability feature.
	 */
	public List<PointOfInterest> getPointsWithFeature(Feature feature) {
		List<PointOfInterest> result = new ArrayList<PointOfInterest>();
		for (PointOfInterest point: registeredPoints.values()) {
			if (point.getFeatures().contains(feature)) {
				result.add(point);
			}
		}
		return result;
	}
	
	protected void addPoint(PointOfInterest point) {
		registeredPoints.put(point.getId(), point);
		pointsByLocation.add(point);
	}
}
