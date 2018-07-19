// Superuserkyselyt -luokka. Sisältään tietokantarajapinnan adminin kyselyille.

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
import java.util.HashMap;
import java.util.ArrayList;

public class SuperuserKyselyt{

	private Yhteys yhteys;
	private Connection connection;
	private ResultSet resultset;
	private String moodi;
	private PreparedStatement preparedStatement;
	private Scanner lukija;
	private HashMap<String,ArrayList<String>> kyselyTulos;

	private String lisaaTeos;
	private String haeMaxID;
	private String lisaaKappale;
	private String lisaaSijainti;

	public SuperuserKyselyt(){

		this.yhteys = new Yhteys();
		this.connection = null;
		this.resultset = null;
		this.preparedStatement = null;
		this.lukija = new Scanner(System.in);

		this.lisaaTeos = "INSERT INTO keskus.Teos VALUES (?,?,?, null, null, null,?)";
		this.haeMaxID = "SELECT MAX(KappaleID) FROM keskus.teosKappale";
		this.lisaaKappale = "INSERT INTO keskus.TeosKappale VALUES (?,?,?, null, null, 'Vapaa')";
		this.lisaaSijainti = "INSERT INTO keskus.Sijainti VALUES (?,?)";
	}

	// Myyntiraportin palautus, palautta hashmapin, jossa ostotiedot
	public HashMap<String,ArrayList<String>> tulostaRaportti(){

		this.moodi = "raportti";
		this.yhteysHandleri();

		return this.kyselyTulos;
	}

	// Teosten luokkatietojen palautus, palauttaa hashmapin, jossa luokkatiedot
	public HashMap<String,ArrayList<String>> luokkaTiedot(){

		this.moodi = "luokkatiedot";
		this.yhteysHandleri();

		return this.kyselyTulos;
	}

	// Uuden kannan luominen XML-datasta
	public void kannanLisays(){
		this.moodi = "kannanlisays";
		this.yhteysHandleri();
	}

	// Yhteinen "yhteyshandleri" kyselyille, joka hoitaa yhteyksien avaamiset ja sulkemiset
	// kyselyn tulokset tallennetaan oliomuuttujiin (HashMap, ArrayList, int)
	private void yhteysHandleri(){

		try {

			this.connection = this.yhteys.uusiYhteys();
			this.connection.setAutoCommit(false);

			// Tässä kohtaa kutsutaan privaattia kyselyfunktiota
			if(this.moodi.equals("raportti")){
				this.myyntiRaportti();

			}else if(this.moodi.equals("luokkatiedot")){
				this.teosLuokat();

			}else if(this.moodi.equals("kannanlisays")){
				this.lisaaKanta();
			}

			// Sitoudutaan muutoksiin
			this.connection.commit();
			this.connection.setAutoCommit(true);

		} catch(XPathExpressionException | ParserConfigurationException | SAXException | IOException | SQLException poikkeus) {

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

	// Luokkien hintoihin liittyvät kyselyt
	public void teosLuokat() throws SQLException{

		this.kyselyTulos = new HashMap<String, ArrayList<String>>();

		String teosLuokittelu = "SELECT * FROM keskus.hinnatluokittain";
		this.preparedStatement=this.connection.prepareStatement(teosLuokittelu);
		this.resultset = this.preparedStatement.executeQuery();

		int i = 0;

		while(this.resultset.next()){

			ArrayList<String> tiedot = new ArrayList<String>();

			String luokka = this.resultset.getString("luokka");
			double kokonaishinta = this.resultset.getDouble("kokonaishinta");
			double keskihinta  = this.resultset.getDouble("keskihinta");

			tiedot.add(luokka);
			tiedot.add(String.valueOf(kokonaishinta));
			tiedot.add(String.valueOf(keskihinta));

			this.kyselyTulos.put(String.valueOf(i),tiedot);
			i = i+1;
		}
	}

	// Myyntiraporttiin liittyvät kyselyt
	public void myyntiRaportti() throws SQLException{

		this.kyselyTulos = new HashMap<String, ArrayList<String>>();

		String myyntiRaportti = "SELECT * FROM keskus.ostotVuodessa";
		this.preparedStatement=this.connection.prepareStatement(myyntiRaportti);
		this.resultset = this.preparedStatement.executeQuery();

		int i = 0;

		while(this.resultset.next()){

			ArrayList<String> tiedot = new ArrayList<String>();

			String etunimi = this.resultset.getString("etunimi");
			String sukunimi = this.resultset.getString("sukunimi");
			int hinta  = this.resultset.getInt("ostot");

			tiedot.add(etunimi);
			tiedot.add(sukunimi);
			tiedot.add(String.valueOf(hinta));

			this.kyselyTulos.put(String.valueOf(i),tiedot);
			i = i+1;
		}
	}
	
	// XML-muotoisen divarin tietojen lisääminen keskustietokantaan
	public void lisaaKanta() throws XPathExpressionException, ParserConfigurationException, SAXException, IOException, SQLException{

		// Pyydetään käyttäjältä XML-tiedoston nimi
		System.out.print("Syötä tiedostonimi, jossa lisättävän tietokannan tiedot sijaitsevat XML-formaatissa (Oletus: \"XML-data\"):\n> ");
		String filename = lukija.nextLine();

		// Jos käyttäjä ei anna mitään, tiedoston nimi on XML-data
		if (filename.length()==0) {
			filename = "XML-data";
		}

		// Lisätään tiedostotyyppi nimeen
		filename += ".xml";

		System.out.println("Luetaan tiedostoa "+filename+"...");

		// Työkaluja XML-tiedoston käsittelyä varten
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		Document doc = null;
		XPathExpression expr = null;

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
				this.preparedStatement.setInt(1,4);
				this.preparedStatement.setInt(2,id);
				this.preparedStatement.executeUpdate();
			}
		}
		System.out.println("XML-data siirretty onnistuneesti keskustietokantaan!");
	}
}