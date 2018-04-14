import java.sql.*;
import java.util.HashMap;

public class Kyselyt{

	// Attribuutteina prepared statementtien merkkijonoesitykset
	private Yhteys yhteys;
	private Connection connection;
	private ResultSet resultset;
	private PreparedStatement preparedStatement;
	private String haeKayttajaTiedotStatement;
	private HashMap<String, String> kyselyTulos;

	// Kontruktori, asetetaan prepared statementtien rungot
	public Kyselyt(){
		this.yhteys = new Yhteys("localhost", 5432, "bookstore", "testuser", "12345");
		this.connection = null;
		this.resultset = null;
		this.preparedStatement = null;
		
		this.haeKayttajaTiedotStatement = "SELECT nimi,kayttajaid,salasana,rooli FROM kayttaja WHERE kayttajaid = ?";
	}

	// Metodi, jota kutsutaan käyttöliittymästä
	public HashMap<String,String> haeKayttajanTiedot(int id){
		String moodi = "haetiedot";
		this.yhteysHandleri(id,moodi);
		return this.kyselyTulos;
	}

	// "Yhteinen hteyshandleri kyselyille, joka hoitaa yhteyksien avaamiset ja sulkemiset"
	// kyselyntulokset tallennetaan hashmappiin, joka attribuuttina
	private void yhteysHandleri(int id,String hakumoodi){

		this.kyselyTulos = new HashMap<String,String>();

		try{
			this.connection = this.yhteys.uusiYhteys();
			
			// tässä kohtaa kutsutaan privaattia kyselyfunktiota
			if(hakumoodi.equals("haetiedot")){
				this.kayttajanTiedot(id);
			}
	

		}catch(SQLException poikkeus) {
        	System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());  

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
	
	private void kayttajanTiedot(int id) throws SQLException{
		this.preparedStatement = this.connection.prepareStatement(this.haeKayttajaTiedotStatement);
		this.preparedStatement.setInt(1,id);
		this.resultset = this.preparedStatement.executeQuery();

		while(this.resultset.next()){
			this.kyselyTulos.put("nimi",this.resultset.getString("nimi"));
			this.kyselyTulos.put("salasana",this.resultset.getString("salasana"));
			this.kyselyTulos.put("rooli",this.resultset.getString("rooli"));
		}

	}
	
}

