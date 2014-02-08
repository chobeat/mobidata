package md.clt.android;

import java.io.Serializable;

public class PoiLVItem implements Serializable{

	private static final long serialVersionUID = 6480391456602199033L;
	private Poi poi;
	private int icon;
	private String text;
	
	public PoiLVItem(){
		super();
	}
	
	PoiLVItem(Poi poi){
		super();
		this.poi = poi;
		this.text = poi.getName();
		// TODO: set icon based on category
		this.icon = R.drawable.ic_star;
	}
	
	public int getIcon(){
		return icon;
	}
	
	public String getText(){
		return text;
	}
	
	public Poi getPoi(){
		return poi;
	}
}
