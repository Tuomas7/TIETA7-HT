import java.sql.*;
import java.util.Scanner;

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
      System.out.println("Yksilöivä ID-tunnus: ");
      int id = scanner.nextInt();
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
    * Parametrit: yhteys, tilausta tekevän asiakkaan ID, tilattavan kappaleen ID
    */
	public static void lisaaOstoskoriin(Connection yhteys, int asiakasID, int kappaleID) {
   
      try {
   
         yhteys.setAutoCommit(false);
   
         // Luodaan tapahtumaolio
         Statement stmt = yhteys.createStatement();
         
         // Haetaan kirjan omistavan divarin id 
         ResultSet rset = stmt.executeQuery("SELECT DivariID FROM sijainti"
            + "WHERE KappaleID=" + kappaleID);
         
         int divariID = rset.getInt(1);
         String divari = "";
         
         // Päätellään divarin nimi tietokannassa
         if (divariID == 1) {
            divari = "D1";
         }
         else if (divariID == 3) {
            divari = "D3";
         }
         else if (divariID == 4) {
            divari = "D4";
         }
         else {
            divari = "keskus";
         }
      
         // Asetetaan teos varatuksi
         stmt.executeUpdate("UPDATE " + divari + ".TeosKappale SET Vapaus='Varattu'"
            + "WHERE KappaleID=" + kappaleID);
      
         // Luodaan uusi käynnissä oleva tilaus (vastaa kappaleen siirtämistä "ostoskoriin")
         stmt.executeUpdate("INSERT INTO keskus.Tilaus VALUES ('" + divariID + "', '"
            + asiakasID + "', '" + kappaleID + "', 'Käynnissä')");
      
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
   
   
   /* Tapahtumat 4 & 5 (tilausvaihe)
    * Kuvaus: Tilataan ostoskorissa olevat teoskappaleet
    * Rooli: Asiakas
    * Parametrit: yhteys, tilausta tekevän asiakkaan ID
    */
	public static void tilaaOstoskori(Connection yhteys, int asiakasID) {
   
      // Varsinainen tilaaminen tulee siis tähän
	}
   
   
   // Tapahtumat 6-8
   
}