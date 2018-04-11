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
		
		boolean tiedotOK = false;

		while(true){

			String rooli ="Asiakas";
			System.out.println("Luodaan käyttäjätunnus asiakkaalle.");
			System.out.println("Syötä tiedot, tähdellä merkityt kentät pakollisia:");

			System.out.print("Etunimi (*):\n> ");
			String etunimi = lukija.nextLine();

			System.out.print("Sukunimi (*):\n> ");
			String sukunimi = lukija.nextLine();

			System.out.print("Osoite (*):\n> ");
			String osoite = lukija.nextLine();

			System.out.print("Sähköposti:\n> ");
			String sahkoposti = lukija.nextLine();

			System.out.print("Puhelin:\n> ");
			String puhelin = lukija.nextLine();

			System.out.println("Luodaan käyttäjätunnus seuraavin tiedoin.\n");
			System.out.println("Nimi: "+etunimi + " " + sukunimi);
			System.out.println("Osoite: "+osoite);
			if(sahkoposti.length()>0){
				System.out.println("Sähköposti: "+sahkoposti);
			}
			if(puhelin.length()>0){
				System.out.println("Puhelin: "+puhelin+"\n");
			}
			
			System.out.println("Tarkista tiedot. Jos tiedot ovat oikein, jatketaan tunnuksen luomiseen. (K/E)\n\n> ");
			String kyllaei = lukija.nextLine(); 

			if(kyllaei.equals("E")){
				continue;
			}else if(kyllaei.equals("K")){
				System.out.println("Luodaan käyttäjätunnus ja salasana:\n");
			}

			// Tähän käyttäjätunnuksen luonti ja tarkistus, että tunnusta ei löydy kannasta
			boolean tunnustarkistus = false;
			String ktunnus= "";
			while(!tunnustarkistus){
				System.out.print("Käyttäjätunnus:\n> ");
				ktunnus = lukija.nextLine();

				if(ktunnus.length()<5){
					System.out.println("Käyttäjätunnus liian lyhyt. Syötä pidempi käyttäjätunnus.");
					continue;
				}
				if(tarkastaTunnus(ktunnus)){
					tunnustarkistus = true;
				}
				if(!tunnustarkistus){
					System.out.println("Käyttäjätunnus on varattu, valitse toinen käyttäjätunnus:\n> ");
				}
				

			}
			
		
			// Tähän salasanat, tarkistetaan että ne mätsää, ja jotain muuta? pituus?
			boolean salasanatarkistus = false;
			char [] password;

			while(!salasanatarkistus){
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

				if(salasanaTarkistus(pw1,pw2)){
					salasanatarkistus = true;
				}
				if(!salasanatarkistus){
					System.out.println("\nSalasanat eivät täsmää tai eivät ole laillisia. Anna salasanat uudelleen.");
				}
			}
			// Tähän lisäys kantaan.
			System.out.println("Tunnus luotu onnistuneesti!");
			return true;
			
			
		}
		
	}

	public static boolean tarkastaTunnus(String tunnus){

		Yhteys yht = new Yhteys("localhost", 5432, "bookstore", "testuser", "12345");
		try{
			Connection yhteys = yht.uusiYhteys();
			Statement stmt = yhteys.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT nimi FROM kayttaja");
		
			while(rs.next()){
				System.out.println(rs.getString("nimi"));
				if(rs.getString("nimi").equals(tunnus)){
					return false;
				}
			}
		}catch (SQLException poikkeus){
			System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());
		}
		return true;

	}

	public static boolean salasanaTarkistus(String pw1, String pw2){
		
		if(pw1.length()>6 && pw2.length()>6 && pw1.equals(pw2)){
			return true;
		}
		return false;
	}

}	