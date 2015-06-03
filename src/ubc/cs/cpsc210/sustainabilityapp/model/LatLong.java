package ubc.cs.cpsc210.sustainabilityapp.model;

/**
 * Encapsulates a latitude/longitude pair.
 * 
 * @author CPSC 210 Instructor
 */
public class LatLong {
	private double latitude;
	private double longitude;
	
	public LatLong(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * We override hashCode() and equals(), so that RouteEndpoints (which contains two LatLong values) can
	 * be used as a key in a HashMap.
	 * 
	 * Code for this method was generated using Eclipse: Source > Generate hashCode() and equals().
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * We override hashCode() and equals(), so that RouteEndpoints (which contains two LatLong values) can
	 * be used as a key in a HashMap.
	 * 
	 * Code for this method was generated using Eclipse: Source > Generate hashCode() and equals().
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LatLong other = (LatLong) obj;
		if (Double.doubleToLongBits(latitude) != Double
				.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longitude) != Double
				.doubleToLongBits(other.longitude))
			return false;
		return true;
	}

	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	/**
	 * Find distance between two lat long points
	 * 
	 * <br><br>
	 * <b>Requires:</b> A valid lat long to compare to<br><br>
	 * <b>Effects:</b>The distance between the two points
	 * 
	 * @param other A valid lat long object
	 * @return Distance between the two lat long points
	 */
    public double distanceTo(LatLong other) {
        double lat1 = Math.toRadians(latitude);
        double lon1 = Math.toRadians(longitude);
        double lat2 = Math.toRadians(other.latitude);
        double lon2 = Math.toRadians(other.longitude);

        double r = 6371;  // Earth's radius.
        return r * Math.acos(
            (Math.sin(lat1) * Math.sin(lat2)) +
            (Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1))
        );
    }
	
}
