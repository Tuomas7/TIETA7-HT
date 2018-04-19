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
		
		Asiakaskyselyt kysely = new Asiakaskyselyt();

		// "Clear screen"
		System.out.print("\033[H\033[2J");

		printBanner();

		System.out.println("\nTervetuloa kirjakauppaan!\nValitse toiminto:\n");

		String syote = "";	
		
		// Main looppi
		while(!syote.equals("4")){

			System.out.println("[ 1 ] Kirjaudu sisään (Asiakas)");
			System.out.println("[ 2 ] Kirjaudu sisään (Ylläpitäjä)");
			System.out.println("[ 3 ] Rekisteröidy asiakkaana");
			System.out.println("[ 4 ] Lopeta");
			System.out.print("\n> ");

			syote = lukija.nextLine();

			if(!(syote.equals("1") || syote.equals("2") ||syote.equals("3") || syote.equals("4"))){
				//System.out.print("\033[H\033[2J");
				printBanner();
				System.out.println("Virheellinen syöte!\n");
			}	

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
			
			
					
			}else if(syote.equals("3")){
				Kirjautumisavustaja avustaja = new Kirjautumisavustaja("Asiakas");
			
				//System.out.print("\033[H\033[2J");
				kirjautuminen = avustaja.rekisteroidy();
				//if(kirjautuminen){

				//}
				
			}
			
		}
			
			
		
		//printBanner();
		//System.out.print("\033[H\033[2J");
		//printBanner();
		System.out.println("Tervetuloa uudelleen");



		
	}

	// Käyttöliittymä sisäänkirjautuneelle ylläpitäjälle
	// @param lukija // Scanner-olio
	// @param istunto // Sessio-olio
	
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
			System.out.println("[ 2 ] Jotain muuta");
			System.out.println("[ 3 ] Jotain muuta");
			System.out.println("[ 4 ] Kirjaudu ulos");
			System.out.print("\n> ");

			syote = lukija.nextLine();

			if(syote.equals("1")){
				
				istunto.lisaaTeos();
				
			}else if(syote.equals("2")){
				//profiili(istunto, lukija);

			}else if(syote.equals("3")){
				//ostoskori(istunto, lukija);
			}
		}
		System.out.println("Kirjauduit ulos.");
	}

	// Käyttöliittymä sisäänkirjautuneelle asiakkaalle
	// @param lukija // Scanner-olio
	// @param istunto // Sessio-olio

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

	public static void profiili(Asiakasistunto istunto, Scanner lukija){

		
		System.out.println();
		String syote = "";
		
		

		while(!(syote.equals("3"))){
			System.out.println("------------");
			System.out.println("| Profiili |");
			System.out.println("------------");
			istunto.tulostaTiedot();

			System.out.println("[ 1 ] Muokkaa tietoja");
			System.out.println("[ 2 ] Lisää rahaa tilille");
			System.out.println("[ 3 ] Palaa päävalikkoon");

			System.out.print("\n>");

			syote=lukija.nextLine();
			if(!(syote.equals("1") || syote.equals("2")|| syote.equals("3"))){
				System.out.println("Tuntematon komento!");
			}	
			if(syote.equals("1")){
				//istunto.tilaaTuotteet();
			}else if(syote.equals("2")){
				istunto.lisaaRahaa();
			}else if(syote.equals("3")){
				
			}

		}
		

	}

	



	public static void printBanner(){
		System.out.println("       _      _       _                                  __  __");
		
		System.out.println("  /\\ /(_)_ __(_) __ _| | ____ _ _   _ _ __  _ __   __ _  \\ \\/ /");
		
		System.out.println(" / //_/ | '__| |/ _` | |/ / _` | | | | '_ \\| '_ \\ / _` |  \\  / ");
		
		System.out.println("/ __ \\| | |  | | (_| |   < (_| | |_| | |_) | |_) | (_| |  /  \\ ");
		
		System.out.println("\\/  \\/|_|_| _/ |\\__,_|_|\\_\\__,_|\\__,_| .__/| .__/ \\__,_| /_/\\_\\");
		System.out.println("           |__/                      |_|   |_|                 ");
	}
		}



