import java.sql.*;
import java.util.Scanner;
import java.io.Console;
import java.util.Arrays;

public class Paavalikko{


	public static boolean kirjauduKayttajana(Scanner lukija){

		int username =0;
		String password = "";

		System.out.println("Kirjaudutaan sisään käyttäjänä.");
		System.out.println("Syötä käyttäjätunnus: ");

		username = Integer.parseInt(lukija.nextLine());

		System.out.println("Syötä salasana: ");
		password = lukija.nextLine();

		Yhteys yht = new Yhteys("localhost", 5432, "bookstore", "testuser", "12345");
		try{
			Connection yhteys = yht.uusiYhteys();
			Statement stmt = yhteys.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT kayttajaid FROM kayttaja WHERE salasana ='"+password+"'");
		
			while(rs.next()){
				if(rs.getInt("kayttajaid") == username){
					System.out.println("täsmää");
					return true;
				}
			}
			System.out.println("ei täsmää");
			
			
		
			
		}catch (SQLException poikkeus){
			System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());
		}
		return false;
		
	}

	public static boolean kirjauduYllapitajana(Scanner lukija){

		int username =0;
		String password = "";
		System.out.println("Kirjaudutaan sisään ylläpitäjänä.");
		System.out.println("Syötä käyttäjätunnus: ");
		username = Integer.parseInt(lukija.nextLine());

		System.out.println("Syötä salasana: ");
		password = lukija.nextLine();

		Yhteys yht = new Yhteys("localhost", 5432, "bookstore", "testuser", "12345");
		try{
			Connection yhteys = yht.uusiYhteys();
			Statement stmt = yhteys.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT kayttajaid FROM kayttaja WHERE salasana ='"+password+"'");
		
			while(rs.next()){
				if(rs.getInt("kayttajaid") == username){
					System.out.println("täsmää");
					return true;
				}
			}
			System.out.println("ei täsmää");
			
			
		
			
		}catch (SQLException poikkeus){
			System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());
		}
		

		return false;
	}

	public static boolean rekisteroidy(Scanner lukija){

		Console console = System.console() ;
		char [] password;

		String rooli ="Asiakas";
		System.out.println("Luodaan käyttäjätunnus asiakkaalle.");
		System.out.println("Syötä tiedot:");
		System.out.print("Nimi:\n> ");
		String nimi = lukija.nextLine();
		System.out.print("Käyttäjätunnus:\n> ");
		String ktunnus = lukija.nextLine();
		password = console.readPassword("Salasana:\n> ");

		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < password.length; i++) {
   			strBuilder.append(password[i]);
			}
		String pw1 = strBuilder.toString();
		Arrays.fill(password,' ');
		System.out.println(pw1);

		password = console.readPassword("Anna salasana uudelleen: \n> ");
		
		strBuilder = new StringBuilder();
		for (int i = 0; i < password.length; i++) {
   			strBuilder.append(password[i]);
			}
		String pw2 = strBuilder.toString();
		Arrays.fill(password,' ');
		System.out.println(pw2);
		
		


		return true;
	}

}