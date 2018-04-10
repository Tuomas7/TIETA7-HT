import java.sql.*;
import java.util.Scanner;

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
		return true;
	}

	public static boolean rekisteroidy(Scanner lukija){
		return true;
	}

}