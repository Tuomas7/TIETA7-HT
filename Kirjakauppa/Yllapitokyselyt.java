// Yllapitokyselyt -luokka. Tietokantarajapinta ylläpitäjälle.

import java.sql.*;
import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;

public class Yllapitokyselyt{

	// Attribuutteina prepared statementtien merkkijonoesitykset
	private Yhteys yhteys;
	private Connection connection;
	private ResultSet resultset;
	private String moodi;
	private PreparedStatement preparedStatement;

	private int divariID;
	private int yllapitoID;
	private boolean onkokannassa;
	private boolean onkoTeoksiaKeskuskannassa;
	private String inputData;
	private HashMap<String,String> inputMap;

	// Prepared Statementit
	private String haeDivariID;
	private String onkoDivariKannassa;
	private String haeISBNlocal;
	private String haeISBNkeskus;
	private String maxID;
	private String paivitaKappale;
	private String paivitaSijainti;

	public Yllapitokyselyt(int yllapitoID){

		this.divariID = 0;
		this.yllapitoID = yllapitoID;
		this.yhteys = new Yhteys();
		this.connection = null;
		this.resultset = null;
		this.preparedStatement = null;
		this.onkokannassa = false;

		// Prepared statementit
		this.haeDivariID = "SELECT divariid FROM keskus.yllapitaja WHERE yllapitajaid =?";
		this.onkoDivariKannassa = "SELECT itsenainen FROM keskus.divari WHERE divariid = ?";

		this.haeISBNkeskus = "SELECT isbn FROM keskus.teos";

		this.maxID = "SELECT MAX(kappaleid) FROM keskus.teosKappale";
		this.paivitaKappale = "INSERT INTO keskus.TeosKappale VALUES (?, ?, ?, ?, ?, ?)";
		this.paivitaSijainti = "INSERT INTO keskus.Sijainti VALUES (?, ?)";
	}

	// Hae divarin id
	public int haeDivari(){

		this.moodi = "haedivari";
		this.yhteysHandleri();

		return this.divariID;
	}

	// Tarkastetaan, onko divari keskustietokannassa
	public boolean tarkastaDivari(){

		this.moodi = "onkokannassa";
		this.yhteysHandleri();

		return this.onkokannassa;
	}

	// Haetaan teoksen isbn paikkallisesta divarista
	public boolean haeISBNpaikallinen(String isbn){

		this.inputData = isbn;
		this.moodi = "localISBN";
		this.yhteysHandleri();

		return this.onkokannassa;
	}

	// Haetaan teoksen isbn keskusdivarista
	public boolean haeISBNkeskus(String isbn){

		this.inputData = isbn;
		this.moodi = "keskusISBN";
		this.yhteysHandleri();

		return this.onkokannassa;
	}

	// Kappaleen tietojen lisäys
	public void lisaaKPLtiedot(HashMap<String,String> tiedot){
		this.inputMap = tiedot;
		this.moodi = "lisaakpl";
		this.yhteysHandleri();
	}

	// Teoksen tietojen + kappaletietojen lisäys
	public void lisaateosKPLtiedot(HashMap<String,String> tiedot){
		this.inputMap = tiedot;
		this.moodi = "lisaateoskpl";
		this.yhteysHandleri();
	}

	// Keskustietokannan päivitys
	public void paivitaKeskustietokanta(){
		this.moodi = "keskusPaivitys";
		this.yhteysHandleri();
	}

	// Yhteinen "yhteyshandleri" kyselyille, joka hoitaa yhteyksien avaamiset ja sulkemiset
	// kyselyn tulokset tallennetaan oliomuuttujiin (HashMap, ArrayList, int)
	private void yhteysHandleri(){

		try {

			this.connection = this.yhteys.uusiYhteys();
			this.connection.setAutoCommit(false);

			// tässä kohtaa kutsutaan privaattia kyselyfunktiota
			if(this.moodi.equals("haedivari")){
				this.asetaDivariID();

			}else if(this.moodi.equals("onkokannassa")){
				this.haeDivaria();

			}else if(this.moodi.equals("localISBN")){
				this.haeISBNpaikallisesta();

			}else if(this.moodi.equals("lisaakpl")){
				this.lisaaKappale();

			}else if(this.moodi.equals("lisaateoskpl")){
				this.lisaaTeosKappale();

			}else if(this.moodi.equals("keskusISBN")){
				this.haeISBNkeskuksesta();

			}else if(this.moodi.equals("keskusPaivitys")){
				this.paivitaKeskus();
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

	// Divari-id:n asetus kyselyoliolle
	public void asetaDivariID() throws SQLException{

		this.preparedStatement = this.connection.prepareStatement(this.haeDivariID);
		this.preparedStatement.setInt(1,this.yllapitoID);
		this.resultset = this.preparedStatement.executeQuery();

		int id = 0;

		while(this.resultset.next()){
			id = this.resultset.getInt("divariid");
		}

		this.divariID = id;
	}

	// Tarkastetaan, löytyykö divarin id divarirelaatiosta
	public void haeDivaria() throws SQLException{

		this.onkokannassa = false;

		this.preparedStatement = this.connection.prepareStatement(this.onkoDivariKannassa);
		this.preparedStatement.setInt(1,this.divariID);
		this.resultset = this.preparedStatement.executeQuery();

		while(this.resultset.next()){
			if(this.resultset.getInt("itsenainen") == 0){
				this.onkokannassa = true;	
			}
		}
	}

	// Haetaan teoksen isbn paikallisesta divarista
	public void haeISBNpaikallisesta() throws SQLException{

		this.onkokannassa = false;

		this.haeISBNlocal = "SELECT isbn FROM D"+this.divariID+".teos";
		this.preparedStatement = this.connection.prepareStatement(this.haeISBNlocal);
		this.resultset = this.preparedStatement.executeQuery();

		while(this.resultset.next()){
			if(this.resultset.getString("isbn").equals(this.inputData)){
				this.onkokannassa = true;	
			}
		}
	}

	// Haetaan teoksen isbn keskusdivarista
	public void haeISBNkeskuksesta() throws SQLException{

		this.onkokannassa = false;

		this.preparedStatement = this.connection.prepareStatement(this.haeISBNkeskus);
		this.resultset = this.preparedStatement.executeQuery();

		while(this.resultset.next()){
			if(this.resultset.getString("isbn").equals(this.inputData)){
				this.onkokannassa = true;	
			}
		}
	}

	// Kappaleen lisäys
	public void lisaaKappale() throws SQLException{

		this.haeDivaria();

		String maxkplid= "";
		String lisaakpl="";
		String lisaasijainti="";

		if(!this.onkokannassa){
			maxkplid = "SELECT MAX(kappaleid) FROM D"+this.divariID+".teosKappale";
			lisaakpl = "INSERT INTO D"+this.divariID+".TeosKappale VALUES (?, ?, ?, ?, null, 'Vapaa')";

		}else{
			maxkplid = "SELECT MAX(kappaleid) FROM keskus.teosKappale";
			lisaakpl = "INSERT INTO keskus.TeosKappale VALUES (?, ?, ?, ?, null, 'Vapaa')";
			lisaasijainti = "INSERT INTO keskus.Sijainti VALUES (?,?)";
		}

		// Haetaan suurin kappaleID tietokannassa ja lisätään siihen 1
		this.preparedStatement = this.connection.prepareStatement(maxkplid);
		this.resultset = this.preparedStatement.executeQuery();

		int id = 0; 

		while(this.resultset.next()){
			id = this.resultset.getInt(1) + 1;
		}

		this.preparedStatement = this.connection.prepareStatement(lisaakpl);
		this.preparedStatement.setInt(1,id);
		this.preparedStatement.setString(2,this.inputMap.get("isbn"));
		this.preparedStatement.setDouble(3,Double.parseDouble(this.inputMap.get("hinta")));
		this.preparedStatement.setDouble(4,Double.parseDouble(this.inputMap.get("ostohinta")));
		this.preparedStatement.executeUpdate();

		if(!lisaasijainti.equals("")){
			this.preparedStatement = this.connection.prepareStatement(lisaasijainti);
			this.preparedStatement.setInt(1,this.divariID);
			this.preparedStatement.setInt(2,id);
			this.preparedStatement.executeUpdate();
		}
	}

	// Teostietojen ja kappaletietojen lisäys
	public void lisaaTeosKappale() throws SQLException{

		this.haeDivaria();

		String lisaateos="";
		String maxkplid= "";
		String lisaakpl="";
		String lisaasijainti="";

		if(!this.onkokannassa){
			lisaateos = "INSERT INTO D"+this.divariID+".Teos VALUES (?,?,?,?,?,?,?)";
			maxkplid = "SELECT MAX(kappaleid) FROM D"+this.divariID+".teosKappale";
			lisaakpl = "INSERT INTO D"+this.divariID+".TeosKappale VALUES (?, ?, ?, ?, null, 'Vapaa')";

		}else{
			lisaateos = "INSERT INTO keskus.Teos VALUES (?,?,?,?,?,?,?)";
			maxkplid = "SELECT MAX(kappaleid) FROM keskus.teosKappale";
			lisaakpl = "INSERT INTO keskus.TeosKappale VALUES (?, ?, ?, ?, null, 'Vapaa')";
			lisaasijainti = "INSERT INTO keskus.Sijainti VALUES (?,?)";
		}

		this.preparedStatement = this.connection.prepareStatement(lisaateos);
		this.preparedStatement.setString(1,this.inputMap.get("isbn"));
		this.preparedStatement.setString(2,this.inputMap.get("nimi"));
		this.preparedStatement.setString(3,this.inputMap.get("tekija"));
		this.preparedStatement.setInt(4,Integer.parseInt(this.inputMap.get("vuosi")));
		this.preparedStatement.setString(5,this.inputMap.get("tyyppi"));
		this.preparedStatement.setString(6,this.inputMap.get("luokka"));
		this.preparedStatement.setDouble(7,Double.parseDouble(this.inputMap.get("paino")));
		this.preparedStatement.executeUpdate();

		// Haetaan suurin kappaleID tietokannassa ja lisätään siihen 1
		this.preparedStatement = this.connection.prepareStatement(maxkplid);
		this.resultset = this.preparedStatement.executeQuery();

		int id = 0; 

		while(this.resultset.next()){
			id = this.resultset.getInt(1) + 1;
		}
       	
		this.preparedStatement = this.connection.prepareStatement(lisaakpl);
		this.preparedStatement.setInt(1,id);
		this.preparedStatement.setString(2,this.inputMap.get("isbn"));
		this.preparedStatement.setDouble(3,Double.parseDouble(this.inputMap.get("hinta")));
		this.preparedStatement.setDouble(4,Double.parseDouble(this.inputMap.get("ostohinta")));
		this.preparedStatement.executeUpdate();

		if(!lisaasijainti.equals("")){
			this.preparedStatement = this.connection.prepareStatement(lisaasijainti);
			this.preparedStatement.setInt(1,this.divariID);
			this.preparedStatement.setInt(2,id);
			this.preparedStatement.executeUpdate();
		}
	}

	// Keskustietokannan päivitys
	public void paivitaKeskus() throws SQLException{

		// Haetaan käyttäjän hallinnoiman divarin ID muuttujaan
		this.haeDivaria();

		// Käydään läpi poikkeustapaukset
		if(this.onkokannassa) {
			System.out.println("Hallinnoimasi divari kuuluu jo valmiiksi keskustietokantaan. Tietoja ei voi päivittää");
			return;
		}
		else if(this.divariID == 3) {
			System.out.println("Hallinnoimasi divarin tiedot päivittyvät automaattisesti. Tietoja ei tarvitse erikseen päivittää");
			return;
		}
		else if(this.divariID == 4) {
			System.out.println("Hallinnoimasi divarin tiedot ovat XML-muodossa. Ota yhteys pääylläpitäjään siirtääksesi tiedot");
			return;
		}

		// Siirretään kaikki teosten yleiset tiedot keskustietokantaan, jos niitä ei vielä ole siellä
		String paivitaTeokset = "INSERT INTO keskus.Teos SELECT * FROM D" + this.divariID + ".Teos ON CONFLICT DO NOTHING";
		this.preparedStatement = this.connection.prepareStatement(paivitaTeokset);
		this.preparedStatement.executeUpdate();

		// Haetaan seuraavaa vaihetta varten keskustietokannan suurin kappaleID
		this.preparedStatement = this.connection.prepareStatement(maxID);
		this.resultset = this.preparedStatement.executeQuery();
		this.resultset.next();

		int id = this.resultset.getInt(1);

		// Haetaan sellaisten teoskappaleiden tiedot, joita ei ole aikaisemmin päivitetty
		String haePaivittamattomat = "SELECT TeosKappale.KappaleID, TeosKappale.ISBN, TeosKappale.Hinta, TeosKappale.Ostohinta, "
			+ "TeosKappale.MyyntiPvm, TeosKappale.Vapaus FROM D" + this.divariID + ".TeosKappale WHERE NOT Paivitetty";
		this.preparedStatement = this.connection.prepareStatement(haePaivittamattomat);
		this.resultset = this.preparedStatement.executeQuery();

		// Käydään jokainen teoskappale läpi
		while (this.resultset.next()) {

			// Kappaleen uusi ID
			id++;

			// Lisätään kappale keskustietokantaan
			this.preparedStatement = this.connection.prepareStatement(paivitaKappale);
			this.preparedStatement.setInt(1,id);
			this.preparedStatement.setString(2,this.resultset.getString(2));
			this.preparedStatement.setBigDecimal(3,this.resultset.getBigDecimal(3));
			this.preparedStatement.setBigDecimal(4,this.resultset.getBigDecimal(4));
			this.preparedStatement.setDate(5,this.resultset.getDate(5));
			this.preparedStatement.setString(6,this.resultset.getString(6));
			this.preparedStatement.executeUpdate();

			// Lisätään kappaleen id myös sijaintitauluun
			this.preparedStatement = this.connection.prepareStatement(paivitaSijainti);
			this.preparedStatement.setInt(1,this.divariID);
			this.preparedStatement.setInt(2,id);
			this.preparedStatement.executeUpdate();

			// Päivitetään id alkuperäiseen kantaan ja asetetaan teoksen status päivitetyksi
			String paivitaStatus = "UPDATE D" + this.divariID + ".TeosKappale SET KappaleID=?, Paivitetty=true WHERE KappaleID=?";
			this.preparedStatement = this.connection.prepareStatement(paivitaStatus);
			this.preparedStatement.setInt(1,id);
			this.preparedStatement.setInt(2,this.resultset.getInt(1));
			this.preparedStatement.executeUpdate();
		}
		System.out.println("Tiedot päivitetty keskustietokantaan onnnistuneesti!");
	}
}