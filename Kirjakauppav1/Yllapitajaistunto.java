// ylläpitäjäistunto-luokka. Mallintaa Ylläpitäjän istuntoa ja metodeita.

import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;

public class Yllapitajaistunto{

	private int kayttajaID;
	private int divariID;
	private Scanner lukija;
	private Yllapitokyselyt kyselyt;
	private boolean divariKeskusKannassa;
	private HashMap<String,String> lisays;
	private String isbn;



	public Yllapitajaistunto(int id){
		
		this.kayttajaID = id;
		this.lukija = new Scanner(System.in);
		//System.out.println(this.kayttajaID);
		this.kyselyt = new Yllapitokyselyt(this.kayttajaID);
		this.divariID = this.kyselyt.haeDivari();
		this.divariKeskusKannassa = this.kyselyt.tarkastaDivari();
	}


	public void tulosta(){
		System.out.println("Olet kirjautuneena Divarin D"+this.divariID+ " ylläpitäjänä.");
		if(this.divariKeskusKannassa){
			System.out.println("Divari on liittynyt keskustietokantaan.");
		}else{
			System.out.println("Divari ei ole liittynyt keskustietokantaan.");
		}
		//System.out.println(this.divariID);
		//System.out.println(this.divariKeskusKannassa);
	}

	public boolean onkoKannassa(){
		return this.divariKeskusKannassa;
	}

	// Teoksen tietojen lisääminen tietokantaan
	public void lisaaTeos(){
		
		this.lisays = new HashMap<String,String>();

		System.out.println("------------------");
		System.out.println("| Teoksen lisäys |");
		System.out.println("------------------");

		if(this.divariKeskusKannassa){
			System.out.println("Lisätään teos keskusdivarin tietokantaan");
			
			System.out.println("Syötä teoksen ISBN:\n> ");
			this.isbn = lukija.nextLine();
			this.lisays.put("isbn",isbn);

			if(this.kyselyt.haeISBNkeskus(this.isbn)){
				System.out.println("Teostiedot löytyvät tietokannasta. Lisätään vain kappaletiedot.");
				this.lisaaKappaletiedot();
				this.kyselyt.lisaaKPLtiedot(this.lisays);
			}else{
				this.lisaaTeostiedot();
				this.lisaaKappaletiedot();
				this.kyselyt.lisaateosKPLtiedot(this.lisays);

			}

		}else{
			System.out.println("Lisätään teos paikalliseen tietokantaan");

			System.out.println("Syötä teoksen ISBN:\n> ");
			this.isbn = lukija.nextLine();
			this.lisays.put("isbn",isbn);

			if(this.kyselyt.haeISBNpaikallinen(this.isbn)){
				System.out.println("Teostiedot löytyvät tietokannasta. Lisätään vain kappaletiedot.");
				this.lisaaKappaletiedot();
				this.kyselyt.lisaaKPLtiedot(this.lisays);
			}else{
				this.lisaaTeostiedot();
				this.lisaaKappaletiedot();
				this.kyselyt.lisaateosKPLtiedot(this.lisays);

			}
		}

	}

   // Päivitetään itsenäisen divarin tiedot keskustietokantaan
	public void paivitaKeskus() {
      	this.kyselyt.paivitaKeskustietokanta();
   	}
   	
   	// Lisätään uuden kappaleen tiedot tietokantaan
	public void lisaaKappaletiedot(){

		System.out.println("-----------------------");
		System.out.println("| Syötä kappaletiedot |");
		System.out.println("-----------------------");

		// Kysytään käyttäjältä yksittäisen kappaleen tiedot
		System.out.println("Anna kappaleen tiedot:");
		System.out.print("Kappaleen myyntihinta:\n> ");
		float hinta = lukija.nextFloat();
		this.lisays.put("hinta",String.valueOf(hinta));
		System.out.print("Kappaleen ostohinta:\n> ");
		float ostohinta = lukija.nextFloat();
		lukija.nextLine();
		this.lisays.put("ostohinta",String.valueOf(ostohinta));
		System.out.println("");
	}

	// Lisätään uuden teoksen tiedot ja kappaletiedot tietokantaan
	public void lisaaTeostiedot(){

		System.out.println("Teostietoja ei löytynyt tietokannasta. Lisätään teostiedot ja kappaletiedot.");
				
		System.out.println("--------------------");
		System.out.println("| Syötä teostiedot |");
		System.out.println("--------------------");

		System.out.print("Syötä teoksen nimi:\n> ");
		String nimi = lukija.nextLine();
		this.lisays.put("nimi",nimi);
		System.out.print("Syötä teoksen tekijä:\n> ");
		String tekija = lukija.nextLine();
		this.lisays.put("tekija",tekija);
		System.out.print("Syötä teoksen julkaisuvuosi:\n> ");
		int vuosi = lukija.nextInt();
		this.lisays.put("vuosi",String.valueOf(vuosi));
		lukija.nextLine();
		System.out.print("Syötä teoksen tyyppi:\n> ");
		String tyyppi = lukija.nextLine();
		this.lisays.put("tyyppi",tyyppi);
		System.out.print("Syötä teoksen luokka:\n> ");
		String luokka = lukija.nextLine();
		this.lisays.put("luokka",luokka);
		System.out.print("Syötä teoksen paino grammoina:\n> ");
		int paino = lukija.nextInt();
		lukija.nextLine();
		this.lisays.put("paino",String.valueOf(paino));
		System.out.println("");

	}

}
