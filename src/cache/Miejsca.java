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

public class Miejsca 
{
    private int id=0;
    private List<String[]> miejsca = new ArrayList<String[]>();
    SQLiteDatabase db;
    public Miejsca(SQLiteDatabase db)
    
    {	
    	Log.d("wysypal sie", "przed konstrukorze");
    this.db=db;    
    Log.d("wysypal sie", "po konstrukorze");
    	
    	
    
        db.execSQL("CREATE TABLE if not exists `miejsca` ( `id` VARCHAR(4) NOT NULL , `nazwa` TEXT NOT NULL , `lokalizacja` TEXT NOT NULL , `uzytkownik` TEXT NOT NULL , `opis` TEXT NOT NULL , PRIMARY KEY (`id`));");
      
        try {
        	 Log.d("Miejsca id_przed", String.valueOf(id));
            id=sprawdz_id(db);
            Log.d("Miejsca id_po", String.valueOf(id));
        } catch (NumberFormatException | InterruptedException
                | ExecutionException e) {
            Log.d("Miejsca_id_blad", e.getMessage());
        }
        Log.d("Miejsca_jestem", "TU jestem1");
        if(sprawdz_czy_aktualna())
        {
        	 Log.d("Miejsca_id_blad", "jest aktualna");
        }
        else
        {
        	//dodaj_nie_aktualne();
        	Log.d("Miejsca_jestem", "TU jestem2");
        	try {
				dopisz_do_SQLite(db);
				Log.d("Miejsca_jestem", "TU jestem3 i mam dopiszwanie");
			} catch (InterruptedException | ExecutionException e) {
				Log.d("Miejsca dopisz_wysypal:", e.getMessage());
				
			}
        	pobierz_sqlite(db);
        	
        }
        test();
        db.close();
}    
    private void test() {
    	
		Log.d("takie lokacje wczytalo :", miejsca.get(0).toString());
		Log.d("takie lokacje wczytalo :", miejsca.get(1).toString());
		Log.d("takie lokacje wczytalo :", miejsca.get(2).toString());
		
	}
	private void dodaj_nie_aktualne() {
		// TODO Auto-generated method stub
		
	}
	private boolean sprawdz_czy_aktualna()  {
    	try {
			if(id==sprawdz_idbazy())
			{
				return true;
			}else
				return false;
			} catch (NumberFormatException | InterruptedException | ExecutionException e){
			Log.d("Miejsca sprawdz_cz_blad", e.getMessage());
			return false;
		}
	
	}
	private int sprawdz_id(SQLiteDatabase db) throws NumberFormatException, InterruptedException, ExecutionException {
        
    	try{
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
    	catch(Exception e)
    	{
    		Log.d("id_curso_sie_wysypal", e.getMessage());
    		return 0;
    	}
    	}
    private void pobierz_sqlite(SQLiteDatabase db)
    {
        Cursor cursor = db.rawQuery("SELECT * from miejsca order by id ASC LIMIT 1;", null);
        cursor.moveToFirst();
        String s[]= new String[5];
        while(!cursor.isLast())
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
    public int get_id()
    {
        return id;
    }
    public void dopisz_do_SQLite(SQLiteDatabase db) throws InterruptedException, ExecutionException
    {
       
        ExecutorService exe = Executors.newFixedThreadPool(4);
        String lokacja="";
        String nazwa="";
        String opis="";
        String user="";
        int id_bazy=sprawdz_idbazy();
        for(;id<id_bazy;id++ )
        {
        Future <String> nazwa_f= exe.submit(new Baza("SELECT `nazwa` FROM `miejsca` where `id`=\""+id+"\"", "zwroc2"));
        Future <String> lokalizacja_f= exe.submit(new Baza("SELECT `lokalizacja` FROM `miejsca` where `id`=\""+id+"\"", "zwroc2"));
        Future <String> user_f= exe.submit(new Baza("SELECT `uzytkownik` FROM `miejsca` where `id`=\""+id+"\"", "zwroc2"));
        Future <String> opis_f= exe.submit(new Baza("SELECT `Opis` FROM `miejsca` where `id`=\""+id+"\"", "zwroc2"));
        nazwa=nazwa_f.get();
        lokacja=lokalizacja_f.get();
        user=user_f.get();
        opis=opis_f.get();
        Log.d("miejsca z bazysgllite", nazwa+lokacja+user+opis);
        db.execSQL("INSERT INTO `miejsca`(`id` ,`nazwa`, `lokalizacja`, `uzytkownik`, `opis`) VALUES ("+id+",\""+nazwa+"\",\""+lokacja+"\",\""+user+"\",\""+opis+"\");");
        Log.d("miejsca z bazysgllite", "po insercie" );
        }
        
        
       
    db.close();
        
    }
    private int sprawdz_idbazy() throws NumberFormatException, InterruptedException, ExecutionException {
        ExecutorService exe = Executors.newFixedThreadPool(1);
        Future<String> fut=exe.submit(new Baza("SELECT `id` from `miejsca` ORDER BY `id` DESC LIMIT 1", "zwroc2"));
        Log.d("id_zwrot_fut", fut.get());
        int i=fut.get().charAt(0)-47;
        Log.d("id_zwrot_i", fut.get());
        if(fut.get().length()>0)
        return i;
        else
            return 0;
    }
}
