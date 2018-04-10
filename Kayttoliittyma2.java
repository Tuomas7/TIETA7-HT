import java.util.Scanner;

public class Kayttoliittyma2{


	public static void main(String[] args){

		Scanner lukija = new Scanner(System.in);

		System.out.println("       _      _       _                                  __  __");
		
		System.out.println("  /\\ /(_)_ __(_) __ _| | ____ _ _   _ _ __  _ __   __ _  \\ \\/ /");
		
		System.out.println(" / //_/ | '__| |/ _` | |/ / _` | | | | '_ \\| '_ \\ / _` |  \\  / ");
		
		System.out.println("/ __ \\| | |  | | (_| |   < (_| | |_| | |_) | |_) | (_| |  /  \\ ");
		
		System.out.println("\\/  \\/|_|_| _/ |\\__,_|_|\\_\\__,_|\\__,_| .__/| .__/ \\__,_| /_/\\_\\");
		System.out.println("           |__/                      |_|   |_|                 ");

		System.out.println("\nTervetuloa kirjakauppaan!\nValitse toiminto:\n");

		String syote = "";		

		while(!syote.equals("3")){

			System.out.println("[ 1 ] Kirjaudu sisään (Asiakas)");
			System.out.println("[ 2 ] Kirjaudu sisään (Ylläpitäjä)");
			System.out.println("[ 3 ] Lopeta");
			System.out.print("\n> ");

			syote = lukija.nextLine();

			if(!(syote.equals("1") || syote.equals("2") || syote.equals("3"))){
				System.out.println("Virheellinen syöte!");
			}

			if(syote.equals("1")){
				String username ="";
				String password = "";
				System.out.println("Kirjaudutaan sisään käyttäjänä.");
				System.out.println("Syötä käyttäjätunnus: ");
				username = lukija.nextLine();
				System.out.println("Syötä salasana: ");
				password = lukija.nextLine();

			}
			
			
		}


		
	}





}