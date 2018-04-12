import java.sql.*;
import java.util.Scanner;
import java.io.Console;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Paavalikko{


	public static boolean kirjauduKayttajana(Scanner lukija, Connection yhteys){


		String username ="";
		Console console = System.console() ;

		System.out.println("Kirjaudutaan sisään käyttäjänä.");
		System.out.print("Syötä käyttäjätunnus:\n> ");

		username = lukija.nextLine();
		char [] salasana;

		
		salasana = console.readPassword("Anna salasana:\n> ");

		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < salasana.length; i++) {
		   	strBuilder.append(salasana[i]);
			}
		String password = strBuilder.toString();
		Arrays.fill(salasana,' ');
		
		//System.out.println(password);
		
		try{
			Statement stmt = yhteys.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT nimi FROM kayttaja WHERE salasana ='"+password+"'");
		
			while(rs.next()){
				if(rs.getString("nimi").equals(username)){
					//System.out.println("täsmää");
					return true;
				}
			}
			System.out.println("Käyttäjätunnus tai salasana väärin.");
			
			
		
			
		}catch (SQLException poikkeus){
			System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());
		}
		return false;
		
	}

	public static boolean kirjauduYllapitajana(Scanner lukija, Connection yhteys){

		int username =0;
		String password = "";
		System.out.println("Kirjaudutaan sisään ylläpitäjänä.");
		System.out.println("Syötä käyttäjätunnus: ");
		username = Integer.parseInt(lukija.nextLine());

		System.out.println("Syötä salasana: ");
		password = lukija.nextLine();

		try{
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

	public static boolean rekisteroidy(Scanner lukija, Connection yhteys){

		Console console = System.console();
		
		boolean tiedotOK = false;

		HashMap<String,String> tiedot = new HashMap<String,String>();

		while(true){

			String rooli ="Asiakas";
			tiedot.put("rooli",rooli);

			System.out.println("Luodaan käyttäjätunnus asiakkaalle.");
			System.out.println("Syötä tiedot, tähdellä merkityt kentät pakollisia:");

			String etunimi = "";
			boolean etunimitarkistus = false;
			while(!etunimitarkistus){
				System.out.print("Etunimi (*):\n> ");
				etunimi = lukija.nextLine();
				etunimitarkistus = tarkastaPituus(etunimi);
			}
			// Lisätään nimi mappiin
			tiedot.put("etunimi",etunimi);
			
			String sukunimi ="";
			boolean sukunimitarkistus = false;
			while(!sukunimitarkistus){
				System.out.print("Sukunimi (*):\n> ");
				sukunimi = lukija.nextLine();
				sukunimitarkistus = tarkastaPituus(sukunimi);
			}
			
			tiedot.put("sukunimi",sukunimi);

			String osoite = "";
			boolean osoitetarkistus = false;
			while(!osoitetarkistus){
				System.out.print("Osoite (*):\n> ");
				osoite = lukija.nextLine();
				osoitetarkistus = tarkastaPituus(osoite);
			}

			tiedot.put("osoite",osoite);
			

			System.out.print("Sähköposti:\n> ");
			String sahkoposti = lukija.nextLine();
			if(sahkoposti.length()>0){
				tiedot.put("sahkoposti",sahkoposti);
			}else{
				tiedot.put("sahkoposti","NULL");
			}

			System.out.print("Puhelin:\n> ");
			String puhelin = lukija.nextLine();
			if(puhelin.length()>0){
				tiedot.put("puhelin",sahkoposti);
			}else{
				tiedot.put("puhelin","NULL");
			}

			System.out.println("Luodaan käyttäjätunnus seuraavin tiedoin.\n");
			System.out.println("Nimi: "+etunimi + " " + sukunimi);
			System.out.println("Osoite: "+osoite);
			if(sahkoposti.length()>0){
				System.out.println("Sähköposti: "+sahkoposti);
			}
			if(puhelin.length()>0){
				System.out.println("Puhelin: "+puhelin+"\n");
			}
			
			
			String varmistus = "";
			while(!(varmistus.equals("E") || varmistus.equals("K"))){
				System.out.print("Tarkista tiedot. Jos tiedot ovat oikein, jatketaan tunnuksen luomiseen. (K/E)\n\n> ");
				varmistus = lukija.nextLine(); 
			}

			if(varmistus.equals("E")){	
				continue;
			}else if(varmistus.equals("K")){
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
				if(tarkastaTunnus(ktunnus, yhteys)){
					tunnustarkistus = true;
					tiedot.put("tunnus",ktunnus);
				}
				if(!tunnustarkistus){
					System.out.print("Käyttäjätunnus on varattu, valitse toinen käyttäjätunnus:\n> ");
				}
				

			}
			
		
			// Tähän salasanat, tarkistetaan että ne mätsää, ja jotain muuta? pituus?
			boolean salasanatarkistus = false;
			char [] password;

			while(!salasanatarkistus){
				password = console.readPassword("Valitse salasana (pituus vähintään viisi merkkiä):\n> ");

				StringBuilder strBuilder = new StringBuilder();
				for (int i = 0; i < password.length; i++) {
		   			strBuilder.append(password[i]);
					}
				String pw1 = strBuilder.toString();
				Arrays.fill(password,' ');
				//System.out.println(pw1);

				password = console.readPassword("Syötä salasana uudelleen: \n> ");
				
				strBuilder = new StringBuilder();
				for (int i = 0; i < password.length; i++) {
		   			strBuilder.append(password[i]);
					}
				String pw2 = strBuilder.toString();
				Arrays.fill(password,' ');
				//System.out.println(pw2);

				if(salasanaTarkistus(pw1,pw2)){
					salasanatarkistus = true;
					tiedot.put("salasana",pw1);
				}
				if(!salasanatarkistus){
					System.out.println("\nSalasanat eivät täsmää tai ovat liian lyhyitä. Anna salasanat uudelleen.");
				}
			}
			// Tähän lisäys kantaan.
			if(lisaaKayttaja(tiedot, yhteys)){
				System.out.println("Tunnus luotu onnistuneesti!");
				System.out.println("Voit nyt kirjautua sisään.");
				return true;
			}
			
			return false;
			
			
		}
		
	}

	public static boolean tarkastaPituus(String tieto){
		if(tieto.length()>0){
			return true;
		}
		System.out.println("Syötit tyhjän arvon!");
		return false;

	}

	public static boolean tarkastaTunnus(String tunnus, Connection yhteys){

		try{
			Statement stmt = yhteys.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT nimi FROM kayttaja");
		
			while(rs.next()){
				//System.out.println(rs.getString("nimi"));
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

	public static boolean lisaaKayttaja(HashMap<String,String> tiedot, Connection yhteys){

		// Luodaan id:
		int id = 0;
		while(id == 0){
			id = luoID(yhteys);
		}
		//System.out.println(id);

		/*
		for (Map.Entry mappi : tiedot.entrySet()) {
          System.out.println("Key: "+mappi.getKey() + " & Value: " + mappi.getValue());
        }
        */

        try{
        	yhteys.setAutoCommit(false);
        	Statement stmt = yhteys.createStatement();
        	//stmt.executeUpdate("INSERT INTO kayttaja VALUES (5,'" + tiedot.get("tunnus") + "', '" + tiedot.get("salasana") + "', '" + tiedot.get("rooli") +"')");
        	//GRANT ALL PRIVILEGES ON table asiakas  TO testuser;
        	//GRANT ALL PRIVILEGES ON table kayttaja  TO testuser;
        	stmt.executeUpdate("INSERT INTO kayttaja VALUES ('" + id + "','" + tiedot.get("tunnus") +"','" + tiedot.get("salasana") +"','"+ tiedot.get("rooli")+"')");
        	stmt.executeUpdate("INSERT INTO asiakas VALUES ('" + id + "','" + tiedot.get("etunimi") +"','" + tiedot.get("sukunimi") +"','"+ tiedot.get("osoite")+"','"+tiedot.get("sahkoposti")+"','"+tiedot.get("puhelin")+"')");

        	yhteys.commit();
         	yhteys.setAutoCommit(true);
         
         	// Suljetaan tapahtumaolio
         	stmt.close();

         	return true;

        }catch (SQLException poikkeus){
			System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());

			try {
            
            // Perutaan tapahtuma
            yhteys.rollback();
            
         } catch (SQLException poikkeus2) {
            System.out.println("Tapahtuman peruutus epäonnistui: " + poikkeus2.getMessage()); 
         }
		}
        

        return false;
		
	}

	// Luodaan rekisteröityvälle käyttäjälle uusi ID, jota ei ole vielä tietokannan rekisterissä
	public static int luoID(Connection yhteys){

		Random rand = new Random();
		int  n = rand.nextInt(1000) + 1;

		try{
			Statement stmt = yhteys.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT kayttajaid FROM kayttaja");
		
			while(rs.next()){
				//System.out.println(rs.getString("nimi"));
				if(rs.getInt("kayttajaid") == n){
					return 0;
				}
			}
			
		}catch (SQLException poikkeus){
			System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());
		}
		return n;

	}

}	