import java.sql.*;
import java.util.Scanner;
import java.util.Stack;

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
    * Parametrit: yhteys, tilausta tekevän asiakkaan sessio, tilattavan kappaleen ID
    */
	public static void lisaaOstoskoriin(Connection yhteys, Sessio sessio, int kappaleID) {
   
      try {
   
         yhteys.setAutoCommit(false);
   
         // Luodaan tapahtumaolio
         Statement stmt = yhteys.createStatement();
         
         // Haetaan kirjan omistavan divarin id 
         ResultSet rset = stmt.executeQuery("SELECT DivariID FROM keskus.sijainti"
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
   
   
   /* Tapahtumat 4 & 5 (tilausvaihe)
    * Kuvaus: Tilataan ostoskorissa olevat teoskappaleet
    * Rooli: Asiakas
    * Parametrit: yhteys, tilausta tekevän asiakkaan sessio
    */
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
           teosKappaleet.push(rset.getInt(2));
           kokoPaino += rset.getInt(3);
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
         
         // Jatketaan, jos asiakas syöttää k:n. Kaikki muut syötteet peruvat tapahtuman
         if (valinta == 'k' || valinta == 'K') {
            
            // Käydään läpi kaikki ostoskorissa olevat teoskappaleet
            while (!(teosKappaleet.isEmpty())) {
               
               int kappaleID = teosKappaleet.pop();
               
               // Asetetaan tilaus suoritetuksi
               stmt.executeUpdate("UPDATE keskus.Tilaus SET Tila='Suoritettu' WHERE KappaleID=" + kappaleID);
               
               // Asetetaan keskusdivarissa oleva kappale myydyksi
               stmt.executeUpdate("UPDATE keskus.TeosKappale SET Vapaus='Myyty' WHERE KappaleID=" + kappaleID);
               
               // Haetaan kappaleen omistavan alkuperäisen divarin ID
               ResultSet rset2 = stmt.executeQuery("SELECT DivariID FROM keskus.sijainti WHERE KappaleID=" + kappaleID);
               int divariID = rset2.getInt(1);
         
               // Jos divari on jokin muu kuin keskustietokanta, asetetaan kappale myydyksi myös siellä
               if (divariID != 2) {
                  stmt.executeUpdate("UPDATE D" + divariID + ".TeosKappale SET Vapaus='Myyty' WHERE KappaleID=" + kappaleID);
               }
            }
            
            System.out.println("Tilaus suoritettu onnistuneesti!");
         }
         else {
            System.out.println("Tilaus peruttu");
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
}