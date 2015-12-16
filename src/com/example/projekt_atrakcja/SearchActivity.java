package com.example.projekt_atrakcja;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import logowanie.Logowanie;
import logowanie.User;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
//---wszystko do poprawy XD
public class SearchActivity extends FragmentActivity implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
	private Location mLastLocation;
	GPSLocation gps;
	protected User user;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        if(wczytaj_pasy(getBaseContext()))
        {
        Log.d("ustaw", "ustawianie");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.d("ustaw", "chyba mapa");
        }
        else
        {
            startActivity(new Intent(SearchActivity.this,Logowanie.class));
            finish(); 
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Add a marker in Sydney, Australia, and move the camera.
    	
    	gps = new GPSLocation(SearchActivity.this);
    	//Localization local=new Localization(map);
    	
    	//local.execute(gps);
    	
    	
    	
    	if(mLastLocation !=null){
        LatLng polozenie = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        map.addMarker(new MarkerOptions().position(polozenie).title("Tu jesteœ ziomeczku"));
        map.moveCamera(CameraUpdateFactory.newLatLng(polozenie));
    	}else
    	{
    		LatLng sydney = new LatLng(0, 0);
            map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    	}
    	
    }
    
    protected synchronized void buildGoogleApiClient(){
		Log.d("Building", "zbudowane");
		mGoogleApiClient= new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
		
	}
    @Override
	public void onConnected(Bundle arg0) {
		Log.d("connected" , "Polaczylo mnie");
		mLastLocation= LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if(mLastLocation != null)
		{
			Log.d("dlugosc", String.valueOf(mLastLocation.getLatitude()));
			Log.d("szerokosc", String.valueOf(mLastLocation.getLongitude()));
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
	private boolean wczytaj_pasy(Context context) 
    {       
        try {
            File plik = new File(context.getFilesDir().getAbsolutePath() + "/" + "userpass" +".txt");
            Scanner in = new Scanner(plik);
            user= new User(in.nextLine(),in.nextLine());  
            in.close();
            return true;
            } catch (IOException e) {   
                e.printStackTrace();
                return false;
            }           
                 
    }
	
}