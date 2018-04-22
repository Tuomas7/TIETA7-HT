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


	public void myyntiRaportti(){
		this.kyselyTulos = kyselyt.tulostaRaportti();

		for(int i = 0; i< this.kyselyTulos.size(); i++){
			System.out.println(this.kyselyTulos.get(String.valueOf(i)));
		}
		

	}

	public void lisaaDivariKantaan(){
		kyselyt.lisaaKanta();
	}

	public void luokkaRaportti(){
		this.kyselyTulos = kyselyt.luokkaTiedot();

		for(int i = 0; i< this.kyselyTulos.size(); i++){
			System.out.println(this.kyselyTulos.get(String.valueOf(i)));
		}
	}



}