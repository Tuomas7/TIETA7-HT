import java.sql.*;
import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

	
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


	public Yllapitokyselyt(int yllapitoID){
		this.divariID = 0;
		this.yllapitoID = yllapitoID;
		this.yhteys = new Yhteys("localhost", 5432, "bookstore", "testuser", "12345");
		this.connection = null;
		this.resultset = null;
		this.preparedStatement = null;

		this.onkokannassa = false;

		this.haeDivariID = "SELECT divariid FROM keskus.yllapitaja WHERE yllapitajaid =?";
		this.onkoDivariKannassa = "SELECT itsenainen FROM keskus.divari WHERE divariid = ?";

		this.haeISBNkeskus = "SELECT isbn FROM keskus.teos";
		

	}

	public int haeDivari(){

		this.moodi = "haedivari";
		this.yhteysHandleri();
		return this.divariID;

	}

	public boolean tarkastaDivari(){
		
		this.moodi = "onkokannassa";
		this.yhteysHandleri();
		return this.onkokannassa;
	}

	public boolean haeISBNpaikallinen(String isbn){
		this.inputData = isbn;
	
		this.moodi = "localISBN";
		this.yhteysHandleri();
		return this.onkokannassa;
	}

	public boolean haeISBNkeskus(String isbn){
		this.inputData = isbn;

		this.moodi = "keskusISBN";
		this.yhteysHandleri();
		return this.onkokannassa;
	}

	public void lisaaKPLtiedot(HashMap<String,String> tiedot){
		this.inputMap = tiedot;
		this.moodi = "lisaakpl";
		this.yhteysHandleri();
	}

	public void lisaateosKPLtiedot(HashMap<String,String> tiedot){
		this.inputMap = tiedot;
		this.moodi = "lisaateoskpl";
		this.yhteysHandleri();
	}


	// Yhteinen "yhteyshandleri" kyselyille, joka hoitaa yhteyksien avaamiset ja sulkemiset
	// kyselyn tulokset tallennetaan oliomuuttujiin (HashMap, ArrayList, int)
	private void yhteysHandleri(){

		
		try{
			this.connection = this.yhteys.uusiYhteys();
			
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
			}
	

		}catch(SQLException poikkeus) {
        	System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());  
        	// Tähän iffeillä kullekin omat virheet?
        	/*
        	try {
            
	            // Perutaan tapahtuma
	            this.connection.rollback();
            
         	} catch (SQLException poikkeus2) {
            	System.out.println("Tapahtuman peruutus epäonnistui: " + poikkeus2.getMessage()); 
         	}
         	*/
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

	// Tarkastetaan löytyykö divarin id divarirelaatiosta
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

	public void lisaaKappale() throws SQLException{
		this.haeDivaria();
		this.connection.setAutoCommit(false);

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
			System.out.println("juu");
			this.preparedStatement = this.connection.prepareStatement(lisaasijainti);
	        this.preparedStatement.setInt(1,this.divariID);
			this.preparedStatement.setInt(2,id);
			this.preparedStatement.executeUpdate();
		}else{
			System.out.println("ei");
		}

		this.connection.commit();
        this.connection.setAutoCommit(true);
	}

	public void lisaaTeosKappale() throws SQLException{

		this.haeDivaria();

		this.connection.setAutoCommit(false);
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
			System.out.println("juu");
			this.preparedStatement = this.connection.prepareStatement(lisaasijainti);
	        this.preparedStatement.setInt(1,this.divariID);
			this.preparedStatement.setInt(2,id);
			this.preparedStatement.executeUpdate();
		}else{
			System.out.println("ei");
		}
		this.connection.commit();
        this.connection.setAutoCommit(true);

	}





}