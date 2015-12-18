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

public class Miejsca extends Thread 
{
    private int id_sql_lita=0;
    private int id_globala=0;
    private List<String[]> miejsca = new ArrayList<String[]>();
    SQLiteDatabase db;
    public Miejsca(SQLiteDatabase db)
    {	
    	this.db=db;    
    	db.execSQL("CREATE TABLE if not exists miejsca ( id INT NOT NULL, nazwa TEXT NOT NULL , lokalizacja TEXT NOT NULL , uzytkownik TEXT NOT NULL , opis TEXT NOT NULL );");
    	try {
	             id_sql_lita=sprawdz_id(db);
	             id_globala=sprawdz_idbazy();
	       	} 
    		catch (NumberFormatException | InterruptedException | ExecutionException e) 
    		{
	       		Log.d("Miejsca_id_blad", e.getMessage());
        	}
        	if(id_sql_lita==id_globala)
        	{
        		
        		Log.d("Miejsca_baza_aktualna", "ID SA SOBIE ROWNE WIEC AKTUALNA");//mozna potem dodac return jakis i zmienic na calla xd
        	}
        	else
        	{
        		try {
					dopisz_do_SQLite(db);
				} catch (InterruptedException | ExecutionException e) {
				Log.d("Miejsca_blad przy dopisywaniu bazy", e.getMessage());
				}
        		
        		
        	}
        	pobierz_sqlite(db);//i sa juz w arrajliscie
}    
    public String[] getRekord(int j) {
    	
    	return miejsca.get(j);
	}
    public  String getUrzytkownik(int j) {
    	String[] cos=miejsca.get(j);
    	return cos[3];
	}
    public String getOpis(int j) {
    	String[] cos=miejsca.get(j);
    	    return cos[4];
    }
    public String getLokalizajca(int j) {
    	String[] cos=miejsca.get(j);
	    return cos[2];
    }
    public String getNazwa(int j) {
    	String[] cos=miejsca.get(j);
	    return cos[1];
    }
    public String getId(int j) {
    	String[] cos=miejsca.get(j);
	    return cos[0];
    }
    public int getLastId()
    {
    	return id_sql_lita;
    }
 public void setRekord(String id,String nazwa,String lokacja, String user, String opis) {
	 	db.execSQL("INSERT INTO miejsca  (id, nazwa , lokalizacja , uzytkownik , opis ) VALUES ('"+id_sql_lita+"','"+nazwa+"','"+lokacja+"','"+user+"','"+opis+"');");
	 	String [] cos= new String[5];
	 	cos[0]=id;
	 	cos[1]=nazwa;
	 	cos[2]=lokacja;
	 	cos[3]=user;
	 	cos[4]=opis;		 	
	 	miejsca.add(cos);
 }
 	
	private int sprawdz_id(SQLiteDatabase db) throws NumberFormatException, InterruptedException, ExecutionException {
        
    	try{
    	Cursor cursor = db.rawQuery("SELECT id from miejsca order by id DESC LIMIT 1;", null);
        cursor.moveToFirst();
        String s = cursor.getString(cursor.getColumnIndex("id"));
        int id=Integer.parseInt(s.replaceAll("[\\D]",""));////trza przetescic
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
	
    private void pobierz_sqlite(SQLiteDatabase db)
    {	Cursor cursor = db.rawQuery("SELECT * FROM MIEJSCA",null);
        cursor.moveToFirst();
        String s[]= new String[5];
        while(!cursor.isLast())
            {              Log.d("Miejsce pobierz_sqlita", "wszedl do whila");
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

    public void dopisz_do_SQLite(SQLiteDatabase db) throws InterruptedException, ExecutionException
    {
       
        ExecutorService exe = Executors.newFixedThreadPool(4);
        String lokacja="";
        String nazwa="";
        String opis="";
        String user="";
        for(;id_sql_lita<=id_globala;id_sql_lita++ )
        {
        Future <String> nazwa_f= exe.submit(new Baza("SELECT `nazwa` FROM `miejsca` where `id`=\""+id_sql_lita+"\"", "zwroc2"));
        nazwa=nazwa_f.get();
        Future <String> lokalizacja_f= exe.submit(new Baza("SELECT `lokalizacja` FROM `miejsca` where `id`=\""+id_sql_lita+"\"", "zwroc2"));
        lokacja=lokalizacja_f.get();
        Future <String> user_f= exe.submit(new Baza("SELECT `uzytkownik` FROM `miejsca` where `id`=\""+id_sql_lita+"\"", "zwroc2"));
        user=user_f.get();
        Future <String> opis_f= exe.submit(new Baza("SELECT `Opis` FROM `miejsca` where `id`=\""+id_sql_lita+"\"", "zwroc2"));
        opis=opis_f.get();
        db.execSQL("INSERT INTO miejsca  (id, nazwa , lokalizacja , uzytkownik , opis ) VALUES ('"+id_sql_lita+"','"+nazwa+"','"+lokacja+"','"+user+"','"+opis+"');");
        Log.d("Miejsce_sql_lite", "dopisano: "+nazwa+lokacja+user+opis);
        }
    }
    private int sprawdz_idbazy() throws NumberFormatException, InterruptedException, ExecutionException {
        ExecutorService exe = Executors.newFixedThreadPool(1);
        Future<String> fut=exe.submit(new Baza("SELECT `id` from `miejsca` ORDER BY `id` DESC LIMIT 1", "zwroc2"));
        int i=Integer.parseInt(fut.get().replaceAll("[\\D]",""));
        Log.d("id_bazy_globalnej", fut.get());
        if(fut.get().length()>0)
        return i;
        else
            return 0;
    }
}
