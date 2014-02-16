package md.clt.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class WebServiceTask extends AsyncTask<String, Integer, String> {

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
	
	public WebServiceTask(int taskType,String function ,Context mContext, String processMessage, ArrayList<NameValuePair> params){
		this.function=function;
		this.taskType = taskType;
		this.mContext = mContext;
		this.processMessage = processMessage;
		this.params=params;
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
			
			Log.v("log",function);
			Log.v("log",""+mContext);
			mContext.getClass().getMethod(function,String.class).invoke(mContext,response);
			
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
