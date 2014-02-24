package md.clt.android.routes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import md.clt.android.MainActivity;
import md.clt.android.R;
import md.clt.android.WebServiceTask;
import md.clt.android.POI.Poi;
import md.clt.android.R.id;
import md.clt.android.R.layout;
import md.clt.android.R.menu;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.db4o.query.Predicate;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

public class RoutesListActivity extends ListActivity{

	LatLng userCoord;
	RoutesLVAdapter routeLvAdapter;
	private RadioGroup radioRouteGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_routes_list);

		Intent caller = this.getIntent();

		userCoord = caller.getParcelableExtra("coord");

		ArrayList<RouteLVItem> routesItemDataAL = (ArrayList<RouteLVItem>) caller
				.getSerializableExtra("routeItemDataAL");

		RouteLVItem[] routesItemData = routesItemDataAL
				.toArray(new RouteLVItem[routesItemDataAL.size()]);
		routeLvAdapter = new RoutesLVAdapter(this, R.layout.listview_item_row,
				routesItemData);

		
		ListView routeList = (ListView) findViewById(android.R.id.list);
		routeList.setAdapter(routeLvAdapter);
		

		radioRouteGroup = (RadioGroup) findViewById(R.id.radioRoute);
		
		radioRouteGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						RadioButton button = (RadioButton) findViewById(group
								.getCheckedRadioButtonId());
						ListView routeList = (ListView) findViewById(android.R.id.list);
						RoutesLVAdapter routesLVAdapter = (RoutesLVAdapter) routeList
								.getAdapter();
						if (button.getId() == R.id.radioPopularityUp)
							routesLVAdapter.sort(new Comparator<RouteLVItem>() {

								@Override
								public int compare(RouteLVItem lhs,
										RouteLVItem rhs) {
									// TODO Auto-generated method stub
									return Integer.compare(lhs.getPopularity(),
											rhs.getPopularity());
								}
							});
						else if (button.getId() == R.id.radioLengthUp)

							routesLVAdapter.sort(new Comparator<RouteLVItem>() {

								@Override
								public int compare(RouteLVItem lhs,
										RouteLVItem rhs) {
									// TODO Auto-generated method stub
									return Integer.compare(lhs.getLength(),
											rhs.getLength());
								}
							});
						else if (button.getId() == R.id.radioPopularityDown) {
							routesLVAdapter.sort(new Comparator<RouteLVItem>() {

								@Override
								public int compare(RouteLVItem lhs,
										RouteLVItem rhs) {
									// TODO Auto-generated method stub
									return Integer.compare(rhs.getPopularity(),
											lhs.getPopularity());
								}
							});
						} else {
							routesLVAdapter.sort(new Comparator<RouteLVItem>() {

								@Override
								public int compare(RouteLVItem lhs,
										RouteLVItem rhs) {
									// TODO Auto-generated method stub
									return Integer.compare(rhs.getLength(),
											lhs.getLength());
								}
							});
						}

					}
				});

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
		Log.v("route","aeeeeeeeee");
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

	}
}
