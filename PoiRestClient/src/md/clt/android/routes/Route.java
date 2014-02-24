package md.clt.android.routes;

import java.io.Serializable;
import java.util.List;

import md.clt.android.POI.Poi;


public class Route implements Serializable{
	
	private static final long serialVersionUID = 7264798056035332982L;
	private String id;
    private String name;
    private String shape;
    private Double length;
    private int popularity;
	public int getPopularity() {
		return popularity;
	}
	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}
	
	public Double getLength() {
		return length;
	}
	public void setLength(Double length) {
		this.length = length;
	}

	private List<Poi> path;
	
    public String getShape() {
		return shape;
	}
	public void setShape(String shape) {
		this.shape = shape;
	}
	// private String category;
    public List<Poi> getPath() {
		return path;
	}
	public void setPath(List<Poi> path) {
		this.path = path;
	}
 
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    /*public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }*/
     
    public Route(String id, String name, String category,List<Poi> path) {
   	    this.id = id;
        this.name = name;
     //   this.category = category;
        this.path=path;
    }
     
    public Route(String id, String name, String category) {	 
        this.id = id;
        this.name = name;
       // this.category = category;
       
    }
      
    public Route() {
        this.id = "-1";
        this.name = "";
   //     this.category = "";
    }
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
      
}