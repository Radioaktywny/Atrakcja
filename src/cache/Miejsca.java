package cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import logowanie.Baza;

public class Miejsca extends SQLiteOpenHelper implements Callable<String>
{
    private int id_sql_lita=0;
    private int id_globala=0;
    private boolean Czy_zakonczona=false;
    private String komenda;
    public List<String[]> miejsca = new ArrayList<String[]>();
    SQLiteDatabase db;
    
    public Miejsca(Context context,String komenda, SQLiteDatabase db)// do callable
    {
    	super(context,"miejsca",null,1);
    	this.komenda=komenda;
    	this.db=db;
    }
    public Miejsca(Context context)
    {
    	super(context,"miejsca",null,1);
    }
    public void stworz(SQLiteDatabase db)
    {	
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
        	db.close();
        	Czy_zakonczona=true;
       // 	pobierz_sqlite(db);//i sa juz w arrajliscie
}    
    public String[] getRekord2(int j) {
    	Log.d("Miejsce getRekord","poczatke");
    	SQLiteDatabase db= this.getReadableDatabase();
    	Cursor cursor = db.rawQuery("SELECT * FROM MIEJSCA",null);
        cursor.moveToFirst();
        String s[]= new String[5];
        
                Log.d("Zarzadca_bazy pobierz_sqlita", "wszedl do whila");
	            s[0]=cursor.getString(cursor.getColumnIndex("id"));
	            s[1]=cursor.getString(cursor.getColumnIndex("nazwa"));
	            s[2]=cursor.getString(cursor.getColumnIndex("lokalizacja"));
	            s[3]=cursor.getString(cursor.getColumnIndex("uzytkownik"));
	            s[4]=cursor.getString(cursor.getColumnIndex("opis"));
	            
	            cursor.moveToNext();
        return s;
	}
    
    public String[] getRekord(int j) {
    	Log.d("Miejsce getRekord","poczatke");
   try{
    SQLiteDatabase db= getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM miejsca where id = "+j+"",null);
    cursor.moveToFirst();
    String s[]= new String[5];
            s[0]=cursor.getString(cursor.getColumnIndex("id"));
            s[1]=cursor.getString(cursor.getColumnIndex("nazwa"));
            s[2]=cursor.getString(cursor.getColumnIndex("lokalizacja"));
            s[3]=cursor.getString(cursor.getColumnIndex("uzytkownik"));
            s[4]=cursor.getString(cursor.getColumnIndex("opis"));
            
            cursor.moveToNext();
            return s;
   }catch(Exception e)
   {
	   Log.d("Miejsca getRekord tworznie sql", e.getMessage());
	   String [] s=test();
	   return s;
   }
   
}
    private String [] test() {
		String [] s= new String[5];
		s[0]="dupa";
		s[1]="dupa";
		s[2]="dupa";
		s[3]="dupa";
		s[4]="dupa";
		return s;
	}
	public  String getUzytkownik(int j) {
		String cos=pobierz_(j,"uzytkownik");
    	return cos;
	}
    public String getOpis(int j) {
    	String cos=pobierz_(j,"opis");
    	return cos;
    }
    public String getLokalizajca(int j) {
    	String cos=pobierz_(j,"lokalizacja");
    	return cos;
    }
	public String getNazwa(int j) {
		String cos=pobierz_(j,"nazwa");
    	return cos;
    }
    public int getId(String nazwa) {
    	String cos=pobierz_Id(nazwa,"id");
    	return Integer.parseInt(cos.replaceAll("[\\D]",""));
    }
   
    
    public int getLastId() throws NumberFormatException, InterruptedException, ExecutionException{
    	SQLiteDatabase db= getReadableDatabase();
    	return sprawdz_id(db);
    }
    private String pobierz_(int j, String string) {
    	SQLiteDatabase db= getReadableDatabase();
	    Cursor cursor = db.rawQuery("SELECT * FROM miejsca where id = "+j+"",null);
	    cursor.moveToFirst();
	    String zwrot=cursor.getString(cursor.getColumnIndex(string));
	    cursor.close();
	    db.close();
		return zwrot;
		
	}
    private String pobierz_Id(String j, String string) {
    	SQLiteDatabase db= getReadableDatabase();
	    Cursor cursor = db.rawQuery("SELECT * FROM miejsca where nazwa = \""+j+"\"",null);
	    cursor.moveToFirst();
	    String cos=cursor.getString(cursor.getColumnIndex(string));
	    cursor.close();
	    db.close();
		return cos;
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
	 	db.close();
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
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.d("Miejsce onCreate", "wszedl do metody onCreate");
		
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.d("Miejsce onUpgrade", "wszedl do metody onUpgrade");
		
	}
	@Override
	public String call() throws Exception {
		if (komenda.equals("aktualizuj"))
		{
			stworz(db);
		}
		Log.d("Miejsca_callable", "LOL zakonczylo");
		return "zakonczono";
	}
}
