package com.example.projekt_atrakcja;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.example.projekt_atrakcja.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import logowanie.Baza;
import logowanie.Logowanie;
import logowanie.Rejestracja;
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
        uzytkownik.setText("Witaj "+user.getLogin());
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
    public void zmien_haslo(View view)
    {
        EditText nowehaslo = (EditText) findViewById(R.id.nowe_haslo);
        EditText nowehaslo_powtorz = (EditText) findViewById(R.id.nowe_haslo1);
        EditText aktualnehaslo = (EditText) findViewById(R.id.haslo_aktualne);
        String haslo1=nowehaslo.getText().toString();
        String haslo2=nowehaslo_powtorz.getText().toString();
        String hasloaktualne=aktualnehaslo.getText().toString();
        //--jezeli formularz jest pusty
            if(haslo1.equals("") || haslo2.equals("") || hasloaktualne.equals("") )
            {
                Toast("Musisz wypelnic wszystkie pola !");
            }
            else
            {     
                //--jezeli formulaz jest wypelniony i konto nie istnieje
                
                    if(haslo1.equals(haslo2))
                    {
                        try {
                        ExecutorService exe = Executors.newFixedThreadPool(1);
                        Future <String> dane_usera= exe.submit(new Baza("select * from `uzytkownicy` where login=\""+user.getLogin()+"\"", "zwroc"));
                        String haslo;
                        
                            haslo = dane_usera.get().toString();
                       
                         
                        if(haslo.startsWith(haslo, 0))
                        {
                            
                            exe.submit(new Baza("UPDATE uzytkownicy SET haslo=\""+haslo1+"\" where `login`=\""+user.getLogin()+"\"", "dodaj"));
                            //startActivity(new Intent(Rejestracja.this, Logowanie.class));
                            // finish();  
                            Toast("Zmiana has³a zakoñczona powodzeniem");
                            zapisz_uzytkownika(getApplicationContext(), user.getLogin(), haslo1);
                            wczytaj_pasy(getBaseContext());
                            
                        }
                        else
                        {
                            Toast("niepooprawne haslo !");
                        }
                    } catch (InterruptedException | ExecutionException | IOException e) 
                        
                    {
                        Toast("Blad w polaczeniu z internetem sprawdz polaczenie");
                        e.printStackTrace();
                    }//pobieram haslo i meil uzytkownika  
                    }
                    else
                    {
                        Toast("Nie prawidlowe haslo sproboj ponownie");
                    }
                
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
    private void zapisz_uzytkownika(Context context,String login,String haslo) throws IOException
    {
        
        PrintWriter zapis = new PrintWriter(context.getFilesDir().getAbsolutePath() + "/" + "userpass" +".txt");
        zapis.println(login);
        zapis.println(haslo);
        {
            zapis.print("0");
        }
        zapis.close();
         
    }
    private void Toast(String informacja)
    {
        Toast info = Toast.makeText(ProfileActivity.this, informacja, Toast.LENGTH_LONG);
        info.setGravity(Gravity.CENTER, 0, 0);
        info.show();
    }
}