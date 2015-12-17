package com.example.projekt_atrakcja;


import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import com.example.projekt_atrakcja.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cache.User;
import logowanie.Logowanie;
 
public class ChallengeActivity extends Activity {
    private User user;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        if(wczytaj_pasy(getBaseContext()))
        {
            setContentView(R.layout.challenge_layout);
            
        }        
        else
        {
            startActivity(new Intent(ChallengeActivity.this,Logowanie.class));
            finish(); 
        }
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