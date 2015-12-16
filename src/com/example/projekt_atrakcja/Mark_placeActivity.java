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
    private Location pobierz_lokalizacje() {
    	Log.d("Lokalizacja", String.valueOf(SearchActivity.getLokalizacja().getLatitude()));
		return SearchActivity.getLokalizacja();
		
		
	}
    
    public void oznacz() throws InterruptedException, ExecutionException
    {	
    	
    	String lokalizacja_string="x"+lokalizacja.getLatitude()+"y"+lokalizacja.getLongitude();
    	EditText edittext1 =(EditText) findViewById(R.id.opis);
		EditText edittext2 =(EditText) findViewById(R.id.nazwa_miejsca);
		String opis = edittext1.getText().toString();
		String nazwa_miejsca = edittext2.getText().toString();	
    	
    	Log.d("oznaczanie", String.valueOf(test_polaczenia())); //---jest polaczenie z internetem        
        if(test_polaczenia())
        {
//        	if(czy_pola_sa_uzupelnione())
//        ExecutorService exe = Executors.newFixedThreadPool(1);
//        Future <String> Czy_istnieje_login= exe.submit(new Baza("select * from `miejsca` where lokalizacja=\""+lokalizacja_string+"\"", "zwroc"));
//        
//        if(Czy_istnieje_login.get()!=null)//pobieram wszystko bez sensu  
     //   {
       // 	dodaj_lokalizacje();
        }
        else{
        	
        	
        	
        }
        
        
        
        
        }
      

       
    private void Toast(String informacja)
   	{
    	Toast info = Toast.makeText(Mark_placeActivity.this, informacja, Toast.LENGTH_LONG);
    	info.setGravity(Gravity.CENTER, 0, 0);
    	info.show();
    }
        
    private void dodaj_lokalizacje() {
    	String lokal;
    	lokal="x"+lokalizacja.getLatitude()+"y"+lokalizacja.getLongitude();
    	ExecutorService exe = Executors.newFixedThreadPool(1);
    	 
    	try{ 
    //	exe.submit(new Baza("INSERT INTO `miejsca`(`nazwa`, `lokalizacja`, `uzytkownik`, `Opis`) VALUES ("cos",123,"test","opis");", "zwroc"));
    	}catch(Exception e)
    	{
    		Log.d("dodaj_lokalizacje", e.getMessage());
    	}
    	 
    //    Future <String> Czy_istnieje_login= exe.submit(new Baza("select * from `miejsca` where lokalizacja=\""++"\"", "zwroc"));
		
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