import java.sql.*;
import java.util.Scanner;
import java.util.Stack;
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

public class Tapahtumat{

   /* Tapahtuma 2
    * Kuvaus: Lisätään uusi teos ja sen yksittäinen kappale divarin D1 tietokantaan
    * Rooli: Divarin D1 ylläpitäjä
    */
	public static void lisaaTeosJaKappale(Connection yhteys) {

      Scanner scanner = new Scanner(System.in);
   
      // Kysytään käyttäjältä teoksen tiedot
      System.out.println("Anna teoksesta seuraavat tiedot:");
      System.out.println("ISBN: ");
      String isbn = scanner.nextLine();
      System.out.println("Nimi: ");
      String nimi = scanner.nextLine();
      System.out.println("Tekijä: ");
      String tekija = scanner.nextLine();
      System.out.println("Julkaisuvuosi: ");
      int vuosi = scanner.nextInt();
      System.out.println("Tyyppi: ");
      String tyyppi = scanner.nextLine();
      System.out.println("Luokka: ");
      String luokka = scanner.nextLine();
      System.out.println("Paino grammoina: ");
      int paino = scanner.nextInt();
      System.out.println("");
   
      // Kysytään käyttäjältä yksittäisen kappaleen tiedot
      System.out.println("Anna kappaleesta seuraavat tiedot:");
      System.out.println("Myyntihinta: ");
      float hinta = scanner.nextFloat();
      System.out.println("Ostohinta: ");
      float ostohinta = scanner.nextFloat();
      System.out.println("");
   
      scanner.close();
   
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


   /* Tapahtuma 3
    * Kuvaus: Lisätään uusi kappale olemassa olevalle teokselle divarin D2 tietokantaan
    * Rooli: Divarin D2 ylläpitäjä
    */
   public static void lisaaKappale(Connection yhteys) {

      Scanner scanner = new Scanner(System.in);
   
      // Kysytään käyttäjältä yksittäisen kappaleen tiedot
      System.out.println("Anna kappaleesta seuraavat tiedot:");
      System.out.println("Yksilöivä ID-tunnus: ");
      int id = scanner.nextInt();
      System.out.println("ISBN: ");
      String isbn = scanner.nextLine();
      System.out.println("Myyntihinta: ");
      float hinta = scanner.nextFloat();
      System.out.println("Ostohinta: ");
      float ostohinta = scanner.nextFloat();
      System.out.println("");
   
      scanner.close();
   
      try {
   
         yhteys.setAutoCommit(false);
   
         // Luodaan tapahtumaolio
         Statement stmt = yhteys.createStatement();
      
         // Tässä kohtaa pitäisi ehkä tarkistaa, että tietokannassa varmasti on ISBN:ä vastaava teos
      
         // Lisätään yksittäisen kappaleen tiedot
         stmt.executeUpdate("INSERT INTO keskus.TeosKappale VALUES ('" + id + "', '" + isbn + "', '"
            + hinta + "', '" + ostohinta + "', null, 'Vapaa')");
      
         // Lisätään kappaleen tunnus myös sijaintitauluun
         stmt.executeUpdate("INSERT INTO keskus.Sijainti VALUES ('2', '" + id + "')");
      
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


   /* Tapahtumat 4 & 5 (varausvaihe)
    * Kuvaus: Varataan yksittäinen kappale ja lisätään se "ostoskoriin"
    * Rooli: Asiakas
    * Parametrit: yhteys, tilausta tekevän asiakkaan sessio, tilattavan kappaleen ID
    */
   /*
	public static void lisaaOstoskoriin(Connection yhteys, Sessio sessio, int kappaleID) {
   
      try {
   
         yhteys.setAutoCommit(false);
   
         // Luodaan tapahtumaolio
         Statement stmt = yhteys.createStatement();
         
         // Asetetaan keskusdivarissa oleva kappale varatuksi
         stmt.executeUpdate("UPDATE keskus.TeosKappale SET Vapaus='Varattu' WHERE KappaleID=" + kappaleID);
               
         // Haetaan kappaleen omistavan alkuperäisen divarin ID
         ResultSet rset = stmt.executeQuery("SELECT DivariID FROM keskus.sijainti WHERE KappaleID=" + kappaleID);
         int divariID = rset.getInt(1);
         
         // Jos divari ei kuulu keskustietokantaan, asetetaan kappale varatuksi myös siellä
         if (divariID != 2 && divariID != 4) {
            stmt.executeUpdate("UPDATE D" + divariID + ".TeosKappale SET Vapaus='Varattu' WHERE KappaleID=" + kappaleID);
         }
      
         // Luodaan uusi käynnissä oleva tilaus (vastaa kappaleen siirtämistä "ostoskoriin")
         stmt.executeUpdate("INSERT INTO keskus.Tilaus VALUES ('" + divariID + "', '"
            + sessio.id + "', '" + kappaleID + "', 'Käynnissä')");
      
         System.out.println("Kappale lisätty ostoskoriin.");
         
         // Sitoudutaan muutoksiin
         yhteys.commit();
         yhteys.setAutoCommit(true);
         
         // Suljetaan tapahtumaolio
         stmt.close();
         
      } catch (SQLException poikkeus) {
         
         System.out.println("Ostoskoriin lisäys epäonnistui: " + poikkeus.getMessage());  
         
         try {
            
            // Perutaan tapahtuma
            yhteys.rollback();
            
         } catch (SQLException poikkeus2) {
            System.out.println("Tapahtuman peruutus epäonnistui: " + poikkeus2.getMessage()); 
         }
      }
   }
   
   */
   /* Tapahtumat 4 & 5 (tilausvaihe)
    * Kuvaus: Tilataan ostoskorissa olevat teoskappaleet
    * Rooli: Asiakas
    * Parametrit: yhteys, tilausta tekevän asiakkaan sessio
    */
   /*
   public static void tilaaOstoskori(Connection yhteys, Sessio sessio) {
   
      try {
   
         yhteys.setAutoCommit(false);
   
         // Luodaan tapahtumaolio
         Statement stmt = yhteys.createStatement();
         
         // Haetaan "ostoskorin" sisältö
         ResultSet rset = stmt.executeQuery("SELECT Teos.Nimi, Teos.Paino, TeosKappale.KappaleID "
            + "FROM keskus.Tilaus NATURAL JOIN keskus.TeosKappale NATURAL JOIN keskus.Teos "
            + "WHERE Tilaus.Tila='Käynnissä' AND Tilaus.AsiakasID=" + sessio.id);
            
         // Oskoskorissa olevien kappaleiden ID-arvot
         Stack<Integer> teosKappaleet = new Stack<Integer>();
            
         // Kirjojen kokonaispaino
         int kokoPaino = 0;
            
         System.out.println("Tilataan seuraavat kirjat:");
         System.out.println();
            
         while (rset.next()) {
            System.out.println(rset.getString(1));
            teosKappaleet.push(rset.getInt(3));
            kokoPaino += rset.getInt(2);
         }
         
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
         
         // Otetaan asiakkaan valinta talteen
         Scanner scanner = new Scanner(System.in);
         char valinta = scanner.next().charAt(0);
         
         // Kysytään valintaa niin kauan, että asiakas syöttää k:n tai e:n
         while (valinta != 'k' && valinta != 'K' && valinta != 'e' && valinta != 'E') {
            System.out.println("Virheellinen valinta. Syötä joko k tai e:");
            valinta = scanner.next().charAt(0);
         }
            
         String tila = "";
         String vapaus = "";
         
         // Asetetaan teosten tila ja vapaus valinnan perusteella
         if (valinta == 'k' || valinta == 'K') {
            tila = "Suoritettu";
            vapaus = "Myyty";
         }
         else {
            tila = "Peruutettu";
            vapaus = "Vapaa";
         }
            
         // Käydään läpi kaikki ostoskorissa olevat teoskappaleet
         while (!(teosKappaleet.isEmpty())) {
               
            int kappaleID = teosKappaleet.pop();
               
            // Asetetaan tilaus suoritetuksi/peruutetuksi
            stmt.executeUpdate("UPDATE keskus.Tilaus SET Tila='" + tila + "' WHERE KappaleID=" + kappaleID);
               
            // Asetetaan keskusdivarissa oleva kappale myydyksi/vapaaksi
            stmt.executeUpdate("UPDATE keskus.TeosKappale SET Vapaus='" + vapaus + "' WHERE KappaleID=" + kappaleID);
               
            // Haetaan kappaleen omistavan alkuperäisen divarin ID
            ResultSet rset2 = stmt.executeQuery("SELECT DivariID FROM keskus.sijainti WHERE KappaleID=" + kappaleID);
            int divariID = rset2.getInt(1);
         
            // Jos divari ei kuulu keskustietokantaan, asetetaan kappale myydyksi/vapaaksi myös siellä
            if (divariID != 2 && divariID != 4) {
               stmt.executeUpdate("UPDATE D" + divariID + ".TeosKappale SET Vapaus='" + vapaus + "' WHERE KappaleID=" + kappaleID);
            }
         }
            
         if (valinta == 'k' || valinta == 'K') {
            System.out.println("Tilaus suoritettu onnistuneesti!");
         }
         else {
            System.out.println("Tilaus peruutettu!");
         }
         
         // Sitoudutaan muutoksiin
         yhteys.commit();
         yhteys.setAutoCommit(true);
         
         // Suljetaan tapahtumaolio
         stmt.close();
         
      } catch (SQLException poikkeus) {
         
         System.out.println("Tilaus epäonnistui: " + poikkeus.getMessage());  
         
         try {
            
            // Perutaan tapahtuma
            yhteys.rollback();
            
         } catch (SQLException poikkeus2) {
            System.out.println("Tapahtuman peruutus epäonnistui: " + poikkeus2.getMessage()); 
         }
      }
   }
   */
   /* Tapahtuma 7
    * Kuvaus: Siirretään uusien teosten tiedot divarista D1 keskustietokantaan
    * Rooli: Divarin D1 ylläpitäjä
    */
   public static void paivitaKeskustietokanta(Connection yhteys) {
   
      try {
   
         yhteys.setAutoCommit(false);
   
         // Luodaan tapahtumaolio
         Statement stmt = yhteys.createStatement();
      
         // Siirretään kaikki teosten yleiset tiedot keskustietokantaan, jos niitä ei vielä ole siellä
         stmt.executeUpdate("INSERT INTO keskus.Teos SELECT * FROM D1.Teos ON CONFLICT DO NOTHING");
      
         // Haetaan seuraavaa vaihetta varten keskustietokannan suurin kappaleID
         ResultSet rset = stmt.executeQuery("SELECT MAX(KappaleID) FROM keskus.teosKappale");
         rset.next();
         int id = rset.getInt(1);
      
         // Haetaan sellaisten teoskappaleiden tiedot, joita ei ole aikaisemmin päivitetty
         rset = stmt.executeQuery("SELECT TeosKappale.KappaleID, TeosKappale.ISBN, TeosKappale.Hinta, "
            + "TeosKappale.Ostohinta, TeosKappale.MyyntiPvm, TeosKappale.Vapaus FROM D1.TeosKappale WHERE NOT Paivitetty");
         
         // Käydään jokainen teoskappale läpi
         while (rset.next()) {
            
            // Kappaleen uusi ID
            id++;
            
            // Lisätään kappale keskustietokantaan
            stmt.executeUpdate("INSERT INTO keskus.TeosKappale VALUES ('" + id + "', '" + rset.getString(2) + "', '" 
               + rset.getBigDecimal(3) + "', '" + rset.getBigDecimal(4) + "', '" + rset.getDate(5) + "', '" + rset.getString(6) + "')");
            
            // Lisätään kappaleen id myös sijaintitauluun
            stmt.executeUpdate("INSERT INTO keskus.Sijainti VALUES ('1', '" + id + "')");
            
            // Päivitetään id alkuperäiseen kantaan ja asetetaan teoksen status päivitetyksi
            stmt.executeUpdate("UPDATE D1.TeosKappale SET KappaleID='" + id + "', Paivitetty=true WHERE KappaleID=" + rset.getInt(1));
         }
      
         System.out.println("Tiedot päivitetty keskustietokantaan onnnistuneesti!");
         
         // Sitoudutaan muutoksiin
         yhteys.commit();
         yhteys.setAutoCommit(true);
         
         // Suljetaan tapahtumaolio
         stmt.close();
         
      } catch (SQLException poikkeus) {
         
         System.out.println("Tietojen päivitys epäonnistui: " + poikkeus.getMessage());  
         
         try {
            
            // Perutaan tapahtuma
            yhteys.rollback();
            
         } catch (SQLException poikkeus2) {
            System.out.println("Tapahtuman peruutus epäonnistui: " + poikkeus2.getMessage()); 
         }
      }
	}
   
   /* Tapahtuma 8
    * Kuvaus: Siirretään divarin D4 XML-muotoinen data keskustietokantaan
    * Rooli: Keskusdivarin ylläpitäjä
    */
   public static void siirraXMLdata(Connection yhteys) {
   
      // Työkaluja XML-tiedoston käsittelyä varten
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder;
      Document doc = null;
      XPathExpression expr = null;
      
      try {
         
         yhteys.setAutoCommit(false);
   
         // Luodaan tapahtumaolio
         Statement stmt = yhteys.createStatement();
         
         // Haetaan XML-dokumentti muuttujaan
         builder = factory.newDocumentBuilder();
         doc = builder.parse("XML-data.xml");

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
            stmt.executeUpdate("INSERT INTO keskus.Teos VALUES ('" + isbn + "', '" + nimi + "', '"
            + tekija + "', null, null, null, '" + paino + "')");
            
            // Lasketaan teoksesta olevien niteiden lukumäärä
            expr = xpath.compile("count(//teos[" + i + "]/nide)");
            int nideLkm = (int)(double)expr.evaluate(doc, XPathConstants.NUMBER);
            
            // Käydään jokainen nide läpi
            for (int j = 1; j < (nideLkm+1); j++) {
            
               // Napataan talteen niteen hinta
               expr = xpath.compile("number(//teos[" + i + "]/nide[" + j + "]/hinta/text())");
               double hinta = (double)expr.evaluate(doc, XPathConstants.NUMBER);
               
               // Haetaan suurin kappaleID tietokannassa ja lisätään siihen 1
               ResultSet rset = stmt.executeQuery("SELECT MAX(KappaleID) FROM keskus.teosKappale");
               rset.next();
               int id = rset.getInt(1) + 1;
               
               // Lisätään niteen tiedot keskustietokantaan
               stmt.executeUpdate("INSERT INTO keskus.TeosKappale VALUES ('" + id + "', '" + isbn + "', '"
                  + hinta + "', null, null, 'Vapaa')");
                  
               // Lisätään myös niteen sijaintitiedot
               stmt.executeUpdate("INSERT INTO keskus.Sijainti VALUES ('4', '" + id + "')");
            }
         }

         System.out.println("XML-data siirretty onnistuneesti keskustietokantaan!");
   
         // Sitoudutaan muutoksiin
         yhteys.commit();
         yhteys.setAutoCommit(true);
         
         // Suljetaan tapahtumaolio
         stmt.close();
         
      } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException | SQLException poikkeus) {
         
         System.out.println("XML-datan lukeminen epäonnistui: " + poikkeus.getMessage());  

         try {
            
            // Perutaan tapahtuma
            yhteys.rollback();
            
         } catch (SQLException poikkeus2) {
            System.out.println("Tapahtuman peruutus epäonnistui: " + poikkeus2.getMessage()); 
         }
      }
	}
}