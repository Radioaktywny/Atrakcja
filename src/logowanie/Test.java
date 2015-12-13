package logowanie;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;

import com.example.projekt_atrakcja.MainActivity;
import com.example.projekt_atrakcja.R;
import com.example.projekt_atrakcja.R.id;
import com.example.projekt_atrakcja.R.layout;
import com.example.projekt_atrakcja.R.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


import android.util.Log;
public class Test implements  Callable<String> {

	String  sql_dodaj=null;
	String sql_wyswietl=null;
	String sql_zwroc=null;
	
	java.sql.Connection connection;
	String sql;
	String nazwabazy;
	Statement s ;
	
	public Test(String nazwabazy)
	{
		if (ladujSterownik())
			System.out.print(" sterownik OK");
		else
			System.exit(1);
		java.sql.Connection connection = connectToDatabase("www.db4free.net:3306",
				"", "maciek2015", "testtest");
		if (connection != null)
			System.out.print(" polaczenie OK\n");
		
		Statement s = createStatement(connection);
	
		executeUpdate(s, "Create database if not exists "+nazwabazy+"");
		this.connection = connectToDatabase("www.db4free.net:3306",""+nazwabazy+"", "maciek2015", "testtest");
		this.s = createStatement(connection);
	}
	
	/**
		 * konstruktor
		 * 
		 * @param sql
		 *            - komenda sql
		 * @param rodzaj
		 *            - rodzaj polcenie "dodaj" , "wyswietl"
	 * @return 
		 */
	
		public  Test(String sql,String rodzaj){
		
			
			
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
				
				
				
			
		}
		/**
		 * Metoda �aduje sterownik jdbc
		 * 
		 * @return true/false
		 */
		static boolean ladujSterownik() {
			// LADOWANIE STEROWNIKA
//			System.out.print("Sprawdzanie sterownika:");
			try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				return true;
			} catch (Exception e) {
				System.out.println("Blad przy ladowaniu sterownika bazy!");
				return false;
			}
		}

			/**
			 * Metoda s�u�y do nawi�zania po��czenia z baz� danych
			 * 
			 * @param adress
			 *            - adres bazy danych
			 * @param dataBaseName
			 *            - nazwa bazy
			 * @param userName
			 *            - login do bazy
			 * @param password
			 *            - has�o do bazy
			 * @return - po��czenie z baz�
			 */
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
		/**
		 * Zwrocenie danych uzyskanych z zapytaniem select
		 * 
		 * @param r
		 *            - wynik zapytania
		 * @return zwroc
		 * 			  - zwraca String z danymi z zapytania select
		 */
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
		/**
		 * Wy�wietla dane uzyskane zapytaniem select
		 * 
		 * @param r
		 *            - wynik zapytania
		 * @return 
		 */
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
	 
		/**
		 * Wykonanie kwerendy i przes�anie wynik�w do obiektu ResultSet
		 * 
		 * @param s
		 *            - Statement
		 * @param sql
		 *            - zapytanie
		 * @return wynik
		 */
		public static ResultSet executeQuery(Statement s, String sql) {
			try {
				return s.executeQuery(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
	 
		/**
		 * tworzenie obiektu Statement przesy�aj�cego zapytania do bazy connection
		 * 
		 * @param connection
		 *            - po��czenie z baz�
		 * @return obiekt Statement przesy�aj�cy zapytania do bazy
		 */
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
	 
		/**
		 * Zamykanie po��czenia z baz� danych
		 * 
		 * @param connection
		 *            - po��czenie z baz�
		 * @param s
		 *            - obiekt przesy�aj�cy zapytanie do bazy
		 */
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
			return zwrot;
		
	}
}

