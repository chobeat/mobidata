package md.clt.android.POI;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import md.clt.android.R;
import md.clt.android.R.layout;
import md.clt.android.R.menu;

import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

public class PoiListActivity extends ListActivity {
	
	LatLng userCoord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poi_list);
		
		Intent caller = this.getIntent();
		
		userCoord = caller.getParcelableExtra("coord"); 
		
		ArrayList<PoiLVItem> poiItemDataAL = 
				(ArrayList<PoiLVItem>) caller.getSerializableExtra("poiItemDataAL");
		
		ListView poiList = (ListView) findViewById(android.R.id.list);
		
		PoiLVItem[] poiItemData = poiItemDataAL.toArray(new PoiLVItem[poiItemDataAL.size()]);

		Log.d("poiItemData.length", " " + poiItemData.length);
		
		PoiLVAdapter poiLvAdapter = new PoiLVAdapter(this, R.layout.listview_item_row, poiItemData);

		poiList.setAdapter(poiLvAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_poi_list, menu);
		return true;
	}
	
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	Log.d("position", " " + position);
    	PoiLVItem poiItem = (PoiLVItem) l.getItemAtPosition(position);
        //startActivity(new Intent(this, demo.activityClass));
    	Log.d("poiItem", poiItem.getText());
    	
    	Intent intentShowMap = new Intent(PoiListActivity.this, PoiMap.class);
		intentShowMap.putExtra("userCoord", userCoord);
		LinkedList<Poi> pl=new LinkedList<Poi>();
		pl.add(poiItem.getPoi());
		intentShowMap.putExtra("pois", pl);
		this.startActivity(intentShowMap);
    	
    }

}
