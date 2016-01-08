package logowanie;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.example.projekt_atrakcja.R;

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

public class Rejestracja extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rejestracja);
		
	}
	public void zarejestruj(View view) throws InterruptedException, ExecutionException
	{
		EditText edittext1 =(EditText) findViewById(R.id.haslo_aktualne);
		EditText edittext2 =(EditText) findViewById(R.id.nowe_haslo);
		EditText edittext3 =(EditText) findViewById(R.id.nowe_haslo1);
		EditText edittext4 =(EditText) findViewById(R.id.editText4);
		final String login=edittext1.getText().toString();
		final String haslo=edittext2.getText().toString();
		String haslo_powtorz=edittext4.getText().toString();
		final String mail=edittext3.getText().toString();
		
		
		//--jest polaczenie
		if(test_polaczenia())
		{
			//--jezeli formularz jest pusty
			if(login.equals("") || haslo.equals("") || haslo_powtorz.equals("") || mail.equals(""))
			{
				Toast("Musisz wypelnic wszystkie pola !");
			}
			else
			{	
				ExecutorService exe = Executors.newFixedThreadPool(1);
				Future <String> Czy_istnieje_login= exe.submit(new Baza("select * from `uzytkownicy` where login=\""+login+"\"", "zwroc"));
				//--jezeli formulaz jest wypelniony i konto nie istnieje
				if(Czy_istnieje_login.get().equals(""))
				{	//--i hasla sa zgodne
					if(haslo.equals(haslo_powtorz))
					{
						
						Log.d("Rejestracja wysylaniie", "watek uruchamia");
						try{
						exe.submit(new Baza("INSERT INTO `uzytkownicy`(`login`, `haslo`, `email`,`zdjecie`) VALUES (\""+login+"\",\""+haslo+"\",\""+mail+"\",\"nie\")", "dodaj"));
						}catch (Exception e)
						{
							Log.d("Rejestracja blad_wysylaniie", e.getMessage());
							Toast("nie zarejestrowano watek sie sypnal");
						}
						startActivity(new Intent(Rejestracja.this, Logowanie.class));
						finish();
					}
					else
					{
						Toast("Nie prawidlowe haslo sproboj ponownie");
					}
				}
				else
				{
					Toast("Uzytkownik o takim loginie juz istnieje");
				}
			}
		}
		else
		{
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
		Toast info = Toast.makeText(Rejestracja.this, informacja, Toast.LENGTH_LONG);
		info.setGravity(Gravity.CENTER, 0, 0);
		info.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.rejestracja, menu);
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
