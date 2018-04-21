import java.sql.*;
import java.util.Scanner;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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




public class SuperuserKyselyt{


	private Yhteys yhteys;
	private Connection connection;
	private ResultSet resultset;
	private String moodi;
	private PreparedStatement preparedStatement;
	private Scanner lukija;

	private String lisaaTeos;
	private String haeMaxID;
	private String lisaaKappale;
	private String lisaaSijainti;


	public SuperuserKyselyt(){
		this.yhteys = new Yhteys("localhost", 5432, "bookstore", "testuser", "12345");
		this.connection = null;
		this.resultset = null;
		this.preparedStatement = null;
		this.lukija = new Scanner(System.in);

		this.lisaaTeos = "INSERT INTO keskus.Teos VALUES (?,?,?, null, null, null,?)";
		this.haeMaxID = "SELECT MAX(KappaleID) FROM keskus.teosKappale";
		this.lisaaKappale = "INSERT INTO keskus.TeosKappale VALUES (?,?,?, null, null, 'Vapaa')";
		this.lisaaSijainti = "INSERT INTO keskus.Sijainti VALUES (?,?)";
	}

	public void lisaaKanta(){

		System.out.print("Syötä tiedostonimi, jossa lisättävän tietokannan tiedot sijaitsevat XML-formaatissa:\n> ");
		String filename = lukija.nextLine();
		System.out.print("Syötä divarin ID:\n> ");
		int divariID = Integer.parseInt(lukija.nextLine());

		System.out.println("Lisätään uusi divari keskustietokantaan ID:llä "+divariID+" ...");
      	// Työkaluja XML-tiedoston käsittelyä varten
      	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      	factory.setNamespaceAware(true);
      	DocumentBuilder builder;
      	Document doc = null;
      	XPathExpression expr = null;
      
      	try {
         
         	// Luodaan tapahtumaolio
         	this.connection = this.yhteys.uusiYhteys();
			this.connection.setAutoCommit(false);
         
         	// Haetaan XML-dokumentti muuttujaan
         	builder = factory.newDocumentBuilder();
         	doc = builder.parse(filename);

         	// Luodaan Xpath-olio yksittäisten tietojen noutamista varten
         	XPathFactory xpathFactory = XPathFactory.newInstance();
         	XPath xpath = xpathFactory.newXPath();
         
         	// Lasketaan teosten lukumäärä tiedostossa
         	expr = xpath.compile("count(//teos)");
         	int teosLkm = (int)(double)expr.evaluate(doc, XPathConstants.NUMBER);
         
        	// Käydään jokainen teos läpi
         	for (int i = 1; i < (teosLkm+1); i++) {
            
            	// Napataan talteen teoksen tiedot
            	expr = xpath.compile("//teos[" + i + "]/ttiedot/isbn/text()");
            	String isbn = (String)expr.evaluate(doc, XPathConstants.STRING);
            
            	expr = xpath.compile("//teos[" + i + "]/ttiedot/nimi/text()");
            	String nimi = (String)expr.evaluate(doc, XPathConstants.STRING);
            
            	expr = xpath.compile("//teos[" + i + "]/ttiedot/tekija/text()");
            	String tekija = (String)expr.evaluate(doc, XPathConstants.STRING);
            
            	expr = xpath.compile("floor(//teos[" + i + "]/ttiedot/paino/text())");
            	int paino = (int)(double)expr.evaluate(doc, XPathConstants.NUMBER);
            
            	// Lisätään teoksen tiedot keskustietokantaan
            	this.preparedStatement = this.connection.prepareStatement(this.lisaaTeos);
            	this.preparedStatement.setString(1,isbn);
				this.preparedStatement.setString(2,nimi);
				this.preparedStatement.setString(3,tekija);
				this.preparedStatement.setInt(4,paino);
				this.preparedStatement.executeUpdate();

            
            	// Lasketaan teoksesta olevien niteiden lukumäärä
            	expr = xpath.compile("count(//teos[" + i + "]/nide)");
            	int nideLkm = (int)(double)expr.evaluate(doc, XPathConstants.NUMBER);
            
            	// Käydään jokainen nide läpi
            	for (int j = 1; j < (nideLkm+1); j++) {
            
               		// Napataan talteen niteen hinta
               		expr = xpath.compile("number(//teos[" + i + "]/nide[" + j + "]/hinta/text())");
               		double hinta = (double)expr.evaluate(doc, XPathConstants.NUMBER);
               
               		// Haetaan suurin kappaleID tietokannassa ja lisätään siihen 1
               		this.preparedStatement = this.connection.prepareStatement(this.haeMaxID);
               		this.resultset = this.preparedStatement.executeQuery();

               		int id = 0;
               		while(this.resultset.next()){
               			id = this.resultset.getInt(1) +1;
               		}
               		
               
               		// Lisätään niteen tiedot keskustietokantaan
               		this.preparedStatement = this.connection.prepareStatement(this.lisaaKappale);
               		this.preparedStatement.setInt(1,id);
					this.preparedStatement.setString(2,isbn);
					this.preparedStatement.setDouble(3,hinta);
               		this.preparedStatement.executeUpdate();
                  
               		// Lisätään myös niteen sijaintitiedot
               		this.preparedStatement = this.connection.prepareStatement(this.lisaaSijainti);
               		this.preparedStatement.setInt(1,divariID);
               		this.preparedStatement.setInt(2,id);
               		this.preparedStatement.executeUpdate();
               		
            		}
         	}

	        System.out.println("XML-data siirretty onnistuneesti keskustietokantaan!");
   
         	// Sitoudutaan muutoksiin
        	this.connection.commit();
        	this.connection.setAutoCommit(true);
         
         
         
      	} catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException | SQLException poikkeus) {
         
         	System.out.println("XML-datan lukeminen epäonnistui: " + poikkeus.getMessage());  

         	try {
            
            	// Perutaan tapahtuma
            	this.connection.rollback();
            
         	} catch (SQLException poikkeus2) {
            	System.out.println("Tapahtuman peruutus epäonnistui: " + poikkeus2.getMessage()); 
         	}
      	}finally {
         
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

		

	public void tulostaRaportti(){

		this.moodi = "raportti";
		//this.yhteysHandleri();
	}



	
	






}