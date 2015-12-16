package com.example.projekt_atrakcja;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import com.example.projekt_atrakcja.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import logowanie.Logowanie;
import logowanie.User;
 
public class ProfileActivity extends Activity {
    protected User user ;
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        if(wczytaj_pasy(getBaseContext()))
        {
        TextView uzytkownik = (TextView) findViewById(R.id.textView2);
        uzytkownik.setText(user.getLogin());
        }
        else
        {
        startActivity(new Intent(ProfileActivity.this,Logowanie.class));
        finish(); 
        }
    }
    public void wyloguj(View view) throws FileNotFoundException
    {
        Context context = getBaseContext();
        PrintWriter zapis = new PrintWriter(context.getFilesDir().getAbsolutePath() + "/" + "userpass" +".txt");
        zapis.println(user.getLogin());
        zapis.println(user.getPassword());
        zapis.println("0");
        zapis.close();
        startActivity(new Intent(ProfileActivity.this,Logowanie.class));
        finish();
        
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
}