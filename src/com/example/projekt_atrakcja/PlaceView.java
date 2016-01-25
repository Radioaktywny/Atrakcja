package com.example.projekt_atrakcja;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import cache.Miejsca;
import logowanie.Baza;
import logowanie.Logowanie;

public class PlaceView extends Activity {
    private TextView opis_miejsca,nazwa_miejsca;
    private ImageView zdjecie_miejsca;
    private RatingBar ocena;
    private Miejsca m;
    private int licznik=0;
    private ProgressBar proces;
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_view);
        kolo("utworz");
        kolo("wlacz");
        int id = getIntent().getExtras().getInt("keyName");
        m= new Miejsca(getBaseContext());
        try {        	
            init(id);
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        kolo("wylacz");
    }
    private void kolo(String info) {
        if(info.equals("utworz"))
        {
            proces=(ProgressBar)findViewById(R.id.progressBar1);
            proces.setVisibility(View.GONE);
        }
        else if(info.equals("wlacz"))
        {
            proces.setVisibility(View.VISIBLE);
        }
        else if(info.equals("wylacz"))
        {
            proces.setVisibility(View.GONE);
        }
    }

    private void init(int id) throws InterruptedException, ExecutionException {
        // TODO Auto-generated method stub
        opis_miejsca=(TextView) findViewById(R.id.textView2);
        nazwa_miejsca=(TextView) findViewById(R.id.textView1);
        zdjecie_miejsca=(ImageView) findViewById(R.id.zdjecie_miejsca);
        ocena=(RatingBar) findViewById(R.id.ratingBar1);
        opis_miejsca.setText(m.getOpis(id));
        nazwa_miejsca.setText(m.getNazwa(id));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(getFilesDir()+"/miejsca/"+id+".png", options);
        if(bitmap!=null)
            zdjecie_miejsca.setImageBitmap(bitmap);
        else
            zdjecie_miejsca.setImageResource(R.drawable.miejsce_default);        
        getMark(id);
    }
    private void getMark(int id) throws InterruptedException, ExecutionException
    {
        ExecutorService exe = Executors.newFixedThreadPool(2);
        String lokalizacja=m.getLokalizajca(id);
        lokalizacja=lokalizacja.substring(0,lokalizacja.length()-1);
        Future <String> ocena=exe.submit(new Baza("Select avg(ocena) from oceny where lokalizacja = '"+lokalizacja+"'", "zwroc2"));
        Future <String> komentarze=exe.submit(new Baza("SELECT uzytkownik,opis,ocena FROM `oceny` WHERE `lokalizacja` = '"+lokalizacja+"';","komentarze"));
        exe.shutdown(); 
        // tu  mi sie sypalo bo moze nie byc oceny i nie wiedzial jak ma przerobic znak ""
       try
       {
    	   this.ocena.setRating(Float.valueOf(ocena.get()));
    	   
       }catch(Exception e)
       {    	  
           this.ocena.setVisibility(0x00000008);
       }
       ustawkomentarze(komentarze.get());
       Log.d("KOMENTY", komentarze.get());
    }
    private void ustawkomentarze(String komentarze)
    {
        String komenty[]=tinjstringa(komentarze);
        // TODO Auto-generated method stub
        TextView [] opisy= {(TextView) findViewById(R.id.textView21),
                            (TextView) findViewById(R.id.textView4),
                            (TextView) findViewById(R.id.textView6),
                            (TextView) findViewById(R.id.textView8)
                                };
        TextView [] nazwy= {(TextView) findViewById(R.id.textView12),
                            (TextView) findViewById(R.id.textView3),
                            (TextView) findViewById(R.id.textView5),
                            (TextView) findViewById(R.id.textView7)
                    };
        RatingBar [] oceny= {(RatingBar) findViewById(R.id.ratingBar11),
                (RatingBar) findViewById(R.id.ratingBar2),
                (RatingBar) findViewById(R.id.ratingBar3),
                (RatingBar) findViewById(R.id.ratingBar4)
        };
        Button zmien=(Button)findViewById(R.id.buttonkomentarze);
        Button zmien1=(Button)findViewById(R.id.textView9);
        for(int i=0;i<4;++i)
        {            
            opisy[i].setVisibility(0x00000008);
            nazwy[i].setVisibility(0x00000008);
            oceny[i].setVisibility(0x00000008);          
        }
//        TextView opis_miejsca1 = (TextView) findViewById(R.id.textView12);
//        TextView nazwa_miejsca1 = (TextView) findViewById(R.id.textView21);
//        RatingBar ocena1 = (RatingBar) findViewById(R.id.ratingBar11);
//        TextView opis_miejsca2 = (TextView) findViewById(R.id.textView3);
//        TextView nazwa_miejsca2 = (TextView) findViewById(R.id.textView4);
//        RatingBar ocena2 = (RatingBar) findViewById(R.id.ratingBar2);
//        TextView opis_miejsca3 = (TextView) findViewById(R.id.textView5);
//        TextView nazwa_miejsca3 = (TextView) findViewById(R.id.textView6);
//        RatingBar ocena3 = (RatingBar) findViewById(R.id.ratingBar3);
//        TextView opis_miejsca4 = (TextView) findViewById(R.id.textView7);
//        TextView nazwa_miejsca4 = (TextView) findViewById(R.id.textView8);
//        RatingBar ocena4 = (RatingBar) findViewById(R.id.ratingBar4);
        try
        {
            int i=0;
        for(;i<(licznik/3);++i)
        {            
            opisy[i].setText(komenty[3*i+1]);
            nazwy[i].setText(komenty[3*i]);
            oceny[i].setRating(Float.valueOf(komenty[3*i+2])); 
            opisy[i].setVisibility(1);
            nazwy[i].setVisibility(1);
            oceny[i].setVisibility(1); 
        }
        if(i==0)
        {
            zmien.setVisibility(0x00000008);
            zmien1.setText("brak komentarzy ");
        }
        }catch(Exception e)
        {
            
            Log.d("LOLOL", "BLAD");
        }
        //Log.d("KURWA", komenty[0]);
      //  opismiejsca.setT
        
        
    }

    private String[] tinjstringa(String komentarze) 
    {
        String s[]=new String[12];
        
        
        int zapamietaj=0;
        try
        {
            for(int i=0;i<komentarze.length();++i)
            {
            if(komentarze.substring(i,i+2).equals("%+"))
            {                 
                s[licznik]=komentarze.substring(zapamietaj,i);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.place_view, menu);
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
