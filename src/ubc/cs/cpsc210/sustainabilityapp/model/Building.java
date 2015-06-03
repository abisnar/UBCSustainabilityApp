package ubc.cs.cpsc210.sustainabilityapp.model;

public class Building extends PointOfInterest {

	private String address;
	
	public Building(String id, String displayName) {
		super(id, displayName);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
