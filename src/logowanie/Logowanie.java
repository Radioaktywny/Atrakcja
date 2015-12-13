package logowanie;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.HttpsURLConnection;

import com.example.projekt_atrakcja.MainActivity;
import com.example.projekt_atrakcja.R;
import com.example.projekt_atrakcja.R.id;
import com.example.projekt_atrakcja.R.layout;
import com.example.projekt_atrakcja.R.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Logowanie extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logowanie);
	}
	
	public void zarejestruj(View view)
	{	

		startActivity(new Intent(Logowanie.this,Rejestracja.class));
	}
	
	public void zaloguj(View view) throws InterruptedException, ExecutionException
	{
		EditText edittext1 =(EditText) findViewById(R.id.text_login);
		EditText edittext2 =(EditText) findViewById(R.id.text_haslo);
		final String login=edittext1.getText().toString();
		final String haslo=edittext2.getText().toString();
		
		Log.d("polaczenie", String.valueOf(test_polaczenia()));
		//---jest polaczenie z internetem
		if(test_polaczenia()){
		ExecutorService exe = Executors.newFixedThreadPool(1);
		Future <String> Czy_istnieje_login= exe.submit(new Baza("select * from `uzytkownicy` where login=\""+login+"\"", "zwroc"));
		String dane_usera=Czy_istnieje_login.get();//pobieram haslo i meil uzytkownika	
		//---jezeli nie podal loginu	
		if(dane_usera.equals(""))
		{
				Toast("Uzytkownik o takim loginie nie istnieje");
		}
		else
		{	//---jezeli nie podal haslo
			if(haslo.equals(""))
			{	
				Toast("Podaj haslo !");
			}
			else{
				//---jezeli podal login i haslo i sa prawidlowe
				if(dane_usera.startsWith(haslo, 0))
				{
					startActivity(new Intent(Logowanie.this, MainActivity.class));
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
		Toast info = Toast.makeText(Logowanie.this, informacja, 10000);
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
