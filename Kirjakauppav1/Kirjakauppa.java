// Tietokantaohjelmointi / harjoitustyö / Kirjakauppa-ohjelma
// Käyttää luokkia Asiakasistunto, Asiakaskyselyt, Kirjautumisavustaja,
// Yhteys, Yllapitajaistunto, Yllapitokyselyt, Superuser ja Superuserkyselyt.
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

		// "Clear screen"
		System.out.print("\033[H\033[2J");

		printBanner();

		System.out.println("\nTervetuloa kirjakauppaan!\nValitse toiminto:\n");

		String syote = "";	
		
		// Main looppi // käyttöliittymän runki
		while(!syote.equals("5")){

			System.out.println("[ 1 ] Kirjaudu sisään (Asiakas)");
			System.out.println("[ 2 ] Kirjaudu sisään (Ylläpitäjä)");
			System.out.println("[ 3 ] Rekisteröidy asiakkaana");
			System.out.println("[ 4 ] ADMIN");
			System.out.println("[ 5 ] Lopeta");
			System.out.print("\n> ");

			syote = lukija.nextLine();

			if(!(syote.equals("1") || syote.equals("2") ||syote.equals("3") || syote.equals("4")|| syote.equals("5"))){
				//System.out.print("\033[H\033[2J");
				printBanner();
				System.out.println("Virheellinen syöte!\n");
			}	

			// asiakkaan sisäänkirjaus
			if(syote.equals("1")){
				// "Clear screen"
				//System.out.print("\033[H\033[2J");
				Kirjautumisavustaja avustaja = new Kirjautumisavustaja("Asiakas");
				Asiakasistunto istunto = null;
				printBanner();
				istunto = avustaja.kirjaudu();
				

				if(istunto != null){
					System.out.println(istunto.haeNimi());
					sisaankirjausKayttaja(istunto, lukija);
				}

			// ylläpitäjän sisäänkirjaus
			}else if(syote.equals("2")){
				//System.out.print("\033[H\033[2J");
				Kirjautumisavustaja avustaja = new Kirjautumisavustaja("Ylläpitäjä");
				Yllapitajaistunto istunto = null;
				istunto = avustaja.kirjauduyp();
				


				if(istunto !=null){
					System.out.println("onnistui");
					sisaankirjausYllapito(istunto,lukija);
				}else{
					System.out.println("ei");
				}
			
			
			// asiakkaan rekisteröinti	
			}else if(syote.equals("3")){
				Kirjautumisavustaja avustaja = new Kirjautumisavustaja("Asiakas");
			
				//System.out.print("\033[H\033[2J");
				kirjautuminen = avustaja.rekisteroidy();
				//if(kirjautuminen){

				//}
			
			// pääadminin sisäänkirjaus
			}else if(syote.equals("4")){
				Kirjautumisavustaja avustaja = new Kirjautumisavustaja("Superuser");
				Superuser istunto = null;
				istunto = avustaja.kirjauduSU();

				if(istunto !=null){
					System.out.println("onnistui");
					sisaankirjausSuperuser(istunto,lukija);
				}else{
					System.out.println("ei");
				}
			}
			
		}
			
			
		
		//printBanner();
		//System.out.print("\033[H\033[2J");
		//printBanner();
		System.out.println("\nTervetuloa uudelleen!\n");	
	}

	// Pääadminin käyttöliittymä
	public static void sisaankirjausSuperuser(Superuser istunto, Scanner lukija){
		
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
			}
		}
	}


	// Käyttöliittymä sisäänkirjautuneelle ylläpitäjälle
	// @param lukija // Scanner-olio
	// @param istunto // Ylläpitoistunto-olio
	public static void sisaankirjausYllapito(Yllapitajaistunto istunto, Scanner lukija){

		//istunto.haeDivariID();
		istunto.tulosta();
		String syote ="";
		while(!syote.equals("4")){
			System.out.println("------------");
			System.out.println("| Ylläpito |");
			System.out.println("------------");
			System.out.println("Olet kirjautuneena ylläpitäjänä:\n");
			System.out.println("[ 1 ] Lisää teoksia");
			System.out.println("[ 2 ] Näytä myynnissä olevat teokset");
			System.out.println("[ 2 ] Päivitä keskustietokanta");
			System.out.println("[ 3 ] Jotain muuta");
			System.out.println("[ 4 ] Kirjaudu ulos");
			System.out.print("\n> ");

			syote = lukija.nextLine();

			if(syote.equals("1")){
				
				istunto.lisaaTeos();
				
			}else if(syote.equals("2")){
				istunto.paivitaKeskus();

			}else if(syote.equals("3")){
				//ostoskori(istunto, lukija);
			}
		}		
		System.out.println("Kirjauduit ulos.");
	}

	// Käyttöliittymä sisäänkirjautuneelle asiakkaalle
	// @param lukija // Scanner-olio
	// @param istunto // Asiakasistunto olio
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
			//System.out.println("[ 5 ] Tulosta hakuhistoria");
			//System.out.println("[ 6 ] Tilaa tuotteet");
			System.out.println("[ 4 ] Kirjaudu ulos");

			System.out.print("\n> ");

			syote = lukija.nextLine();

			if(syote.equals("1")){
				istunto.haeTeoksia();

			}else if(syote.equals("2")){
				profiili(istunto, lukija);

			}else if(syote.equals("3")){
				ostoskori(istunto, lukija);
				
				/*
			}else if(syote.equals("5")){
				istunto.tulostaHistoria();

			
				*/
			}
		
		}
		System.out.println("Kirjauduit ulos.");
		
	}

	// Ostoskorin käyttöliittymä
	public static void ostoskori(Asiakasistunto istunto, Scanner lukija){


		String syote = "";
				

		System.out.print("\n>");
		while(!(syote.equals("3"))){
			System.out.println("-------------");
			System.out.println("| Ostoskori |");
			System.out.println("-------------");
			istunto.tulostaOstoskori();

			System.out.println("[ 1 ] Tilaa tuotteet");
			System.out.println("[ 2 ] Tyhjennä ostoskori");
			System.out.println("[ 3 ] Palaa päävalikkoon");

			syote=lukija.nextLine();

			if(!(syote.equals("1") || syote.equals("2")|| syote.equals("3"))){
				System.out.println("Tuntematon komento!");
			}	
			if(syote.equals("1")){
				istunto.tilaaTuotteet();
			}else if(syote.equals("2")){
				istunto.tyhjennaKori();
			}
		}
		
	
	}

	// Asiakasprofiilin käyttöliittymä
	public static void profiili(Asiakasistunto istunto, Scanner lukija){
		System.out.println();
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
			if(!(syote.equals("1") || syote.equals("2")){
				System.out.println("Tuntematon komento!");
			}	
			if(syote.equals("1")){
				istunto.lisaaRahaa()
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



