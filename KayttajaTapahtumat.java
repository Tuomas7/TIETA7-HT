// Luokka, joka sisältä kirjautuneen käyttäjän tapahtumat
import java.util.Scanner;

public class KayttajaTapahtumat{


	public static boolean haeTeoksia(Scanner lukija){
		System.out.println("Kirjahaku");
		System.out.println("Valitse hakuehto:");
		String hakuehto = "";
		while(!hakuehto.equals("4")){
			System.out.println("[ 1 ] Hae nimen perusteella");
			System.out.println("[ 2 ] Hae tekijän perusteella");
			System.out.println("[ 3 ] Hae luokan perusteella");
			System.out.println("[ 4 ] Takaisin päävalikkoon");
			System.out.print("\n> ");
			hakuehto = lukija.nextLine();
			if(hakuehto.equals("1")){
				haeNimella(lukija);
			}else if(hakuehto.equals("2")){
				haeTekijalla(lukija);
			}else if(hakuehto.equals("3")){
				haeLuokalla(lukija);
				
			}else{
				System.out.println("Tuntematon komento!");
			}
		}
		return true;
	}

	public static boolean haeNimella(Scanner lukija){
		String nimi ="";
		System.out.print("Syötä haettavan kirjan nimi:\n> ");
		nimi = lukija.nextLine();

		return true;
	}

	public static boolean haeTekijalla(Scanner lukija){
		String tekija ="";
		System.out.print("Syötä haettavan kirjan nimi:\n> ");
		tekija = lukija.nextLine();
		return true;
	}

	public static boolean haeLuokalla(Scanner lukija){
		String luokka ="";
		System.out.print("Syötä haettavan kirjan nimi:\n> ");
		luokka = lukija.nextLine();
		return true;
	}

















}