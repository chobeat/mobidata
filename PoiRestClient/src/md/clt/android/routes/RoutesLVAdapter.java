package md.clt.android.routes;

import java.util.List;

import md.clt.android.R;
import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;
import com.db4o.query.Predicate;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class RoutesLVAdapter extends ArrayAdapter<RouteLVItem> {

	Context context;
	int layoutResourceId;
	RouteLVItem data[] = null;
	EmbeddedObjectContainer db;

	public RoutesLVAdapter(Context context, int layoutResourceId,
			RouteLVItem[] data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;

		db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), Environment
				.getExternalStorageDirectory().getPath() + "/RouteFinder");
	}

	public void opendb(){
		if (db == null || db.ext().isClosed()) {

			db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(),
					Environment.getExternalStorageDirectory().getPath()
							+ "/RouteFinder");
		}
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RouteLVItemHolder holder = null;
		
		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new RouteLVItemHolder();
			holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);
			holder.txtPopularity = (TextView) row
					.findViewById(R.id.txtPopularity);
			holder.txtLength = (TextView) row.findViewById(R.id.txtLength);

			row.setTag(holder);
		} else {
			holder = (RouteLVItemHolder) row.getTag();
		}
		RouteLVItem routeItem = data[position];
		holder.txtTitle.setText(routeItem.getText());
		holder.txtLength.setText("" + routeItem.getLength());
		holder.txtPopularity.setText("" + routeItem.getPopularity());
		ToggleButton saveButton = (ToggleButton) row
				.findViewById(R.id.routetogglebutton);
		saveButton.setOnClickListener(new SaveButtonListener(routeItem.getRoute()));
				
		
		opendb();
		try {
			saveButton.setChecked(false);
			List<Route> dbroute = db.query(new MatchIDPredicate(routeItem.getRoute()));
			if (dbroute.size() > 0){
				saveButton.setChecked(true);
			}
			
		} finally {
			db.close();
		}
		Button shareButton = (Button) row.findViewById(R.id.routesharebutton);

		shareButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}

		});
		return row;
	}

	protected void saveRoute(Route route) {

		if (db == null || db.ext().isClosed()) {

			db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(),
					Environment.getExternalStorageDirectory().getPath()
							+ "/RouteFinder");
		}

		try {
			db.store(route);
			db.commit();
		} finally {
			db.close();
		}
	}

	protected void deleteRoute(Route route) {
		if (db == null || db.ext().isClosed()) {

			db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(),
					Environment.getExternalStorageDirectory().getPath()
							+ "/RouteFinder");
		}
		
		try {
			List<Route> l=db.query(new MatchIDPredicate(route));
			for(Route r:l){
				db.delete(r);}
			db.commit();
		} finally {
			db.close();
		}
	}
	
	@Override
    public int getViewTypeCount() {                 
                  //Count=Size of ArrayList.
        return data.length;
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

	static class RouteLVItemHolder {
		ImageView imgIcon;

		TextView txtTitle;
		TextView txtPopularity;
		TextView txtLength;
	}
	
	class SaveButtonListener implements OnClickListener{
		Route r;
		
		public SaveButtonListener(Route r){
			this.r=r;
			
		}
		
		
		
			@Override
			public void onClick(View vw) {
				boolean isChecked= ((ToggleButton)vw).isChecked();
				
				if (isChecked) {
					saveRoute(this.r);
				} else {
					deleteRoute(this.r);
				}

			}
		
		
	}
	 class MatchIDPredicate extends Predicate<Route>{
	
		private static final long serialVersionUID = 2610894504507148978L;
			Route r;
			
			public MatchIDPredicate(Route r){
				super();
				this.r=r;
			}
			@Override
			public boolean match(Route arg0) {
				//	Log.v("route","arg"+arg0.getId()+" "+r.getId());
					return arg0.getId().equals(r.getId());
			}
			
			
		
	}
	
}