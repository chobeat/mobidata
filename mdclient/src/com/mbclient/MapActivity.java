package com.mbclient;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.TextView;

public class MapActivity extends FragmentActivity {
	GoogleMap map; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		getLayoutInflater().
		v.setText(GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext()));
		//map = ((MapFragment) getFragmentManager() .findFragmentById(R.id.map)).getMap();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

}
