
import java.util.HashMap;
import java.util.ArrayList;

public class Sessio{

	private int id;
	private String nimi;
	private HashMap<String,String> tiedot;
	private ArrayList<String> hakuhistoria;
	private double saldo;
	private HashMap<String,String> ostoskori;

	public Sessio(HashMap<String, String> tiedot){
		this.id = tiedot.get["id"];
		this.nimi = tiedot.get["etunimi"] + " " + tiedot.get["sukunimi"];
		this.tiedot = tiedot;
		this.hakuhistoria = new ArrayList<>();
		this.ostoskori = new HashMap<String,String>();
		this.saldo = tiedot.get["saldo"];
	}

	public String haeNimi(){
		return this.nimi;
	}

	public int haeID(){
		return this.id;
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

	public void tulostaTiedot(){

	}

	public void tulostaOstoskori(){

	}


}