package com.example.projekt_atrakcja;


import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.example.projekt_atrakcja.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cache.Miejsca;
import cache.User;
import logowanie.Baza;
import logowanie.Logowanie;
import logowanie.Rejestracja;
 
public class ChallengeActivity extends Activity {
    private User user;
    private int licznik=0;
    private Miejsca m;
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
        m= new Miejsca(getBaseContext());
        try {
            init();
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private void init() throws InterruptedException, ExecutionException 
    {
        ExecutorService exe = Executors.newFixedThreadPool(2);
        TextView [] nazwy= {(TextView) findViewById(R.id.textView1),
                (TextView) findViewById(R.id.textView12),
                (TextView) findViewById(R.id.textView13),
                (TextView) findViewById(R.id.textView14),
                (TextView) findViewById(R.id.textView15)
                    };
        TextView [] opisy= {(TextView) findViewById(R.id.textView2),
                (TextView) findViewById(R.id.textView22),
                (TextView) findViewById(R.id.textView23),
                (TextView) findViewById(R.id.textView24),
                (TextView) findViewById(R.id.textView25)
        };
        RatingBar [] oceny= {(RatingBar) findViewById(R.id.ratingBar1),
                (RatingBar) findViewById(R.id.ratingBar12),
                (RatingBar) findViewById(R.id.ratingBar13),
                (RatingBar) findViewById(R.id.ratingBar14),
                (RatingBar) findViewById(R.id.ratingBar15)
        };
        ImageView []zdjecia= {(ImageView) findViewById(R.id.zdjecie_miejsca),
                (ImageView) findViewById(R.id.zdjecie_miejsca2),
                (ImageView) findViewById(R.id.zdjecie_miejsca3),
                (ImageView) findViewById(R.id.zdjecie_miejsca4),
                (ImageView) findViewById(R.id.zdjecie_miejsca5),
                
        };
        Future <String> ocena=exe.submit(new Baza("SELECT u.id, AVG(o.ocena) AS srednia FROM  oceny o JOIN miejsca u\r\n" + 
                "                 ON (u.lokalizacja=o.lokalizacja) group by u.id order by srednia DESC LIMIT 5", "komentarze"));
        String ocenyId[]=podzielStringa(ocena.get());
        Log.d("KURWA", ocena.get());
        for(int i=0;i<licznik/2;++i)
        {
            int id=Integer.valueOf(ocenyId[i*2]);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(getFilesDir()+"/miejsca/"+id+".png", options);
            if(bitmap!=null)
                zdjecia[i].setImageBitmap(bitmap); 
            else
                zdjecia[i].setImageResource(R.drawable.miejsce_default);             
            nazwy[i].setText(m.getNazwa(id));
            opisy[i].setText(m.getOpis(id));           
                       
            oceny[i].setRating(Float.valueOf(ocenyId[(i*2)+1]));            
            
        }
        // TODO Auto-generated method stub
        
    }
    private String[] podzielStringa(String oceny) 
    {
        String s[]=new String[12];
        
        
        int zapamietaj=0;
        try
        {
            for(int i=0;i<oceny.length();++i)
            {
            if(oceny.substring(i,i+2).equals("%+"))
            {                 
                s[licznik]=oceny.substring(zapamietaj,i);
                zapamietaj=i+2;
                ++licznik;
            }
            }
            
        }
        catch(Exception e)
        {
            return s;            
        }
        // TODO Auto-generated method stub
        return s;
    }
    private boolean wczytaj_pasy(Context context) 
    {       
        try {
            File plik = new File(context.getFilesDir().getAbsolutePath() + "/" + "userpass" +".txt");
            Scanner in = new Scanner(plik);
            user= new User(in.nextLine(),in.nextLine(),in.nextLine());  
            in.close();
            return true;
            } catch (IOException e) {   
                e.printStackTrace();
                return false;
            }           
                 
    }
    //do TESTOW !
    public void pobierz(View view)
    {
       // FTP f = new FTP();
        //f.pobierz(this.getBaseContext(),"miejsca");
        Intent activity = new Intent(ChallengeActivity.this, AddComments.class);  
        startActivity(activity);
//        ImageView miniaturka =(ImageView) findViewById(R.id.imageView2);
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = BitmapFactory.decodeFile(getFilesDir()+"/miejsca/LOL.png", options);
//        miniaturka.setImageBitmap(bitmap);
                
    }
}