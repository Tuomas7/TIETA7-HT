// Luokka yhteydelle, jossa luodaan uusi yhteys-olio.
// uusiyhteys metodi palauttaa yhteyden. Luokka mahdollistaa
// yhteyden luonnin myös parametrillisella rakentajalla 
// ilman että lähdekoodiin tarvitsee muutella aina parametreja.

import java.sql.*;

public class Yhteys{

	// Yhteyden attribuutit
   	private String protokolla;
   	private String kayttaja;
   	private String salasana;
   	private Connection yhteys;
   	private String url;

   	// Parametrillinen rakentaja
   	public Yhteys(String palvelin, int portti, String tietokanta, String kayttaja, String salasana){
   		this.yhteys = null;
   		this.protokolla = "jdbc:postgresql:";
   		this.kayttaja = kayttaja;
   		this.salasana = salasana;
   		this.url = this.protokolla + "//"+ palvelin + ":"+ portti + "/" + tietokanta;
   	}

   	// Parametriton rakentaja
   	public Yhteys(){
   		this.yhteys = null;
   		this.protokolla = "jdbc:postgresql:";
   		this.kayttaja = "tt422098";
   		this.salasana = "Salasana123";
   		this.url = this.protokolla + "//" + "dbstud2.sis.uta.fi" + ":5432/" + "tt422098";
   	}

   	public Connection uusiYhteys() throws SQLException{
   		yhteys = DriverManager.getConnection(this.url, this.kayttaja, this.salasana);
   		//System.out.println("Yhteys luotu");
   		return yhteys;
   	}
}