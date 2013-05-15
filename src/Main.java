import java.io.FileNotFoundException;
import java.io.IOException;
import roundword.*;


public class Main {

	public static void main(String[] args) {
		
		/*
		 * Qui si dovrebbe chiamare il server per dargli la disponibilita',
		 * aspettare la risposta e poi avviare il gioco
		 * */

		try {
			Dictionary d = new Dictionary(Constants.dictionaryPath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
