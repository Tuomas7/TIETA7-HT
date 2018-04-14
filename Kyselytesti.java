import java.util.HashMap;

public class Kyselytesti{


	public static void main(String[] args){

		HashMap<String,String> kyselytulos = new HashMap<String,String>();
		

      	Kyselyt kysely = new Kyselyt();

      	kyselytulos = kysely.haeKayttajanTiedot(1);

      	System.out.println(kyselytulos.get("nimi"));
      	System.out.println(kyselytulos.get("salasana"));
      	System.out.println(kyselytulos.get("rooli"));

      	int id = kysely.luoID();
      	System.out.println(id);

      	boolean kirjautuminen = kysely.tarkastaKirjautuminen("ecoor1","asdasdada");
      	System.out.println(kirjautuminen);
	}






}
