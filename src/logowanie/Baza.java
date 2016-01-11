package logowanie;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;

import android.util.Log;
public class Baza implements  Callable<String> {

	private String  sql_dodaj=null;
	private String sql_wyswietl=null;
	private String sql_zwroc=null;
	private String sql_zwroc2=null;
	private String sprawdz_polaczenie=null;	
	private String sql;
	private String sql_zwroc_wersja_beta=null;
	private Statement s ;
		
	public Baza(String sql,String rodzaj)
	{			
			if(rodzaj.equals("dodaj"))
				{				
					sql_dodaj=rodzaj;
					this.sql=sql;
				}
				else if (rodzaj.equals("wyswietl"))
				{	sql_wyswietl=rodzaj;
					this.sql=sql;
				}
				else if (rodzaj.equals("zwroc"))
				{	
					this.sql=sql;
					sql_zwroc=rodzaj;	
				}
				else if (rodzaj.equals("sprawdz_polaczenie"))
				{	
					sprawdz_polaczenie="sprawdzam";
				}
				else if (rodzaj.equals("zwroc2"))
				{	
					this.sql=sql;
					sql_zwroc2=rodzaj;	
				}
				else if (rodzaj.equals("zwroc_beta"))
				{
					this.sql=sql;
					sql_zwroc_wersja_beta=rodzaj;
				}
	}
	static boolean ladujSterownik() 
	{
			try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				return true;
			} catch (Exception e) {
				System.out.println("Blad przy ladowaniu sterownika bazy!");
				return false;
			}
	}
		public static Connection connectToDatabase(String adress,
				String dataBaseName, String userName, String password) {
		//	System.out.print("\nLaczenie z baza danych:");
			String baza = "jdbc:mysql://" + adress + "/" + dataBaseName;
			// objasnienie opisu bazy:
			// jdbc: - mechanizm laczenia z baza (moze byc inny, np. odbc)
			// mysql: - rodzaj bazy
			// adress - adres serwera z baza (moze byc tez w nazwy)
			// dataBaseName - nazwa bazy (poniewaz na serwerze moze byc kilka roznych
			// baz...)
			java.sql.Connection connection = null;
			try {
				connection = DriverManager.getConnection(baza, userName, password);
			} catch (SQLException e) {
				System.out.println("Blad przy ladowaniu sterownika bazy!");
				System.exit(1);
			}
			return connection;
		}
		public static String returnDataFromQuery(ResultSet r) {
			ResultSetMetaData rsmd;
			String zwroc="";
			try {
				rsmd = r.getMetaData();
				int numcols = rsmd.getColumnCount(); // pobieranie liczby kolum
				/**
				 * r.next() - przej�cie do kolejnego rekordu (wiersza) otrzymanych
				 * wynik�w
				 */
				// wyswietlanie kolejnych rekordow:
				while (r.next()) {
					for (int i = 1; i <= numcols; i++) {
						Object obj = r.getObject(i);
						if (obj != null){
							if(i!=1 && i !=5)
							zwroc=zwroc+obj.toString()+"\n";//nie dodaje 1 i ostatniej lini
							if(i==5)
							zwroc=zwroc+obj.toString();//ostatnia linie dopisz bez znaku \n
						}
					}			
				}
			} catch (SQLException e) {
				System.out.println("Bl�d odczytu z bazy! " + e.toString());
				System.exit(3);
			}
			return zwroc;
			
		}
		//maciek psuje stary wiem ze trzeba to poprawic ale nic lepszego na mysl mi nie przychodzi
		public static String returnDataFromQuery_wersja_beta(ResultSet r) {
			ResultSetMetaData rsmd;
			String zwroc="";
			
			
			try {
				rsmd = r.getMetaData();
				int numcols = rsmd.getColumnCount(); // pobieranie liczby kolum
				/**
				 * r.next() - przej�cie do kolejnego rekordu (wiersza) otrzymanych
				 * wynik�w
				 */
				// wyswietlanie kolejnych rekordow:
				while (r.next()) {
					for (int i = 1; i <= numcols; i++) {
						if(i==1)
						{zwroc+="/1/";//id
						}else
						if(i==2)
						{zwroc+="/2/";//nazwa
						}else
						if(i==3)
						{
							zwroc+="/3/";	//kordynaty sa dla i = 1 i i=2
						}else
						if(i==4)
						{
							zwroc+="/4/";	//uzytkownik
						}else
						if(i==5)
						{
							zwroc+="/5/";	//opis
						}
						Object obj = r.getObject(i);
						if (obj != null){
							if( i !=5)
							zwroc=zwroc+obj.toString()+"\n";//nie dodaje 1 i ostatniej lini
							if(i==5)
							zwroc=zwroc+obj.toString()+"@$";//ostatnia linie dopisz bez znaku \n
						}
					}			
				}
			} catch (SQLException e) {
				System.out.println("Bl�d odczytu z bazy! " + e.toString());
				System.exit(3);
			}
			return zwroc;
			
		}
		
		public static String returnDataFromQuery_wersja_gamma(ResultSet r) {
			ResultSetMetaData rsmd;
			String zwroc="";
			
			try {
				rsmd = r.getMetaData();
				int numcols = rsmd.getColumnCount(); // pobieranie liczby kolum
				/**
				 * r.next() - przej�cie do kolejnego rekordu (wiersza) otrzymanych
				 * wynik�w
				 */
				// wyswietlanie kolejnych rekordow:
				while (r.next()) {
					for (int i = 1; i <= numcols; i++) {
						Object obj = r.getObject(i);
						if (obj != null){
							if(i !=5)
							zwroc=zwroc+obj.toString()+"\n";//nie dodaje 1 i ostatniej lini
							if(i==5)
							zwroc=zwroc+obj.toString();//ostatnia linie dopisz bez znaku \n
						}
					}			
				}
			} catch (SQLException e) {
				System.out.println("Bl�d odczytu z bazy! " + e.toString());
				System.exit(3);
			}
			return zwroc;
			
		}
		
		
		public static void printDataFromQuery(ResultSet r) {
			ResultSetMetaData rsmd;
		
			try {
				rsmd = r.getMetaData();
				int numcols = rsmd.getColumnCount(); // pobieranie liczby kolumn
				// wyswietlanie nazw kolumn:
				for (int i = 1; i <= numcols; i++) {
					System.out.print("\t" + rsmd.getColumnLabel(i) + "\t|");
				}
				System.out.print("\n____________________________________________________________________________\n");
				/**
				 * r.next() - przej�cie do kolejnego rekordu (wiersza) otrzymanych
				 * wynik�w
				 */
				// wyswietlanie kolejnych rekordow:
				while (r.next()) {
					for (int i = 1; i <= numcols; i++) {
						Object obj = r.getObject(i);
						if (obj != null)
							System.out.print("\t" + obj.toString() + "\t|");
						
							
						else
							System.out.print("\t");
					}
					System.out.println();
					
				}
			} catch (SQLException e) {
				System.out.println("Bl�d odczytu z bazy! " + e.toString());
				System.exit(3);
			}
			
		}
		public static ResultSet executeQuery(Statement s, String sql) {
			try {
				return s.executeQuery(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
		public static Statement createStatement(Connection connection) {
			try {
				return connection.createStatement();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			;
			return null;
		}
		private static int executeUpdate(Statement s, String sql) {
			try {
				return s.executeUpdate(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return -1;
		}
		public static void closeConnection(Connection connection, Statement s) {
			try {
				s.close();
				connection.close();
			} catch (SQLException e) {
				System.exit(4);
			}
		}		
		public void dodaj_do_bazy(String sql)
		{	
			executeUpdate(s, sql);
		}
		public ResultSet wyswietl_z_bazy(String sql)
		{	
			ResultSet r = executeQuery(s, sql);
			return r;
		}		
		@Override
		public String call() throws Exception {
			
			String zwrot="dziala";
			
			if(sql_dodaj != null)
			{
			Log.d("baza", "weszlo do dodaj");
			
				try {
					Class.forName("com.mysql.jdbc.Driver");
					Log.d("sterownik", "sterownik zaladowany");
				
				String baza = "jdbc:mysql://www.db4free.net:3306/projekt_2015";
				
				java.sql.Connection c = DriverManager.getConnection(baza, "maciek2015", "testtest");
				Statement s = c.createStatement();
				s.executeUpdate(sql);
				s.close();
				Log.d("baza", "wyslano sqlka");
			} catch (SQLException | ClassNotFoundException e) {
				Log.d("baza", e.getMessage());
			}
				
			}
			
			if(sql_wyswietl != null)
			{
			Log.d("baza", "weszlo do dodaj");
			
				try {
					Class.forName("com.mysql.jdbc.Driver");
					Log.d("sterownik", "sterownik zaladowany");
				String baza = "jdbc:mysql://www.db4free.net:3306/projekt_2015";
				java.sql.Connection c = DriverManager.getConnection(baza, "maciek2015", "testtest");
				Statement s = c.createStatement();
				s.executeUpdate(sql);
				Log.d("baza", "wyslano sqlka");
			} catch (SQLException | ClassNotFoundException e) {
				Log.d("baza", e.getMessage());
			}
			}
			if(sql_zwroc != null)
			{	
				Class.forName("com.mysql.jdbc.Driver");
				Log.d("sterownik", "sterownik zaladowany");
			String baza = "jdbc:mysql://www.db4free.net:3306/projekt_2015";
			java.sql.Connection c = DriverManager.getConnection(baza, "maciek2015", "testtest");
			Statement s = c.createStatement();
				ResultSet r = executeQuery(s, sql);
				zwrot = returnDataFromQuery(r);
				try {
					s.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Log.d("baza blad", e.getMessage());
				}				
			}
			if(sql_zwroc2 != null)
			{	
				Class.forName("com.mysql.jdbc.Driver");
				Log.d("sterownik", "sterownik zaladowany");
			String baza = "jdbc:mysql://www.db4free.net:3306/projekt_2015";
			java.sql.Connection c = DriverManager.getConnection(baza, "maciek2015", "testtest");
			Statement s = c.createStatement();
				ResultSet r = executeQuery(s, sql);
				zwrot = returnDataFromQuery_wersja_gamma(r); //--- powinna byc gamma xxd
				try {
					s.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Log.d("baza blad", e.getMessage());
				}				
			}
			if(sql_zwroc_wersja_beta != null)
			{	
				Class.forName("com.mysql.jdbc.Driver");
				Log.d("sterownik", "sterownik zaladowany");
			String baza = "jdbc:mysql://www.db4free.net:3306/projekt_2015";
			java.sql.Connection c = DriverManager.getConnection(baza, "maciek2015", "testtest");
			Statement s = c.createStatement();
				ResultSet r = executeQuery(s, sql);
				zwrot = returnDataFromQuery_wersja_beta(r); //--- powinna byc beta xxd
				try {
					s.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Log.d("baza blad", e.getMessage());
				}				
			}
			if(sprawdz_polaczenie != null)
			{
				try {
					Class.forName("com.mysql.jdbc.Driver");
					Log.d("sterownik", "sterownik zaladowany");
				String baza = "jdbc:mysql://www.db4free.net:3306/projekt_2015";
				java.sql.Connection c = DriverManager.getConnection(baza, "maciek2015", "testtest");
				zwrot="polaczono";
				} catch (SQLException | ClassNotFoundException e) {
					return "nie polaczono";
				}
			}
			
			return zwrot;		
	}
}

