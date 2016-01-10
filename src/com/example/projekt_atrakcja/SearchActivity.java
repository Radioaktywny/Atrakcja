package com.example.projekt_atrakcja;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.SharedElementCallback;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;
import cache.Miejsca;
import cache.User;
import logowanie.Baza;
import logowanie.Logowanie;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
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



public class SearchActivity extends FragmentActivity implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener  {

	//    private static final String REQUESTING_LOCATION_UPDATES_KEY = null;
//	private static final String LOCATION_KEY = null;
//	private static final String LAST_UPDATED_TIME_STRING_KEY = null;
//	String mLastUpdateTime;
//	Location mCurrentLocation;
//	boolean mRequestingLocationUpdates = true;
	private GoogleApiClient mGoogleApiClient;
	Handler hand=new Handler();
	private Location mLastLocation;
	GPSLocation gps;
	protected User user;
	private int id=0;
	private double x=0;
	private double y=0;
	protected Miejsca m;
	protected static Location Aktualna_Lokalizacja=null;
	boolean start=true;
	GoogleMap moja_mapka;
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
    public void onMapReady(GoogleMap map ) {
    if(start){
    	Log.d("nacisnieto " , "zaladowalo mape");
    	gps = new GPSLocation(SearchActivity.this);   	
    	Aktualna_Lokalizacja=gps.getLocation();
    	moja_mapka=map;
    	
    	LatLng polozenie = new LatLng(gps.getLatitude(), gps.getLongitude());
    	map.addMarker(new MarkerOptions().position(polozenie).title("Tu jestes!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
	   	map.moveCamera(CameraUpdateFactory.newLatLngZoom(polozenie, 16));
	   	//wypisz najblizsze markery
	   	
    }else{
    //	dodawaj markery jezeli nie istnieja pytanie jak XD
    	final GoogleMap map1=map;
    	
    
	   			try {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					 try {
						pobierz_lokalizacje_sqllite(map1);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						hand.post(new Runnable() {
							
							@Override
							public void run() {
								Toast("czekaj chwile bo sie sypie");
								
							}
						});
						
					}
					
				}
			}).start();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			Log.d("znalezione lokalizacje", "nic nie znalazlo wysyapl sie bo "+e.getMessage());
		}
//		map.addMarker(new MarkerOptions().position(polozenie).title("Tu jestes!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
//	   	map.moveCamera(CameraUpdateFactory.newLatLngZoom(polozenie, 16));
    } }
    
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
	private void rysuj_innych(final GoogleMap map,int id, String user, final String opis, final String nazwa, Double d, Double d2) {
	    final LatLng polozenie = new LatLng(d, d2);
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
	    Log.d("znalezione lokacje", "przed handem");
	    hand.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
					
				 moja_mapka.addMarker(new MarkerOptions().position(polozenie).title(nazwa).snippet(opis)); 		
				}catch(Exception e){
					hand.post(new Runnable() {
						
						@Override
						public void run() {
							Toast("nie istnieje taki marker ale bo czemu :C");
							
						}
					});
					
				}
				}
		});
       
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
  
    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    
 //  @Override
//    public void onConnected(Bundle connectionHint) {
//    	Log.d("connected" , "Polaczylo mnie");
//        boolean mRequestingLocationUpdates = false;
//		if (mRequestingLocationUpdates) {
//            startLocationUpdates();
//        }
   // }

//    protected void startLocationUpdates() {
//        LocationRequest mLocationRequest = null;
//		LocationServices.FusedLocationApi.requestLocationUpdates(
//                mGoogleApiClient, mLocationRequest, this);
//    }
    
//	@Override
//	public void onConnectionFailed(ConnectionResult arg0) {
//		// TODO Auto-generated method stub
//		
//	}

//	@Override
//	public void onConnectionSuspended(int arg0) {
//		// TODO Auto-generated method stub
//		
//	}
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
//	@Override
//    public void onLocationChanged(Location location) {
//      Log.d("Pozycja", String.valueOf(location.getLatitude()));
//      Toast(String.valueOf(location.getLatitude()));
//      mCurrentLocation = location;
//         mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//      
//    }
//	@Override
//	public void onSaveInstanceState(Bundle savedInstanceState) {
//		savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
//	            mRequestingLocationUpdates);
//	    
//		savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
//	    savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
//	    super.onSaveInstanceState(savedInstanceState);
//	}

	private void Toast(String informacja)
	{
		
		Toast info = Toast.makeText(SearchActivity.this, informacja, Toast.LENGTH_SHORT);
		info.setGravity(Gravity.CENTER, 0, 0);
		info.show();
		
	}
	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onResumeFragments() {
		Log.d("nacisniety", " onResumeFragments() kurwa nacisnalem xd");
		
		// dziala jak wracam do mapy
		super.onResumeFragments();
	}

	@Override
	public void onUserInteraction(){
		///--- dziala przy dotyku
		Log.d("nacisniety", " onUserInteraction() kurwa nacisnalem xd");
		start=false;
		onMapReady(moja_mapka);
	}

}
