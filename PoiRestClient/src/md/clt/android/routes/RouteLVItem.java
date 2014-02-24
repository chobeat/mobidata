package md.clt.android.routes;

import java.io.Serializable;

import md.clt.android.R;
import md.clt.android.R.drawable;

public class RouteLVItem implements Serializable{

	private static final long serialVersionUID = 6480391456602199033L;
	private Route route;
	private int icon;
	private String text;
	private int length;

	private int popularity;
	public int getPopularity() {
		return popularity;
	}

	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}

	public RouteLVItem(){
		super();
	}
	
	public RouteLVItem(Route route){
		super();
		this.route = route;
		this.text = route.getName();
		this.length= route.getLength().intValue();
		this.popularity=route.getPopularity();
		// TODO: set icon based on category
		this.icon = R.drawable.ic_star;
	}
	
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
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
