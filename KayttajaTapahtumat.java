// Luokka, joka sisältä kirjautuneen käyttäjän tapahtumat
import java.util.Scanner;

public class KayttajaTapahtumat{


	public static boolean haeTeoksia(Scanner lukija, Sessio istunto){
		System.out.println("Kirjahaku");
		System.out.println("Valitse hakuehto:");
		String hakuehto = "";
		String hakukriteeri = "";
		while(!hakuehto.equals("4")){

			System.out.println("[ 1 ] Hae nimen perusteella");
			System.out.println("[ 2 ] Hae tekijän perusteella");
			System.out.println("[ 3 ] Hae luokan perusteella");
			System.out.println("[ 4 ] Takaisin päävalikkoon");
			System.out.print("\n> ");
			hakuehto = lukija.nextLine();

			if(hakuehto.equals("1")){
				hakukriteeri = "nimi";
				haekirjoja(lukija, hakukriteeri, istunto);
			}else if(hakuehto.equals("2")){
				hakukriteeri = "tekija";
				haekirjoja(lukija, hakukriteeri, istunto);
			}else if(hakuehto.equals("3")){
				hakukriteeri = "luokka";
				haekirjoja(lukija, hakukriteeri, istunto);
				
			}else if(!hakuehto.equals("4")){
				System.out.println("Tuntematon komento!");
			}
		}
		return true;
	}

	public static boolean haekirjoja(Scanner lukija, String hakuehto, Sessio istunto){
		String nimi ="";
		System.out.print("Syötä haettavan kirjan "+hakuehto+":\n> ");
		nimi = lukija.nextLine();

		return true;
	}

	public static void naytaProfiili(Scanner lukija, Sessio istunto){

	}

















}