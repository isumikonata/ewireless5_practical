package com.example.maptest;


//import android.os.Bundle;
//import android.app.Activity;
//import android.view.Menu;
//
//public class MainActivity extends Activity {
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
//
//}

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends Activity {
// GoogleMap map;
// @SuppressLint("NewApi")
// @Override
// protected void onCreate(Bundle savedInstanceState) {
//  super.onCreate(savedInstanceState);
//  setContentView(R.layout.activity_main);
//
//  map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
//    .getMap();
//  // map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//  // map.setMapType(GoogleMap.MAP_TYPE_NONE);
//  map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//  // map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//  // map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
// }
	
	private GoogleMapOptions options;
	private GoogleMap mMap;
	
	
	LocationManager locationManager;
	LocationListener locationlistener;
	String bestprovider;
	Criteria criteria;
	double tlat;
	double tlong;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		options = new GoogleMapOptions();
		options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
			.compassEnabled(true)
			.rotateGesturesEnabled(true)
			.scrollGesturesEnabled(true)
			.tiltGesturesEnabled(true);
		
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.setMyLocationEnabled(true);
		
		
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationlistener = new mylocationlistener();
		
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			Toast.makeText(this,  "Open GPS",  Toast.LENGTH_LONG).show();
		}
		
		bestprovider = locationManager.getBestProvider(getcriteria(), true);
		
		//Update the current activity periodically
		locationManager.requestLocationUpdates(bestprovider, 500, 5, locationlistener);
				
		LatLng latlng = new LatLng(tlat,tlong);
		mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onPause() {
		//Remove the listener when the activity is not in use
		locationManager.removeUpdates(locationlistener);
		super.onPause();
	}



	@Override
	protected void onResume() {
		//register the listener
		locationManager.requestLocationUpdates(bestprovider, 500, 5, locationlistener);
		super.onResume();
	}
	
	class mylocationlistener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			if(location!=null){
				tlat = location.getLatitude();
				tlong = location.getLongitude();
			}
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	private Criteria getcriteria(){
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		return criteria;
	}
}
