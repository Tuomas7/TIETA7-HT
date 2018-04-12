import java.util.Scanner;
import java.sql.*;
import java.util.Map;
import java.util.HashMap;

public class Kayttoliittyma2{

	private static final HashMap<String,String> kirjautunutKayttaja = null;

	public static void main(String[] args){

		Scanner lukija = new Scanner(System.in);

		// Luodaan uusi yhteys
		Connection yhteys = null;
		Yhteys con = new Yhteys("localhost", 5432, "bookstore", "testuser", "12345");
		try{
			yhteys = con.uusiYhteys();
		}catch(SQLException poikkeus) {
        	System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());  
      	}
		

		// "Clear screen"
		System.out.print("\033[H\033[2J");

		printBanner();

		System.out.println("\nTervetuloa kirjakauppaan!\nValitse toiminto:\n");

		String syote = "";	
		boolean kirjautuminen = false;	

		while(!syote.equals("4")){

			System.out.println("[ 1 ] Kirjaudu sisään (Asiakas)");
			System.out.println("[ 2 ] Kirjaudu sisään (Ylläpitäjä)");
			System.out.println("[ 3 ] Rekisteröidy");
			System.out.println("[ 4 ] Lopeta");
			System.out.print("\n> ");

			syote = lukija.nextLine();

			if(!(syote.equals("1") || syote.equals("2") ||syote.equals("3") || syote.equals("4"))){
				System.out.print("\033[H\033[2J");
				printBanner();
				System.out.println("Virheellinen syöte!\n");
			}	

			if(syote.equals("1")){
				// "Clear screen"
				System.out.print("\033[H\033[2J");
				printBanner();
				kirjautuminen = Paavalikko.kirjauduKayttajana(lukija, yhteys);
				if(kirjautuminen){
					sisaankirjaus(lukija);
				}

			}else if(syote.equals("2")){
				System.out.print("\033[H\033[2J");
				kirjautuminen = Paavalikko.kirjauduYllapitajana(lukija, yhteys);
					
			}else if(syote.equals("3")){
				System.out.print("\033[H\033[2J");
				kirjautuminen = Paavalikko.rekisteroidy(lukija, yhteys);
				//if(kirjautuminen){

				//}
				
			}
			
			
		}
		printBanner();
		System.out.print("\033[H\033[2J");
		printBanner();
		System.out.println("Tervetuloa uudelleen");



		
	}



	public static void sisaankirjaus(Scanner lukija){

		String syote ="";
		while(!syote.equals("4")){
			System.out.println("Olet kirjautuneena käyttäjänä: ");
			System.out.println("[ 1 ] Hae teoksia");
			System.out.println("[ 2 ] Jotain muuta");
			System.out.println("[ 3 ] Jotain muuta");
			System.out.println("[ 4 ] Kirjaudu ulos");
			System.out.print("\n> ");

			syote = lukija.nextLine();
		}
		System.out.println("Kirjauduit ulos.");
		
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