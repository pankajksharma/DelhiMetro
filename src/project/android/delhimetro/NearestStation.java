package project.android.delhimetro;


import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class NearestStation extends MapActivity{

	MapView metroMap;
	MapController mc;
	Button btnExit;
	SeekBar sbRadius;
	LocationManager lm;
	LocationListener ll;
	Location presentLocation;
	
	int radius = 500;
	
	ArrayList<Location> nearbyStations;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.neareststation);
		bindItems();
		seekBarListener();
	}
	
	private void seekBarListener() {
		sbRadius.setOnSeekBarChangeListener(new SeekBarListener());
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	private void bindItems() {
		metroMap = (MapView) findViewById(R.id.myMap);
		metroMap.setBuiltInZoomControls(false);
		metroMap.setSatellite(false);
		mc=metroMap.getController();
		mc.setCenter(new GeoPoint((int)(28.613309*1E6),(int)(77.231348*1E6)));
		mc.setZoom(15);
		btnExit = (Button) findViewById(R.id.mapExit);
		sbRadius = (SeekBar) findViewById(R.id.seekRadius);
		lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		ll = new MyLocationListener();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
		
		btnExit.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				NearestStation.this.finish();
			}
		});
		
	}

	
	
	private void draw_items() {
		for(int i=0;i<nearbyStations.size();i++){
			MyOverlay overlay = new MyOverlay(nearbyStations.get(i), R.drawable.station_icon);
			metroMap.getOverlays().add(overlay);
		}
	}



	class MyOverlay extends Overlay{
		
		private GeoPoint geoPoint;
		private int imgId;
		
		public MyOverlay(Location loc,int imgId){
			this.geoPoint = new GeoPoint ((int)(loc.getLatitude()*1E6), (int)(loc.getLongitude()*1E6));
			this.imgId = imgId;
		}
		
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,long when) {
						
			Point screenPoint = new Point();
			mapView.getProjection().toPixels(geoPoint, screenPoint);
			
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), imgId);
			canvas.drawBitmap(bmp, screenPoint.x-25, screenPoint.y-50, null);
			
			super.draw(canvas, mapView, shadow);
			return true;
		}
	}
	
	class MyLocationListener implements LocationListener{

		public void onLocationChanged(Location location) {
			if(location!=null){
				
				metroMap.getOverlays().clear();
				
				presentLocation = location;
				//Set center to present location
				mc.setCenter(new GeoPoint((int)(location.getLatitude()*1E6),(int)(location.getLongitude()*1E6)));
				
				//Add overlay for present location
				MyOverlay presentLoc = new MyOverlay(location, R.drawable.present_loc);
				metroMap.getOverlays().add(presentLoc);
				
				nearbyStations = new ArrayList<Location>();
				String []lats = getResources().getStringArray(R.array.latitudes);
				String []lngs = getResources().getStringArray(R.array.logitude);
				
				Toast.makeText(getApplicationContext(), "Radius: "+radius+"m",  Toast.LENGTH_SHORT).show();
				
				for(int i=0;i<lats.length;i++){
					Location l1 = new Location("");
					l1.setLatitude(Double.parseDouble(lats[i]));
					l1.setLongitude(Double.parseDouble(lngs[i]));
					if(location.distanceTo(l1)<radius){
						nearbyStations.add(l1);												
					}
					
				}
				draw_items();
			}
			
		}

		public void onProviderDisabled(String provider) {
			Toast.makeText(getApplicationContext(), "Please enable your GPS", Toast.LENGTH_LONG).show();
			
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        	radius = progress;
        	new MyLocationListener().onLocationChanged(presentLocation);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {}

        public void onStopTrackingTouch(SeekBar seekBar) {}

    }

}
	