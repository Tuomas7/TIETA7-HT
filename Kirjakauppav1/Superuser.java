// Superuser - luokka. Mallintaa tietokannan pääadminin istuntoa ja toimintoja.

import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;


public class Superuser{

	private Scanner lukija;
	private int id;
	private SuperuserKyselyt kyselyt;
	private HashMap<String,ArrayList<String>> kyselyTulos;


	public Superuser(int id){
		this.id = id;
		lukija = new Scanner(System.in);
		this.kyselyt = new SuperuserKyselyt();

	}

	// Myyntiraportin tulostus
	public void myyntiRaportti(){
		this.kyselyTulos = kyselyt.tulostaRaportti();

		System.out.println("\n----------------------------------------------------------------------");
		System.out.format("%30s%10s\n","Nimi","Ostot");
		System.out.println("----------------------------------------------------------------------");

		for(int i = 0 ; i < this.kyselyTulos.size(); i++){
			String nimi = this.kyselyTulos.get(String.valueOf(i)).get(0)+" "+this.kyselyTulos.get(String.valueOf(i)).get(1);
			System.out.format("%30s",nimi);
			System.out.format("%10s",this.kyselyTulos.get(String.valueOf(i)).get(2));
			System.out.println();
		}
		System.out.println("\n");
		
		

	}

	// Uuden divarin tietojen lisääminen keskustietokantaan
	public void lisaaDivariKantaan(){
		kyselyt.lisaaKanta();
	}

	// Raportti myynnissä olevien teoten hinnoista luokittain
	public void luokkaRaportti(){
		this.kyselyTulos = kyselyt.luokkaTiedot();

		System.out.println("\n----------------------------------------------------------------------");
		System.out.format("%20s%20s%20s\n","Luokka","Kokonaishinta","Keskihinta");
		System.out.println("----------------------------------------------------------------------");

		for(int i = 0 ; i < this.kyselyTulos.size(); i++){
			System.out.format("%20s",this.kyselyTulos.get(String.valueOf(i)).get(0));
			System.out.format("%20s",this.kyselyTulos.get(String.valueOf(i)).get(1));
			System.out.format("%20s",this.kyselyTulos.get(String.valueOf(i)).get(2));
			System.out.println();
		}
		System.out.println("\n");
	}



}