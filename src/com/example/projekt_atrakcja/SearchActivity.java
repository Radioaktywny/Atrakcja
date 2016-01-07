package com.example.projekt_atrakcja;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import cache.Miejsca;
import cache.User;
import logowanie.Baza;
import logowanie.Logowanie;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//---wszystko do poprawy XD



public class SearchActivity extends FragmentActivity implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
	private Location mLastLocation;
	GPSLocation gps;
	protected User user;
	private int id=0;
	private double x=0;
	private double y=0;
	protected Miejsca m;
	protected static Location Aktualna_Lokalizacja=null;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		try {
			id=sprawdz_id();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			Log.d("znalezione lokacje:", e.getMessage());
		}
        super.onCreate(savedInstanceState);
        Log.d("Search_onCreate:", "start");
        setContentView(R.layout.search_layout);
        Log.d("Search_onCreate:", "start");
        if(wczytaj_pasy(getBaseContext()))
        {
        Log.d("ustaw", "ustawianie");
        com.google.android.gms.maps.SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
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
//najwazniejsza funkcja
    @Override
    public void onMapReady(GoogleMap map) {
    	gps = new GPSLocation(SearchActivity.this);
    	Aktualna_Lokalizacja=gps.getLocation();
    	LatLng polozenie = new LatLng(gps.getLatitude(), gps.getLongitude());
		try {
			pobierz_lokalizacje_sqllite(map);
		} catch (NumberFormatException | InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			Log.d("znalezione lokalizacje", "nic nie znalazlo wysyapl sie bo "+e.getMessage());
		}
		map.addMarker(new MarkerOptions().position(polozenie).title("Tu jestes!"));
	   	map.moveCamera(CameraUpdateFactory.newLatLngZoom(polozenie, 16));
    } 	
    private void pobierz_lokalizacje_sqllite(GoogleMap map) throws NumberFormatException, InterruptedException, ExecutionException {
    	m= new Miejsca(getBaseContext(),null,null);//inicjalizacja miejsca 
    	String lokacja="";
    	String nazwa="";
    	String opis="";
    	String user="";
    	Double [] d= new Double[2];
    	int p=0;
    	for(int i=0; i<=m.getLastId();i++)
    	{
    	    p++;
    		lokacja=m.getLokalizajca(i);
    		nazwa=m.getNazwa(i);
    		opis=m.getOpis(i);
    		user=m.getUzytkownik(i);
    		d=przerob_lokacje(lokacja);
    		Log.d("znalezione lokacje:", lokacja+nazwa+opis+user+d);
    		rysuj_innych(map,i,user,opis,nazwa,d[0],d[1]);
    		
    	}
    	}
	//nazwa=\"z\""
    private void pobierz_lokalizacje(GoogleMap map ) throws InterruptedException, ExecutionException {
    	ExecutorService exe = Executors.newFixedThreadPool(4);
    	String lokacja="";
    	String nazwa="";
    	String opis="";
    	String user="";
    	for(int i=0;i<id;i++ )
    	{
        Future <String> nazwa_f= exe.submit(new Baza("SELECT `nazwa` FROM `miejsca` where `id`=\""+i+"\"", "zwroc2"));
    	Future <String> lokalizacja_f= exe.submit(new Baza("SELECT `lokalizacja` FROM `miejsca` where `id`=\""+i+"\"", "zwroc2"));
    	Future <String> user_f= exe.submit(new Baza("SELECT `uzytkownik` FROM `miejsca` where `id`=\""+i+"\"", "zwroc2"));
    	Future <String> opis_f= exe.submit(new Baza("SELECT `Opis` FROM `miejsca` where `id`=\""+i+"\"", "zwroc2"));
    	przerob_lokacje(lokalizacja_f.get());
    	nazwa=nazwa_f.get();
    	lokacja=lokalizacja_f.get();
    	user=user_f.get();
    	opis=opis_f.get();
   // 	rysuj_innych(map,user,opis,nazwa);
    	Log.d("znalezione lokacje:", nazwa+String.valueOf(x)+String.valueOf(y)+opis);
    	
    	}
    }
	private void rysuj_innych(GoogleMap map,int id, String user, String opis, String nazwa, Double d, Double d2) {
	    LatLng polozenie = new LatLng(d, d2);
//		FTP f = new FTP();
//		                                                                            //przygotowanie pod zdjecia
//        f.pobierz(this.getBaseContext(),"miejsca",String.valueOf(id));
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = BitmapFactory.decodeFile(getFilesDir()+"/miejsca/"+String.valueOf(id)+".png", options);
//        
//        if(bitmap!=null)
//        {
//        BitmapDescriptor b = BitmapDescriptorFactory.fromBitmap(bitmap);
//        map.addMarker(new MarkerOptions().position(polozenie).title(nazwa).snippet(opis));  
//        }
//        else
        map.addMarker(new MarkerOptions().position(polozenie).title(nazwa).snippet(opis)); 
        Log.d("znalezione lokacje", "narysowalem dla :"+String.valueOf(x));
	}
	private Double[] przerob_lokacje(String string) {
		int koniec=0;
		Double[] d =new Double[2];
		try{
		for(int i=0; i <string.length()-3; i++)
		{
			if(string.startsWith("x", i))
			{	
				while(!string.startsWith("y", koniec))//dop�ki nie napotkam kolejnej liczby
				{
	   			 koniec++;
				}
				d[0]= Double.valueOf(string.substring(i+1, koniec));
				Log.d("zparsowalo_x", string.substring(i+1, koniec));
				break;
			}
		}
		Log.d("zparsowalo_y", string.substring(koniec+1, string.length()));
		d[1] = Double.valueOf(string.substring(koniec+1, string.length()-1));
		
		}
		catch(NumberFormatException e)
		{
			Log.d("nie zparsowalo", e.getMessage());
		}
		return d;
	}
	protected void rysuj(GoogleMap map, GPSLocation gps, String nazwa) {
		LatLng polozenie = new LatLng(gps.getLatitude(), gps.getLongitude());
        map.addMarker(new MarkerOptions().position(polozenie).title(nazwa));
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
	private int sprawdz_id() throws NumberFormatException, InterruptedException, ExecutionException {
		ExecutorService exe = Executors.newFixedThreadPool(1);
		Future<String> fut=exe.submit(new Baza("SELECT `id` from `miejsca` ORDER BY `id` DESC LIMIT 1", "zwroc2"));
		Log.d("id_zwrot_fut", fut.get());
		int i=fut.get().charAt(0)-47;
		Log.d("id_zwrot_i", fut.get());
		if(fut.get().length()>0)
		return i;
		else
			return 0;
	}
	private boolean wczytaj_pasy(Context context) 
    {       
        try {
            File plik = new File(context.getFilesDir().getAbsolutePath() + "/" + "userpass" +".txt");
            Scanner in = new Scanner(plik);
            user= new User(in.nextLine(),in.nextLine(),in.nextLine());  
            in.close();
            return true;
            } catch (IOException e) {   
                e.printStackTrace();
                return false;
            }           
                 
    }
	public static Location getLokalizacja() {
		return Aktualna_Lokalizacja;

	} 	
}