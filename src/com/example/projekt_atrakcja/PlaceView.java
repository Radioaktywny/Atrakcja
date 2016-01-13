package com.example.projekt_atrakcja;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cache.Miejsca;
import logowanie.Baza;
import logowanie.Logowanie;

public class PlaceView extends Activity {
    private TextView opis_miejsca,nazwa_miejsca;
    private ImageView zdjecie_miejsca;
    private RatingBar ocena;
    private Miejsca m;
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_view);
        int id = getIntent().getExtras().getInt("keyName");
        m= new Miejsca(getBaseContext());
        try {
        	
            init(id);
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        Future <String> komentarze=exe.submit(new Baza("SELECT ocena,opis,uzytkownik FROM `projekt_2015`.`oceny` WHERE `lokalizacja` = '"+lokalizacja+"' ORDER BY ocena ASC","zwroc"));
        exe.shutdown(); 
        // tu  mi sie sypalo bo moze nie byc oceny i nie wiedzial jak ma przerobic znak ""
       try
       {
    	   this.ocena.setRating(Float.valueOf(ocena.get()));
    	   
       }catch(Exception e)
       {
    	   this.ocena.setVisibility(0x00000008);
       }
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
