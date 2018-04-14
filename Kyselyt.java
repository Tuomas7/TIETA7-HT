import java.sql.*;

public class Kyselyt{

	// Attribuutteina prepared statementtien merkkijonoesitykset
	private Connection yhteys;
	private PreparedStatement preparedStatement;
	private String haeKayttajaTiedot;

	// Kontruktori, asetetaan prepared statementtien rungot
	public Kyselyt(Connection yhteys){
		this.yhteys = yhteys;
		this.preparedStatement = null;
		this.haeKayttajaTiedot = "SELECT nimi,kayttajaid,salasana,rooli FROM kayttaja WHERE kayttajaid = ?";
	}

	// Metodi palauttaa rs.objektin
	public ResultSet haeKayttajaTiedot(int id){
		this.preparedStatement = this.yhteys.prepareStatement(this.haeKayttajaTiedot);
		this.preparedStatement.setInt(1,id);
		ResultSet rs = this.preparedStatement.executeQuery();
		return rs;
	}

}

