package md.clt.android;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.*;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

public class RoutesListActivity extends ListActivity {

	LatLng userCoord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_routes_list);

		Intent caller = this.getIntent();

		userCoord = caller.getParcelableExtra("coord");

		ArrayList<RouteLVItem> routesItemDataAL = (ArrayList<RouteLVItem>) caller
				.getSerializableExtra("routeItemDataAL");

		ListView poiList = (ListView) findViewById(android.R.id.list);

		RouteLVItem[] routesItemData = routesItemDataAL
				.toArray(new RouteLVItem[routesItemDataAL.size()]);

		RoutesLVAdapter poiLvAdapter = new RoutesLVAdapter(this,
				R.layout.listview_item_row, routesItemData);

		poiList.setAdapter(poiLvAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_routes_list, menu);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		RouteLVItem routeItem = (RouteLVItem) l.getItemAtPosition(position);
		// startActivity(new Intent(this, demo.activityClass));
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", routeItem.getRoute().getId()));
		String URL = MainActivity.SERVICE_URL + "poi/routeinfo";

		WebServiceTask tsk = new WebServiceTask(WebServiceTask.POST_TASK,
				"handleRouteInfo", v.getContext(), "Downloading Route Info",
				params);
		// pl.add(routeItem.getRoute());
		tsk.execute(new String[] { URL });
		// intentShowMap.putExtra("routes", pl);
		// this.startActivity(intentShowMap);

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
		// parse the json object
		JSONArray jArray;
		try {
			jArray = new JSONArray(response);

			// TODO: handle JSONexceptions
			/*
			 * for (int n=0; n < jArray.length(); n++){
			 * 
			 * JSONObject tmpObjPoi = jArray.getJSONObject(n); Poi tmpPoi = new
			 * Poi(); tmpPoi.setCategory(tmpObjPoi.getString("categoria"));
			 * tmpPoi.setId(tmpObjPoi.getString("venueid"));
			 * tmpPoi.setName(tmpObjPoi.getString("nome"));
			 * tmpPoi.setLat(tmpObjPoi.getDouble("latitude"));
			 * tmpPoi.setLng(tmpObjPoi.getDouble("longitude"));
			 * 
			 * routeArrayList.add(tmpPoi); }
			 */

			String points = jArray.getJSONObject(0).getString("points");
			WKTReader reader = new WKTReader();
			Log.v("log", points);
			points = points.substring(1, points.length() - 2);

			LineString line = null;
			ArrayList<LatLng> points_arr = new ArrayList<LatLng>();

			StringTokenizer t = new StringTokenizer(points, ",");

			while (true) {
				String current=t.nextToken();
				current=current.substring(7, current.length()-3);
				StringTokenizer intern= new StringTokenizer(current," ");
				double x=Double.parseDouble(intern.nextToken());
				double y=Double.parseDouble(intern.nextToken());
				Log.v("log",x+" "+y);
				points_arr.add(new LatLng(y, x));
				if(!t.hasMoreElements())
					break;
			}
			intentShowMap.putExtra("userCoord", coord);
			intentShowMap.putParcelableArrayListExtra("shape", points_arr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		// intentShowMap.putExtra("pois", pois);
		this.startActivity(intentShowMap);

	}
}
