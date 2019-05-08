package view;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

import model.GameModel;

/******************************************************************
 * CLASSE TilePanel 
 * 
 * The tile panel displays all the tiles (one per digit) of the game.
 * 
 ******************************************************************/
public class TilePanel extends JPanel{

	/**
	 * Instancier le mod�le du jeu
	 */
	private GameModel gameModelHandle;

	/**
	 * Police de caract�re des valeurs affich�es sur les tuiles
	 */
	private Font font;

	/**
	 * Applique un d�lais d'affichage sur les tuiles rouges ou vertes
	 */
	private Timer timer;

	/**
	 * Variables au support des tuiles interactives
	 * 
	 * 1- R�cup�re la position de la tuile s�lectionn�e par l'utilisateur
	 * 2- R�cup�re le d�callage entre les tuiles
	 * 3- R�cup�re la position d'une couleur dans le tableau de couleurs
	 */
	private int tuileActive;
	private int decaler;
	private int activeColourIndex;

	/**
	 * Tableaux qui re�oit les valeurs � afficher sur les tuiles
	 */
	private String[] tabValeurHasard;
	
	/**
	 * Tableaux qui re�oit les couleurs � peinturer sur les tuiles
	 */
	private Color[] tileColours;

	
	/******************************************************
	 * CONSTRUCTEUR 
	 * 
	 ******************************************************/
	public TilePanel(GameModel gameModel) {

		if (gameModel == null)
			throw new IllegalArgumentException("Should provide a valid instance of GameModel!");
		
		gameModelHandle = gameModel;
		
		//Permet le changement de couleurs des composants Graphics
		initializeColours();

		//Permet d'obtenir des tuiles blanches par d�faut 
		tuileActive = -1;
	}

	
	/******************************************************
	 * GRAPHICS PAINTING COLORS
	 * 
	 * R�sumer:
	 * 
	 * Initializes an array of pre-set colours
	 * The colours are picked to ensure readability and avoid confusion.
	 * 
	 ******************************************************/
	public void initializeColours() {

		// Some tile colours in the '0xRRGGBB' format
		String[] tileColourCodes = new String[]{

				"0xFFFFFF", "0xFF9933", "0xFFD700",  "0x99FF33", "0x33FF99", "0x33FFFF", "0x3399FF", "0xB266FF", "0xFF3399" };

		// Allocate and fill our colour array with the colour codes
		tileColours = new Color[tileColourCodes.length];

		for (int i = 0; i < tileColours.length; ++i)

			tileColours[i] = Color.decode(tileColourCodes[i]);
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponents(g);

		//Applique le d�calage de d�part des tuiles
		decaler = 20;

		//Tableau contenant les valeurs g�n�r�es au hasard
		tabValeurHasard = new String[gameModelHandle.getQuantiteValeurs()];
		
		//Si tuileActive n'est pas -1 (valeurs de d�part), on cr�� des tuiles de couleurs
		if(tuileActive >= 0){

			//Incr�mente le d�calage des tuiles
			decaler += (190 * tuileActive);

			//Grossissement de la police de charact�re des valeurs sur les tuiles
			font = new Font("tailleValeur", Font.BOLD, 80);

			g.setColor(tileColours[activeColourIndex]);
			g.fillRoundRect(decaler, 10, 180, 140, 20, 20);
			g.setFont(font);
			g.setColor(Color.BLACK);
			g.drawString(gameModelHandle.getValeurTuile(tuileActive),decaler + 70,105);
			g.drawRoundRect(decaler, 10, 180, 140, 20, 20);
		}
		else{

			//Boucle qui permet de reproduire le nombre de tuiles 
			for(int i = 0; i < gameModelHandle.getQuantiteValeurs(); i++){	
				
				//Grossissement de la police de charact�re des valeurs sur les tuiles
				font = new Font("tailleValeur", Font.BOLD, 80);

				g.setColor(tileColours[activeColourIndex]);
				g.fillRoundRect(decaler, 10, 180, 140, 20, 20);
				g.setFont(font);
				g.setColor(Color.BLACK);
				g.drawString(gameModelHandle.getValeurTuile(i),decaler + 70 ,105);
				g.drawRoundRect(decaler, 10, 180, 140, 20, 20);
				
				decaler += 190;
			}
		}
		repaint();
	}
	
	
	/******************************************************
	 * CHANGEMENT COULEUR
	 * 
	 * R�sumer:
	 * 
	 * Permet le changement de couleurs des tuiles en
	 * fonction des param�tres re�us.
	 * 
	 ******************************************************/
	public void changeColour() {
		
		//setActiveColorIndex(activeColourIndex);

		if (activeColourIndex < tileColours.length - 1)

			++activeColourIndex;
		
		else
			activeColourIndex = 0;
	}

	
	/****************************************************************************
	 * PARTIE GAGN�:VERT OU ECHEC:ROUGE
	 * 
	 * R�sumer:
	 * 
	 * Permet d'afficher les tuiles en rouge ou vert selon les r�sultats du joueur.
	 * Si les tuiles deviennent rouges, on ajout un d�lais d'une seconde avant
	 * que les tuilles se remettent � l'�tat d'origine.
	 * 
	 *@Source Inspiration chronom�tre: http://stackoverflow.com/questions/4044726/how-to-set-a-timer-in-java
	 *
	 ****************************************************************************/
	public void joueurGagne(int gagne) {

		// Contient des codes de couleurs: Rouge et vert
		String[]  tileColourCodes = new String[] {"0x00FF00", "0xFF0000"};

		//Retrouve et rempli les tuiles de la couleur
		tileColours = new Color[tileColourCodes.length];

		if(gagne == 0){

			//Si on re�oit en param�tre 0, cela indique que le joueur a gagn�
			tileColours[0] = Color.decode(tileColourCodes[0]);
		}
		else{
			//Si on re�oit en param�tre 1, cela indique que le joueur a peredu
			tileColours[0] = Color.decode(tileColourCodes[1]);
			
			//Nouveau chronom�tre
			timer = new Timer();

			//Apr�s 1000 milisecondes, on performe l'action suivante
			timer.schedule(new TimerTask() {

				@Override
				public void run() {

					//Red�marre le processus de s�lection de couleurs
					initializeColours();
				};
			}, 1000);	
		}	
	}

	public void setTuileActive(int index){
		
		this.tuileActive = index;
	}

	public int getTuileActive(){
		
		return this.tuileActive;
	}
	
	public void setActiveColorIndex(int colorIndex){

		this.activeColourIndex = colorIndex;
	}
	
	public int getActiveColorIndex(){
		
		return activeColourIndex;
	}
}