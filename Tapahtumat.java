import java.sql.*;
import java.util.Scanner;

public class Tapahtumat{

   /* Tapahtuma 1
    * Kuvaus: Kirjaudutaan sisään
    * Rooli: Asiakas
    */
	public static void tapahtuma1() {

      

	}


   /* Tapahtuma 2
    * Kuvaus: Lisätään uusi teos ja sen yksittäinen kappale divarin D1 tietokantaan
    * Rooli: Divarin D1 ylläpitäjä
    */
	public static void tapahtuma2(Connection yhteys) {

      Scanner scanner = new Scanner(System.in);
   
      // Kysytään käyttäjältä kirjan tiedot
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
   
         // Luodaan tapahtumaolio
         Statement stmt = yhteys.createStatement();
      
         // Lisätään teoksen tiedot
         stmt.executeUpdate("INSERT INTO D1.Teos VALUES ('" + isbn + "', '" + nimi + "', '"
            + tekija + "', '" + vuosi + "', '" + tyyppi + "', '" + luokka + "', '" + paino + "')");
      
         // Lisätään yksittäisen kappaleen tiedot
         stmt.executeUpdate("INSERT INTO D1.TeosKappale VALUES ('" + id + "', '" + isbn + "', '"
            + hinta + "', '" + ostohinta + "', null, 'Vapaa')");
      
         System.out.println("Tiedot lisätty onnnistuneesti!");
         stmt.close();
         
      } catch (SQLException poikkeus) {
         System.out.println("Tietojen lisäys epäonnistui: " + poikkeus.getMessage());  
      }
	}


   /* Tapahtuma 3
    * Kuvaus: Lisätään uusi kappale olemassa olevalle teokselle divarin D2 tietokantaan
    * Rooli: Divarin D2 ylläpitäjä
    */
   public static void tapahtuma3(Connection yhteys) {

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
   
         // Luodaan tapahtumaolio
         Statement stmt = yhteys.createStatement();
      
         // Tässä kohtaa pitäisi ehkä tarkistaa, että tietokannassa varmasti on ISBN:ä vastaava teos
      
         // Lisätään yksittäisen kappaleen tiedot
         stmt.executeUpdate("INSERT INTO keskus.TeosKappale VALUES ('" + id + "', '" + isbn + "', '"
            + hinta + "', '" + ostohinta + "', null, 'Vapaa')");
      
         System.out.println("Tiedot lisätty onnnistuneesti!");
         stmt.close();
         
      } catch (SQLException poikkeus) {
         System.out.println("Tietojen lisäys epäonnistui: " + poikkeus.getMessage());  
      }
	}


   // Tapahtumat 4-8
   
}