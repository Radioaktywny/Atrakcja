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
public class Baza implements  Callable<String> {

	private String  sql_dodaj=null;
	private String sql_wyswietl=null;
	private String sql_zwroc=null;
	private String sprawdz_polaczenie=null;	
	private String sql;
	private Statement s ;
		
	public  Baza(String sql,String rodzaj)
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
				 * r.next() - przejœcie do kolejnego rekordu (wiersza) otrzymanych
				 * wyników
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
				System.out.println("Bl¹d odczytu z bazy! " + e.toString());
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
				 * r.next() - przejœcie do kolejnego rekordu (wiersza) otrzymanych
				 * wyników
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
				System.out.println("Bl¹d odczytu z bazy! " + e.toString());
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

