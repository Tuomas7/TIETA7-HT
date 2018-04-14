import java.sql.*;
import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;

public class Kyselyt{

	// Attribuutteina prepared statementtien merkkijonoesitykset
	private Yhteys yhteys;
	private Connection connection;
	private ResultSet resultset;
	
	private String moodi;

	private PreparedStatement preparedStatement;

	private String haeKayttajaTiedotStatement;
	private String kirjautumisStatement;
	private String luoIDStatement;
	private String tarkastaNimiStatement;

	// Tietorakenteet, joihin tallennetaan kyselyiden tuloksia luokan sisällä
	private HashMap<String, String> kyselyTulos;
	private ArrayList<String> kyselyLista;
	private int kyselyLuku;
	private boolean kyselybool;

	// Apumerkkijonoja
	private String input1;
	private String input2;
	private int paramInt;

	private int returnInt;


	// Kontruktori, asetetaan prepared statementtien rungot
	public Kyselyt(){
		this.yhteys = new Yhteys("localhost", 5432, "bookstore", "testuser", "12345");
		this.connection = null;
		this.resultset = null;
		this.preparedStatement = null;
		
		this.haeKayttajaTiedotStatement = "SELECT nimi,kayttajaid,salasana,rooli FROM kayttaja WHERE kayttajaid = ?";
		this.kirjautumisStatement = "SELECT nimi,kayttajaid,rooli FROM kayttaja WHERE salasana =?";
		this.luoIDStatement = "SELECT kayttajaid FROM kayttaja";
		this.tarkastaNimiStatement = "SELECT nimi FROM kayttaja";

	}

	// Metodi, jota kutsutaan käyttöliittymästä
	public HashMap<String,String> haeKayttajanTiedot(int id){
		
		this.paramInt = id;
		this.moodi = "haetiedot";
		this.kyselyTulos = new HashMap<String,String>();
		
		this.yhteysHandleri();

		return this.kyselyTulos;
	}

	// Luo käyttäjälle uuden ID:n
	public int luoID(){
		this.moodi ="luoid";
		this.yhteysHandleri();

		return this.returnInt;

	}

	// 
	public boolean tarkastaKirjautuminen(String tunnus, String salasana){

		this.kyselybool = false;
		this.input1 = tunnus;
		this.input2 = salasana;
		this.moodi = "tarkastakirjautuminen";
		this.kyselyLista = new ArrayList<>();
		
		this.yhteysHandleri();

		return this.kyselybool;
	}

	// Yhteinen "yhteyshandleri" kyselyille, joka hoitaa yhteyksien avaamiset ja sulkemiset
	// kyselyn tulokset tallennetaan oliomuuttujiin (HashMap, ArrayList, int)
	private void yhteysHandleri(){


		try{
			this.connection = this.yhteys.uusiYhteys();
			
			// tässä kohtaa kutsutaan privaattia kyselyfunktiota
			if(this.moodi.equals("haetiedot")){
				this.kayttajanTiedot();
			}else if(this.moodi.equals("luoid")){
				this.id();
			}else if(this.moodi.equals("tarkastakirjautuminen")){
				this.kirjautuminen();
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

	private void kayttajanTiedot() throws SQLException{

		this.preparedStatement = this.connection.prepareStatement(this.haeKayttajaTiedotStatement);
		this.preparedStatement.setInt(1,this.paramInt);
		this.resultset = this.preparedStatement.executeQuery();

		while(this.resultset.next()){
			this.kyselyTulos.put("nimi",this.resultset.getString("nimi"));
			this.kyselyTulos.put("salasana",this.resultset.getString("salasana"));
			this.kyselyTulos.put("rooli",this.resultset.getString("rooli"));
		}

	}
	private void id() throws SQLException{

		// Luodaan satunnainen luku välillä 1-1000
		Random rand = new Random();
		this.returnInt = rand.nextInt(1000) + 1;
		
		this.preparedStatement = this.connection.prepareStatement(this.luoIDStatement);
		this.resultset = this.preparedStatement.executeQuery();

		// Käydään läpi kaikki käyttäjä-relaation käyttäjäID:t.
		while(this.resultset.next()){
			
			// Jos luotu ID löytyy jo kannasta asetetaan luvun arvoksi 0
			if(this.resultset.getInt("kayttajaid") == this.returnInt){
				this.returnInt = 0;
			}
		}
	}

	private void kirjautuminen() throws SQLException{

		this.preparedStatement = this.connection.prepareStatement(this.kirjautumisStatement);
		this.preparedStatement.setString(1,this.input2);
		this.resultset = this.preparedStatement.executeQuery();

		while(this.resultset.next()){
			if(this.resultset.getString("nimi").equals(this.input1)){
				this.kyselybool = true;
			}
		}

	}
	
}

