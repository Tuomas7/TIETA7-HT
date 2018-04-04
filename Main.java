import java.sql.*;

public class Main{

   // Tiedot yhteyden luomista varten
   private static final String PROTOKOLLA = "jdbc:postgresql:";
   private static final String PALVELIN = "dbstud2.sis.uta.fi";
   private static final int PORTTI = 5432;
   private static final String TIETOKANTA = "tt422098";
   private static final String KAYTTAJA = "tt422098";
   private static final String SALASANA = "Salasana123";

   // Main-metodi, joka avaa/sulkee yhteyden ja kutsuu käyttöliittymää
   public static void main(String[] args){

      Connection yhteys = null;
   
      try {
         
         // Avataan yhteys
         yhteys = DriverManager.getConnection(PROTOKOLLA + "//" + PALVELIN + ":" + PORTTI + "/" + TIETOKANTA, KAYTTAJA, SALASANA);
         
         // Kutsutaan käyttöliittymää
         Kayttoliittyma.teeAsioita(yhteys);
         
      } catch(SQLException poikkeus) {
         System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());  
      }

      // Suljetaan yhteys
      if (yhteys != null) try {
         yhteys.close();
      } catch(SQLException poikkeus) {
         System.out.println("Yhteyden sulkeminen tietokantaan ei onnistunut. Lopetetaan ohjelman suoritus.");
         return;
      }
   }
}