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
        
        m= new Miejsca(getBaseContext());
        try {
            init(AddComments.getId());
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
        ocena=(RatingBar) findViewById(R.id.ratingBar2);
        opis_miejsca.setText(m.getOpis(id));
        nazwa_miejsca.setText(m.getNazwa(id));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(getFilesDir()+"/miejsca/"+id+".png", options);
        zdjecie_miejsca.setImageBitmap(bitmap);
        getMark(id);
    }
    private void getMark(int id) throws InterruptedException, ExecutionException
    {
        ExecutorService exe = Executors.newFixedThreadPool(1);
        Future <String> ocena=exe.submit(new Baza("Select avg(ocena) from oceny where lokalizacja = '"+m.getLokalizajca(id)+"'", "zwroc2"));
        exe.shutdown();        
        this.ocena.setRating(Float.valueOf(ocena.get()));
        
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
