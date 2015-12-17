package cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.util.Log;
import logowanie.Baza;

public class Miejsca extends Activity
{
    private int id=0;
    private List<String[]> miejsca = new ArrayList<String[]>();
    public Miejsca()
    {
        SQLiteDatabase db = openOrCreateDatabase("cache",Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE `egzamin`.`miejsca` ( `id` VARCHAR(4) NOT NULL , `nazwa` TEXT NOT NULL , `lokalizacja` TEXT NOT NULL , `uzytkownik` TEXT NOT NULL , `opis` TEXT NOT NULL , PRIMARY KEY (`id`));");
        try {
            id=sprawdz_id(db);
        } catch (NumberFormatException | InterruptedException
                | ExecutionException e) {
            // TODO Auto-generated catch block
            Log.d("id_blad", e.getMessage());
            e.printStackTrace();
        }        
        pobierz_sqlite(db);
        
        db.close();
}    
    private int sprawdz_id(SQLiteDatabase db) throws NumberFormatException, InterruptedException, ExecutionException {
        Cursor cursor = db.rawQuery("SELECT id from miejsca order by id DESC LIMIT 1;", null);
        cursor.moveToFirst();
        String s = cursor.getString(cursor.getColumnIndex("id"));
        int id= Integer.valueOf(s.toString());
        Log.d("id_zwrot_fut", s);
       //int i=fut.get().charAt(0)-47;       
        db.close();
        if(id>0)
            return id;
        else
            return 0;        
    }
    private void pobierz_sqlite(SQLiteDatabase db)
    {
        Cursor cursor = db.rawQuery("SELECT * from miejsca order by id ASC LIMIT 1;", null);
        cursor.moveToFirst();
        String s[]= new String[5];
        while(!cursor.isAfterLast())
            {            
            s[0]=cursor.getString(cursor.getColumnIndex("id"));
            s[1]=cursor.getString(cursor.getColumnIndex("nazwa"));
            s[2]=cursor.getString(cursor.getColumnIndex("lokalizacja"));
            s[3]=cursor.getString(cursor.getColumnIndex("uzytkownik"));
            s[4]=cursor.getString(cursor.getColumnIndex("opis"));
            miejsca.add(s); 
            cursor.moveToNext();               
            }        
        db.close();
     }
}
