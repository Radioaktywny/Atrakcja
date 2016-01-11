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
import android.widget.TextView;
import android.widget.Toast;
import cache.Miejsca;
import cache.Miejsca2;
import cache.User;
import logowanie.Baza;
import logowanie.Logowanie;
import logowanie.Rejestracja;

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
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
	private Marker moj_aktualny_marker;
	private Marker marker_wywolania;
	Handler hand=new Handler();
	private Location mLastLocation;
	GPSLocation gps;
	GPSLocation aktualizuj_gps;
	int ilosc_markerow=-1;
	protected User user;
	private int id=0;
	private int id_sqllite;
	private double x=0;
	private double y=0;
	protected Miejsca m;
	private int index_zdjec;
	protected static Location Aktualna_Lokalizacja=null;
	boolean start=true;
	GoogleMap moja_mapka;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		
		
		sprawdzaj_id_watek();
		//test_gps();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        laduj_zdjecia_watek(0);
        
        if(wczytaj_pasy(getBaseContext()))
        {
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
	private void laduj_zdjecia_watek(final int i) {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				FTP f = new FTP();
				if(i==0)
				for(;index_zdjec<=id_sqllite ; index_zdjec++){ // tu trzeba poprawic
					try{
						f.pobierz(getBaseContext(),"miejsca",String.valueOf(index_zdjec));
						Log.d("Search Mam zdjecia dla:",  String.valueOf(index_zdjec));
					}
					catch(Exception e)
					{
						Log.d("Search zdjecia", "takich zjec to nima dla"+ index_zdjec);
					}
					}
				f.pobierz(getBaseContext(),"miejsca",String.valueOf(i));		
			}
		}).start();
	}
	private void sprawdzaj_id_watek(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					while(true){
						Log.d("Search activity:sprawdzaniu watku", "sprawdza");
						id_sqllite=sprawdz_id();
						Log.d("Search activity:sprawdzaniu watku", "id:"+id+ " id_lite"+id_sqllite);
						Thread.sleep(7000);
						if(id!=id_sqllite && moja_mapka != null)
						{Log.d("Search activity:sprawdzaniu watku", "dodawanie id_sqllite");
							
							aktualizuj_sqllita();
							laduj_zdjecia_watek(id_sqllite);
							
						}
					}
					} catch (InterruptedException | ExecutionException e) {
					Log.d("Search activity: blad przy sprawdzaniu watku", e.getMessage());
				}
			}
		}).start();
	}
		
	private void aktualizuj_sqllita() {
				try{
			    	m= new Miejsca(getBaseContext(),null,null);//inicjalizacja miejsca 
			    	final SQLiteDatabase db = openOrCreateDatabase("miejsca", MODE_PRIVATE, null);
			    	ExecutorService exe = Executors.newFixedThreadPool(1);
					Future <String> zwrot=exe.submit(new Miejsca2(getBaseContext(),"aktualizuj",db));
						String Zaladowany_sqllite = zwrot.get();
						Log.d("Logowanie aktualizacja_sqllita", "powiodla sie");
//					} catch (InterruptedException | ExecutionException e) {
//						// TODO Auto-generated catch block
//						Log.d("Logowanie aktualizacja_sqllita", e.getMessage());
//					}
					if(Zaladowany_sqllite.equals("zakonczono"))
					{
						String lokacja="";
				    	String nazwa="";
				    	String opis="";
				    	String user="";
				    	Double [] d= new Double[2];
				    	int p=0;
				    	for(; id<=m.getLastId();id++)
				    	{	
				    	    p++;
				    		lokacja=m.getLokalizajca(id);
				    		nazwa=m.getNazwa(id);
				    		opis=m.getOpis(id);
				    		user=m.getUzytkownik(id);
				    		d=przerob_lokacje(lokacja);
				    		Log.d("znalezione lokacje:", lokacja+nazwa+opis+user+d);
				    		rysuj_innych(moja_mapka,id,user,opis,nazwa,d[0],d[1]);
				    		
				    	}
				    	id--;
					}
			}	catch(Exception e){}
		}
	
//najwazniejsza funkcja
    @Override
    public void onMapReady(GoogleMap map ) {
    if(start){
    	Log.d("nacisnieto " , "zaladowalo mape");
    	gps = new GPSLocation(SearchActivity.this);
    	aktualizuj_gps= new GPSLocation(SearchActivity.this);
    	test_gps_watek();
    	Aktualna_Lokalizacja=gps.getLocation();
    	moja_mapka=map;
    	inicjalizuj_layout_markerow(moja_mapka);
    	LatLng polozenie = new LatLng(gps.getLatitude(), gps.getLongitude());
    	moj_aktualny_marker = map.addMarker(new MarkerOptions().position(polozenie).title("Tu jestes!").snippet("").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
	   	map.moveCamera(CameraUpdateFactory.newLatLngZoom(polozenie, 16));
	   	//wypisz najblizsze markery
	   		try{
	   			onUserInteraction();
	   		}catch(Exception e){
	   				
	   			}
    }else{
    //	tu powinien byc ifek dodawaj markery jezeli nie istnieja pytanie jak Xd
	   			try {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					 try {
						pobierz_lokalizacje_sqllite(moja_mapka);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						
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

    	} 
    }
    
    LatLng polozenie2;
    private void test_gps_watek()
    {	
			new Thread(new Runnable() {
				public void run() {
							// TODO Auto-generated method stub
							while(true){
								try {
									Thread.sleep(5000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							hand.post(new Runnable() {
							@Override
							public void run() {
							aktualizuj_gps = new GPSLocation(getBaseContext());
									}
								});
							Location lokalizacja = aktualizuj_gps.getLocation();
							if(gps.getLatitude() != aktualizuj_gps.getLatitude() && gps.getLongitude() != aktualizuj_gps.getLongitude())
							{	polozenie2 = new LatLng(aktualizuj_gps.getLatitude(), aktualizuj_gps.getLongitude());
							hand.post(new Runnable() {
								@Override
								public void run() {
									moj_aktualny_marker.remove();
									moj_aktualny_marker=moja_mapka.addMarker(new MarkerOptions().position(polozenie2).title("Tu jestes!").snippet("").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
								}
							});	
							  gps=aktualizuj_gps;
							}	
							Log.d("Search test_gpsu", String.valueOf(lokalizacja.getLatitude()));
							}			
				}
			}).start();
    }
    

    private void inicjalizuj_layout_markerow(GoogleMap map) {
    	map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			
			@Override
			public void onInfoWindowClick(Marker marker) {
				marker_wywolania=marker;
				Intent activity = new Intent(SearchActivity.this, PlaceView.class);  
		        startActivity(activity);
				
			}
		});
    	map.setInfoWindowAdapter(new InfoWindowAdapter() {
    		
			@Override
			public View getInfoWindow(Marker arg0) {
				return null;//musi byc tak jak jest
			}
			
			@Override
			public View getInfoContents(Marker marker) {
				
				try {
				m= new Miejsca(getBaseContext(),null,null);
				int id=m.getId(marker.getTitle());
				
				View v = getLayoutInflater().inflate(R.layout.infookienko, null);
				TextView nazwa_miejscaTV=(TextView) v.findViewById(R.id.nazwa);
				TextView opisTV=(TextView) v.findViewById(R.id.opis);
				ImageView obrazek=(ImageView) v.findViewById(R.id.fota);
				
				nazwa_miejscaTV.setText(marker.getTitle());
				opisTV.setText(marker.getSnippet());
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				Bitmap bitmap = BitmapFactory.decodeFile(getFilesDir()+"/miejsca/"+String.valueOf(id)+".png", options);
				Log.d("RYSUJE DLA ID", "powinno utworzyc bitmape dla"+id);
				if(bitmap!=null)
				{
					BitmapDescriptor b = BitmapDescriptorFactory.fromBitmap(bitmap);
					obrazek.setImageBitmap(BitmapFactory.decodeFile(getFilesDir()+"/miejsca/"+id+".png", options));
					Log.d("RYSUJE DLA ID", "no i ustawilo");
					return v;
				}else{
					return v;
				}
				
				}catch(Exception e){
					Log.d("RYSUJE DLA ID", "nie mam obrazka "+e.getMessage());
				}				
//				Log.d("RYSUJE DLA ID", "laduje bez obrazka bo sie sypnal");
//				View v = getLayoutInflater().inflate(R.layout.infookienko, null);
//				TextView nazwa_miejscaTV=(TextView) v.findViewById(R.id.nazwa);
//				TextView opisTV=(TextView) v.findViewById(R.id.opis);
//				ImageView obrazek=(ImageView) v.findViewById(R.id.fota);
//				
//				nazwa_miejscaTV.setText(marker.getTitle());
//				opisTV.setText(marker.getSnippet());
//				
//				
//				
//			
				return null;
					
    	
				//return v;
    	}	
		});

    	
	}
    public void nowy_layout(View v)
	{
    	Log.d("Search Activity", "laduje layout");
		Toast("nowy laduje go xD");
	}
	private void inicjalizuj_markery() {
		// TODO Auto-generated method stub
		
	}
	private void pobierz_lokalizacje_sqllite(GoogleMap map ) throws NumberFormatException, InterruptedException, ExecutionException {
    	m= new Miejsca(getBaseContext(),null,null);//inicjalizacja miejsca 
    	String lokacja="";
    	String nazwa="";
    	String opis="";
    	String user="";
    	Double [] d= new Double[2];
    	int p=0;
    	for(; id<=m.getLastId();id++)
    	{	
    	    p++;
    		lokacja=m.getLokalizajca(id);
    		nazwa=m.getNazwa(id);
    		opis=m.getOpis(id);
    		user=m.getUzytkownik(id);
    		d=przerob_lokacje(lokacja);
    		Log.d("znalezione lokacje:", lokacja+nazwa+opis+user+d);
    		rysuj_innych(map,id,user,opis,nazwa,d[0],d[1]);
    		
    	}
    	
    	}
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
//        	BitmapDescriptor b = BitmapDescriptorFactory.fromBitmap(bitmap);
//        	moja_mapka.addMarker(new MarkerOptions().position(polozenie).title(nazwa).snippet(opis).icon(b));  
//        }
//        else
	    Log.d("znalezione lokacje", "przed handem");
	    hand.post(new Runnable() {
			@Override
			public void run() {
				// rysowanie markerow
				try{
					
				
				moja_mapka.addMarker(new MarkerOptions().position(polozenie).title(nazwa).snippet(opis)
						 ); 	
				ilosc_markerow++;
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
    	///do wywaleni wszystko nie dziala
    	
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    


	private int sprawdz_id() throws NumberFormatException, InterruptedException, ExecutionException {
		
		
		ExecutorService exe = Executors.newFixedThreadPool(1);
		Future<String> fut=exe.submit(new Baza("SELECT `id` from `miejsca` ORDER BY `id` DESC LIMIT 1", "zwroc2"));
		int i=Integer.parseInt(fut.get().replaceAll("[\\D]",""));
		if(i !=0)
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
		super.onResumeFragments();
	
		//onUserInteraction();
	
	}

	@Override
	public void onUserInteraction(){
		///--- dziala przy dotyku
		if(!Sa_wszystkie_markery()){
		Log.d("nacisniety", " onUserInteraction() kurwa nacisnalem xd");
		start=false;
		onMapReady(moja_mapka);
		Log.d("Search sawszystkiemarkery", "markery:"+String.valueOf(ilosc_markerow) + "id:"+String.valueOf(id));
	}
		
	}
	public Marker getMarker_wywolania()
	{
		return moj_aktualny_marker;
	}	
	private boolean Sa_wszystkie_markery()
	{	
		if(ilosc_markerow== id_sqllite)
		{
			return true;
		}else
		return false;
		
	}
	
}