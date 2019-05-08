package view;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import controller.GameViewController;

/**************************************************
 *�cole de Technologies Sup�rieur
 *
 *
 *Auteurs: Alexandre Laroche
 *
 *Date: 03 mars 2017
 **************************************************/

/**
 * The game frame is the main window of the game. It instantiates the model
 * and the view-controller. The frame is filled by a single panel containing
 * all the elements, which is the {@link GameViewController} object.
 *
 */
public class GameFrame extends JFrame {

	/**
	 * Permet au joueur d'attendre avant de commencer le jeu
	 */
	private Introduction introduction;

	/**
	 * Instancie le mode Training
	 */
	private GameViewController modeTraining;

	/**
	 * Instancie le mode Replay
	 */
	private GameViewController modeReplay;

	/**
	 * Instancie le mode Arcade
	 */
	private GameViewController modeArcade;

	/**
	 * Permet de supporter un affichage avec onglets
	 */
	private JTabbedPane tabbedPane;
	
	int index;
	
	/*********************************************************
	 * FENETRE DE JEU
	 * 
	 * R�sumer:
	 * 
	 * Initialise les principaux param�tres du Frame.
	 * La dimension du Frame est adapt� de sorte � ne
	 * pas retrouv�es de tuiles � l'ext�rieur des dimensions
	 * de la fen�tre de jeu.
	 *
	 *********************************************************/
	public void initUI() {

		setTitle("Mean Sum");
		setSize(2100, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/*****************************************************************
	 * CONSTRUCTEUR
	 * 
	 * R�sumer:
	 * 
	 * Constructeur initialise le mod�le de jeu et l'interface
	 * utilisateur muni d'onglets qui m�nent vers diff�rents
	 * mode de jeu.
	 * 
	 * @Source Inspiration JTabbedPane: http://www.java2s.com/Tutorial/Java/0240__Swing/SpecifyingatabslocationTOPBOTTOMLEFTorRIGHT.htm
	 *		
	 ******************************************************************/
	public GameFrame() {		

		// Initialisation des param�tres du Frame
		initUI();

		//Permet de supporter un affichage avec onglets
		tabbedPane = new JTabbedPane();

		//Initialise une section d'attente 
		introduction = new Introduction(tabbedPane);
		
		//Initialise le mode Training et son GUI
		modeTraining = new GameViewController(tabbedPane);

		//Initialise le mode Replay et son GUI
		modeReplay = new GameViewController(tabbedPane);

		//Initialise le mode Arcade et son GUI
		modeArcade = new GameViewController(tabbedPane);
			
		//Ajout d'un onglet qui contient le mode de jeu Training
		tabbedPane.add("  Pause  ", introduction);
				
		//Ajout d'un onglet qui contient le mode de jeu Training
		tabbedPane.add(" Training ", modeTraining);

		//Ajout d'un onglet qui contient le mode de jeu Replay
		tabbedPane.add("  Replay  ", modeReplay);

		//Ajout d'un onglet qui contient le mode de jeu Arcade
		tabbedPane.add("  Arcade  ", modeArcade);

		//Grossi la police de caract�re des onglets
		tabbedPane.setFont(new Font("Arial", Font.BOLD, 20));

		//Pour que les onglets apparaissent vers le bas
		tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);

		//Frame initialise la fen�tre muni d'onglets
		setContentPane(tabbedPane);
	}
}
