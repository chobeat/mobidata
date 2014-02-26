package md.clt.android.routes;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import md.clt.android.MainActivity;
import md.clt.android.WebServiceTask;
import md.clt.android.POI.Poi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SharedRoute extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String arr[]=getIntent().getDataString().split("/");
		int routeID=Integer.parseInt(arr[arr.length-1]);
		String URL = MainActivity.SERVICE_URL + "poi/routeinfo";
		Log.v("shared",""+URL);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", ""+routeID));
		
		WebServiceTask tsk = new WebServiceTask(WebServiceTask.POST_TASK,
						"handleRouteInfo", this, "Downloading Route Info",params);
				// pl.add(routeItem.getRoute());
				
				tsk.execute(new String[] { URL});

	
	}
	
	public void handleRouteInfo(String response) {
		Intent intentShowMap = new Intent(MainActivity.instance, RouteMap.class);
		LatLng coord;

		if (MainActivity.instance.location != null) {
			coord = new LatLng(MainActivity.instance.location.getLatitude(),
					MainActivity.instance.location.getLongitude());
		} else {
			coord = new LatLng(40.73, -73.99);
		}
		ArrayList<Poi> routeArrayList = new ArrayList<Poi>();
		JSONArray jArray;

		try {
			jArray = new JSONArray(response);
			// TODO: handle JSONexceptions

			for (int n = 0; n < jArray.length(); n++) {
				JSONObject tmpObjPoi = jArray.getJSONObject(n);
				Poi tmpPoi = new Poi();
				tmpPoi.setCategory(tmpObjPoi.getString("categoria"));
				tmpPoi.setId(tmpObjPoi.getString("venueid"));
				tmpPoi.setAddress(tmpObjPoi.getString("indirizzo"));
				tmpPoi.setName(tmpObjPoi.getString("nome"));
				tmpPoi.setLat(tmpObjPoi.getDouble("latitude"));
				tmpPoi.setLng(tmpObjPoi.getDouble("longitude"));
				routeArrayList.add(tmpPoi);
			}

			intentShowMap.putExtra("userCoord", coord);
			intentShowMap.putExtra("pois", routeArrayList);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// intentShowMap.putExtra("pois", pois);
		this.startActivity(intentShowMap);
		finish();
	}
}
