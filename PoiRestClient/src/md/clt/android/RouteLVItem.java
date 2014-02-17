package md.clt.android;

import java.io.Serializable;

public class RouteLVItem implements Serializable{

	private static final long serialVersionUID = 6480391456602199033L;
	private Route route;
	private int icon;
	private String text;
	private String length;

	private String popularity;
	public String getPopularity() {
		return popularity;
	}

	public void setPopularity(String popularity) {
		this.popularity = popularity;
	}

	public RouteLVItem(){
		super();
	}
	
	RouteLVItem(Route route){
		super();
		this.route = route;
		this.text = route.getName();
		this.length= ""+route.getLength();
		this.popularity=""+route.getPopularity();
		// TODO: set icon based on category
		this.icon = R.drawable.ic_star;
	}
	
	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public int getIcon(){
		return icon;
	}
	
	public String getText(){
		return text;
	}
	
	public Route getRoute(){
		return route;
	}
}
