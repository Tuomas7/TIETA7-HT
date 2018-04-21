import java.util.Scanner;



public class Superuser{

	private Scanner lukija;
	private int id;
	private SuperuserKyselyt kyselyt;


	public Superuser(int id){
		this.id = id;
		lukija = new Scanner(System.in);
		this.kyselyt = new SuperuserKyselyt();
	}


	public void myyntiRaportti(){
		System.out.println("tulostetaan myyntirapsa");

		

	}

	public void lisaaDivariKantaan(){


		System.out.println("tässä lisätään uusi divari keskusdivariin");
		kyselyt.lisaaKanta();
		

	}



}