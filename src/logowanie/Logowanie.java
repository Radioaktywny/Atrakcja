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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Logowanie extends Activity 
{
    private  String login;
    private  String haslo;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		// sprawdzenie czy istnieje plik z danymi do logowania jesli istnieje to proba zalogowania
		if(wczytaj_pasy(getApplicationContext()))
            try 
		    {
                zaloguj_do_bazy(false);
            } catch (InterruptedException | ExecutionException
                    | IOException e)
		    {
                setContentView(R.layout.activity_logowanie);
                finish();
                e.printStackTrace();
            }
		else
		    setContentView(R.layout.activity_logowanie);
        
            //jesli sie nie udalo wczytaj hasla i loginu to zaladuj normalny widok do logowania	
	}	
	public void zarejestruj(View view)
	{	
		startActivity(new Intent(Logowanie.this,Rejestracja.class));
		finish();
				
	}	
	public void zaloguj(View view) throws InterruptedException, ExecutionException, IOException 
	{
		EditText edittext1 =(EditText) findViewById(R.id.text_login);
		EditText edittext2 =(EditText) findViewById(R.id.text_haslo);
		login=edittext1.getText().toString();
		haslo=edittext2.getText().toString();	
		zaloguj_do_bazy(true);
		
	}
	private void zaloguj_do_bazy(boolean czy_zapisywac) throws InterruptedException, ExecutionException, IOException
	{
	    
	    Log.d("polaczenie", String.valueOf(test_polaczenia())); //---jest polaczenie z internetem        
        if(test_polaczenia())
        {
        ExecutorService exe = Executors.newFixedThreadPool(1);
        Future <String> Czy_istnieje_login= exe.submit(new Baza("select * from `uzytkownicy` where login=\""+login+"\"", "zwroc"));
        String dane_usera=Czy_istnieje_login.get();//pobieram haslo i meil uzytkownika  
        
        if(dane_usera.equals(""))//---jezeli nie podal loginu    
        {
                Toast("Uzytkownik o takim loginie nie istnieje");
        }
        else
        {  
            if(haslo.equals("")) //---jezeli nie podal haslo
            {   
                Toast("Podaj haslo !");
            }
            else
            {
             
                if(dane_usera.startsWith(haslo, 0))//---jezeli podal login i haslo i sa prawidlowe
                {           if(czy_zapisywac)
                        zapisz_uzytkownika(getBaseContext());  
                        //User user = new User(haslo,login);
                        Intent activity = new Intent(Logowanie.this, MainActivity.class);
                        
                        //activity.putExtra(); // tutaj trzeba wyslac dane usera nie wiem jak :CCC
                        
                        startActivity(activity);                        
                        finish();                        
                }
                else
                {
                    Toast("Nie poprawne haslo");
                }
            }
        }
        
        }   
    else{
            Toast("Brak polaczenia z internetem");
        }
	}
	private void zapisz_uzytkownika(Context context) throws IOException
	{
	    CheckBox czy_zapisac = (CheckBox) findViewById(R.id.check_zapamietaj);
        if(czy_zapisac.isChecked())
        {
//            File plik = new File(context.getFilesDir().getAbsolutePath() + "/" + "userpass" +".txt");
//            FileOutputStream fos = context.openFileOutput(plik.getCanonicalPath(),Context.MODE_PRIVATE);
//            Writer out = new OutputStreamWriter(fos);     
//        out.write(login+"\n"+haslo);
//        out.close();
        PrintWriter zapis = new PrintWriter(context.getFilesDir().getAbsolutePath() + "/" + "userpass" +".txt");
        zapis.println(login);
        zapis.println(haslo);
        zapis.close();
        }   
    }
	private boolean wczytaj_pasy(Context context) 
	{	    
	    try {
	        File plik = new File(context.getFilesDir().getAbsolutePath() + "/" + "userpass" +".txt");
	        Scanner in = new Scanner(plik);
	        login=in.nextLine();
	        haslo=in.nextLine();
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
	
	private void Toast(String informacja)
	{
		Toast info = Toast.makeText(Logowanie.this, informacja, Toast.LENGTH_LONG);
		info.setGravity(Gravity.CENTER, 0, 0);
		info.show();
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
