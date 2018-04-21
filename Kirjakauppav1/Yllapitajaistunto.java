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
		System.out.println("dfds");
		this.kayttajaID = id;
		this.lukija = new Scanner(System.in);
		//System.out.println(this.kayttajaID);
		this.kyselyt = new Yllapitokyselyt(this.kayttajaID);
		this.divariID = this.kyselyt.haeDivari();
		this.divariKeskusKannassa = this.kyselyt.tarkastaDivari();
	}


	public void tulosta(){
		System.out.println(this.divariID);
		System.out.println(this.divariKeskusKannassa);
	}

	public boolean onkoKannassa(){
		return this.divariKeskusKannassa;
	}

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
/*
 
   
     
   
      try {

         yhteys.setAutoCommit(false);
      
         // Luodaan tapahtumaolio
         Statement stmt = yhteys.createStatement();
      
         // Lisätään teoksen tiedot
         stmt.executeUpdate("INSERT INTO D1.Teos VALUES ('" + isbn + "', '" + nimi + "', '"
            + tekija + "', '" + vuosi + "', '" + tyyppi + "', '" + luokka + "', '" + paino + "')");
      
         // Haetaan suurin kappaleID tietokannassa ja lisätään siihen 1
         ResultSet rset = stmt.executeQuery("SELECT MAX(KappaleID) FROM D1.teosKappale");
         rset.next();
         int id = rset.getInt(1) + 1;
      
         // Lisätään yksittäisen kappaleen tiedot
         stmt.executeUpdate("INSERT INTO D1.TeosKappale VALUES ('" + id + "', '" + isbn + "', '"
            + hinta + "', '" + ostohinta + "', null, 'Vapaa')");
      
         System.out.println("Tiedot lisätty onnnistuneesti!");
         
         // Sitoudutaan muutoksiin
         yhteys.commit();
         yhteys.setAutoCommit(true);
         
         // Suljetaan tapahtumaolio
         stmt.close();
         
      } catch (SQLException poikkeus) {
         
         System.out.println("Tietojen lisäys epäonnistui: " + poikkeus.getMessage());  
         
         try {
            
            // Perutaan tapahtuma
            yhteys.rollback();
            
         } catch (SQLException poikkeus2) {
            System.out.println("Tapahtuman peruutus epäonnistui: " + poikkeus2.getMessage()); 
         }
      }
	}
*/