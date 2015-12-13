package logowanie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import android.util.Log;
 
public class Baza {
	/**
	 * Metoda ³aduje sterownik jdbc
	 * 
	 * @return true/false
	 */
	static boolean ladujSterownik() {
		// LADOWANIE STEROWNIKA
		System.out.print("Sprawdzanie sterownika:");
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			return true;
		} catch (Exception e) {
			System.out.println("Blad przy ladowaniu sterownika bazy!");
			return false;
		}
	}
 
	/**
	 * Metoda s³u¿y do nawi¹zania po³¹czenia z baz¹ danych
	 * 
	 * @param adress
	 *            - adres bazy danych
	 * @param dataBaseName
	 *            - nazwa bazy
	 * @param userName
	 *            - login do bazy
	 * @param password
	 *            - has³o do bazy
	 * @return - po³¹czenie z baz¹
	 */
	private static Connection connectToDatabase(String adress,
			String dataBaseName, String userName, String password) {
		System.out.print("\nLaczenie z baza danych:");
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
	 * Wyœwietla dane uzyskane zapytaniem select
	 * 
	 * @param r
	 *            - wynik zapytania
	 */
	private static void printDataFromQuery(ResultSet r) {
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
 
	/**
	 * Wykonanie kwerendy i przes³anie wyników do obiektu ResultSet
	 * 
	 * @param s
	 *            - Statement
	 * @param sql
	 *            - zapytanie
	 * @return wynik
	 */
	private static ResultSet executeQuery(Statement s, String sql) {
		try {
			return s.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
 
	/**
	 * tworzenie obiektu Statement przesy³aj¹cego zapytania do bazy connection
	 * 
	 * @param connection
	 *            - po³¹czenie z baz¹
	 * @return obiekt Statement przesy³aj¹cy zapytania do bazy
	 */
	private static Statement createStatement(Connection connection) {
		try {
			return connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		;
		return null;
	}
 
	/**
	 * Zamykanie po³¹czenia z baz¹ danych
	 * 
	 * @param connection
	 *            - po³¹czenie z baz¹
	 * @param s
	 *            - obiekt przesy³aj¹cy zapytanie do bazy
	 */
	private static void closeConnection(Connection connection, Statement s) {
		System.out.print("\nZamykanie polaczenia z baza¹:");
		try {
			s.close();
			connection.close();
		} catch (SQLException e) {
			System.out
					.println("Bl¹d przy zamykaniu pol¹czenia " + e.toString());
			System.exit(4);
		}
		System.out.print(" zamkniêcie OK");
	}

	
	public static void dodaj_do_bazy(String sql)
	{
	
		try {
			java.sql.Connection connection = connectToDatabase("www.db4free.net:3306",
					"projekt_2015", "maciek2015", "testtest");
			Statement s = createStatement(connection);
			s.executeUpdate(sql);
			s.close();
		} catch (SQLException e) {
			//Log.d("baza",e.printStackTrace());
		}
		
	}
//	public static void main(String[] args) {
// 
//		if (ladujSterownik())
//			System.out.print(" sterownik OK");
//		else
//			System.exit(1);
//		java.sql.Connection connection = connectToDatabase("www.db4free.net:3306",
//				"projekt_2015", "maciek2015", "testtest");
//		if (connection != null)
//			System.out.print(" polaczenie OK\n");
// 
//		// WYKONYWANIE OPERACJI NA BAZIE DANYCH
//		System.out.println("Pobieranie danych z bazy:");
//		String sql = "Select * from rezerwacje";
//		Statement s = createStatement(connection);
//		ResultSet r = executeQuery(s, sql);
//		printDataFromQuery(r);
//		closeConnection(connection, s);
//	}
}