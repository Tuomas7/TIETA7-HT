// Kirjautumisavustajaluokka. Käytetään käyttäjän sisäänkirjautumisessa.

import java.util.Scanner;
import java.util.HashMap;
import java.io.Console;
import java.util.Arrays;
import java.util.Random;

public class Kirjautumisavustaja{

	private Scanner lukija;
	private Asiakaskyselyt kysely;
	private String rooli; 

	public Kirjautumisavustaja(String rooli){
		this.lukija = new Scanner(System.in);
		this.kysely = new Asiakaskyselyt();
		this.rooli = rooli;
	}

	// Funktio kirjautumiseen
	// @param kyselyt // Asiakaskyselyt-olio
	// @param rooli // merkkijono kayttajan roolista ("Asiakas" tai "Ylläpitäjä")
	// @return 
	public Asiakasistunto kirjaudu(){

		String tunnus ="";
		Asiakasistunto istunto = null;
		Console console = System.console() ;

		System.out.println("Kirjaudutaan sisään:");
		System.out.print("Syötä käyttäjätunnus:\n> ");
		tunnus = this.lukija.nextLine();

		char [] salasana;
		salasana = console.readPassword("Anna salasana:\n> ");

		StringBuilder strBuilder = new StringBuilder();

		for (int i = 0; i < salasana.length; i++) {
		   strBuilder.append(salasana[i]);
		}

		String password = strBuilder.toString();
		Arrays.fill(salasana,' ');

		int id = this.kysely.tarkastaKirjautuminen(tunnus, password, this.rooli);

		if(id == 0){
			System.out.println("Käyttäjätunnus tai salasana väärin.");
		}else{
			istunto = new Asiakasistunto(id);
		}

		return istunto;
	}

	public Yllapitajaistunto kirjauduyp(){

		String tunnus ="";
		Yllapitajaistunto istunto = null;
		Console console = System.console() ;

		System.out.println("Kirjaudutaan sisään ylläpitäjänä:");
		System.out.print("Syötä käyttäjätunnus:\n> ");
		tunnus = this.lukija.nextLine();

		char [] salasana;
		salasana = console.readPassword("Anna salasana:\n> ");

		StringBuilder strBuilder = new StringBuilder();

		for (int i = 0; i < salasana.length; i++) {
		   strBuilder.append(salasana[i]);
		}

		String password = strBuilder.toString();
		Arrays.fill(salasana,' ');

		int id = this.kysely.tarkastaKirjautuminen(tunnus, password, this.rooli);

		if(id == 0){
			System.out.println("Käyttäjätunnus tai salasana väärin.");
		}else{
			istunto = new Yllapitajaistunto(id);
		}

		return istunto;	
	}

	public Superuseristunto kirjauduSU(){

		String tunnus ="";
		Superuseristunto istunto = null;
		Console console = System.console() ;

		System.out.println("Kirjaudutaan sisään keskustietokannan pääkäyttäjänä:");
		System.out.print("Syötä käyttäjätunnus:\n> ");
		tunnus = this.lukija.nextLine();

		char [] salasana;
		salasana = console.readPassword("Anna salasana:\n> ");

		StringBuilder strBuilder = new StringBuilder();

		for (int i = 0; i < salasana.length; i++) {
		   strBuilder.append(salasana[i]);
		}

		String password = strBuilder.toString();
		Arrays.fill(salasana,' ');

		int id = this.kysely.tarkastaKirjautuminen(tunnus, password, this.rooli);

		if(id == 0){
			System.out.println("Käyttäjätunnus tai salasana väärin.");
		}else{
			istunto = new Superuseristunto(id);
		}

		return istunto;
	}

	// Uuden asiakkaan rekisteröityminen
	public boolean rekisteroidy(){

		Console console = System.console();
		boolean tiedotOK = false;

		// Talllennetaan syötettävät tiedot hashmappiin
		HashMap<String,String> tiedot = new HashMap<String,String>();

		// Syötetään asiakkaan tiedot
		while(true){

			String rooli ="Asiakas";
			tiedot.put("rooli",rooli);

			System.out.println("Luodaan käyttäjätunnus asiakkaalle.");
			System.out.println("Syötä tiedot, tähdellä merkityt kentät pakollisia:");

			String etunimi = "";
			boolean etunimitarkistus = false;

			// Tarkistetaan, ettei syötetty tyhjää arvoa
			while(!etunimitarkistus){
				System.out.print("Etunimi (*):\n> ");
				etunimi = lukija.nextLine();
				etunimitarkistus = this.tarkastaPituus(etunimi);
			}

			// Lisätään nimi mappiin
			tiedot.put("etunimi",etunimi);

			String sukunimi ="";
			boolean sukunimitarkistus = false;

			// Tarkistetaan, ettei syötetty tyhjää arvoa
			while(!sukunimitarkistus){
				System.out.print("Sukunimi (*):\n> ");
				sukunimi = lukija.nextLine();
				sukunimitarkistus = this.tarkastaPituus(sukunimi);
			}

			tiedot.put("sukunimi",sukunimi);

			String osoite = "";
			boolean osoitetarkistus = false;

			// Tarkistetaan, ettei syötetty tyhjää arvoa
			while(!osoitetarkistus){
				System.out.print("Osoite (*):\n> ");
				osoite = lukija.nextLine();
				osoitetarkistus = this.tarkastaPituus(osoite);
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
				tiedot.put("puhelin",puhelin);
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

			// Varmistetaan että tiedot oikein
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

			// Käyttäjätunnuksen luonti ja tarkistus, että tunnusta ei löydy kannasta
			boolean tunnustarkistus = false;
			String ktunnus= "";

			while(!tunnustarkistus){

				System.out.print("Käyttäjätunnus:\n> ");
				ktunnus = lukija.nextLine();

				if(ktunnus.length()<5){
					System.out.println("Käyttäjätunnus liian lyhyt. Syötä pidempi käyttäjätunnus.");
					continue;
				}
				if(this.kysely.tunnusVarattu(ktunnus)){
					tunnustarkistus = true;
					tiedot.put("tunnus",ktunnus);
				}
				if(!tunnustarkistus){
					System.out.print("Käyttäjätunnus on varattu, valitse toinen käyttäjätunnus:\n> ");
				}
			}

			// Tähän salasanat, tarkistetaan että ne mätsää
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

				password = console.readPassword("Syötä salasana uudelleen: \n> ");
				strBuilder = new StringBuilder();

				for (int i = 0; i < password.length; i++) {
		   		strBuilder.append(password[i]);
				}

				String pw2 = strBuilder.toString();
				Arrays.fill(password,' ');

				if(salasanaTarkistus(pw1,pw2)){
					salasanatarkistus = true;
					tiedot.put("salasana",pw1);
				}
				if(!salasanatarkistus){
					System.out.println("\nSalasanat eivät täsmää tai ovat liian lyhyitä. Anna salasanat uudelleen.");
				}
			}

			// Tietojen päivitys tietokantaan
			if(this.kysely.lisaaKayttaja(tiedot)){
				System.out.println("Tunnus luotu onnistuneesti!");
				System.out.println("Voit nyt kirjautua sisään.");
				return true;
			}

			return false;			
		}
	}

	// Metodi tarkistaa, ettei syötetä tyhjiä arvoja
	public boolean tarkastaPituus(String tieto){

		if(tieto.length()>0){
			return true;
		}

		System.out.println("Syötit tyhjän arvon!");
		return false;
	}

	// Metodi tarkistaa, täsmäävätkö syötetyt salasanat
	public boolean salasanaTarkistus(String pw1, String pw2){

		if(pw1.length()>6 && pw2.length()>6 && pw1.equals(pw2)){
			return true;
		}

		return false;
	}
}