import java.sql.*;
import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;

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

	// Tietorakenteet, joihin tallennetaan kyselyiden tuloksia luokan sisällä ja joita luokan metodit palauttavat
	private HashMap<String, String> kyselyMap;
	private ArrayList<String> kyselyLista;
	private int kyselyLuku;
	private boolean kyselybool;
	private int returnInt;
	private HashMap<String,ArrayList<String>> teoskysely;

	// Aputietorakenteita, näihin asetetaan luokan metodeihin tulleita parametrien arvoja
	private String input1;
	private String input2;
	private int paramInt;
	private HashMap<String,String> mapInput;


	 

	// Kontruktori, asetetaan prepared statementtien rungot
	public Asiakaskyselyt(){
		this.yhteys = new Yhteys("localhost", 5432, "bookstore", "testuser", "12345");
		this.connection = null;
		this.resultset = null;
		this.preparedStatement = null;
		
		this.haeKayttajaTiedotStatement = "SELECT nimi,kayttajaid,salasana,rooli FROM kayttaja WHERE kayttajaid = ?";
		this.haeAsiakasTiedotStatement = "SELECT etunimi,sukunimi,osoite,sahkoposti,puhelin,saldo FROM asiakas WHERE asiakasid = ?";
		this.kirjautumisStatement = "SELECT nimi,kayttajaid,rooli FROM kayttaja WHERE salasana =?";
		this.luoIDStatement = "SELECT kayttajaid FROM kayttaja";
		this.tarkastaNimiStatement = "SELECT nimi FROM kayttaja";
		this.insertKayttaja = "INSERT INTO kayttaja VALUES (?,?,?,?)";
		this.insertAsiakas = "INSERT INTO asiakas VALUES (?,?,?,?,?,?)";
		this.haeTeosNimet = "SELECT isbn,nimi,tekija,vuosi,tyyppi,luokka,paino,kappaleid,hinta,vapaus FROM teos NATURAL JOIN teoskappale WHERE nimi LIKE ?";
		this.haeTeosTekijat = "SELECT isbn,nimi,tekija,vuosi,tyyppi,luokka,paino,kappaleid,hinta,vapaus FROM teos NATURAL JOIN teoskappale WHERE tekija LIKE ?";
		this.haeTeosLuokat = "SELECT isbn,nimi,tekija,vuosi,tyyppi,luokka,paino,kappaleid,hinta,vapaus FROM teos NATURAL JOIN teoskappale WHERE luokka LIKE ?";
		this.haeTeosTyypit = "SELECT isbn,nimi,tekija,vuosi,tyyppi,luokka,paino,kappaleid,hinta,vapaus FROM teos NATURAL JOIN teoskappale WHERE tyyppi LIKE ?";
		this.haeTeosKaikki = "SELECT isbn,nimi,tekija,vuosi,tyyppi,luokka,paino,kappaleid,hinta,vapaus FROM teos NATURAL JOIN teoskappale WHERE nimi LIKE ? OR tekija LIKE ? OR luokka LIKE ? OR tyyppi LIKE ?";
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
	public int tarkastaKirjautuminen(String tunnus, String salasana){

		this.kyselybool = false;
		this.input1 = tunnus;
		this.input2 = salasana;
		this.moodi = "tarkastakirjautuminen";
		this.kyselyLista = new ArrayList<>();
		
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

	// Yhteinen "yhteyshandleri" kyselyille, joka hoitaa yhteyksien avaamiset ja sulkemiset
	// kyselyn tulokset tallennetaan oliomuuttujiin (HashMap, ArrayList, int)
	private void yhteysHandleri(){

		
		try{
			this.connection = this.yhteys.uusiYhteys();
			
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
			}
	

		}catch(SQLException poikkeus) {
        	System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());  

      	}finally {
    		if (this.resultset != null) {
        		try {
            		this.resultset.close();
        		} catch (SQLException e) { /* ignored */}
    		}
    		if (this.preparedStatement != null) {
        		try {
            		this.preparedStatement.close();
        		} catch (SQLException e) { /* ignored */}
    		}
    		if (this.connection != null) {
        		try {
            		this.connection.close();
        		} catch (SQLException e) { /* ignored */}
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
			this.kyselyMap.put("nimi",this.resultset.getString("nimi"));
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

		int indeksi = 1;

		while(this.resultset.next()){

			String kyselynumero = String.valueOf(indeksi);

			// Lisätään teoksen kaikki tiedot merkkijonoina ArrayListiin
			ArrayList<String> teostiedot = new ArrayList<>();

			teostiedot.add(this.resultset.getString("isbn"));
			teostiedot.add(this.resultset.getString("kappaleid"));
			teostiedot.add(this.resultset.getString("nimi"));
			teostiedot.add(this.resultset.getString("tekija"));
			teostiedot.add(this.resultset.getString("vuosi"));
			teostiedot.add(this.resultset.getString("tyyppi"));
			teostiedot.add(this.resultset.getString("luokka"));
			teostiedot.add(this.resultset.getString("paino"));
			teostiedot.add(this.resultset.getString("hinta"));
			teostiedot.add(this.resultset.getString("vapaus"));
			
			// Lisätään tiedot HashMappiin, avaimena kyselynumero, arvona arraylist
			this.teoskysely.put(kyselynumero,teostiedot);

			indeksi = indeksi +1;
			System.out.println(indeksi);
			System.out.println(this.resultset.getString("nimi"));
			
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
	
}		

