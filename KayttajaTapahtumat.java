// Luokka, joka sisältä kirjautuneen käyttäjän tapahtumat
import java.util.Scanner;
import java.sql.*;

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
		String haku ="";
		System.out.print("Syötä haettavan kirjan "+hakuehto+":\n> ");
		haku = lukija.nextLine();
		if(haku.length()>0){
			istunto.lisaaHakuHistoriaan(haku);
		}

		try{
			Statement stmt = istunto.haeYhteys().createStatement();
			ResultSet rs = stmt.executeQuery("SELECT nimi,tekija,luokka FROM teos WHERE '"+hakuehto+"' LIKE '%"+haku+"%'");
		
			while(rs.next()){
				System.out.println(rs.getString("nimi") +", "+rs.getString("tekija")+", "+rs.getString("luokka"));
			}
			
			
		}catch (SQLException poikkeus){
			System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());
		}


		return true;
	}

	

















}