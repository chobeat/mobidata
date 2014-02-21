package md.clt.android;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RoutesLVAdapter extends ArrayAdapter<RouteLVItem>{

    Context context;
    int layoutResourceId;   
    RouteLVItem data[] = null;
   
    public RoutesLVAdapter(Context context, int layoutResourceId, RouteLVItem[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RouteLVItemHolder holder = null;
       
        if(row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new RouteLVItemHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtPopularity = (TextView)row.findViewById(R.id.txtPopularity);
            holder.txtLength = (TextView)row.findViewById(R.id.txtLength);
            
           
            row.setTag(holder);
        }
        else{
            holder = (RouteLVItemHolder)row.getTag();
        }       
        RouteLVItem routeItem = data[position];
        holder.txtTitle.setText(routeItem.getText());
        holder.txtLength.setText(""+routeItem.getLength());
        holder.txtPopularity.setText(""+routeItem.getPopularity());
        return row;
    }
   
    static class RouteLVItemHolder    {
        ImageView imgIcon;
        
        TextView txtTitle;
        TextView txtPopularity;
        TextView txtLength;
    }
}