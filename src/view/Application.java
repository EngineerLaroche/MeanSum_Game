package view;

import javax.swing.SwingUtilities;

public class Application {

	/*****************************************************************
	 * RUN GAME
	 * 
	 * R�sumer:
	 * 
	 * Permet de d�marrer le logiciel en initialisant un nouveau Frame
	 * pr�configur� avec des param�tres d'affichage.
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
