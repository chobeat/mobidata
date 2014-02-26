package md.clt.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import md.clt.android.routes.Route;
import md.clt.android.routes.RouteLVItem;
import md.clt.android.routes.RoutesListActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;
import com.db4o.query.Predicate;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.maps.model.LatLng;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/* 
 * @@@lez3 - we want our activity to listen for location changes;
 * so we need to implement the LocationListener interface (an interface 
 * is a group of related methods with empty bodies)
 */
public class MainActivity extends Activity implements LocationListener {

	// private static final String SERVICE_URL =
	// "http://192.168.10.1:8080/PoiWebService/rest/progetto_md";
	public static final String SERVICE_URL = "http://192.168.56.1:9876/mobidata/";
	public static MainActivity instance;
	private static final String TAG = "AndroidRESTClientActivity";
	private LocationManager locationManager;
	private String provider;
	private TextView latituteField;
	private TextView longitudeField;
	private TextView providerField;
	public Location location;
	private SeekBar currentPOISlider;
	private SeekBar currentRangeSlider;
	public String userid;
	private AccountManager mAccountManager;

	public static Context getContext() {
		return instance;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.activity_main);
		userid = getAccountNames()[0];

		latituteField = (TextView) findViewById(R.id.tvLatVal);
		longitudeField = (TextView) findViewById(R.id.tvLonVal);
		providerField = (TextView) findViewById(R.id.tvProvVal);
		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		currentPOISlider = (SeekBar) findViewById(R.id.PoiSlider);

		currentPOISlider.setOnSeekBarChangeListener(new POISliderListener(
				(TextView) this.findViewById(R.id.currentPOIValue)));

		currentRangeSlider = (SeekBar) findViewById(R.id.RangeSlider);
		currentRangeSlider.setOnSeekBarChangeListener(new RangeSliderListener(
				(TextView) this.findViewById(R.id.currentRangeValue)));

		// get the value of askedGps from the last saved instance of the
		// activity
		// if (savedInstanceState.containsKey("asked_gps")) askedGps =
		// savedInstanceState.getBoolean("asked_gps");
		// ask the user to enable GPS (if it is off)
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						// Yes button clicked
						startActivity(new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						// No button clicked
						break;
					}
				}
			};
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("GPS is turned of. Do you want to turn it on?")
					.setPositiveButton("Yes", dialogClickListener)
					.setNegativeButton("No", dialogClickListener).show();
		}

		// Define the criteria how to select the location provider -> ask fine
		// accuracy
		Criteria criteria = new Criteria();
		criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
		provider = locationManager.getBestProvider(criteria, false);
		location = locationManager.getLastKnownLocation(provider);

		// Initialize the location fields
		if (location != null) {
			providerField.setText(provider);
			onLocationChanged(location);
		} else {
			latituteField.setText("not available");
			longitudeField.setText("not available");
		}
	}

	private String[] getAccountNames() {
		mAccountManager = AccountManager.get(this);
		Account[] accounts = mAccountManager
				.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
		String[] names = new String[accounts.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = accounts[i].name;
		}
		return names;
	}

	public void showPOIMap(View vw) {
		callRetrieveCloseRoutes(vw, "handleRoutesMapResponse");
	}

	public void launchInterests(View vw) {
		Intent intent = new Intent(this, InterestActivity.class);
		intent.putExtra("userid", userid);
		startActivity(intent);
	}

	public void retrieveCloseRoutes(View vw) {
		callRetrieveCloseRoutes(vw, "handleRoutesResponse");
	}

	public void callRetrieveCloseRoutes(View vw, String handler) {

		// url of the web service
		String URL = SERVICE_URL + "poi/closeRoutes";

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		if (location != null) {
			params.add(new BasicNameValuePair("lat", ""
					+ location.getLatitude()));
			params.add(new BasicNameValuePair("lng", ""
					+ location.getLongitude()));
		} else {
			params.add(new BasicNameValuePair("lat", "" + 40.73));
			params.add(new BasicNameValuePair("lng", "" + -73.99));

		}

		params.add(new BasicNameValuePair("userid", "" + userid));
		params.add(new BasicNameValuePair("poiNR", ""
				+ (currentPOISlider.getProgress() + POISliderListener.OFFSET)));
		params.add(new BasicNameValuePair("range", ""
				+ (currentRangeSlider.getProgress() * 100)));

		WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK,
				handler, this, "Downloading Routes...", params);

		// execute the call
		wst.execute(new String[] { URL });

	}

	public void launchFavorites(View vw) {
		EmbeddedObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded
				.newConfiguration(), Environment.getExternalStorageDirectory()
				.getPath() + "/RouteFinder");

		try {
			List<Route> l = db.query(new Predicate<Route>() {

				@Override
				public boolean match(Route arg0) {
					// TODO Auto-generated method stub
					return true;
				}
			});
			updateRoutesList(l);
		} finally {
			db.close();

		}
	}

	public void handleRoutesResponse(String response) {

		updateRoutesList(responseToRouteList(response));

	}

	private ArrayList<Route> responseToRouteList(String response) {
		ArrayList<Route> l = new ArrayList<Route>();
		JSONArray jArray;
		try {
			jArray = new JSONArray(response);

			// TODO: handle JSONexceptions

			for (int n = 0; n < jArray.length(); n++) {
				JSONObject tmpObjRoute = jArray.getJSONObject(n);
				Route tmpRoute = new Route();
				tmpRoute.setName("Route from " + tmpObjRoute.getString("start")
						+ " to " + tmpObjRoute.getString("end"));
				tmpRoute.setLength(tmpObjRoute.getDouble("length"));
				tmpRoute.setId(tmpObjRoute.getString("id"));
				tmpRoute.setPopularity(tmpObjRoute.getInt("popularity"));
				l.add(tmpRoute);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return l;
	}

	private void updateRoutesList(List<Route> routeArrayList) {

		RouteLVItem routeItemData[] = new RouteLVItem[routeArrayList.size()];
		if (routeArrayList.size() <= 0) {
			new AlertDialog.Builder(this)
					.setTitle("No Routes Found")
					.setMessage("No Routes Found")
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							})

					.show();
			return;
		}
		for (int n = 0; n < routeArrayList.size(); n++) {
			Route tmpRoute = routeArrayList.get(n);

			RouteLVItem tmpRouteItem = new RouteLVItem(tmpRoute);
			routeItemData[n] = tmpRouteItem;

		}

		Intent showRouteList = new Intent(MainActivity.this,
				RoutesListActivity.class);
		ArrayList<RouteLVItem> routeItemDataAL = new ArrayList<RouteLVItem>(
				Arrays.asList(routeItemData));

		showRouteList.putExtra("routeItemDataAL", routeItemDataAL);

		LatLng coord;
		if (location != null) {
			coord = new LatLng(location.getLatitude(), location.getLongitude());
		} else {
			coord = new LatLng(40.73, -73.99);
		}
		showRouteList.putExtra("coord", coord);

		this.startActivity(showRouteList);
	}

	public void hideKeyboard() {

		InputMethodManager inputManager = (InputMethodManager) MainActivity.this
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		inputManager.hideSoftInputFromWindow(MainActivity.this
				.getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	// @@@lez3
	@Override
	public void onLocationChanged(Location loc) {
		location = loc;
		double lat = loc.getLatitude();
		double lng = loc.getLongitude();
		String prov = loc.getProvider();
		latituteField.setText(String.valueOf((float) lat));
		longitudeField.setText(String.valueOf((float) lng));
		providerField.setText(String.valueOf(prov));

	}

	/* Request updates at startup */
	@Override
	protected void onResume() {
		super.onResume();
		// time is in milliseconds; distance in meters
		locationManager.requestLocationUpdates(provider, 5000, 50, this);
	}

	/* Remove the locationlistener updates when Activity is paused */
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

}
