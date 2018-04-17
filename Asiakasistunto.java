
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;

public class Asiakasistunto{

	private Asiakaskyselyt kyselyt;
	private int id;
	private String nimi;
	private HashMap<String,String> asiakastiedot;
	private ArrayList<String> hakuhistoria;
	private double saldo;

	// Ostoskorista olio?
	private HashMap<String,String> ostoskori;
	private String hakukriteeri;
	private String hakusana;
	private HashMap<String,ArrayList<String>> teoshakutulokset;
	private Scanner lukija;
	
	

	public Asiakasistunto(int id){

		this.lukija = new Scanner(System.in);
		this.id=id;
		this.kyselyt = new Asiakaskyselyt();
		this.asiakastiedot = this.kyselyt.haeKayttajanTiedot(this.id);
		this.nimi = this.asiakastiedot.get("etunimi")+" "+this.asiakastiedot.get("sukunimi");
		// Tyhjä lista, merkkijonoille, joilla on tehty hakuja 
		this.hakuhistoria = new ArrayList<>();

		// Ajatus ostoskorista: luodaan kantaan attribuutti ostoskorin merkkijonolle, joka ladataan kun käyttäjä kirjautuu sisään?
		this.ostoskori = new HashMap<String,String>();
		this.saldo = Double.parseDouble(this.asiakastiedot.get("saldo"));
	}


	public String haeNimi(){
		return this.nimi;
	}

	public int haeID(){
		return this.id;
	}

	public String haeOsoite(){
		return this.asiakastiedot.get("osoite");
	}

	public String haePuhelin(){
		String puhelin = this.asiakastiedot.get("puhelin");
		if(puhelin.equals("NULL")){
			return "";
		}else{
			return puhelin;
		}
	}

	public String haeSahkoposti(){
		String sahkoposti = this.asiakastiedot.get("sahkoposti");
		if(sahkoposti.equals("NULL")){
			return "";
		}else{
			return sahkoposti;
		}
	}

	public ArrayList<String> haeHistoria(){
		return this.hakuhistoria;
	}

	public HashMap<String,String> haeOstoskori(){
		return this.ostoskori;
	}

	public double haeSaldo(){
		return this.saldo;
	}

	public String haeTunnus(){
		return this.asiakastiedot.get("tunnus");
	}

	public String haeSalasana(){
		String maskattu = "";
		for(int i = 0; i< this.asiakastiedot.get("salasana").length();i++){
			maskattu = maskattu + "*";
		}
		return maskattu;
	}


	public void tulostaTiedot(){
		System.out.println("\nProfiilin tiedot:\n");
		System.out.println("Nimi: "+this.haeNimi());
		System.out.println("Osoite: "+this.haeOsoite());
		System.out.println("Puhelin: "+this.haePuhelin());
		System.out.println("Sähköposti: "+this.haeSahkoposti());
		System.out.println("Saldo: "+ this.haeSaldo());
		System.out.println("\nKäyttäjätunnus: "+this.haeTunnus());
		System.out.println("Salasana: "+this.haeSalasana());
	}

	public void tulostaOstoskori(){
		if(this.ostoskori.size() == 0){
			System.out.println("Ostoskori on tyhjä.");
		}else{
			System.out.println("Ei tyhjä.");
		}
	}

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

	public void lisaaHakuHistoriaan(String haku){
		this.hakuhistoria.add(haku);
	}

	public void haeTeoksia(){
		System.out.println("Kirjahaku");
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
		System.out.println("\n--------------------------------------------------------------------------------------------------------------------------------------------------------------");
      	System.out.format("%5s%15s%15s%15s%15s%15s%15s%15s%15s%15s%15s\n","","ISBN","Kappaleid","Nimi","Tekija","Vuosi","Tyyppi","Luokka","Paino","Hinta","Vapaana");
      	System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------");

      	for(int i = 1 ; i< this.teoshakutulokset.size()+1; i++){
      		String avain = String.valueOf(i);
      		//System.out.println(haut.get(avain));
      		System.out.format("%5s",avain);
      		for(int j = 0 ; j<this.teoshakutulokset.get(avain).size();j++){

      			System.out.format("%15s",this.teoshakutulokset.get(avain).get(j));

      		}
      		System.out.println();

      		
      	}
      	System.out.println("\n");
	}



	
}