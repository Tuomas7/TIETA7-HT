import java.sql.*;
import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Stack;

public class Asiakaskyselyt{

	// Attribuutteina prepared statementtien merkkijonoesitykset
	private Yhteys yhteys;
	private Connection connection;
	private ResultSet resultset;
	
	private String moodi;

	private PreparedStatement preparedStatement;

	// Prepared statement merkkijonot
	private String haeKayttajaTiedotStatement;
	private String haeAsiakasTiedotStatement;
	private String kirjautumisStatement;
	private String luoIDStatement;
	private String tarkastaNimiStatement;
	private String insertKayttaja;
	private String insertAsiakas;
	private String haeTeosNimet;
	private String haeTeosTekijat;
	private String haeTeosLuokat;
	private String haeTeosTyypit;
	private String haeTeosKaikki;
	private String keskusdivariVaraus;
	private String haeDivariID;
	private String yksittainendivariVaraus;
	private String lisaaVaraus;
	private String haeVaraukset;
   private String lisaaToimitus;
   private String poistaToimitus;
   private String laskePostikulut;
   private String haeSuurinMaksuluokka;
   private String haeOikeaMaksuluokka;
	//private String tilaaTuotteet;
	private String tilausSuoritus;
	private String tilausPeruutus;
	private String myynti;
	private String vapautus;
	private String lisaaRahaa;


	// Tietorakenteet, joihin tallennetaan kyselyiden tuloksia luokan sisällä ja joita luokan metodit palauttavat
	private HashMap<String, String> kyselyMap;
	private ArrayList<String> kyselyLista;
	private int kyselyLuku;
	private boolean kyselybool;
	private int returnInt;
	private HashMap<String,ArrayList<String>> teoskysely;

	// Aputietorakenteita, näihin asetetaan luokan metodeihin tulleita parametrien arvoja
	private int asiakasID;
	private String input1;
	private String input2;
	private String input3;
	private int paramInt;
	private HashMap<String,String> mapInput;
	private double inputDouble;


	 

	// Kontruktori, asetetaan prepared statementtien rungot
	public Asiakaskyselyt(){
		this.asiakasID = 0;
		this.yhteys = new Yhteys("localhost", 5432, "postgres", "postgres", "Salasana");
		this.connection = null;
		this.resultset = null;
		this.preparedStatement = null;
		
		this.haeKayttajaTiedotStatement = "SELECT nimi,kayttajaid,salasana,rooli FROM keskus.kayttaja WHERE kayttajaid = ?";
		this.haeAsiakasTiedotStatement = "SELECT etunimi,sukunimi,osoite,sahkoposti,puhelin,saldo FROM keskus.asiakas WHERE asiakasid = ?";
		this.kirjautumisStatement = "SELECT nimi,kayttajaid,rooli FROM keskus.kayttaja WHERE salasana =? AND rooli = ?";
		
		// Rekisteröinnin statementit
		this.luoIDStatement = "SELECT kayttajaid FROM keskus.kayttaja";
		this.tarkastaNimiStatement = "SELECT nimi FROM keskus.kayttaja";
		this.insertKayttaja = "INSERT INTO keskus.kayttaja VALUES (?,?,?,?)";
		this.insertAsiakas = "INSERT INTO keskus.asiakas VALUES (?,?,?,?,?,?)";

		// Haun statementit
		this.haeTeosNimet = "SELECT isbn,nimi,tekija,vuosi,tyyppi,luokka,paino,kappaleid,hinta FROM keskus.teos NATURAL JOIN keskus.teoskappale WHERE nimi LIKE ? AND vapaus = 'Vapaa'";
		this.haeTeosTekijat = "SELECT isbn,nimi,tekija,vuosi,tyyppi,luokka,paino,kappaleid,hinta,vapaus FROM keskus.teos NATURAL JOIN keskus.teoskappale WHERE tekija LIKE ? AND vapaus = 'Vapaa'";
		this.haeTeosLuokat = "SELECT isbn,nimi,tekija,vuosi,tyyppi,luokka,paino,kappaleid,hinta,vapaus FROM keskus.teos NATURAL JOIN keskus.teoskappale WHERE luokka LIKE ? AND vapaus = 'Vapaa'";
		this.haeTeosTyypit = "SELECT isbn,nimi,tekija,vuosi,tyyppi,luokka,paino,kappaleid,hinta,vapaus FROM keskus.teos NATURAL JOIN keskus.teoskappale WHERE tyyppi LIKE ? AND vapaus = 'Vapaa'";
		this.haeTeosKaikki = "SELECT isbn,nimi,tekija,vuosi,tyyppi,luokka,paino,kappaleid,hinta,vapaus FROM keskus.teos NATURAL JOIN keskus.teoskappale WHERE nimi LIKE ? OR tekija LIKE ? OR luokka LIKE ? OR tyyppi LIKE ? AND vapaus = 'Vapaa'";

		// Varaustapahtuman statementit
		this.keskusdivariVaraus = "UPDATE keskus.teoskappale SET Vapaus='Varattu' WHERE KappaleID=?";
		this.haeDivariID = "SELECT DivariID FROM keskus.sijainti WHERE KappaleID=?";
		
		this.lisaaVaraus = "INSERT INTO keskus.tilaus VALUES (?, ?, ?, ?)";
      this.haeVaraukset = "SELECT kappaleid, DivariID, isbn, hinta, nimi, tekija, vuosi, tyyppi, luokka, paino FROM keskus.tilaus NATURAL JOIN keskus.teoskappale NATURAL JOIN keskus.teos WHERE asiakasid = ? AND tila = 'Kaynnissa'";

      this.lisaaToimitus = "INSERT INTO keskus.toimitus VALUES (?, ?, ?, ?)";
      this.poistaToimitus = "DELETE FROM keskus.toimitus WHERE AsiakasID=?";
      this.laskePostikulut = "SELECT SUM(Maksu) FROM keskus.Toimitus NATURAL JOIN keskus.Postikulut WHERE AsiakasID=?";

      this.haeSuurinMaksuluokka = "SELECT MAX(Maksuluokka) FROM keskus.Postikulut";
      this.haeOikeaMaksuluokka = "SELECT MIN(Maksuluokka) FROM keskus.Postikulut WHERE Maksuluokka >= ?";
      
		// Tilaus statement
		//his.tilaaTuotteet = "SELECT Teos.Nimi, Teos.Paino, TeosKappale.KappaleID FROM keskus.Tilaus NATURAL JOIN keskus.TeosKappale NATURAL JOIN keskus.Teos WHERE Tilaus.Tila='Kaynnissa' AND Tilaus.AsiakasID=?";

		// Asetetaan tilaus suoritetuksi/peruutetuksi
        this.tilausSuoritus = "UPDATE keskus.Tilaus SET Tila='Suoritettu' WHERE KappaleID=?";
        //this.tilausPeruutus = "UPDATE keskus.Tilaus SET Tila='Peruutettu' WHERE KappaleID=?";
        this.tilausPeruutus = "DELETE FROM keskus.tilaus WHERE tila ='Kaynnissa' AND asiakasid = ?";
               
        // Asetetaan keskusdivarissa oleva kappale myydyksi/vapaaksi
        this.myynti = "UPDATE keskus.TeosKappale SET Vapaus='Myyty', MyyntiPvm=now() WHERE KappaleID=?";
        this.vapautus = "UPDATE keskus.TeosKappale SET Vapaus='Vapaa' WHERE KappaleID=?";

        this.lisaaRahaa = "UPDATE keskus.asiakas SET Saldo=? WHERE asiakasid =?";


	}
	public void asetaID(int id){
		this.asiakasID = id;
	}

	public boolean tunnusVarattu(String tunnus){
		this.kyselybool = true;
		this.input1 = tunnus;
		this.moodi = "tarkastatunnus";
		this.yhteysHandleri();

		return this.kyselybool;
	}


	// Metodi, jota kutsutaan käyttöliittymästä. Testi
	public HashMap<String,String> haeKayttajanTiedot(int id){
		
		this.paramInt = id;
		this.moodi = "haetiedot";
		this.kyselyMap = new HashMap<String,String>();
		
		this.yhteysHandleri();

		return this.kyselyMap;
	}

	// Luo käyttäjälle uuden ID:n randomilla, tarkastaa ettei löydy kannasta
	public void luoID(){
		this.moodi ="luoid";
		this.yhteysHandleri();

	}

	// Kirjautumisen yhteydessä, tarkastaa, että tunnus ja salasana täsmäävät kannassa
	public int tarkastaKirjautuminen(String tunnus, String salasana,String rooli){

		this.kyselybool = false;
		this.input1 = tunnus;
		this.input2 = salasana;
		this.input3 = rooli;
		this.moodi = "tarkastakirjautuminen";
		//this.kyselyLista = new ArrayList<>();
		
		this.yhteysHandleri();

		// Jos tunnus ja salasana täsmäsivät, palautetaan niitä vastaava käyttäjäid
		if(this.kyselybool == true){
			return this.returnInt;
		}
		// Jos ei täsmännyt, palautetaan 0
		return 0;
		
	}

	public boolean lisaaKayttaja(HashMap<String,String> kayttajatiedot){
		
		this.kyselybool = false;
		
		// Tallennetaan parametrina saatu tietorakenne olio-attribuuttiin, jolloin se näkyy muille metodeille.
		this.mapInput = kayttajatiedot;
		// Luodaan käyttäjälle uusi id
		int id = 0;
		while(id == 0){
			// asettaa olio-attribuutin returnInt arvon
			this.luoID();
			id = this.returnInt;
		}

		// luoID() - kutsu asettaa moodin, joten se täytyy asettaa vasta tässä vaiheessa
		this.moodi = "lisaakayttaja";
		
		this.yhteysHandleri();

		return this.kyselybool;

	}

	public HashMap<String,ArrayList<String>> haeTeoksia(String hakuehto, String hakusana){
		this.input1 = hakuehto;
		this.input2 = hakusana;
		this.moodi = "haeteoksia";

		this.yhteysHandleri();

		return this.teoskysely;

	}

	public HashMap<String,ArrayList<String>> haeVaraukset(int id){

		this.teoskysely = new HashMap<String,ArrayList<String>>();
		this.asiakasID = asiakasID;
		this.moodi = "haevaraukset";
		this.yhteysHandleri();
		return teoskysely;
	}

	public void lisaaVaraus(int ID){
		this.paramInt = ID;
		//this.asiakasID = asiakasID;
		this.moodi = "lisaavaraus";
		this.yhteysHandleri();
		
	}

   public void luoToimitus() {
      this.moodi = "luotoimitus";
      this.yhteysHandleri();
   }
   
	public void teeTilaus(HashMap<String,ArrayList<String>> ostoskori){
		this.teoskysely = ostoskori;
		this.moodi = "teetilaus";
		this.yhteysHandleri();
	}

	public void siirraRahaa(double summa){

		this.inputDouble = summa;
		this.moodi="lisaarahaa";
		this.yhteysHandleri();
	}

	public void tyhjennaKori(HashMap<String,ArrayList<String>> ostoskori){

		this.teoskysely = ostoskori;
		this.moodi = "perutilaus";
		this.yhteysHandleri();
	}

	// Yhteinen "yhteyshandleri" kyselyille, joka hoitaa yhteyksien avaamiset ja sulkemiset
	// kyselyn tulokset tallennetaan oliomuuttujiin (HashMap, ArrayList, int)
	private void yhteysHandleri(){

		
		try {
         
			this.connection = this.yhteys.uusiYhteys();
			this.connection.setAutoCommit(false);
         
			// tässä kohtaa kutsutaan privaattia kyselyfunktiota
			if(this.moodi.equals("haetiedot")){
				this.kayttajanTiedot();

			}else if(this.moodi.equals("tarkastatunnus")){
				this.tunnusVarattu();

			}else if(this.moodi.equals("luoid")){
				this.id();

			}else if(this.moodi.equals("tarkastakirjautuminen")){
				this.kirjautuminen();

			}else if(this.moodi.equals("lisaakayttaja")){
				this.lisaaKayttajaTiedot();

			}else if(this.moodi.equals("haeteoksia")){
				this.teoshaku();

			}else if(this.moodi.equals("lisaavaraus")){
				this.varauksenLisays();
            
			}else if(this.moodi.equals("luotoimitus")){
				this.toimituksenLuonti();

			}else if(this.moodi.equals("haevaraukset")){
				this.varauksienHaku();

			}else if(this.moodi.equals("teetilaus")){
				this.tilauksenTeko();

			}else if(this.moodi.equals("lisaarahaa")){
				this.rahanLisays();

			}else if(this.moodi.equals("perutilaus")){
				this.tilauksenPeruutus();
			}
	
         // Sitoudutaan muutoksiin
         this.connection.commit();
         this.connection.setAutoCommit(true);

		} catch(SQLException poikkeus) {
         
        	System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());  

        	try {
            
	         // Perutaan tapahtuma
	         this.connection.rollback();
            
         } catch (SQLException poikkeus2) {
            System.out.println("Tapahtuman peruutus epäonnistui: " + poikkeus2.getMessage()); 
         }
         
      } finally {
         
         try {
            
            // Vapautetaan resurssit
    		   if (this.resultset != null) {
            	this.resultset.close();
    		   }
    		   if (this.preparedStatement != null) {
            	this.preparedStatement.close();
    		   }
    		   if (this.connection != null) {
            	this.connection.close();
    		   }
         } catch (SQLException poikkeus) {
            System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());
         }
		}
	}

	// Hakee käyttäjän tiedot kannasta
	private void kayttajanTiedot() throws SQLException{

		// Haetaan ensin tiedot kayttaja-relaatiosta
		this.preparedStatement = this.connection.prepareStatement(this.haeKayttajaTiedotStatement);
		this.preparedStatement.setInt(1,this.paramInt);
		this.resultset = this.preparedStatement.executeQuery();

		// Lisätään tiedot HashMappiin
		while(this.resultset.next()){
			this.kyselyMap.put("tunnus",this.resultset.getString("nimi"));
			this.kyselyMap.put("salasana",this.resultset.getString("salasana"));
			this.kyselyMap.put("rooli",this.resultset.getString("rooli"));

		}

		// Haetaan myös tiedot asiakas-relaatiosta
		this.preparedStatement = this.connection.prepareStatement(this.haeAsiakasTiedotStatement);
		this.preparedStatement.setInt(1,this.paramInt);
		this.resultset = this.preparedStatement.executeQuery();

		while(this.resultset.next()){
			this.kyselyMap.put("etunimi",this.resultset.getString("etunimi"));
			this.kyselyMap.put("sukunimi",this.resultset.getString("sukunimi"));
			this.kyselyMap.put("osoite",this.resultset.getString("osoite"));
			this.kyselyMap.put("puhelin",this.resultset.getString("puhelin"));
			this.kyselyMap.put("sahkoposti", this.resultset.getString("sahkoposti"));
			this.kyselyMap.put("saldo",this.resultset.getString("saldo"));
		}

	}
	private void id() throws SQLException{

		// Luodaan satunnainen luku välillä 1-1000
		Random rand = new Random();
		this.returnInt = rand.nextInt(1000) + 1;
		
		this.preparedStatement = this.connection.prepareStatement(this.luoIDStatement);
		this.resultset = this.preparedStatement.executeQuery();

		// Käydään läpi kaikki käyttäjä-relaation käyttäjäID:t.
		while(this.resultset.next()){
			
			// Jos luotu ID löytyy jo kannasta asetetaan luvun arvoksi 0
			if(this.resultset.getInt("kayttajaid") == this.returnInt){
				this.returnInt = 0;
			}
		}
	}

	private void kirjautuminen() throws SQLException{

		this.preparedStatement = this.connection.prepareStatement(this.kirjautumisStatement);
		this.preparedStatement.setString(1,this.input2);
		this.preparedStatement.setString(2,this.input3);
		this.resultset = this.preparedStatement.executeQuery();

		while(this.resultset.next()){
			if(this.resultset.getString("nimi").equals(this.input1)){
				this.returnInt = resultset.getInt("kayttajaid");
				this.kyselybool = true;
			}
		}

	}

	private void lisaaKayttajaTiedot() throws SQLException{
		this.preparedStatement = this.connection.prepareStatement(this.insertKayttaja);
		// ID on tällä hetkellä tallennettuna returnInt-attribuuttiin
		System.out.println(this.mapInput);
		this.preparedStatement.setInt(1,this.returnInt);
		this.preparedStatement.setString(2,this.mapInput.get("tunnus"));
		this.preparedStatement.setString(3,this.mapInput.get("salasana"));
		this.preparedStatement.setString(4,this.mapInput.get("rooli"));

		this.preparedStatement.executeUpdate();


		this.preparedStatement = this.connection.prepareStatement(this.insertAsiakas);
		// ID on tällä hetkellä tallennettuna returnInt-attribuuttiin
		this.preparedStatement.setInt(1,this.returnInt);
		this.preparedStatement.setString(2,this.mapInput.get("etunimi"));
		this.preparedStatement.setString(3,this.mapInput.get("sukunimi"));
		this.preparedStatement.setString(4,this.mapInput.get("osoite"));
		this.preparedStatement.setString(5,this.mapInput.get("sahkoposti"));
		this.preparedStatement.setString(6,this.mapInput.get("puhelin"));

		this.preparedStatement.executeUpdate();

		this.kyselybool = true;

	}

	private void teoshaku() throws SQLException{
		if(this.input1.equals("nimi")){
			this.preparedStatement = this.connection.prepareStatement(this.haeTeosNimet);
		}else if(this.input1.equals("tekija")){
			this.preparedStatement = this.connection.prepareStatement(this.haeTeosTekijat);
		}else if(this.input1.equals("luokka")){
			this.preparedStatement = this.connection.prepareStatement(this.haeTeosLuokat);
		}else if(this.input1.equals("tyyppi")){
			this.preparedStatement = this.connection.prepareStatement(this.haeTeosTyypit);
		}else if(this.input1.equals("kaikki")){
			this.preparedStatement = this.connection.prepareStatement(this.haeTeosKaikki);
		}

		if(this.input1.equals("kaikki")){
			this.preparedStatement.setString(1,'%'+this.input2+'%');
			this.preparedStatement.setString(2,'%'+this.input2+'%');
			this.preparedStatement.setString(3,'%'+this.input2+'%');
			this.preparedStatement.setString(4,'%'+this.input2+'%');

		}else{
			this.preparedStatement.setString(1,'%'+this.input2+'%');
		}
		

		this.resultset = this.preparedStatement.executeQuery();

		this.teoskysely = new HashMap<String,ArrayList<String>>();

		int kyselynumero = 1;

		while(this.resultset.next()){

			String indeksi = String.valueOf(kyselynumero);
			// Lisätään teoksen kaikki tiedot merkkijonoina ArrayListiin
			ArrayList<String> teostiedot = new ArrayList<>();

			teostiedot.add(this.resultset.getString("kappaleid"));
			teostiedot.add(this.resultset.getString("isbn"));
			teostiedot.add(this.resultset.getString("nimi"));
			teostiedot.add(this.resultset.getString("tekija"));
			teostiedot.add(this.resultset.getString("vuosi"));
			teostiedot.add(this.resultset.getString("tyyppi"));
			teostiedot.add(this.resultset.getString("luokka"));
			teostiedot.add(this.resultset.getString("paino"));
			teostiedot.add(this.resultset.getString("hinta"));
			
			
			// Lisätään tiedot HashMappiin, avaimena kyselynumero, arvona arraylist
			this.teoskysely.put(indeksi,teostiedot);
			kyselynumero = kyselynumero +1;
			
		}

	}

	public void tunnusVarattu() throws SQLException{

		this.preparedStatement = this.connection.prepareStatement(this.tarkastaNimiStatement);
		this.resultset = this.preparedStatement.executeQuery();


		while(this.resultset.next()){
			if(this.resultset.getString("nimi").equals(this.input1)){
				this.kyselybool = false;
			}
		}
	}

   // Lisätään uusi varaus asiakkaan ostoskoriin
	public void varauksenLisays() throws SQLException{

		// Asetetaan keskusdivarissa oleva kappale varatuksi
		this.preparedStatement = this.connection.prepareStatement(this.keskusdivariVaraus);
		this.preparedStatement.setInt(1,this.paramInt);
		this.preparedStatement.executeUpdate();

		// Haetaan kappaleen omistavan alkuperäisen divarin ID
		this.preparedStatement = this.connection.prepareStatement(this.haeDivariID);
		this.preparedStatement.setInt(1,this.paramInt);
		this.resultset = this.preparedStatement.executeQuery();
      this.resultset.next();
      
      int divariID = this.resultset.getInt("DivariID");
      
		// Jos divari ei kuulu keskustietokantaan, asetetaan kappale varatuksi myös siellä
		if (divariID != 2 && divariID != 4) {
			this.yksittainendivariVaraus = "UPDATE D"+divariID+".TeosKappale SET Vapaus='Varattu' WHERE KappaleID=?";
         this.preparedStatement = this.connection.prepareStatement(this.yksittainendivariVaraus);
         this.preparedStatement.setInt(1,this.paramInt);
			this.preparedStatement.executeUpdate();
      }
      
      // Luodaan uusi käynnissä oleva tilaus (vastaa kappaleen siirtämistä "ostoskoriin")
      this.preparedStatement = this.connection.prepareStatement(this.lisaaVaraus);
      this.preparedStatement.setInt(1,divariID);
      this.preparedStatement.setInt(2,this.asiakasID);
      this.preparedStatement.setInt(3,this.paramInt);
      this.preparedStatement.setString(4,"Kaynnissa");
      this.preparedStatement.executeUpdate();
	}

   // Luodaan uusi toimitus asiakkaan ostoskorissa olevista kappaleista
	public void toimituksenLuonti() throws SQLException{
   
      // Aloitetaan puhtaalta pöydältä poistamalla mahdolliset vanhat toimitustiedot
		this.preparedStatement = this.connection.prepareStatement(this.poistaToimitus);
		this.preparedStatement.setInt(1,this.asiakasID);
		this.preparedStatement.executeUpdate();
   
      // Haetaan suurin maksuluokka (sama kuin täyteen pakatun toimituspaketin paino grammoina)
		this.preparedStatement = this.connection.prepareStatement(this.haeSuurinMaksuluokka);
		this.resultset = this.preparedStatement.executeQuery();
      this.resultset.next();
      
      int maksuLuokka = this.resultset.getInt(1);
      
      // Haetaan asiakkaan tekemät varaukset
		this.preparedStatement = this.connection.prepareStatement(this.haeVaraukset);
		this.preparedStatement.setInt(1,this.asiakasID);
		this.resultset = this.preparedStatement.executeQuery();
      
      int toimitusLkm = 1;
      int toimitusPaino = 0;
      
      // Jaetaan varaus erillisiin toimituksiin painon perusteella
      while(this.resultset.next()){
         
         int kappalePaino = this.resultset.getInt("paino");
         
         // Toimituspakettiin ei mahdu enää yhtäkään kirjaa
         if ((toimitusPaino + kappalePaino) > maksuLuokka) {

            // Lisätään uusi toimitus tietokantaan
            this.preparedStatement = this.connection.prepareStatement(this.lisaaToimitus);
            this.preparedStatement.setInt(1,toimitusLkm);
            this.preparedStatement.setInt(2,this.asiakasID);
            this.preparedStatement.setInt(3,toimitusPaino);
            this.preparedStatement.setInt(4,maksuLuokka);
            this.preparedStatement.executeUpdate();
            
            toimitusLkm++;
            toimitusPaino = 0;
         }
         
         toimitusPaino += kappalePaino;
      }
      
      // Haetaan jäljelle jääneen toimituksen painoa vastaava maksuluokka tietokannasta
		this.preparedStatement = this.connection.prepareStatement(this.haeOikeaMaksuluokka);
      this.preparedStatement.setInt(1,toimitusPaino);
		this.resultset = this.preparedStatement.executeQuery();
      this.resultset.next();

      maksuLuokka = this.resultset.getInt(1);
      
      // Lisätään viimeinenkin toimitus tietokantaan
      this.preparedStatement = this.connection.prepareStatement(this.lisaaToimitus);
      this.preparedStatement.setInt(1,toimitusLkm);
      this.preparedStatement.setInt(2,this.asiakasID);
      this.preparedStatement.setInt(3,toimitusPaino);
      this.preparedStatement.setInt(4,maksuLuokka);
      this.preparedStatement.executeUpdate();
      
      // Ilmoitetaan lopuksi käyttäjälle toimitusten lukumäärä ja postikulut
		this.preparedStatement = this.connection.prepareStatement(this.laskePostikulut);
      this.preparedStatement.setInt(1,asiakasID);
		this.resultset = this.preparedStatement.executeQuery();
      this.resultset.next();
      
      System.out.println("Tilaus toimitetaan " + toimitusLkm + " erässä. Postikulut ovat yhteensä " + this.resultset.getInt(1) + " rahaa.");
   }
   
	public void varauksienHaku() throws SQLException{

		this.preparedStatement = this.connection.prepareStatement(this.haeVaraukset);
		this.preparedStatement.setInt(1,this.asiakasID);
		this.resultset = this.preparedStatement.executeQuery();

		int kyselynumero = 1;
		while(this.resultset.next()){
			
			// Lisätään teoksen kaikki tiedot merkkijonoina ArrayListiin
			ArrayList<String> teostiedot = new ArrayList<>();
			teostiedot.add(this.resultset.getString("kappaleid"));
			teostiedot.add(this.resultset.getString("isbn"));
			teostiedot.add(this.resultset.getString("nimi"));
			teostiedot.add(this.resultset.getString("tekija"));
			teostiedot.add(this.resultset.getString("vuosi"));
			teostiedot.add(this.resultset.getString("tyyppi"));
			teostiedot.add(this.resultset.getString("luokka"));
			teostiedot.add(this.resultset.getString("paino"));
			teostiedot.add(this.resultset.getString("hinta"));
			
			// Lisätään tiedot HashMappiin, avaimena kyselynumero, arvona arraylist
			this.teoskysely.put(String.valueOf(kyselynumero),teostiedot);
			kyselynumero = kyselynumero+1;
		}
	}

	// Tilataan ostoskorissa olevat tuotteet
	private void tilauksenTeko() throws SQLException{

		// Käydään ostoskorin teokset läpi
		for(int i = 1; i < this.teoskysely.size()+1;i++){

			// Muunnetaan int Merkkijonoksi, jolla päästään käsiksi mapin avaimiin.
			String indeksi = String.valueOf(i);

			// Haetaan arraylistista teoksen kappaleid indeksistä 0
			int teosid = Integer.parseInt(this.teoskysely.get(indeksi).get(0));
			
         // Asetetaan keskustietokannassa oleva kappale myydyksi
			this.preparedStatement = this.connection.prepareStatement(this.myynti);
			this.preparedStatement.setInt(1,teosid);
			this.preparedStatement.executeUpdate();
			
	   	// Haetaan kappaleen omistavan alkuperäisen divarin ID
		   this.preparedStatement = this.connection.prepareStatement(this.haeDivariID);
		   this.preparedStatement.setInt(1,teosid);
		   this.resultset = this.preparedStatement.executeQuery();
         this.resultset.next();
      
         int divariID = this.resultset.getInt("DivariID");
		
			// Jos divari ei kuulu keskustietokantaan, asetetaan kappale myydyksi myös siellä
			if (divariID != 2 && divariID != 4) {
				String myynti = "UPDATE D"+divariID+".TeosKappale SET Vapaus='Myyty', MyyntiPvm=now() WHERE KappaleID=?";
				this.preparedStatement=this.connection.prepareStatement(myynti);
				this.preparedStatement.setInt(1,teosid);
				this.preparedStatement.executeUpdate();
         }

         // Asetetaan tilaus suoritetuksi tietokannassa
         this.preparedStatement = this.connection.prepareStatement(this.tilausSuoritus);
         this.preparedStatement.setInt(1,teosid);
         this.preparedStatement.executeUpdate();
		}
      
      // Poistetaan toimituskulut, koska niitä ei enää tarvita
      this.preparedStatement = this.connection.prepareStatement(this.poistaToimitus);
		this.preparedStatement.setInt(1,this.asiakasID);
		this.preparedStatement.executeUpdate();
	}

	private void tilauksenPeruutus() throws SQLException{

		this.connection.setAutoCommit(false);

		this.preparedStatement = this.connection.prepareStatement(this.tilausPeruutus);
        this.preparedStatement.setInt(1,this.asiakasID);
        this.preparedStatement.executeUpdate();

		// Käydään ostoskorin teokset läpi
		for(int i = 1; i < this.teoskysely.size()+1;i++){
			System.out.println("jotain");
			// Muunna int Merkkijonoksi, jolla päästään käsiksi mapin avaimiin.
			String indeksi = String.valueOf(i);

			// Hae arraylistista teoksen kappaleid indeksistä 0
			int teosid = Integer.parseInt(this.teoskysely.get(indeksi).get(0));

			this.preparedStatement = this.connection.prepareStatement(this.vapautus);
			this.preparedStatement.setInt(1,teosid);
			this.preparedStatement.executeUpdate();

			// Haetaan divarin ID, jossa teos myynnissä.
			this.preparedStatement = this.connection.prepareStatement(this.haeDivariID);
			this.preparedStatement.setInt(1,teosid);
			this.resultset = this.preparedStatement.executeQuery();

			// Haetaan kappaleen omistavan alkuperäisen divarin ID
			// Haetaan kappaleen omistavan alkuperäisen divarin ID
			int divariID=0;
			while(this.resultset.next()){
				 divariID = this.resultset.getInt("DivariID");
			}

			// Jos divari ei kuulu keskustietokantaan, asetetaan kappale myydyksi/vapaaksi myös siellä
			if (divariID != 2 && divariID != 4) {
				String vapautus = "UPDATE D"+divariID+".TeosKappale SET Vapaus='Vapaa' WHERE KappaleID=?";
				this.preparedStatement=this.connection.prepareStatement(vapautus);
				this.preparedStatement.setInt(1,teosid);
				this.preparedStatement.executeUpdate();
            }

            
		}
		this.connection.commit();
        this.connection.setAutoCommit(true); 
	}

	public void rahanLisays() throws SQLException{
		this.preparedStatement = this.connection.prepareStatement(this.lisaaRahaa);
		this.preparedStatement.setDouble(1,inputDouble);
		this.preparedStatement.setInt(2,this.asiakasID);
		this.preparedStatement.executeUpdate();
	}

}		




               
         