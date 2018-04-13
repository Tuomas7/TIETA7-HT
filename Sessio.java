
import java.util.HashMap;
import java.util.ArrayList;
import java.sql.*;

public class Sessio{

	private Connection yhteys;
	private int id;
	private String nimi;
	private HashMap<String,String> tiedot;
	private ArrayList<String> hakuhistoria;
	private double saldo;
	private HashMap<String,String> ostoskori;
	

	public Sessio(Connection yhteys, int id){
		this.yhteys = yhteys;
		this.id=id;
		this.tiedot = new HashMap<String,String>();
		this.haeTiedot();
		this.nimi = this.tiedot.get("etunimi")+" "+this.tiedot.get("sukunimi"); 
		this.hakuhistoria = new ArrayList<>();

		// Ajatus ostoskorista: luodaan kantaan attribuutti ostoskorin merkkijonolle, joka ladataan kun käyttäjä kirjautuu sisään?
		this.ostoskori = new HashMap<String,String>();
		this.saldo = Double.parseDouble(this.tiedot.get("saldo"));
	}


	public String haeNimi(){
		return this.nimi;
	}

	public int haeID(){
		return this.id;
	}

	public String haeOsoite(){
		return this.tiedot.get("osoite");
	}

	public String haePuhelin(){
		String puhelin = this.tiedot.get("puhelin");
		if(puhelin.equals("NULL")){
			return "";
		}else{
			return puhelin;
		}
	}

	public String haeSahkoposti(){
		String sahkoposti = this.tiedot.get("sahkoposti");
		if(sahkoposti.equals("NULL")){
			return "";
		}else{
			return sahkoposti;
		}
	}

	public ArrayList<String> haeHistoria(){
		return this.hakuhistoria;
	}

	public HashMap<String,String> haeOstoskori(){
		return this.ostoskori;
	}

	public double haeSaldo(){
		return this.saldo;
	}

	public String haeTunnus(){
		return this.tiedot.get("tunnus");
	}

	public String haeSalasana(){
		String maskattu = "";
		for(int i = 0; i< this.tiedot.get("salasana").length();i++){
			maskattu = maskattu + "*";
		}
		return maskattu;
	}

	public void tulostaTiedot(){
		System.out.println("\nProfiilin tiedot:\n");
		System.out.println("Nimi: "+this.haeNimi());
		System.out.println("Osoite: "+this.haeOsoite());
		System.out.println("Puhelin: "+this.haePuhelin());
		System.out.println("Sähköposti: "+this.haeSahkoposti());
		System.out.println("Saldo: "+ this.haeSaldo());
		System.out.println("\nKäyttäjätunnus: "+this.haeTunnus());
		System.out.println("Salasana: "+this.haeSalasana());


	}

	public void tulostaOstoskori(){

	}

	public void tulostaHistoria(){
		for(String haku : this.hakuhistoria){
			System.out.println(haku);
		}
	}

	public void lisaaHakuHistoriaan(String haku){
		this.hakuhistoria.add(haku);
	}
	public void haeTiedot(){
		
		try{
			Statement stmt = this.yhteys.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT nimi,kayttajaid,salasana,rooli FROM kayttaja WHERE kayttajaid ='"+this.id+"'");
		
			while(rs.next()){
				this.tiedot.put("tunnus",rs.getString("nimi"));
				this.tiedot.put("id",rs.getString("kayttajaid"));
				this.tiedot.put("salasana", rs.getString("salasana"));
				this.tiedot.put("rooli", rs.getString("rooli"));
			}
			if(this.tiedot.get("rooli").equals("Asiakas")){
				rs = stmt.executeQuery("SELECT etunimi,sukunimi,osoite,sahkoposti,puhelin,saldo FROM asiakas WHERE asiakasid ='"+this.id+"'");

				while(rs.next()){
					this.tiedot.put("etunimi",rs.getString("etunimi"));
					this.tiedot.put("sukunimi",rs.getString("sukunimi"));
					this.tiedot.put("osoite",rs.getString("osoite"));
					this.tiedot.put("puhelin",rs.getString("puhelin"));
					this.tiedot.put("sahkoposti", rs.getString("sahkoposti"));
					this.tiedot.put("saldo",rs.getString("saldo"));
				}
			}
			
		}catch (SQLException poikkeus){
			System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());
		}
	}
}