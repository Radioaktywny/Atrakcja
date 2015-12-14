package com.example.projekt_atrakcja;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.os.AsyncTask;

public class Localization extends AsyncTask<GPSLocation,Location, Object>{
	int i=0;
	Location mLastLocation;
	GoogleMap map;
	public Localization(GoogleMap map) {
		this.map=map;
	}

	@Override
	protected Object doInBackground(GPSLocation...gps) {
		while(true)
		{
		if(gps[0].canGetLocation()) {
    		onProgressUpdate(gps[0].location);
		} else {
			gps[0].showSettingsAlert();
		}
		}
		
	}
	
	@Override
	protected void onProgressUpdate(Location... gps)
	{	
		
		if(gps !=null){
	        LatLng polozenie = new LatLng(gps[i].getLatitude(), gps[i].getLongitude());
	        map.addMarker(new MarkerOptions().position(polozenie).title("Tu jesteœ ziomeczku"));
	        map.moveCamera(CameraUpdateFactory.newLatLng(polozenie));
	    	}
		i++;
	}
	

}
