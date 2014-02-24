package md.clt.android.routes;

import java.util.ArrayList;
import java.util.StringTokenizer;

import md.clt.android.R;
import md.clt.android.POI.Poi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

/* While the Fragment API was introduced in HONEYCOMB (Android 3.0), 
 * a version of the API at is also available for use on older platforms 
 * through FragmentActivity
 */
public class RouteMap extends FragmentActivity {

	private GoogleMap pMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent callerIntent = getIntent();
		final LatLng userCoord = callerIntent.getParcelableExtra("userCoord");
		final ArrayList<Poi> pois = (ArrayList<Poi>) callerIntent.getSerializableExtra("pois");
		setContentView(R.layout.activity_routes_map);
		setUpMapIfNeeded(userCoord);
		
		pMap.setInfoWindowAdapter(new InfoWindowAdapter() {
			

	        @Override
	        public View getInfoWindow(Marker arg0) {
	            return null;
	        }

	        @Override
	        public View getInfoContents(Marker marker) {
	        	StringTokenizer tokenizer=new StringTokenizer(marker.getSnippet(),"|");
	            View v = getLayoutInflater().inflate(R.layout.marker, null);
	            TextView title = (TextView) v.findViewById(R.id.title);
	            title.setText(marker.getTitle());
	            TextView category= (TextView) v.findViewById(R.id.category);
	            TextView address= (TextView) v.findViewById(R.id.address);
	            
	            category.setText(tokenizer.nextToken());
	            address.setText(tokenizer.nextToken());
	            v.setClickable(false);
	            return v;
	        }
	    });
		// Sets a callback that's invoked when the camera changes.
		pMap.setOnCameraChangeListener(new OnCameraChangeListener() {

		    @Override
		    /* Only use the simpler method newLatLngBounds(boundary, padding) to generate 
		     * a CameraUpdate if it is going to be used to move the camera *after* the map 
		     * has undergone layout. During layout, the API calculates the display boundaries 
		     * of the map which are needed to correctly project the bounding box. 
		     * In comparison, you can use the CameraUpdate returned by the more complex method 
		     * newLatLngBounds(boundary, width, height, padding) at any time, even before the 
		     * map has undergone layout, because the API calculates the display boundaries 
		     * from the arguments that you pass. 
		     * @see com.google.android.gms.maps.GoogleMap.OnCameraChangeListener#onCameraChange(com.google.android.gms.maps.model.CameraPosition)
		     */
		    public void onCameraChange(CameraPosition arg0) {
		        // Move camera.
		    	Double minLat;
		    	LatLngBounds.Builder bounds= new LatLngBounds.Builder();
				
		    	if (pois != null){
		    		PolylineOptions options= new PolylineOptions();
		    		options.visible(true);
		    		options.color(Color.RED);
		    		options.width(5);
		    	
					for(Poi p:pois){
					pMap.addMarker(new MarkerOptions()
					.position(p.getLatLng())
					.title(p.getName())
					.snippet(p.getCategory()+"|"+p.getAddress())
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_star)));
					options.add(p.getLatLng());
					bounds.include (p.getLatLng());
					}
					
					pMap.addPolyline(options);
		    		
					}

				bounds.include(userCoord);
		    	pMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 200));
				
		    	pMap.setOnCameraChangeListener(null);					
		    	
		    }
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_routes_map, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded(new LatLng(0.0, 0.0));
	}

	private void setUpMapIfNeeded(LatLng coord) {
		// Do a null check to confirm that we have not already instantiated the map.
		if (pMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			pMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
					.getMap();
			// Check if we were successful in obtaining the map.
			if (pMap != null) {
				setUpMap(coord);
			}
		}
	}

	private void setUpMap(LatLng userCoord) {
		pMap.addMarker(new MarkerOptions()
							.position(userCoord)
							.title("Your location")
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user)));
		//pMap.animateCamera(CameraUpdateFactory.newLatLng(userCoord));
		
		/* public static CameraUpdate newLatLngZoom (LatLng latLng, float zoom) 
		 * Returns a CameraUpdate that moves the center of the screen to a latitude 
		 * and longitude specified by a LatLng object, and moves to the given zoom 
		 * level.
		 * Parameters
		 * latLng 	a LatLng object containing the desired latitude and longitude.
		 * zoom 	the desired zoom level, in the range of 2.0 to 21.0. 
		 * Values below this range are set to 2.0, and values above it are set to 21.0. 
		 * Increase the value to zoom in. Not all areas have tiles at the largest zoom levels.
		 * Returns
		 * a CameraUpdate containing the transformation. 
*/
		pMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userCoord, 15));
		
	}

}
