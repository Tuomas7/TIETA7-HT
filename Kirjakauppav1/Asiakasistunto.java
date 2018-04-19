
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
	
	
	// Konstrukotri, joka saa parametrina id:n, joka asetetaan istunnon id:ksi
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
		
		// Tyhjä lista, merkkijonoille, joilla on tehty hakuja 
		this.hakuhistoria = new ArrayList<>();

		// Ajatus ostoskorista: luodaan kantaan attribuutti ostoskorin merkkijonolle, joka ladataan kun käyttäjä kirjautuu sisään?
		this.ostoskori = new HashMap<String,ArrayList<String>>();

		this.saldo = Double.parseDouble(this.asiakastiedot.get("saldo"));
	}

	// Getteri asiakkaan nimelle
	public String haeNimi(){
		return this.nimi;
	}

	// privaatti metodi joka palauttaa merkkijonoesityksen puhelinnumerosta
	// Käytetään tulostaTiedot-metodissa. Palauttaa tyhjän merkkijonon, jos
	// arvo on "NULL", muuten palauttaa puhelinnumeron.
	private String haePuhelin(){
		String puhelin = this.asiakastiedot.get("puhelin");
		if(puhelin.equals("NULL")){
			return "";
		}else{
			return puhelin;
		}
	}

	// privaatti metodi joka palauttaa merkkijonoesityksen sähköpostiosoitteesta
	// Käytetään tulostaTiedot-metodissa. Palauttaa tyhjän merkkijonon, jos
	// arvo on "NULL", muuten palauttaa sähköpostiosoitteen.
	private String haeSahkoposti(){
		String sahkoposti = this.asiakastiedot.get("sahkoposti");
		if(sahkoposti.equals("NULL")){
			return "";
		}else{
			return sahkoposti;
		}
	}

	// privaatti metodi joka palauttaa merkkijonoesityksen salasanan
	// Käytetään tulostaTiedot-metodissa. Palauttaa salansanan pituisen
	// merkkijonon tähtiä (tietoturva-aspekti).
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
			System.out.println("\n--------------------------------------------------------------------------------");
			System.out.format("%30s%30s%10s%10s\n","Nimi","Tekija","Paino","Hinta");
			System.out.println("--------------------------------------------------------------------------------");

			for(int i = 1 ; i < this.ostoskori.size()+1; i++){
					
				System.out.format("%30s",this.ostoskori.get(String.valueOf(i)).get(2));
				System.out.format("%30s",this.ostoskori.get(String.valueOf(i)).get(3));
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

	public void haeTeoksia(){

		System.out.println("------------");
		System.out.println("| Teoshaku |");
		System.out.println("------------");
	
		System.out.println("Valitse hakuehto:");
		String hakusyote="";
		while(!hakusyote.equals("6")){

			System.out.println("[ 1 ] Hae nimen perusteella");
			System.out.println("[ 2 ] Hae tekijän perusteella");
			System.out.println("[ 3 ] Hae luokan perusteella");
			System.out.println("[ 4 ] Hae tyypin perusteella");
			System.out.println("[ 5 ] Hae kaikkien tietojen perusteella");
			System.out.println("[ 6 ] Takaisin päävalikkoon");

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

			}else if(!hakusyote.equals("6")){
				System.out.println("Tuntematon komento!");
			}
			
		}
		
	}

	public void haekirjoja(){
		
		String hakusana="";
		if(this.hakukriteeri.equals("kaikki")){
			System.out.print("Syötä hakusana:\n> ");
		}else{
			System.out.print("Syötä haettavan kirjan "+this.hakukriteeri+":\n> ");
		}
		

		hakusana = this.lukija.nextLine();

		if(hakusana.length()>0){
			this.lisaaHakuHistoriaan(hakusana);
		}

		if(this.hakukriteeri.equals("nimi") || this.hakukriteeri.equals("tekija") || this.hakukriteeri.equals("luokka") || this.hakukriteeri.equals("tyyppi") || this.hakukriteeri.equals("kaikki")){
			this.teoshakutulokset = kyselyt.haeTeoksia(this.hakukriteeri, hakusana);

		}

		if(this.teoshakutulokset.size() == 0){
			System.out.println("Mitään ei löytynyt!\n");
		}else{
			this.tulostaHaku();
		}
		
	}

	public void tulostaHaku(){
		System.out.println("\n-------------------------------------------------------------------------------------------------------------------------------------------------");
      	System.out.format("%5s%15s%30s%30s%15s%15s%15s%10s%10s\n","","ISBN","Nimi","Tekija","Vuosi","Tyyppi","Luokka","Paino","Hinta");
      	System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------");

      	for(int i = 1 ; i< this.teoshakutulokset.size()+1; i++){
      		String avain = String.valueOf(i);
      		//System.out.println(haut.get(avain));
      		System.out.format("%5s",avain);
      		for(int j = 1 ; j<this.teoshakutulokset.get(avain).size();j++){
      			if(j==2 || j==3){
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

      	System.out.println("Valitse haluamasi kirja ostoskoriin tai tee uusi haku");

      	System.out.println("[ 1 ] Lisaa kirja ostoskoriin");
		System.out.println("[ 2 ] Tee uusi haku");

      	String syote3 = "";
      	while(!syote3.equals("3")){
      		syote3 = lukija.nextLine();
      		if(syote3.equals("1")){
      			if(this.lisaaKoriin()){
      				System.out.println("Teos lisätty ostoskoriin onnistuneesti!");
      				break;
      			}
      		}else if(syote3.equals("2")){
      			break;
      		}
      		System.out.println("Tuntematon komento!");
      	}

	}

	public boolean lisaaKoriin(){


		System.out.print("Syötä hakutuloksessa näkyvä rivin ensimmäinen numero:\n> ");
		String hakuid = lukija.nextLine();
		
		String id = this.teoshakutulokset.get(hakuid).get(0);
		System.out.println(id);
		//this.kyselyt.lisaaVaraus(Integer.parseInt(hakuid));
		this.kyselyt.lisaaVaraus(Integer.parseInt(id));


		return true;

		
		//System.out.println(this.teoshakutulokset.get(hakuid));
		//this.ostoskori.put(hakuid,this.teoshakutulokset.get(hakuid));
		//return true;
	} 

	// Metodi tuotteiden tilaamiselle
	public void tilaaTuotteet(){

		//System.out.println("Ostoskorin sisältö:");
		//this.tulostaOstoskori();

		String vastaus = "";
		System.out.println("Haluatko varmasti siirtyä tilauksen tekemiseen? (k/e)\n>");

		while(!(vastaus.equals("k") || vastaus.equals("e") || vastaus.equals("K")|| vastaus.equals("E"))){
			vastaus = lukija.nextLine();
			if(vastaus.equals("k") || vastaus.equals("K")){
				break;
			}else if(vastaus.equals("e") || vastaus.equals("E")){
				return;
			}else{
				System.out.println("Virheellinen komento!");
			}
		}

        // Kirjojen kokonaispaino
        int kokoPaino = 0;
            
        System.out.println("Tilataan seuraavat kirjat:");
        System.out.println();

        // Tilauserien lukumäärä
         int eraLkm = 1;
         
         // Lasketaan, moneenko erään tilaus täytyy jakaa (yksi erä on maksimissaan 2000 grammaa)
         while (kokoPaino > 2000) {
            eraLkm++;
            kokoPaino -= 2000;
         }
         
         if (eraLkm > 1) {
            System.out.println();
            System.out.println("Tilaus jaetaan painon vuoksi " + eraLkm + " erään.");
         }
         
         // Postikulujen summa
         float postikulut = 0;
         
         // Jokainen 2000 grammaa painava erä maksaa 14 euroa
         postikulut += (eraLkm-1)*14.00;
         
         // Lasketaan yli menevän osan postikulut
         if (kokoPaino <= 50) {
            postikulut += 1.40;
         }
         else if (kokoPaino <= 100) {
            postikulut += 2.10;
         }
         else if (kokoPaino <= 250) {
            postikulut += 2.80;
         }
         else if (kokoPaino <= 500) {
            postikulut += 5.60;
         }
         else if (kokoPaino <= 1000) {
            postikulut += 8.40;
         }
         else {
            postikulut += 14.00;
         }
         
         System.out.println();
         System.out.println("Tilauksen postikulut ovat " + postikulut + " euroa. Vahvistetaanko tilaus? (k/e)");

        char valinta = this.lukija.next().charAt(0);
        // Kysytään valintaa niin kauan, että asiakas syöttää k:n tai e:n
        while (valinta != 'k' && valinta != 'K' && valinta != 'e' && valinta != 'E') {
        	System.out.println("Virheellinen valinta. Syötä joko k tai e:");
        	valinta = this.lukija.next().charAt(0);
       	}

       	if(valinta=='k' || valinta == 'K'){
       		this.kyselyt.teeTilaus(this.ostoskori);
       	}
       	
         
	}

	public void tyhjennaKori(){
		System.out.println("Haluatko varmasti tyhjentää ostoskorin? (k/e)\n>");
		String vastaus = "";
		while(!(vastaus.equals("k") || vastaus.equals("e") || vastaus.equals("K")|| vastaus.equals("E"))){
			vastaus = lukija.nextLine();
			if(vastaus.equals("k") || vastaus.equals("K")){
				break;
			}else if(vastaus.equals("e") || vastaus.equals("E")){
				return;
			}else{
				System.out.println("Virheellinen komento!");
			}
		}
		this.kyselyt.tyhjennaKori(this.ostoskori);

	}

	public void lisaaRahaa(){
		System.out.println("-----------------------");
		System.out.println("| Lisää tilille rahaa |");
		System.out.println("-----------------------");

		System.out.print("Kuinka paljon haluat siirtää käyttötilille?\n>");
		try {
			double siirto = Double.parseDouble(lukija.nextLine()); 
			
			this.kyselyt.siirraRahaa(this.saldo+siirto);
			
			// Päivitetään asiakastiedot
			this.asiakastiedot = this.kyselyt.haeKayttajanTiedot(this.id);
			
		}catch (Exception e){
			System.out.println("Siirto ei onnistunut\n");
		}
		
	}



	
}