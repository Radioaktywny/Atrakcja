package com.example.projekt_atrakcja;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class AddComments extends Activity {
    final SQLiteDatabase db = openOrCreateDatabase("miejsca", MODE_PRIVATE, null);
    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comments);
        Location l = SearchActivity.getLokalizacja();
       
        try {
            findlocation(l,sprawdz_id(db));
        } catch (NumberFormatException | InterruptedException
                | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void findlocation(Location location, int id) {
        int i=0;
        TextView nazwa_text=(TextView) findViewById(R.id.textView1);
        TextView opis_text=(TextView) findViewById(R.id.textView2);
        ImageView zdjecie=(ImageView) findViewById(R.id.imageView1);
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();
        Cursor cursor=db.rawQuery("SELECT lokalizacja FROM miejsca ", null);
        for(;i<id;++i)
        {
        Double d[]=przerob_lokacje(cursor.getString(i));
        if(d[0]==latitude && d[1]==longitude)
        {
            break;
        }
        }
        
        cursor=db.rawQuery("SELECT nazwa FROM miejsca ", null);
        String nazwa=cursor.getString(i);
        cursor=db.rawQuery("SELECT opis FROM miejsca ", null);
        String opis=cursor.getString(i);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(getFilesDir()+"/miejsca/"+String.valueOf(i)+".png", options);
        nazwa_text.setText(nazwa);
        opis_text.setText(opis);
        zdjecie.setImageBitmap(bitmap);
        
        // TODO Auto-generated method stub
        
    }
private int sprawdz_id(SQLiteDatabase db) throws NumberFormatException, InterruptedException, ExecutionException {
        
        try{
        Cursor cursor = db.rawQuery("SELECT id from miejsca order by id DESC LIMIT 1;", null);
        cursor.moveToFirst();
        String s = cursor.getString(cursor.getColumnIndex("id"));
        int id=Integer.parseInt(s.replaceAll("[\\D]",""));////dziala dobrze !!!
        Log.d("Miejsce_id_sql_lita", String.valueOf(id));
        if(id>0)
            return id;
        else
            return 0;        
        }
        catch(Exception e)
        {
            Log.d("id_curso_sie_wysypal", e.getMessage());
            return 0;
        }
    }
    private Double[] przerob_lokacje(String string) {
        int koniec=0;
        Double[] d =new Double[2];
        try{
        for(int i=0; i <string.length()-3; i++)
        {
            if(string.startsWith("x", i))
            {   
                while(!string.startsWith("y", koniec))//dopoki nie napotkam kolejnej liczby
                {
                 koniec++;
                }
                d[0]= Double.valueOf(string.substring(i+1, koniec));
                Log.d("zparsowalo_x", string.substring(i+1, koniec));
                break;
            }
        }
        Log.d("zparsowalo_y", string.substring(koniec+1, string.length()));
        d[1] = Double.valueOf(string.substring(koniec+1, string.length()-1));
        
        }
        catch(NumberFormatException e)
        {
            Log.d("nie zparsowalo", e.getMessage());
        }
        return d;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_comments, menu);
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
