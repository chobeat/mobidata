package md.clt.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class CategoryListAdapter<T> extends ArrayAdapter<T> {

	public CategoryListAdapter(Context context, int resource, List<T> l ) {
		super(context, resource,l);
		list=l;
	}

	public final List<T> list;
	
}
