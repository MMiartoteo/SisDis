import java.util.ArrayList;
import java.util.List;

import roundword.*;
import roundword.ui.*;
import roundword.net.*;
import roundword.test.*;

import java.net.*;
import java.io.*;
import java.util.Set;

import org.json.*;
import org.json.zip.*;

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

		try {
			Starter starter = new Starter();

			/// 0 - Leggi parametri del giocatore e del peer locale
			if (args.length == 3) {
				String nickname = args[0];
				int portno = Integer.parseInt(args[1]); // TODO <--- REGISTRALA ANCHE NEL PEER?
				String registrarURL = args[2];
				System.out.println(nickname + ", " + portno + ", " + registrarURL);

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

				starter.startGame(nickname, portno, registrarURL);

			} else {
				System.out.println("No arguments, open the window to choose them");
				ArgumentsChooser ac = new ArgumentsChooser(starter);
				ac.setVisible(true);
			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}



	}



}
