package md.clt.android;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class InterestActivity extends Activity {
	String userid;
	ArrayList<String> allCategories;
	ArrayList<String> interests= new ArrayList<String>();
	AccountManager mAccountManager;
	CategoryListAdapter<String> catAdapter;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_interests);
		userid=this.getIntent().getExtras().getString("userid");
		allCategories=new ArrayList<String>();
		WebServiceTask catRequest= new WebServiceTask(WebServiceTask.GET_TASK, "handleCategoriesResponse", this, "Downloading Categories"); 
		catRequest.execute(new String[]{MainActivity.SERVICE_URL+"poi/categories"});
		
		WebServiceTask myInterest= new WebServiceTask(WebServiceTask.GET_TASK, "handleInterestResponse", this, "Downloading Your Interests"); 
		myInterest.execute(new String[]{MainActivity.SERVICE_URL+"poi/getinterests?uid="+userid});
		
		
	}
	
	
	
	
	
	public void handleCategoriesResponse(String response){
		JSONArray jArray;
		ArrayList<String> l= new ArrayList<String>();
		Log.v("interest",response);
		try {
			jArray = new JSONArray(response);
		
		
		// TODO: handle JSONexceptions

		for (int n=0; n < jArray.length(); n++){
			
			JSONObject tmpObj =  jArray.getJSONObject(n);
			
			
			l.add(tmpObj.getString("nomemc"));
	
		}
		allCategories=new ArrayList<String>(l);
		updateCategoryList();
	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateCategoryList(){

		Spinner spCat = (Spinner) findViewById(R.id.interestSpinner);
		ArrayList<String> l= new ArrayList<String>();
		
		List<String> currentInterests=interests;

		for(String s:allCategories){
			if(!currentInterests.contains(s)){
				l.add(s);
			}
			
		}
		
		catAdapter=new CategoryListAdapter<String>(this,android.R.layout.simple_list_item_1 ,l);
		
		spCat.setAdapter(catAdapter);
		
	}
	
	public void handleInterestAddedResponse(String response){
		Log.v("interest",response);
	}
	
	public void addInterestButtonClicked(View vw){
		ArrayList<NameValuePair> params=new ArrayList<NameValuePair>();
		params.add(new  BasicNameValuePair("uid", userid));
		Spinner sp= (Spinner)findViewById(R.id.interestSpinner);
		params.add(new  BasicNameValuePair("interest", ""+sp.getSelectedItem()));
		
		WebServiceTask myInterest= new WebServiceTask(WebServiceTask.POST_TASK, "handleInterestResponse", this, "Adding Interest",params); 
		myInterest.execute(new String[]{MainActivity.SERVICE_URL+"poi/addinterest"});
		
	}
	

	
	public void handleInterestResponse(String response){
		JSONArray jArray;
		ArrayList<String> l= new ArrayList<String>();

		try {
			jArray = new JSONArray(response);
		
		
		// TODO: handle JSONexceptions

		for (int n=0; n < jArray.length(); n++){
			
			JSONObject tmpObj =  jArray.getJSONObject(n);
			
			
			l.add(tmpObj.getString("interest"));
		}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateInterestList(l);
	}
	public void updateInterestList(ArrayList<String> l){
		ListView lsInt = (ListView) findViewById(R.id.interestList);
		interests= new ArrayList<String>(l);
		ArrayAdapter<String> aa=new ArrayAdapter<String>(this,R.layout.interest_item_row ,R.id.interestTitle,l);
		Spinner spCat = (Spinner) findViewById(R.id.interestSpinner);
		
		CategoryListAdapter<String> catAdapter=(CategoryListAdapter<String>)spCat.getAdapter();
		if(catAdapter!=null){
		for(String s:l){
			catAdapter.list.remove(s);
			
		}
		
		spCat.setAdapter(new CategoryListAdapter<String>(this,android.R.layout.simple_list_item_1 , catAdapter.list));
		}
		
		lsInt.setAdapter(aa);
		updateCategoryList();
		
	}
	
	public void onCancelInterestClicked(View vw){
		TextView tx=(TextView) ((ViewGroup)vw.getParent()).getChildAt(0);
		ArrayList<NameValuePair> params=new ArrayList<NameValuePair>();
		params.add(new  BasicNameValuePair("uid", userid));
		params.add(new  BasicNameValuePair("interest", ""+tx.getText()));
		WebServiceTask deleteInterest= new WebServiceTask(WebServiceTask.POST_TASK, "handleInterestResponse", this, "Removing your interest",params); 
		deleteInterest.execute(new String[]{MainActivity.SERVICE_URL+"poi/removeinterest"});
		
		
	}
	
}
