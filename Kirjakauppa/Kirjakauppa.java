// Tietokantaohjelmointi / harjoitustyö / Kirjakauppa-ohjelma
// Käyttää luokkia Asiakasistunto, Asiakaskyselyt, Kirjautumisavustaja,
// Yhteys, Yllapitajaistunto, Yllapitokyselyt, Superuseristunto ja Superuserkyselyt.
// Tarkempi dokumentaatio erikseen.
// Tekijät: Tapio Nevalainen & Tuomas Tammela. 

import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.io.Console;
import java.util.Arrays;
import java.util.Random;

public class Kirjakauppa{

	public static void main(String[] args){

		Scanner lukija = new Scanner(System.in);
		boolean kirjautuminen = false;

		// Luodaan kysely-olio, jonka avulla kirjaudutaan
		Asiakaskyselyt kysely = new Asiakaskyselyt();

		// Dummyt kääntämisen helpottamiseksi, ei käytetä tässä
		Yllapitokyselyt kys = new Yllapitokyselyt(0);
		SuperuserKyselyt kys2 = new SuperuserKyselyt();
		Yllapitokyselyt kyselyt = new Yllapitokyselyt(0);
      Yhteys yht = new Yhteys();
      
		printBanner();

		System.out.println("\nTervetuloa kirjakauppaan!\nValitse toiminto:\n");
		String syote = "";	

		// Main looppi, käyttöliittymän runko
		while(!syote.equals("5")){

			System.out.println("[ 1 ] Kirjaudu sisään (Asiakas)");
			System.out.println("[ 2 ] Kirjaudu sisään (Ylläpitäjä)");
			System.out.println("[ 3 ] Kirjaudu sisään (Pääylläpitäjä)");
			System.out.println("[ 4 ] Rekisteröidy asiakkaana");
			System.out.println("[ 5 ] Lopeta");
			System.out.print("\n> ");

			syote = lukija.nextLine();

			// Asiakkaan sisäänkirjaus
			if(syote.equals("1")){

				Kirjautumisavustaja avustaja = new Kirjautumisavustaja("Asiakas");
				Asiakasistunto istunto = null;
				istunto = avustaja.kirjaudu();

				if(istunto != null){
					sisaankirjausKayttaja(istunto, lukija);
				}else{
					System.out.println("Kirjautuminen ei onnistunut.");
				}

			// Ylläpitäjän sisäänkirjaus
			}else if(syote.equals("2")){

				Kirjautumisavustaja avustaja = new Kirjautumisavustaja("Yllapitaja");
				Yllapitajaistunto istunto = null;
				istunto = avustaja.kirjauduyp();

				if(istunto !=null){
					sisaankirjausYllapito(istunto,lukija);
				}else{
					System.out.println("Kirjautuminen ei onnistunut.");
				}

			// Pääadminin sisäänkirjaus
			}else if(syote.equals("3")){

				Kirjautumisavustaja avustaja = new Kirjautumisavustaja("Superuser");
				Superuseristunto istunto = null;
				istunto = avustaja.kirjauduSU();
				if(istunto !=null){
					sisaankirjausSuperuser(istunto,lukija);
				}else{
					System.out.println("Kirjautuminen ei onnistunut.");
				}

			// Asiakkaan rekisteröinti	
			}else if(syote.equals("4")){

				Kirjautumisavustaja avustaja = new Kirjautumisavustaja("Asiakas");
				kirjautuminen = avustaja.rekisteroidy();

			}else if(!syote.equals("5")){
				System.out.println("Tuntematon komento!");
			}
		}

		System.out.println("\nTervetuloa uudelleen!\n");	
	}

	// Pääadminin käyttöliittymä
	// @param istunto // Superuseristunto-olio
   // @param lukija // Scanner-olio
	public static void sisaankirjausSuperuser(Superuseristunto istunto, Scanner lukija){

		String syote = "";

		while(!syote.equals("4")){

			System.out.println("-------------");
			System.out.println("| Superuser |");
			System.out.println("-------------");

			System.out.println("Olet kirjautuneena keskustietokannan pääkäyttäjänä:\n");
			System.out.println("[ 1 ] Lisää paikallisen tietokannan tiedot keskustietokantaan");
			System.out.println("[ 2 ] Tulosta teosten luokkien kokonaismyyntihinnat ja keskihinnat");
			System.out.println("[ 3 ] Tulosta myyntiraportti");
			System.out.println("[ 4 ] Kirjaudu ulos");
			System.out.print("\n> ");

			syote = lukija.nextLine();

			if(syote.equals("1")){
				istunto.lisaaDivariKantaan();

			}else if(syote.equals("2")){
				istunto.luokkaRaportti();

			}else if(syote.equals("3")){
				istunto.myyntiRaportti();

			}else if(!syote.equals("4")){
				System.out.println("Tuntematon komento!");
			}
		}
      System.out.println("Kirjauduit ulos.");
	}

	// Käyttöliittymä sisäänkirjautuneelle ylläpitäjälle
	// @param istunto // Ylläpitoistunto-olio
	// @param lukija // Scanner-olio
	public static void sisaankirjausYllapito(Yllapitajaistunto istunto, Scanner lukija){

		istunto.tulosta();
		String syote ="";

		while(!syote.equals("3")){

			System.out.println("------------");
			System.out.println("| Ylläpito |");
			System.out.println("------------");

			System.out.println("Olet kirjautuneena ylläpitäjänä:\n");
			System.out.println("[ 1 ] Lisää teoksia");
			System.out.println("[ 2 ] Päivitä keskustietokanta");
			System.out.println("[ 3 ] Kirjaudu ulos");
			System.out.print("\n> ");

			syote = lukija.nextLine();

			if(syote.equals("1")){
				istunto.lisaaTeos();

			}else if(syote.equals("2")){
				istunto.paivitaKeskus();

			}else if(!syote.equals("3")){
				System.out.println("Tuntematon komento!");
			}
		}		
		System.out.println("Kirjauduit ulos.");
	}

	// Käyttöliittymä sisäänkirjautuneelle asiakkaalle
	// @param istunto // Asiakasistunto-olio
   // @param lukija // Scanner-olio
	public static void sisaankirjausKayttaja(Asiakasistunto istunto, Scanner lukija){

		String syote ="";

		while(!syote.equals("4")){

			System.out.println("--------------");
			System.out.println("| Päävalikko |");
			System.out.println("--------------");

			System.out.println("Olet kirjautuneena käyttäjänä: "+istunto.haeNimi()+"\n");
			System.out.println("[ 1 ] Hae teoksia");
			System.out.println("[ 2 ] Profiili");
			System.out.println("[ 3 ] Ostoskori");
			System.out.println("[ 4 ] Kirjaudu ulos");
			System.out.print("\n>");

			syote = lukija.nextLine();

			if(syote.equals("1")){
				istunto.haeTeoksia();

			}else if(syote.equals("2")){
				profiili(istunto, lukija);

			}else if(syote.equals("3")){
				ostoskori(istunto, lukija);

			}else if(!syote.equals("4")){
				System.out.println("Tuntematon komento!");
			}
		}
		System.out.println("Kirjauduit ulos.");
	}

	// Ostoskorin käyttöliittymä
	public static void ostoskori(Asiakasistunto istunto, Scanner lukija){

		String syote = "";

		while(!(syote.equals("3"))){

			System.out.println("-------------");
			System.out.println("| Ostoskori |");
			System.out.println("-------------");
			istunto.tulostaOstoskori();

			System.out.println("[ 1 ] Tilaa tuotteet");
			System.out.println("[ 2 ] Tyhjennä ostoskori");
			System.out.println("[ 3 ] Palaa päävalikkoon");

			syote=lukija.nextLine();

			if(syote.equals("1")){
				istunto.tilaaTuotteet();

			}else if(syote.equals("2")){
				istunto.tyhjennaKori();

			}else if(!syote.equals("3")){
				System.out.println("Tuntematon komento!");
			}
		}
	}

	// Asiakasprofiilin käyttöliittymä
	public static void profiili(Asiakasistunto istunto, Scanner lukija){

		String syote = "";

		while(!(syote.equals("2"))){
         
			System.out.println("------------");
			System.out.println("| Profiili |");
			System.out.println("------------");
			istunto.tulostaTiedot();

			System.out.println("[ 1 ] Lisää rahaa tilille");
			System.out.println("[ 2 ] Palaa päävalikkoon");
			System.out.print("\n>");

			syote=lukija.nextLine();

			if(syote.equals("1")){
				istunto.lisaaRahaa();

			}else if(!syote.equals("2")){
				System.out.println("Tuntematon komento!");
			}
		}
	}

	// "Bannerin" tulostus
	public static void printBanner(){
		System.out.println("       _      _       _                                  __  __");
		System.out.println("  /\\ /(_)_ __(_) __ _| | ____ _ _   _ _ __  _ __   __ _  \\ \\/ /");
		System.out.println(" / //_/ | '__| |/ _` | |/ / _` | | | | '_ \\| '_ \\ / _` |  \\  / ");
		System.out.println("/ __ \\| | |  | | (_| |   < (_| | |_| | |_) | |_) | (_| |  /  \\ ");
		System.out.println("\\/  \\/|_|_| _/ |\\__,_|_|\\_\\__,_|\\__,_| .__/| .__/ \\__,_| /_/\\_\\");
		System.out.println("           |__/                      |_|   |_|                 ");
	}
}



