package com.example.projekt_atrakcja;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cache.Miejsca;
import cache.User;
import logowanie.Baza;
import logowanie.Logowanie;

public class AddComments extends Activity {
    Miejsca m;
   
    private int id;
    private User user;
    private String lokalizacja;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        m= new Miejsca(getBaseContext());
        if(wczytaj_pasy(getBaseContext()))
        {
            setContentView(R.layout.activity_add_comments);            
        }        
        else
        {
            startActivity(new Intent(AddComments.this,Logowanie.class));
            finish(); 
        }
    
    
        Location l = SearchActivity.getLokalizacja();
       
        try {
            findlocation(l,sprawdz_id());
        } catch (NumberFormatException | InterruptedException
                | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void findlocation(Location location, int id) {
        int i=0;
        
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();
        for(;i<id;++i)
        {
            lokalizacja=m.getLokalizajca(i);
        Double d[]=przerob_lokacje(lokalizacja);
        if(d[0]==latitude && d[1]==longitude)
        {
            break;
        }
        }
        TextView nazwa_text=(TextView) findViewById(R.id.textView1);
        TextView opis_text=(TextView) findViewById(R.id.textView2);
        ImageView zdjecie=(ImageView) findViewById(R.id.imageView1);
        
//        String nazwa=m.getNazwa(i);
//        String opis=m.getOpis(i);
          String nazwa="DUPA MACIEK IDZIE DO SEWCIA";
          String opis="DUPA MACIEK IDZIE DO SEWCIA to jest opis";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(getFilesDir()+"/miejsca/"+"18"+".png", options);
        nazwa_text.setText(nazwa);
        opis_text.setText(opis);
        zdjecie.setImageBitmap(bitmap);
        
        // TODO Auto-generated method stub
        
    }
private int sprawdz_id() throws NumberFormatException, InterruptedException, ExecutionException {
        
        try{
            final SQLiteDatabase db= openOrCreateDatabase("miejsca", MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT id from miejsca order by id DESC LIMIT 1;", null);
        cursor.moveToFirst();
        String s = cursor.getString(cursor.getColumnIndex("id"));
        int id=Integer.parseInt(s.replaceAll("[\\D]",""));////dziala dobrze !!!
        Log.d("Miejsce_id_sql_lita", String.valueOf(id));
        cursor.close();
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
    public void wyslijkomentarz(View view)
    {
        
        EditText koment=(EditText) findViewById(R.id.editText1) ;  
        RatingBar ocena = (RatingBar) findViewById(R.id.ratingBar1);
        commentToDatabase(lokalizacja,(int)ocena.getRating(),koment.getText().toString());
    }
    private void commentToDatabase(String lokalizacja,int ocena,String opis)
    {
        ExecutorService exe = Executors.newFixedThreadPool(1);
        exe.submit(new Baza("INSERT INTO `oceny`(`lokalizacja`, `ocena`, `opis`,`uzytkownik`) VALUES (\""+lokalizacja+"\",\""+ocena+"\",\""+opis+"\",\""+user.getLogin()+"\")", "dodaj"));
                
    }
    private boolean wczytaj_pasy(Context context) 
    {       
        try {
            File plik = new File(context.getFilesDir().getAbsolutePath() + "/" + "userpass" +".txt");
            Scanner in = new Scanner(plik);
            this.user= new User(in.nextLine(),in.nextLine(),in.nextLine());  
            in.close();
            return true;
            } catch (IOException e) {   
                e.printStackTrace();
                return false;
            }           
                 
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
