package com.example.projekt_atrakcja;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import com.example.projekt_atrakcja.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import logowanie.Logowanie;
 
public class ProfileActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        String m="tu bedzie login";
        TextView b_wyloguj = (TextView) findViewById(R.id.textView2);
        b_wyloguj.setText( m);
              
    }
    public void wyloguj(View view) throws FileNotFoundException
    {
        Context context = getBaseContext();
        PrintWriter zapis = new PrintWriter(context.getFilesDir().getAbsolutePath() + "/" + "userpass" +".txt");
        zapis.print("brak_uzytkownika");
        zapis.close();
        
        startActivity(new Intent(ProfileActivity.this,Logowanie.class));
        finish();
        
    }
}