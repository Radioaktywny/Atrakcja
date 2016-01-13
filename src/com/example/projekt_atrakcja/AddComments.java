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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import cache.Miejsca;
import cache.User;
import logowanie.Baza;
import logowanie.Logowanie;

public class AddComments extends Activity {
    Miejsca m;
   
    private static int id;
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
            Toast("Niestety musisz zalogowac siê ponownie");
            startActivity(new Intent(AddComments.this,Logowanie.class));
            finish(); 
        }
        
        id = getIntent().getExtras().getInt("keyName");
        
        Location l = SearchActivity.getLokalizacja();      
        
        try {
            findlocation(l,id);
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void findlocation(Location location, int id) {
              
        
        TextView nazwa_text=(TextView) findViewById(R.id.opismiejsca);
        TextView opis_text=(TextView) findViewById(R.id.textView2);
        ImageView zdjecie=(ImageView) findViewById(R.id.imageView1);
        
        lokalizacja=m.getLokalizajca(id);
        String nazwa=m.getNazwa(id);
        String opis=m.getOpis(id);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(getFilesDir()+"/miejsca/"+id+".png", options);
        if(bitmap!=null)
            zdjecie.setImageBitmap(bitmap);
        else
            zdjecie.setImageResource(R.drawable.miejsce_default);
        
        nazwa_text.setText(nazwa);
        opis_text.setText(opis);
               
        // TODO Auto-generated method stub
        
    }
    public void wyslijkomentarz(View view)
    {
        
        EditText koment=(EditText) findViewById(R.id.editText1) ;  
        RatingBar ocena = (RatingBar) findViewById(R.id.ratingBar2);
        commentToDatabase(lokalizacja,(int)ocena.getRating(),koment.getText().toString());
            
        Toast("Dodano komentarz !");
        
        Intent intent =new Intent(AddComments.this,PlaceView.class);
        intent.putExtra("keyName", id);
        startActivity(intent);
        finish(); 
        
        
    }
    private void commentToDatabase(String lokalizacja,int ocena,String opis)
    {
        ExecutorService exe = Executors.newFixedThreadPool(1);
        exe.submit(new Baza("INSERT INTO `oceny`(`lokalizacja`, `ocena`, `opis`,`uzytkownik`) VALUES (\""+lokalizacja+"\",\""+ocena+"\",\""+opis+"\",\""+user.getLogin()+"\")", "dodaj"));
        exe.shutdown();        
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
    private void Toast(String informacja)
    {
        
        Toast info = Toast.makeText(AddComments.this, informacja, Toast.LENGTH_SHORT);
        info.setGravity(Gravity.CENTER, 0, 0);
        info.show();
        
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
    public static int getId()
    {
        return id;
    }
}
