package logowanie;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.example.projekt_atrakcja.MainActivity;
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
		EditText edittext1 =(EditText) findViewById(R.id.editText1);
		EditText edittext2 =(EditText) findViewById(R.id.editText2);
		EditText edittext3 =(EditText) findViewById(R.id.editText3);
		EditText edittext4 =(EditText) findViewById(R.id.editText4);
		final String login=edittext1.getText().toString();
		final String haslo=edittext2.getText().toString();
		String haslo_powtorz=edittext4.getText().toString();
		final String mail=edittext3.getText().toString();
		ExecutorService exe = Executors.newFixedThreadPool(1);
		Future <String> Czy_istnieje_login= exe.submit(new Test("select * from `uzytkownicy` where login=\""+login+"\"", "zwroc"));
		
		
		if(Czy_istnieje_login.get().equals(""))
		{
			if(haslo.equals(haslo_powtorz))
			{
				exe.submit(new Test("INSERT INTO `uzytkownicy`(`login`, `haslo`, `mail`) VALUES (\""+login+"\",\""+haslo+"\",\""+mail+"\")", "dodaj"));
				startActivity(new Intent(Rejestracja.this, MainActivity.class));
			}
			else
			{
				Toast info = Toast.makeText(Rejestracja.this, "Nie prawidlowe haslo sproboj ponownie", 10000);
				info.setGravity(Gravity.CENTER, 0, 0);
				info.show();
			}
		}
		else
		{
			Toast info = Toast.makeText(Rejestracja.this, "Uzytkownik o takim loginie juz istnieje", 10000);
			info.setGravity(Gravity.CENTER, 0, 0);
			info.show();
		}
		
	
		
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
