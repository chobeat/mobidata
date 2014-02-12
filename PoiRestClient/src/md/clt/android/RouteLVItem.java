package md.clt.android;

import java.io.Serializable;

public class RouteLVItem implements Serializable{

	private static final long serialVersionUID = 6480391456602199033L;
	private Route route;
	private int icon;
	private String text;
	
	public RouteLVItem(){
		super();
	}
	
	RouteLVItem(Route route){
		super();
		this.route = route;
		this.text = route.getName();
		// TODO: set icon based on category
		this.icon = R.drawable.ic_star;
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
