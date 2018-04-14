import java.util.HashMap;

public class Kyselytesti{


	public static void main(String[] args){

		HashMap<String,String> kyselytulos = new HashMap<String,String>();
		

      	Kyselyt kysely = new Kyselyt();

      	kyselytulos = kysely.haeKayttajanTiedot(1);

      	System.out.println(kyselytulos.get("nimi"));
      	System.out.println(kyselytulos.get("salasana"));
      	System.out.println(kyselytulos.get("rooli"));


	}






}
