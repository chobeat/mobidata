package md.clt.android;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Poi implements Serializable{
	
	private static final long serialVersionUID = 7264798056035332982L;
	private String id;
    private String name;
    private String category;
    private double lat;
    private double lng;
    private String address;
    public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
     
 
    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    
    public double getLng() {
        return lng;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }
    
    public LatLng getLatLng(){
    	return new LatLng(lat,  lng);
    }
    
    public Poi(String id, String name, String category, double lat, double lng) {
   	    this.id = id;
        this.name = name;
        this.category = category;
        this.lat = lat;
        this.lng = lng;
    }
     
    public Poi(String id, String name, String category) {	 
        this.id = id;
        this.name = name;
        this.category = category;
        this.lat = 0.0;
        this.lng = 0.0;
    }
      
    public Poi() {
        this.id = "-1";
        this.name = "";
        this.category = "";
        this.lat = 0.0;
        this.lng = 0.0;
    }
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
      
}