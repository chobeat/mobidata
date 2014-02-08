package md.clt.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Policy;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/* 
 * @@@lez3 - we want our activity to listen for location changes;
 * so we need to implement the LocationListener interface (an interface 
 * is a group of related methods with empty bodies)
 */
public class MainActivity extends Activity implements LocationListener{

	//private static final String SERVICE_URL = "http://192.168.10.1:8080/PoiWebService/rest/progetto_md";
	private static final String SERVICE_URL = "http://192.168.56.1:9876/mobidata/poi/poi";

	private static final String TAG = "AndroidRESTClientActivity";
	private LocationManager locationManager;
	private String provider;
	private TextView latituteField;
	private TextView longitudeField;
	private TextView providerField;
	private Location location;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Radius spinner
		Spinner spRadius = (Spinner) findViewById(R.id.kValueSpinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> RadiusAdapter = ArrayAdapter.createFromResource(this, 
				R.array.kValue_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		RadiusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spRadius.setAdapter(RadiusAdapter);

		// @@@lez3
		latituteField = (TextView) findViewById(R.id.tvLatVal);
		longitudeField = (TextView) findViewById(R.id.tvLonVal);
		providerField = (TextView) findViewById(R.id.tvProvVal);
		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// get the value of askedGps from the last saved instance of the activity
		//if (savedInstanceState.containsKey("asked_gps")) askedGps = savedInstanceState.getBoolean("asked_gps");
		// ask the user to enable GPS (if it is off)
		if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER )){

			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which){
					case DialogInterface.BUTTON_POSITIVE:
						//Yes button clicked
						startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						//No button clicked
						break;
					}
				}
			};
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("GPS is turned of. Do you want to turn it on?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
		}

		// Define the criteria how to select the location provider -> ask fine accuracy 
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

	public void showPOIMap(View vw){
		callRetrievePOI(vw, "handlePOIMapResponse");
	}
	
	public void callShowMap(ArrayList<Poi> pois){
		
			Intent intentShowMap = new Intent(MainActivity.this, PoiMap.class);
			LatLng coord;
			if (location != null){
				coord = new LatLng(location.getLatitude(), location.getLongitude());
			}else{
				coord = new LatLng(40.73, -73.99);
			}
			intentShowMap.putExtra("userCoord", coord);
			intentShowMap.putExtra("pois", pois);
			this.startActivity(intentShowMap);
		
	}
	public void showMap(View vw){
		callShowMap(new ArrayList<Poi>());
	}
	public void handlePOIMapResponse(String response){
		callShowMap(POIResponseToPOIList(response));
		
	}
	
	public void retrievePOI(View vw){
		callRetrievePOI(vw, "handlePOIResponse");
	}
	
	public void callRetrievePOI(View vw, String handler) {

		// url of the web service
		String sampleURL = SERVICE_URL + "/";

		// web service calls must be executed in a separate thread
		WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, handler, this, "Downloading POIs...");

		// get keyword
		EditText etKeyword = (EditText) findViewById(R.id.etKeyword);
		String sKeyword = etKeyword.getText().toString();
		if ( sKeyword.length() == 0 ) sKeyword = "*";

		// get value k
		Spinner kValueSpinner = (Spinner) findViewById(R.id.kValueSpinner);
		String sKvalue = (String) kValueSpinner.getSelectedItem();

		// compose the request url
		String url = sampleURL; //+ "/" + sKvalue + "/" +  sKeyword + "/" 
			//	+ location.getLatitude() + "/" + location.getLongitude();

		Log.d("MainActivity - url", url);

		// show toast with req url
/*		Context context = getApplicationContext();
		CharSequence text = url;
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();*/

		// execute the call
		wst.execute(new String[] { url });

	}


	/* ASYNCTASK
	 * http://developer.android.com/reference/android/os/AsyncTask.html
	 * 
	 * AsyncTask enables proper and easy use of the UI thread. This class allows to perform 
	 * background operations and publish results on the UI thread without having to manipulate 
	 * threads and/or handlers.
	 * 
	 * AsyncTask is designed to be a helper class around Thread and Handler and does not 
	 * constitute a generic threading framework. AsyncTasks should ideally be used for short 
	 * operations (a few seconds at the most.) If you need to keep threads running for long 
	 * periods of time, it is highly recommended you use the various APIs provided by the 
	 * java.util.concurrent package such as Executor, ThreadPoolExecutor and FutureTask.
	 * 
	 * An asynchronous task is defined by a computation that runs on a background thread and 
	 * whose result is published on the UI thread. An asynchronous task is defined by 3 generic 
	 * types, called Params, Progress and Result, and 4 steps, called onPreExecute, 
	 * doInBackground, onProgressUpdate and onPostExecute.
	 * 
	 * The three types used by an asynchronous task are the following:
	 * - Params, the type of the parameters sent to the task upon execution.
	 * - Progress, the type of the progress units published during the background computation.
	 * - Result, the type of the result of the background computation.
	 * 
	 * A task can be cancelled at any time by invoking cancel(boolean). Invoking this method 
	 * will cause subsequent calls to isCancelled() to return true. After invoking this method, 
	 * onCancelled(Object), instead of onPostExecute(Object) will be invoked after 
	 * doInBackground(Object[]) returns. To ensure that a task is cancelled as quickly as 
	 * possible, you should always check the return value of isCancelled() periodically from 
	 * doInBackground(Object[]), if possible (inside a loop for instance.)
	 */

	private class WebServiceTask extends AsyncTask<String, Integer, String> {

		public static final int POST_TASK = 1;
		public static final int GET_TASK = 2;

		private static final String TAG = "WebServiceTask";

		// connection timeout, in milliseconds (waiting to connect)
		private static final int CONN_TIMEOUT = 6000;

		// socket timeout, in milliseconds (waiting for data)
		private static final int SOCKET_TIMEOUT = 10000;

		private int taskType = GET_TASK;
		private Context mContext = null;
		private String processMessage = "Processing...";

		// params are used only for UrlEncoded POST requests
		private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		private String function;
		private ProgressDialog pDlg = null;

		public WebServiceTask(int taskType,String function ,Context mContext, String processMessage) {
			this.function=function;
			this.taskType = taskType;
			this.mContext = mContext;
			this.processMessage = processMessage;
		}

		private void showProgressDialog() {

			pDlg = new ProgressDialog(mContext);
			pDlg.setMessage(processMessage);
			pDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDlg.setCancelable(false);
			pDlg.show();

		}

		@Override
		protected void onPreExecute() {

			hideKeyboard();
			showProgressDialog();

		}

		// "..." is a construct called varargs to pass an arbitrary number of values to a method
		// the final argument may be passed as an array or as a sequence of arguments
		protected String doInBackground(String... urls) {

			String url = urls[0];
			String result = "";

			HttpResponse response = doResponse(url);

			if (response == null) {
				return result;
			} else {

				try {

					result = inputStreamToString(response.getEntity().getContent());

				} catch (IllegalStateException e) {
					Log.e(TAG, e.getLocalizedMessage(), e);

				} catch (IOException e) {
					Log.e(TAG, e.getLocalizedMessage(), e);
				}

			}

			return result;
		}

		@Override
		protected void onPostExecute(String response) {
			try {
				

				MainActivity.class.getMethod(function,String.class).invoke(mContext,response);
				Log.d("log","si può fare");
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	//		handlePOIResponse(response);
			pDlg.dismiss();

		}

		// Establish connection and socket (data retrieval) timeouts
		private HttpParams getHttpParams() {

			HttpParams htpp = new BasicHttpParams();

			HttpConnectionParams.setConnectionTimeout(htpp, CONN_TIMEOUT);
			HttpConnectionParams.setSoTimeout(htpp, SOCKET_TIMEOUT);

			return htpp;
		}

		private HttpResponse doResponse(String url) {

			// Use our connection and data timeouts as parameters for our
			// DefaultHttpClient
			HttpClient httpclient = new DefaultHttpClient(getHttpParams());

			HttpResponse response = null;

			try {
				switch (taskType) {

				case POST_TASK:
					HttpPost httppost = new HttpPost(url);
					// Add parameters
					httppost.setEntity(new UrlEncodedFormEntity(params));

					response = httpclient.execute(httppost);
					break;
				case GET_TASK:
					HttpGet httpget = new HttpGet(url);
					response = httpclient.execute(httpget);
					break;
				}
			} catch (Exception e) {

				Log.e(TAG, e.getLocalizedMessage(), e);

			}

			return response;
		}

		private String inputStreamToString(InputStream is) {

			String line = "";
			StringBuilder total = new StringBuilder();

			// Wrap a BufferedReader around the InputStream
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			try {
				// Read response until the end
				while ((line = rd.readLine()) != null) {
					total.append(line);
				}
			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
			}

			// Return full string
			return total.toString();
		}

	}

	public ArrayList<Poi> POIResponseToPOIList(String response){
		

			ArrayList<Poi> poiArrayList = new ArrayList<Poi>();
			// parse the json object
			JSONArray jArray;
			try {
				jArray = new JSONArray(response);
			
			
			// TODO: handle JSONexceptions

			for (int n=0; n < jArray.length(); n++){
				
				JSONObject tmpObjPoi =  jArray.getJSONObject(n);
				Poi tmpPoi = new Poi();
				tmpPoi.setCategory(tmpObjPoi.getString("categoria"));
				tmpPoi.setId(tmpObjPoi.getString("venueid"));
				tmpPoi.setName(tmpObjPoi.getString("nome"));
				tmpPoi.setLat(tmpObjPoi.getDouble("latitude"));
				tmpPoi.setLng(tmpObjPoi.getDouble("longitude"));
				
				poiArrayList.add(tmpPoi);
			}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return poiArrayList;
	}
	// update the ArrayAdapter with pois
	public void handlePOIResponse(String response) {

		
			updatePoiList(POIResponseToPOIList(response));

		
	}


	private void updatePoiList(ArrayList<Poi> poiArrayList){

		PoiLVItem poiItemData[] = new PoiLVItem[poiArrayList.size()];

		for (int n=0; n < poiArrayList.size(); n++){
			Poi tmpPoi = poiArrayList.get(n);

			PoiLVItem tmpPoiItem = new PoiLVItem(tmpPoi);
			poiItemData[n] = tmpPoiItem;

			Log.d("MainActivity - add poiItemData", "added poi " + n);

		}

		// launch activity poi list
		Intent showPoiList = new Intent(MainActivity.this, PoiListActivity.class);
		// pass poi items for filling the list
		ArrayList<PoiLVItem> poiItemDataAL = new ArrayList<PoiLVItem>(Arrays.asList(poiItemData));
		showPoiList.putExtra("poiItemDataAL", poiItemDataAL);
		
		LatLng coord;
		if (location != null){
			coord = new LatLng(location.getLatitude(), location.getLongitude());
		}else{
			coord = new LatLng(40.73, -73.99);
		}
		showPoiList.putExtra("coord", coord);
		
		this.startActivity(showPoiList);
	}


	private void hideKeyboard() {

		InputMethodManager inputManager = (InputMethodManager) MainActivity.this
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		inputManager.hideSoftInputFromWindow(
				MainActivity.this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}


	// @@@lez3
	@Override
	public void onLocationChanged(Location loc) {
		location = loc;
		double lat = loc.getLatitude();
		double lng = loc.getLongitude();
		String prov = loc.getProvider();
		latituteField.setText(String.valueOf((float)lat));
		longitudeField.setText(String.valueOf((float)lng));
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
