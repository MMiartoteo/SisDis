import roundword.*;

import javax.swing.*;

public class Main {



	public static void main(String[] args) {

		//System Swing Look and Feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Error: Impossible to load the system Look and Feel");
			System.exit(-1);
		}

//		Tests.fakePlayers_test();
//		Tests.dictionary_test();

		/// 0 - Leggi parametri del giocatore e del peer locale
		if (args.length >= 3) {
			Starter starter = new Starter();

			Constants.nickName = args[0];
			Constants.portNumber = Integer.parseInt(args[1]); // TODO <--- REGISTRALA ANCHE NEL PEER?
			Constants.registrarURL = args[2];

			//Decide if we want an artificial player
			boolean artificial = false;
			if (args.length > 3 && args[3].equals("ai")) {
				artificial = true;
			}

			System.out.println(Constants.nickName + ", " + Constants.portNumber + ", " + Constants.registrarURL);

			starter.setMessageUpdateListener(new Starter.EventListener() {
				public void messageUpdate(String msg) {
					System.out.println(msg);
				}

				@Override
				public void gameStarted() {}

				@Override
				public void gameFailedToStart(String msg) {
					System.out.println(msg);
					System.exit(-1);
				}
			});


			starter.initializeGame(Constants.nickName, Constants.portNumber, Constants.registrarURL, artificial);

		} else {
			System.out.println("No arguments, open the main menu to choose them");
			Starter.startMainMenuGame();
		}

	}



}
