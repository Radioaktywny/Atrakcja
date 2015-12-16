package com.example.projekt_atrakcja;



import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.example.projekt_atrakcja.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import logowanie.Baza;
import logowanie.Logowanie;
import logowanie.User;
 
public class Mark_placeActivity extends Activity {
    private User user;
    Location lokalizacja;
    
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
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
    	
    	String lokalizacja_string="x"+String.valueOf(lokalizacja.getLatitude())+"y"+String.valueOf(lokalizacja.getLongitude());
    	EditText edittext1 =(EditText) findViewById(R.id.opis);
		EditText edittext2 =(EditText) findViewById(R.id.nazwa_miejsca);
		String opis = edittext1.getText().toString();
		String nazwa_miejsca = edittext2.getText().toString();	
    	
    	Log.d("oznaczanie", String.valueOf(test_polaczenia())); //---jest polaczenie z internetem        
        if(opis.equals("") || nazwa_miejsca.equals("") )
        {
        	Toast("Wype³nij Wszystkie pola !");// tu bedzie layout
        }else
    	if(test_polaczenia())
        {
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
    		exe.submit(new Baza("INSERT INTO `miejsca`(`nazwa`, `lokalizacja`, `uzytkownik`, `Opis`) VALUES (\""+nazwa_miejsca+"\",\""+lokal+"\",\""+user.getLogin()+"\",\""+opis+"\");", "dodaj"));
    	}catch(Exception e)
    	{
    		Log.d("dodaj_lokalizacje", e.getMessage());
    	}

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