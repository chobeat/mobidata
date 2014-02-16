package md.clt.android;

import android.app.Activity; 
import android.os.Bundle; 
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.SeekBar; 
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView; 
public class POISliderListener extends Activity implements OnSeekBarChangeListener {
	 	public static final int OFFSET=4;
		TextView currValue;
		public POISliderListener(TextView v) {
			currValue=v; 
		}
 @Override 
 public void onProgressChanged(SeekBar seekBar, int progress, 
   boolean fromUser) { 
  currValue.setText(String.valueOf(progress+OFFSET)); 
 }

@Override
public void onStartTrackingTouch(SeekBar seekBar) {
	// TODO Auto-generated method stub
	
}

@Override
public void onStopTrackingTouch(SeekBar seekBar) {
	// TODO Auto-generated method stub
	
}
 

     }
