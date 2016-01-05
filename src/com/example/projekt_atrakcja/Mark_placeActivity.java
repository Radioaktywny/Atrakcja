package com.example.projekt_atrakcja;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;



import com.example.projekt_atrakcja.R;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import cache.Miejsca;
import cache.User;
import logowanie.Baza;
import logowanie.Logowanie;
 
public class Mark_placeActivity extends Activity {
    private User user;
    Location lokalizacja;
    private int id=0;
    protected Miejsca m;
    private Bitmap photo=null;
    
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	wyslijcos();
    	Log.d("zwrot", String.valueOf(sprawdz_czy_juz_nie_dodane()));
    	if(sprawdz_czy_juz_nie_dodane())
        {	
           	startActivity(new Intent(Mark_placeActivity.this,MainActivity.class));
        	finish();
        	Toast("To miejsce juz istnieje ! tu trzeba dodac nowy leyout z wyswietleniem informacji o miejscu");
        }
        else{
        	super.onCreate(savedInstanceState);
	        lokalizacja=pobierz_lokalizacje();
	        if(wczytaj_pasy(getBaseContext()))
	        {
	            setContentView(R.layout.mark_place_layout);
	        }        
	        else
	        {
	            startActivity(new Intent(Mark_placeActivity.this,Logowanie.class));
	            finish();
	        }
        } 
    	
    	
    }
    private void wyslijcos() 
    {
        
                // TODO Auto-generated method stub
        
    }
    public void zrob_zdjecie(View view)
    {
    	//Obsluga_zdjecia ob=new Obsluga_zdjecia();
    	Intent zdjecie=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	startActivityForResult(zdjecie, 1);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
        
    	
    	if(requestCode == 1 && resultCode == RESULT_OK)
    	{
    	    ImageView miniaturka =(ImageView) findViewById(R.id.imageView1);
            
            photo = (Bitmap) data.getExtras().get("data"); 
            miniaturka.setImageBitmap(photo); 
            
    	}
    }
    
    private boolean sprawdz_czy_juz_nie_dodane() {
    	try{
    	  ExecutorService exe = Executors.newFixedThreadPool(1);
          Future <String> czy= exe.submit(new Baza("select * from `miejsce` where lokalizacja=\""+"x"+String.valueOf(lokalizacja.getLatitude())+"y"+String.valueOf(lokalizacja.getLongitude())+"\"", "sprawdz_polaczenie"));
          if(czy.get().equals("polaczono"))
			{
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
		return false;
		}
    	
	}
	private Location pobierz_lokalizacje() {
    	Log.d("Lokalizacja", String.valueOf(SearchActivity.getLokalizacja().getLatitude()));
		return SearchActivity.getLokalizacja();	
	}
    
    public void oznacz(View view) 
    {	
    	try {
		id = sprawdz_id();
    	} catch (NumberFormatException | InterruptedException | ExecutionException e1) {
		Log.d("id_blad",e1.getMessage() );
    	}
    	
    	String lokalizacja_string="x"+String.valueOf(lokalizacja.getLatitude())+"y"+String.valueOf(lokalizacja.getLongitude());
    	EditText edittext1 =(EditText) findViewById(R.id.opis);
		EditText edittext2 =(EditText) findViewById(R.id.nazwa_miejsca);
		String opis = edittext1.getText().toString();
		String nazwa_miejsca = edittext2.getText().toString();	
		Log.d ("rozmiar opisu", String.valueOf(opis.length()));
		Log.d ("rozmiar opisu", String.valueOf(nazwa_miejsca.length()));  
		
		
		
		
		if(opis.length() >100 || nazwa_miejsca.length() >40)
		{
			if(opis.length() >100)
			Toast("Zbyt d³ugi opis");
			else
			Toast("Zbyt dluga nazwa");
		}else
        if(opis.equals("") || nazwa_miejsca.equals("") )
        {
        	Toast("Wype³nij Wszystkie pola !");// tu bedzie layout
        }else
    	if(test_polaczenia())
        {
    	    if(photo!=null)
            {
                FTP serwerftp= new FTP();
                try {
                    serwerftp.wyslij(photo,nazwa_miejsca);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }                           
            }
            else
            {
                Toast("Brak zdjêcia :C");               
            }
        	dodaj_lokalizacje(nazwa_miejsca, opis);
        	Toast("Miejsce zostalo dodane :) ");
        	
        	setContentView(R.layout.mark_place_layout);
        }
        else{
        	Toast("brak polaczenie z internetem");
        }
        }
      

       
    private void wyczysc_przyciski() {
    	
		
	}
	private void Toast(String informacja)
   	{
    	Toast info = Toast.makeText(Mark_placeActivity.this, informacja, Toast.LENGTH_LONG);
    	info.setGravity(Gravity.CENTER, 0, 0);
    	info.show();
    }
        
    private void dodaj_lokalizacje(String nazwa_miejsca, String opis) {
    	
		
    	String lokal;
    	lokal="x"+lokalizacja.getLatitude()+"y"+lokalizacja.getLongitude();
    	ExecutorService exe = Executors.newFixedThreadPool(1);
    	try{ 
    		exe.submit(new Baza("INSERT INTO `miejsca`(`id` ,`nazwa`, `lokalizacja`, `uzytkownik`, `Opis`) VALUES ("+id+",\""+nazwa_miejsca+"\",\""+lokal+"\",\""+user.getLogin()+"\",\""+opis+"\");", "dodaj"));
    		Log.d("dodaj_lokalizacje","INSERT INTO `miejsca`(`id` , `nazwa`, `lokalizacja`, `uzytkownik`, `Opis`) VALUES ("+id+",\""+nazwa_miejsca+"\",\""+lokal+"\",\""+user.getLogin()+"\",\""+opis+"\");");
    		}catch(Exception e)
    	{
    		Log.d("dodaj_lokalizacje", e.getMessage());
    	}

	}
	

    
	
	private int sprawdz_id() throws NumberFormatException, InterruptedException, ExecutionException {
		ExecutorService exe = Executors.newFixedThreadPool(1);
		Future<String> fut=exe.submit(new Baza("SELECT `id` from `miejsca` ORDER BY `id` DESC LIMIT 1", "zwroc2"));
		Log.d("id_zwrot_fut", fut.get());
		int i=Integer.parseInt(fut.get().replaceAll("[\\D]",""));
		i++;
		Log.d("id_zwrot_i", String.valueOf(i));
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
            user= new User(in.nextLine(),in.nextLine());  
            in.close();
            return true;
            } catch (IOException e) {   
                e.printStackTrace();
                return false;
            }   
		
                 
    }
	private boolean test_polaczenia() {
		try {
			ExecutorService exe = Executors.newFixedThreadPool(1);
			Future <String> Czy_istnieje_baza= exe.submit(new Baza("","sprawdz_polaczenie"));
			
			if(Czy_istnieje_baza.get().equals("polaczono"))
			{
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
		return false;
		}
}
}