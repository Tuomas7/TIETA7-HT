import java.util.Scanner;
import java.sql.*;

public class Kayttoliittyma2{


	public static void main(String[] args){

		Scanner lukija = new Scanner(System.in);

		// "Clear screen"
		System.out.print("\033[H\033[2J");

		printBanner();

		System.out.println("\nTervetuloa kirjakauppaan!\nValitse toiminto:\n");

		String syote = "";		

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
				kirjaudu(lukija);

			}else if(syote.equals("2")){
				System.out.print("\033[H\033[2J");
				kirjaudu(lukija);
			}
			
			
		}
		printBanner();
		System.out.print("\033[H\033[2J");
		printBanner();
		System.out.println("Tervetuloa uudelleen");


		
	}

	public static boolean kirjaudu(Scanner lukija){
		printBanner();
		String username ="";
		String password = "";
		System.out.println("Kirjaudutaan sisään käyttäjänä.");
		System.out.println("Syötä käyttäjätunnus: ");
		username = lukija.nextLine();
		System.out.println("Syötä salasana: ");
		password = lukija.nextLine();
		Yhteys yht = new Yhteys();
		try{
			Connection yhteys = yht.uusiYhteys();
		}catch (SQLException poikkeus){
			System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());
		}
		

		return true;
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