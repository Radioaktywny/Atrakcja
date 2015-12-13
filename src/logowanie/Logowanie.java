package logowanie;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
		
		ExecutorService exe = Executors.newFixedThreadPool(1);
		Future <String> Czy_istnieje_login= exe.submit(new Test("select * from `uzytkownicy` where login=\""+login+"\"", "zwroc"));
		String dane_usera=Czy_istnieje_login.get();//pobieram haslo i meil uzytkownika
		//---jezeli nie podal loginu
		if(dane_usera.equals(""))
		{
			
				Toast info = Toast.makeText(Logowanie.this, "Uzytkownik o takim loginie nie istnieje", 10000);
				info.setGravity(Gravity.CENTER, 0, 0);
				info.show();
		}
		else
		{	//---jezeli nie podal haslo
			if(haslo.equals(""))
			{
				Toast info = Toast.makeText(Logowanie.this, "Podaj haslo !", 10000);
				info.setGravity(Gravity.CENTER, 0, 0);
				info.show();
			}
			else{
				//---jezeli podal login i haslo i sa prawidlowe
				if(dane_usera.startsWith(haslo, 0))
				{
					startActivity(new Intent(Logowanie.this, MainActivity.class));
				}
				else
				{
					Toast info = Toast.makeText(Logowanie.this, "Nie poprawne haslo", 10000);
					info.setGravity(Gravity.CENTER, 0, 0);
					info.show();
				}
			}
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
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
