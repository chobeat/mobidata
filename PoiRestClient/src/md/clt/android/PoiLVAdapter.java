package md.clt.android;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PoiLVAdapter extends ArrayAdapter<PoiLVItem>{

    Context context;
    int layoutResourceId;   
    PoiLVItem data[] = null;
   
    public PoiLVAdapter(Context context, int layoutResourceId, PoiLVItem[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PoiLVItemHolder holder = null;
       
        if(row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new PoiLVItemHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
           
            row.setTag(holder);
        }
        else{
            holder = (PoiLVItemHolder)row.getTag();
        }       
        PoiLVItem poiItem = data[position];
        holder.txtTitle.setText(poiItem.getText());
        holder.imgIcon.setImageResource(poiItem.getIcon());
       
        return row;
    }
   
    static class PoiLVItemHolder    {
        ImageView imgIcon;
        TextView txtTitle;
    }
}