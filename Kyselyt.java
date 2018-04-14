import java.sql.*;
import java.util.HashMap;

public class Kyselyt{

	// Attribuutteina prepared statementtien merkkijonoesitykset
	private Yhteys yhteys;
	private Connection connection;
	private ResultSet resultset;
	private PreparedStatement preparedStatement;
	private String haeKayttajaTiedot;
	private HashMap<String, String> kyselyTulos;

	// Kontruktori, asetetaan prepared statementtien rungot
	public Kyselyt(){
		this.yhteys = new Yhteys("localhost", 5432, "bookstore", "testuser", "12345");
		this.connection = null;
		this.resultset = null;
		this.preparedStatement = null;
		
		this.haeKayttajaTiedot = "SELECT nimi,kayttajaid,salasana,rooli FROM kayttaja WHERE kayttajaid = ?";
	}

	// Metodi palauttaa rs.objektin
	public HashMap<String,String> haeKayttajaTiedot(int id){

		this.kyselyTulos = new HashMap<String,String>();

		try{
			this.connection = this.yhteys.uusiYhteys();
			this.preparedStatement = this.connection.prepareStatement(this.haeKayttajaTiedot);
			this.preparedStatement.setInt(1,id);
			this.resultset = this.preparedStatement.executeQuery();

			while(this.resultset.next()){
				this.kyselyTulos.put("nimi",this.resultset.getString("nimi"));
				this.kyselyTulos.put("salasana",this.resultset.getString("salasana"));
				this.kyselyTulos.put("rooli",this.resultset.getString("rooli"));
			}
			

		}catch(SQLException poikkeus) {
        	System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());  
      	}
      	return this.kyselyTulos;
		
	}

	
}

