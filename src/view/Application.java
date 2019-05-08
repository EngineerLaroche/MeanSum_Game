package view;

import javax.swing.SwingUtilities;

public class Application {

	/*****************************************************************
	 * RUN GAME
	 * 
	 * Résumer:
	 * 
	 * Permet de démarrer le logiciel en initialisant un nouveau Frame
	 * préconfiguré avec des paramètres d'affichage.
	 * 
	 ******************************************************************/
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				GameFrame game = new GameFrame();
				game.setVisible(true);
			}
		});
	}
}
