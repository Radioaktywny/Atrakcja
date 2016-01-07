package logowanie;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.example.projekt_atrakcja.MainActivity;
import com.example.projekt_atrakcja.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import cache.Miejsca;
import cache.User;

public class Logowanie extends Activity 
{
    private  String login;
    private  String haslo;
    protected  Miejsca m;
    private ProgressBar proces;
    private String Zaladowany_sqllite="";
    private Handler handler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{	 final SQLiteDatabase db = openOrCreateDatabase("miejsca", MODE_PRIVATE, null);
	     super.onCreate(savedInstanceState);
	     if(test_polaczenia()){
	     new Thread(new Runnable() {
			@Override
			public void run() {
				 aktualizuj_sqllita(db);
			}
		}).start();}else{
			
			buildDialog(Logowanie.this).show();
			//tu trzeba dodac activity
			//finish();
			
		}
	    
		if(wczytaj_pasy(getApplicationContext()))
            try 
		    {
                zaloguj_do_bazy(false);
            } catch ( Exception e)
		    {
                setContentView(R.layout.activity_logowanie);
          //      finish();
                e.printStackTrace();
            }
		else
		    setContentView(R.layout.activity_logowanie);
            //jesli sie nie udalo wczytaj hasla i loginu to zaladuj normalny widok do logowania	
	}	
	
	private void kolo(String info) {
		if(info.equals("utworz"))
		{
			proces=(ProgressBar)findViewById(R.id.kolo_ladowania);
		    proces.setVisibility(View.GONE);
		}
		else if(info.equals("wlacz"))
		{
			proces.setVisibility(View.VISIBLE);
		}
		else if(info.equals("wylacz"))
		{
			proces.setVisibility(View.GONE);
		}
	}

	void aktualizuj_sqllita(final SQLiteDatabase db){
		
		ExecutorService exe = Executors.newFixedThreadPool(1);
		Future <String> zwrot=exe.submit(new Miejsca(getBaseContext(),"aktualizuj",db));
		try {
			Zaladowany_sqllite=zwrot.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			Log.d("Logowanie aktualizacja sqllita", e.getMessage());
			Zaladowany_sqllite="jeszcze nie ";
		}
		
		
	}
	
	
//	int i=0;
	public void zarejestruj(View view) throws NumberFormatException, InterruptedException, ExecutionException
	{	
		kolo("wlacz");
		Intent activity = new Intent(Logowanie.this, Rejestracja.class);  
        startActivity(activity);
        kolo("wylacz");
        
// wyswietlalo przy kliknieciu zawartosc sqllita		
//        m=new Miejsca(getBaseContext());
//		int id=m.getLastId();
//		id++;
//	//testy 
//		//Toast("KURWA DZIALA");
//	Log.d("i", String.valueOf(i));
//				String [] s= m.getRekord(i);
//				Toast(s[0]+"\n"+s[1]+s[2]+s[3]);
//				i++;
//				if(i==id)
//				{
//					i=0;
//				}
			
		
		
			
	}	
	public void zaloguj(View view) throws InterruptedException, ExecutionException, IOException 
	{	
		chowaj_klawiature(view);
	    kolo("wlacz");
		EditText edittext1 =(EditText) findViewById(R.id.text_login);
		EditText edittext2 =(EditText) findViewById(R.id.text_haslo);
		login=edittext1.getText().toString();
		haslo=edittext2.getText().toString();	
	 //	Intent activity = new Intent(Logowanie.this, MainActivity.class);  
     //   startActivity(activity);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					zaloguj_do_bazy(Czy_zczytalo_baze());
				} catch (Exception e) {
					Log.d("Logowanie zaloduj do bazy", e.getMessage());
					kolo("wylacz");
					e=null;
				}	
			}
		}).start();
	}
	private void chowaj_klawiature(View view) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		
	}

	private void zaloguj_do_bazy(final boolean czy_zapisywac) throws InterruptedException, ExecutionException, IOException , Exception
	{	   
	    Log.d("polaczenie", String.valueOf(test_polaczenia())); //---jest polaczenie z internetem        
        if(test_polaczenia())
        {
        ExecutorService exe = Executors.newFixedThreadPool(1);
        Future <String> Czy_istnieje_login= exe.submit(new Baza("select * from `uzytkownicy` where login=\""+login+"\"", "zwroc"));
        String dane_usera=Czy_istnieje_login.get();//pobieram haslo i meil uzytkownika  
      //  Log.d(tag, msg);
        if(login.equals(""))//---jezeli nie podal loginu    
        {		
        	handler.post(new Runnable() {
        	    public void run() {
        	    	kolo("wylacz");// tu sie wysypuje 
        	    	Toast("Podaj Login !");
        	    }
        	        });
        }
        else
        {  
            if(haslo.equals("")) //---jezeli nie podal haslo
            {   
           
            	handler.post(new Runnable() {
            	    public void run() {
            	    	kolo("wylacz");// tu sie wysypuje 
            	    	Toast("Podaj haslo !");
            	    }
            	        });
            	
            }
            else
            {
             
                if(dane_usera.startsWith(haslo, 0))//---jezeli podal login i haslo i sa prawidlowe
                {           
                    if(dane_usera.substring(dane_usera.length()-4,dane_usera.length()-1).equals("tak"))
                        {
                        zdjecie=true;
                        Log.d("KURWA", "dziala");
                        }
                    else
                    {
                        System.out.print("LOL "+dane_usera.substring(dane_usera.length()-4)+"LOLOLO");
                        }
                	   new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Czekaj_na_odczyt_z_bazy(czy_zapisywac);
							} catch (IOException | InterruptedException e) {
								Log.d("Logowanie czeka wysypalo sie", e.getMessage());
							}
							
						}
					}).start();                  
                }
                else
                {	
                	handler.post(new Runnable() {
                	    public void run() {
                	    	kolo("wylacz");
                    		Toast("Nie poprawne haslo !");
                	    }
                	        });
                }
            }
        }
        
        }   
    else{
    	handler.post(new Runnable() {
    	    public void run() {
    	    	kolo("wylacz");
    	    	//Toast("Brak polaczenia z internetem");

    	    	buildDialog(Logowanie.this).show();
    	    	
    	    }
    	        });
    	
        }
	}
	private void Czekaj_na_odczyt_z_bazy(boolean czy_zapisywac) throws IOException, InterruptedException {
		       
            //bez sensu troche ale nie mam pomyslu na teraz xd
            while(Czy_zczytalo_baze() == false){
            	Thread.sleep(100);
            }
            if(czy_zapisywac)//nie wiem czy tak moze byc ale tam widzialem ze3 true przesylasz xd
                zapisz_uzytkownika(getBaseContext());  
                 User user = new User(haslo,login);  
            Intent activity = new Intent(Logowanie.this, MainActivity.class);
            activity.putExtra("nazwa","dupa"); // tutaj trzeba wyslac dane usera nie wiem jak :CCC
            startActivity(activity);   
            finish();
	}

	private boolean Czy_zczytalo_baze(){
		
		if (Zaladowany_sqllite.equals("zakonczono"))
		{
			return true;
		}
		return false;
		
	}
	private void zapisz_uzytkownika(Context context) throws IOException
    {
        CheckBox czy_zapisac = (CheckBox) findViewById(R.id.check_zapamietaj);
        PrintWriter zapis = new PrintWriter(context.getFilesDir().getAbsolutePath() + "/" + "userpass" +".txt");
        zapis.println(login);
        zapis.println(haslo);
        if(czy_zapisac.isChecked())
        {
            zapis.println("1");
        }
        else
        {
            zapis.print("0");
        }
        zapis.close();
         
    }
    private boolean wczytaj_pasy(Context context) 
    {       
        try {
            File plik = new File(context.getFilesDir().getAbsolutePath() + "/" + "userpass" +".txt");
            Scanner in = new Scanner(plik);
            login=in.nextLine();        
            haslo=in.nextLine();            
            if(in.nextLine().startsWith("0"))
            { in.close(); 
                return false;
            }
            else    
            {
                in.close(); 
                return true;
            }           
            } catch (IOException e) {   
                e.printStackTrace();
                return false;
            }           
                 
    }
    public AlertDialog.Builder buildDialog(final Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Brak Polaczenia z internetem");
        builder.setMessage("Czy chcesz wlaczyc internet ?");

        builder.setPositiveButton("Wifi", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            	Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
				startActivity(intent);
				
            }
        });
        builder.setNegativeButton("Dane", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
				startActivity(intent);
			}
		});

        return builder;
    }

    private boolean test_polaczenia() {
    	 ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	 NetworkInfo netInfo = cm.getActiveNetworkInfo();
    	 return netInfo != null && netInfo.isConnectedOrConnecting();	
	}
	
	private void Toast(String informacja)
	{
		try{
		Toast info = Toast.makeText(Logowanie.this, informacja, Toast.LENGTH_LONG);
		info.setGravity(Gravity.CENTER, 0, 0);
		info.show();
		}catch(Exception e)
		{
			Toast info = Toast.makeText(Logowanie.this, "cos poszlo nie tak", Toast.LENGTH_LONG);
			info.setGravity(Gravity.CENTER, 0, 0);
			info.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.logowanie, menu);
		return true;
	}

	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
