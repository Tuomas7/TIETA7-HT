// Asiakasistunto-luokka mallintaa kirjautuneen asiakkaan istuntoa.
// Sisältää asiakkaan tiedot tallennettuna oliomuuttujiin, sekä Asiakaskyselyt-
// olion, joka toimii rajapintana tietokantaan ja on vastuussa kaikista kyselyistä.
// Asikaskyselyiden metodit palauttavat Hashmappeja teoshauista ja ostoskorin sisällöstä,
// jotka tallennetaan myös istunnon oliomuuttujiin.

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;
public class Asiakasistunto{

	// Attribuutit asiakasistunnolle
	// kyselyt : Asiakaskysely-olio, joka muodostaa rajapinnan tietokantaan
	// id: asiakkaan id
	// nimi: Asiakkaan nimi
	// asiakastiedot: asiakkaan tiedot tallennettuna HashMap-tietorakenteeseen
	// hakuhistoria: Istunnon aikana hauissa käytetyt hakusanat arraylistissä
	// saldo: miten paljon asiakkaalla on rahaa käytettävissä
	private Asiakaskyselyt kyselyt;
	private int id;
	private String nimi;
	private HashMap<String,String> asiakastiedot;
	private ArrayList<String> hakuhistoria;
	private ArrayList<String> monisanahaku;
	private double saldo;

	// ostoskori: Ostoskoriin lisätyt teokset Hashmapissa
	// hakukriteeri: käytetään hyväksi teoshauissa
	// hakusana: käytetään hyväksi teoshauissa
	// teoshakutulokset: HashMap, joka sisältää teoshaun tulokset tietokannasta
	private HashMap<String,ArrayList<String>> ostoskori;
	private String hakukriteeri;
	private String hakusana;
	private HashMap<String,ArrayList<String>> teoshakutulokset;
	private Scanner lukija;

	// Konstruktori, joka saa parametrina id:n, joka asetetaan istunnon id:ksi
	public Asiakasistunto(int id){

		this.lukija = new Scanner(System.in);
		this.id=id;

		// Luodaan uusi kysely-olio ja asetetaan olion attribuutiksi sama id
		// jolloin kyselyt tehdään automaattisesti asiakkaan omin tietoihin
		// eikä id:tä tarvitse välittää parametrina
		this.kyselyt = new Asiakaskyselyt();
		this.kyselyt.asetaID(this.id);

		// Haetaan kysely-oliolla asiakastiedot asiakastiedot - hashmappiin
		this.asiakastiedot = this.kyselyt.haeKayttajanTiedot(this.id);

		// Asetetaan asiakkaan nimi 
		this.nimi = this.asiakastiedot.get("etunimi")+" "+this.asiakastiedot.get("sukunimi");

		// Tyhjä lista merkkijonoille, joilla on tehty hakuja 
		this.hakuhistoria = new ArrayList<>();

		this.ostoskori = new HashMap<String,ArrayList<String>>();
		this.saldo = Double.parseDouble(this.asiakastiedot.get("saldo"));
	}

	// Getteri asiakkaan nimelle
	public String haeNimi(){
		return this.nimi;
	}

	// Privaatti metodi, joka palauttaa merkkijonoesityksen puhelinnumerosta.
	// Käytetään tulostaTiedot-metodissa. Palauttaa tyhjän merkkijonon, jos
	// arvo on "NULL", muuten palauttaa puhelinnumeron
	private String haePuhelin(){

		String puhelin = this.asiakastiedot.get("puhelin");

		if(puhelin.equals("NULL")){
			return "";
		}else{
			return puhelin;
		}
	}

	// Privaatti metodi, joka palauttaa merkkijonoesityksen sähköpostiosoitteesta.
	// Käytetään tulostaTiedot-metodissa. Palauttaa tyhjän merkkijonon, jos
	// arvo on "NULL", muuten palauttaa sähköpostiosoitteen
	private String haeSahkoposti(){

		String sahkoposti = this.asiakastiedot.get("sahkoposti");

		if(sahkoposti.equals("NULL")){
			return "";
		}else{
			return sahkoposti;
		}
	}

	// Privaatti metodi, joka palauttaa merkkijonoesityksen salasanan.
	// Käytetään tulostaTiedot-metodissa. Palauttaa salansanan pituisen
	// merkkijonon tähtiä (tietoturva-aspekti)
	private String haeSalasana(){

		String maskattu = "";

		for(int i = 0; i< this.asiakastiedot.get("salasana").length();i++){
			maskattu = maskattu + "*";
		}

		return maskattu;
	}

	public ArrayList<String> haeHistoria(){
		return this.hakuhistoria;
	}

	public HashMap<String,ArrayList<String>> haeOstoskori(){
		return this.ostoskori;
	}

	// Tulostaa asiakkaan tiedot
	public void tulostaTiedot(){

		System.out.println("\nProfiilin tiedot:\n");
		System.out.println("Nimi: "+this.nimi);
		System.out.println("Osoite: "+this.asiakastiedot.get("osoite"));
		System.out.println("Puhelin: "+this.haePuhelin());
		System.out.println("Sähköposti: "+this.haeSahkoposti());
		System.out.println("Saldo: "+ this.asiakastiedot.get("saldo"));
		System.out.println("\nKäyttäjätunnus: "+this.asiakastiedot.get("tunnus"));
		System.out.println("Salasana: "+this.haeSalasana());
	}

	// Metodi, joka tulostaa ostoskorin sisällön, eli tiedot teoksista, jotka on
	// varattu käyttäjälle tällä hetkellä.
	public void tulostaOstoskori(){

		// Haetaan ostoskori-hahsmappiin tiedot varauksista käyttäen kysely-oliota.
		this.ostoskori = this.kyselyt.haeVaraukset(this.id);

		if(this.ostoskori.size() == 0){
			System.out.println("Ostoskori on tyhjä.");

		}else{

			// Tulostetaan muotoiltuna teosten nimet, tekijat, painot ja hinnat
			System.out.println("\n---------------------------------------------------------------------------------------------------------");
			System.out.format("%50s%40s%10s%10s\n","Nimi","Tekija","Paino","Hinta");
			System.out.println("---------------------------------------------------------------------------------------------------------");

			for(int i = 1 ; i < this.ostoskori.size()+1; i++){

				System.out.format("%50s",this.ostoskori.get(String.valueOf(i)).get(2));
				System.out.format("%40s",this.ostoskori.get(String.valueOf(i)).get(3));
				System.out.format("%10s",this.ostoskori.get(String.valueOf(i)).get(7));
				System.out.format("%10s",this.ostoskori.get(String.valueOf(i)).get(8));
				System.out.println();
			}
			System.out.println("\n");
		}
	}

	// Metodi tulostaa hakuhistorian, eli hakusanat joilla teoksia on haettu tämän
	// istunnon aikana.
	public void tulostaHistoria(){

		if(this.hakuhistoria.size() == 0){
			System.out.println("Hakuhistoria on tyhjä.\n");

		}else{
			System.out.println("Olet tehnyt seuraavia hakuja:\n");

			for(String haku : this.hakuhistoria){
				System.out.println(haku);
			}
		}
	}

	// Metodi lisää hakuhistoriaan uuden hakusanan
	public void lisaaHakuHistoriaan(String haku){
		this.hakuhistoria.add(haku);
	}

	// Metodi, joka palauttaa tulostuksen teoskappaleista, joihin hakusana 
	// sisältyy. Haku voidaan kohdistaa nimeen, tekijään, luokkaan, tyyppiin
	// tai kaikkiin em. kenttiin kerralla. Lisäksi voidaan hakea useammalla
	// hakusanalla
	public void haeTeoksia(){

		System.out.println("------------");
		System.out.println("| Teoshaku |");
		System.out.println("------------");

		System.out.println("Valitse hakuehto:");
		String hakusyote="";

		while(!hakusyote.equals("7")){

			System.out.println("[ 1 ] Hae nimen perusteella");
			System.out.println("[ 2 ] Hae tekijän perusteella");
			System.out.println("[ 3 ] Hae luokan perusteella");
			System.out.println("[ 4 ] Hae tyypin perusteella");
			System.out.println("[ 5 ] Hae kaikkien tietojen perusteella");
			System.out.println("[ 6 ] Hae nimen perusteella (useita hakusanoja)");
			System.out.println("[ 7 ] Takaisin päävalikkoon");
			System.out.print("\n> ");

			hakusyote = this.lukija.nextLine();

			if(hakusyote.equals("1")){
				this.hakukriteeri = "nimi";
				this.haekirjoja();

			}else if(hakusyote.equals("2")){
				this.hakukriteeri = "tekija";
				this.haekirjoja();

			}else if(hakusyote.equals("3")){
				this.hakukriteeri = "luokka";
				this.haekirjoja();

			}else if(hakusyote.equals("4")){
				this.hakukriteeri = "tyyppi";
				this.haekirjoja();

			}else if(hakusyote.equals("5")){
				this.hakukriteeri = "kaikki";
				this.haekirjoja();

			}else if(hakusyote.equals("6")){
				this.hakukriteeri = "monisana";
				this.haeKirjojaMulti();

			}else if(!hakusyote.equals("7")){
				System.out.println("Tuntematon komento!");
			}
		}
	}

	// Apumetodi teoskappaleiden haulle.
	public void haekirjoja(){

		String hakusana="";

		if(this.hakukriteeri.equals("kaikki")){
			System.out.print("Syötä hakusana:\n> ");
		}else{
			System.out.print("Syötä haettavan kirjan "+this.hakukriteeri+":\n> ");
		}

		hakusana = this.lukija.nextLine();

		// Lisätään hakusana hakuhistoriaan
		if(hakusana.length()>0){
			this.lisaaHakuHistoriaan(hakusana);
		}

		// haetaan hakutulokset hashmappii
		if(this.hakukriteeri.equals("nimi") || this.hakukriteeri.equals("tekija") || this.hakukriteeri.equals("luokka") || this.hakukriteeri.equals("tyyppi") || this.hakukriteeri.equals("kaikki")){
			this.teoshakutulokset = kyselyt.haeTeoksia(this.hakukriteeri, hakusana);
		}

		if(this.teoshakutulokset.size() == 0){
			System.out.println("Mitään ei löytynyt!\n");
		}else{
			this.tulostaHaku();
		}
	}

	// Metodi, jossa teoskappaleita hetaan usemmalla hakusanalla
	public void haeKirjojaMulti(){

		String hakusanat = "";
		System.out.println("Syötä hakusanat välilyönnillä erotettuna:");
		hakusanat = lukija.nextLine();

		// Hakusanat arraylistiin
		this.monisanahaku = new ArrayList<String>();
		for(String hakusana : hakusanat.split(" ")){
    		this.monisanahaku.add(hakusana);
		}

		// Luodaan arraylistin hakusanoista query string
		String query = "SELECT isbn,nimi,tekija,vuosi,tyyppi,luokka,paino,kappaleid,hinta FROM keskus.teos NATURAL JOIN keskus.teoskappale WHERE nimi like '%"+this.monisanahaku.get(0)+"%'";

		for(int i = 1; i<this.monisanahaku.size();i++){
			query = query + " UNION ALL SELECT isbn,nimi,tekija,vuosi,tyyppi,luokka,paino,kappaleid,hinta FROM keskus.teos NATURAL JOIN keskus.teoskappale WHERE nimi like '%"+this.monisanahaku.get(i)+"%'";
		}

		// Viimeistelty query string
		String fullquery = "SELECT isbn,nimi,tekija,vuosi,tyyppi,luokka,paino,kappaleid,hinta FROM ("+query+") AS comb GROUP BY nimi,isbn,tekija,vuosi,tyyppi,luokka,paino,kappaleid,hinta ORDER BY COUNT(nimi) DESC,nimi DESC";

		// Haetaan teokset Hashmappiin
		this.teoshakutulokset = kyselyt.haeUseallaHakusanalla(fullquery);

		// Tulostetaan hakutulokset
		if(this.teoshakutulokset.size() == 0){
			System.out.println("Mitään ei löytynyt!\n");
		}else{
			this.tulostaHaku();
		}
	}

	// Metodi, joka vastaa hakutulosten tulostuksesta
	public void tulostaHaku(){

		System.out.println("\n-------------------------------------------------------------------------------------------------------------------------------------------------");
		System.out.format("%5s%15s%50s%30s%15s%15s%15s%10s%10s\n","","ISBN","Nimi","Tekija","Vuosi","Tyyppi","Luokka","Paino","Hinta");
		System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------");

		for(int i = 1 ; i< this.teoshakutulokset.size()+1; i++){

			String avain = String.valueOf(i);
			System.out.format("%5s",avain);

			for(int j = 1 ; j<this.teoshakutulokset.get(avain).size();j++){

				if(j==2){
					System.out.format("%50s",this.teoshakutulokset.get(avain).get(j));

				}else if(j==3){
					System.out.format("%30s",this.teoshakutulokset.get(avain).get(j));

				}else if(j==7 || j==8){
					System.out.format("%10s",this.teoshakutulokset.get(avain).get(j));

				}else{
					System.out.format("%15s",this.teoshakutulokset.get(avain).get(j));
				}
			}
			System.out.println();
		}

		System.out.println("\n");
		System.out.println("Valitse haluamasi kirja ostoskoriin tai palaa hakuvalikkoon");
		System.out.println("[ 1 ] Lisää kirja ostoskoriin");
		System.out.println("[ 2 ] Palaa hakuvalikkoon");

		String syote3 = "";

		while(!syote3.equals("2")){

			syote3 = lukija.nextLine();

			if(syote3.equals("1")){

				if(this.lisaaKoriin()){
					System.out.println("Teos lisätty ostoskoriin onnistuneesti!");
               return;
				}

			}else if(!syote3.equals("2")){
				System.out.println("Tuntematon komento!");
			}
		}
	}

	// Metodi, joka lisää teoksen ostoskoriin
	public boolean lisaaKoriin(){

		System.out.print("Syötä hakutuloksessa näkyvä rivin ensimmäinen numero:\n> ");
		String hakuid = lukija.nextLine();
		
		// Haetaan teoskappaleen id
		String id = this.teoshakutulokset.get(hakuid).get(0);
		
		// Tehdään muutokset tietokantaan
		this.kyselyt.lisaaVaraus(Integer.parseInt(id));

		return true;
	} 

	// Metodi tuotteiden tilaamiselle
	public void tilaaTuotteet(){

		System.out.println("Haluatko varmasti siirtyä tilauksen tekemiseen? (k/e)\n>");
		String vastaus = lukija.nextLine();

		// Kysytään valintaa niin kauan, että asiakas syöttää k:n tai e:n
		while(!(vastaus.equals("k") || vastaus.equals("K"))) {

			if(vastaus.equals("e") || vastaus.equals("E")){
				return;
			}else{
				System.out.println("Virheellinen komento! (k/e)\n>");
				vastaus = lukija.nextLine();
			}
		}

		// Luodaan uusi toimitus (metodi myös kertoo käyttäjälle toimituskulut)
		this.kyselyt.luoToimitus();

		System.out.println("Vahvistetaanko tilaus? (k/e)\n>");
		vastaus = lukija.nextLine();

		// Kysytään valintaa niin kauan, että asiakas syöttää k:n tai e:n
		while(!(vastaus.equals("k") || vastaus.equals("K"))) {

			if(vastaus.equals("e") || vastaus.equals("E")){
				return;
			}else{
				System.out.println("Virheellinen komento! (k/e)\n>");
				vastaus = lukija.nextLine();
			}
		}

		System.out.println("Tilaus suoritettu onnistuneesti!");

		// Asetetaan tuotteet tilatuiksi ja tilaus suoritetuksi
		this.kyselyt.teeTilaus(this.ostoskori);
	}

	// Metodi ostoskorin tyhjennykselle
	public void tyhjennaKori(){

		System.out.println("Haluatko varmasti tyhjentää ostoskorin? (k/e)\n>");
		String vastaus = lukija.nextLine();

		while(!(vastaus.equals("k") || vastaus.equals("K"))) {

			if(vastaus.equals("e") || vastaus.equals("E")){
				return;
			}else{
				System.out.println("Virheellinen komento! (k/e)\n>");
				vastaus = lukija.nextLine();
			}
		}

		// Suoritetaan muutokset tietokantatasolla
		this.kyselyt.tyhjennaKori(this.ostoskori);
	}

	// Metodi rahan lisäämiselle tilille
	public void lisaaRahaa(){

		System.out.println("-----------------------");
		System.out.println("| Lisää tilille rahaa |");
		System.out.println("-----------------------");

		System.out.print("Kuinka paljon haluat siirtää käyttötilille?\n>");

		try {
			double siirto = Double.parseDouble(lukija.nextLine()); 

			// Tehdään muutokset tietokantaan
			this.kyselyt.siirraRahaa(this.saldo+siirto);

			// Päivitetään asiakastiedot
			this.asiakastiedot = this.kyselyt.haeKayttajanTiedot(this.id);

		}catch (Exception e){
			System.out.println("Siirto ei onnistunut\n");
		}	
	}	
}