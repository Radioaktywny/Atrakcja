package logowanie;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.example.projekt_atrakcja.R;
import com.example.projekt_atrakcja.R.id;
import com.example.projekt_atrakcja.R.layout;
import com.example.projekt_atrakcja.R.menu;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class Rejestracja extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rejestracja);
		
	}
	public void zarejestruj(View view)
	{
		EditText edittext1 =(EditText) findViewById(R.id.editText1);
		EditText edittext2 =(EditText) findViewById(R.id.editText2);
		EditText edittext3 =(EditText) findViewById(R.id.editText3);
		EditText edittext4 =(EditText) findViewById(R.id.editText4);
		final String login=edittext1.getText().toString();
		final String haslo=edittext2.getText().toString();
		String haslo_powtorz=edittext4.getText().toString();
		final String mail=edittext3.getText().toString();
		Log.d("baza", "nacisnieto");
		new Thread(new Runnable()
		{
			@Override
			public void run() {
				
				insert("INSERT INTO `uzytkownicy`(`login`, `haslo`, `mail`) VALUES (\""+login+"\",\""+haslo+"\",\""+mail+"\")");
			}
		}).start();
		
		
	}
	public void nacisnieto(View view)
	{	
		new Thread(new Runnable()
		{
			@Override
			public void run() {
				// TODO Auto-generated method stub
			//	insert();
			}
		}).start();
	}
	
	protected void insert(String sql) {
		Log.d("baza", "weszlo do inserta");
		try {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Log.d("sterownik", "sterownik zaladowany");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				Log.d("sterownik", "jak zwykle sie zjebal");
			}
			String baza = "jdbc:mysql://www.db4free.net:3306/projekt_2015";
			
			java.sql.Connection c = DriverManager.getConnection(baza, "maciek2015", "testtest");
			Statement s = c.createStatement();
			s.executeUpdate(sql);
			Log.d("baza", "wyslano sqlka");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.d("baza", e.getMessage());
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
