package controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.GameModel;
import view.TilePanel;


/** ***********************************************************************
 * The view-controller class handles the display (tiles, buttons, etc.)
 * and the user input (actions from selections, clicks, etc.).
 * 
 * !!!! IMPORTANT !!!!
 * 
 * POUR INITIALISER UNE PARTIE ET OBTENIR LES BON
 * PARAM�TRES DE JEU SELON LE MODE CHOISI, IL FAUT
 * CLIQUER UNE FOIS SUR LE BOUTON NEXT. 
 * 
 * UNE FOIS CLIQU�, IL NE SERA PLUS N�CESSAIRE DE
 * REFAIRE CETTE �TAPE, TOUT SERA ACTIV� COMME DEMAND�. 
 *
 ***********************************************************************/
public class GameViewController extends JPanel implements Interface{

	/**
	 * Mod�le du jeu 
	 */
	public GameModel gameModel;

	/**
	 * Tuiles interractives qui changent de couleurs
	 */
	private TilePanel tilePanel;

	/**
	 * Gestion de l'affichage du temps
	 */
	private Chronometre chronometre;

	/**
	 * Gestion des d�lais de coloration des tuiles (gagner / perdu).
	 */
	private Timer timer;

	/**
	 * Les �l�ments Swing du panneau Menu.
	 */
	private JPanel panelInfo, panelPreviousNext;
	private Border contourPanel;
	private JLabel labelGoal, labelSum, espaceAvant, espaceApres;
	private JCheckBox checkMean, checkNoHelp;
	private JButton btnPrevious, btnNext, btnGiveUp, btnReset, btnRestart;

	/**
	 * Les �l�ments Swing de la barre d'affichage du panneau Score.
	 */
	private JPanel panelScore;
	private JButton sauvegarder, cancelSauvegarde;
	private Border contourNoir;
	private JLabel labelLevel, labelTimer, labelReset, labelScore;

	/**
	 * Variables permettant l'interaction etre les tuiles. 
	 * On les initialisent avec une valeur de d�part.
	 */
	private int nombreReset = 0;
	private int sommeTemporaire = 0;
	private int limitationClick = 0;
	private int clickRegroupement = 0;
	
	/**
	 * Variable qui garde en m�moire le niveau du joueur. 
	 * Le niveau commence � 1.
	 */
	private int level = 1;
	
	/**
	 * Variable qui garde en m�moire le numero de l'onglet utilis�
	 */
	private int index;

	/**
	 * Variables permettant de faire la somme des regroupements de valeurs.
	 * PositionTuile r�cup�re une position et doubleTuile permet le calcul des valeurs.
	 */
	private int positionTuile;
	private String doubleTuile;

	/**
	 * Variables qui r�cup�rent la position des tuiles selon la s�lection.
	 * Permet le changement de couleurs des tuiles choisies.
	 */
	private int numeroTuileClick = 0;
	private int numeroTuileRelache = 0;

	/**
	 * Constantes qui supportent le m�canisme de la remise par d�faut
	 */
	private static final int setZero = 0;
	private static final int resetTuile = 1;
	private static final int updateLabelCheck = 1;
	private static final int resetTilesAndGame = 2;

	/**
	 * Constantes qui supportent le m�canisme de perte ou victoire
	 */
	private static final int joueurPerd = 1;
	private static final int joueurGagne = 0;

	/**
	 * Listes qui r�cup�rent la position des tuiles via le click et le rel�chement
	 * de la souris pour �viter de recliquer � nouveau sur les m�mes tuiles. 
	 */
	private List<Integer> listePositionClick = new ArrayList<Integer>();
	private List<Integer> listePositionRelache = new ArrayList<Integer>();

	/**
	 * Liste qui r�cup�re l'information du jeu pour le mode Replay
	 */
	private List<Interface> replay;
	
	/**
	 * Classe qui permet l'�criture d'information dans un fichier .txt
	 */
	private Sauvegarde sauvegarde;

	/**
	 * Permet de r�cup�rer le l'information sur l'onglet utilis�
	 */
	private JTabbedPane pane;
	
	/**
	 * Variables utilis�es au support du mode Arcade
	 */
	private double numberCount;
	private double doubleDigitProba;
	private double meanProba;
	private double noHelpProba;

	/**
	 * Permet d'identifier qu'il y a un changement d'onglet
	 */
	private ChangeListener changeListener;
	
	/**
	 * �vite que le joueur puisse continuer si il abandonne dans le mode Arcade
	 */
	private boolean niveauFermer = false;


	/*******************************************************
	 * CONSTRUCTEUR
	 * 
	 * R�sumer:
	 * 
	 * Constructeur de l'interface graphique du jeu.
	 * 
	 * Re�oit en param�tre un JTabbedPane pour identifier
	 * sur quel onglet le joueur a cliqu�. 
	 * 			
	 *******************************************************/
	public GameViewController(JTabbedPane pane) {
		
		//Re�oit la s�lection d'onglets
		setPane(pane);

		//Couleur de fond gris du panneau
		setBackground(Color.GRAY);

		//Organisation des panneaux sur l'axe Y
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		//Initialise le mod�le du jeu
		gameModel = new GameModel();

		//Initialise les tuiles du jeu
		tilePanel = new TilePanel(gameModel);

		//On cr�� un nouvel enregistrement (fichier.txt)
		sauvegarde = new Sauvegarde();

		interfaceGui();
	}

	/****************************************************
	 * INTERFACE GUI
	 * 
	 * R�sumer:
	 * 
	 * Regroupement des principaux �l�ments Swing qui
	 * permet au joueur d'avoir acc�s � divers options
	 * de jeu, d'obtenir des indices de performance et 
	 * assurer une bonne exp�rience de jeu. 
	 * 			
	 ****************************************************/
	private void interfaceGui(){

		//Barre d'affichage qui regroupe les �l�ments visuels sur l'avancement du joueur
		panneauScore();

		//Ajout des tuiles interactives au panneau 
		add(tilePanel);

		//Menu qui regroupe les options de l'utilisateur 
		panneauMenu();

		//Appel les �couteurs du jeu
		setupListeners();
	}


	/*********************************************************************
	 * PANNEAU MENU 
	 * 
	 * Menu du jeu form� d'un panneau contenant tous les �l�ments Swing
	 * permettant au joueur de faire la s�lection de divers modes.
	 * 
	 * @Source setFont: http://stackoverflow.com/questions/20462167/increasing-font-size-in-a-jbutton
	 * 			
	 *********************************************************************/
	private void panneauMenu(){

		//Affiche le nombre � atteindre "Goal".
		labelGoal = new JLabel(" Goal: " + gameModel.getSommeTuiles());
		//Grossissement de la police de charact�re du nombre � atteindre.
		labelGoal.setFont(new Font("Arial", Font.BOLD, 26));


		//Affiche la somme et la quantit� de groupes form�s
		labelSum = new JLabel(" Current sum: 0 ( 0 )"); 
		//Grossissement de la police de charact�re 
		labelSum.setFont(new Font("Arial", Font.BOLD, 22));


		//Bouton qui permet de sauter � la prochaine partie
		btnPrevious = new JButton("         PREVIOUS        ");
		//Grossissement de la police de charact�re du bouton
		btnPrevious.setFont(new Font("Arial", Font.BOLD, 14));
		//On le d�sactive puisqu'il sera seulement utilis� dans le mode Replay
		btnPrevious.setEnabled(false);
		

		//Bouton qui permet de revenir � la partie pr�c�dente
		btnNext = new JButton("             NEXT            ");
		//Grossissement de la police de charact�re du bouton
		btnNext.setFont(new Font("Arial", Font.BOLD, 14));


		//Panneau regroupant les boutons "Previous" et "Next"
		panelPreviousNext = new JPanel();
		//Pour que les boutons soient orient�s horizontalement
		panelPreviousNext.setLayout(new BoxLayout(panelPreviousNext, BoxLayout.X_AXIS));
		//Garde le panneau � gauche
		panelPreviousNext.setAlignmentX( Component.LEFT_ALIGNMENT );
		//Ajout des boutons "Previous" et "Next" sur le panneau
		panelPreviousNext.add(btnPrevious);
		panelPreviousNext.add(btnNext);


		//Bouton qui permet l'abandon de la partie et qui affiche la solution
		btnGiveUp = new JButton("                               GIVE UP                                ");
		//Grossissement de la police de charact�re du bouton
		btnGiveUp.setFont(new Font("Arial", Font.BOLD, 14));


		//Bouton qui permet la remise des couleurs et de la somme temporaire � l'�tat d'origine
		btnReset = new JButton("                                 RESET                                 ");
		//Grossissement de la police de charact�re du bouton
		btnReset.setFont(new Font("Arial", Font.BOLD, 14));


		//Bouton qui permet la remise au premier niveau dans le mode Arcade seulement
		btnRestart = new JButton("                              RESTART                               ");
		//Grossissement de la police de charact�re du bouton
		btnRestart.setFont(new Font("Arial", Font.BOLD, 14));
		//On le d�sactive puisqu'il n'est disponible que dans le ode Arcade
		btnRestart.setEnabled(false);


		//Ajout d'un CheckBox pour l'option Mean
		checkMean = new JCheckBox(" Find Mean");
		//Grossissement de la police de charact�re de l'option Mean 
		checkMean.setFont(new Font("Arial", Font.BOLD, 18));
		//Couleur de fond gris du CheckBox
		checkMean.setBackground(Color.GRAY);


		//Ajout d'un CheckBox pour l'option No Help
		checkNoHelp = new JCheckBox(" No Help");
		//Grossissement de la police de charact�re de l'option No Help 
		checkNoHelp.setFont(new Font("Arial", Font.BOLD, 18));
		//Couleur de fond gris du CheckBox
		checkNoHelp.setBackground(Color.GRAY);


		//Labels utilis�s seulement pour cr�er de l'espace entre les composants Swing 
		espaceAvant = new JLabel(" ");
		espaceApres = new JLabel(" ");


		//Panneau regroupant les elements swing du menu
		panelInfo = new JPanel();
		//Couleur de fond gris du panneau Menu 
		panelInfo.setBackground(Color.GRAY);
		//Organisation des �l�ments Swing du menu sur l'axe Y
		panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
		//Garde le panneau � gauche 
		panelInfo.setAlignmentX( Component.LEFT_ALIGNMENT );
		//Ins�re un contour style 3D au panneau Menu 
		panelInfo.setBorder(BorderFactory.createRaisedBevelBorder());


		//Ajout des �l�ments Swing au panneau Menu
		panelInfo.add(labelGoal);
		panelInfo.add(labelSum);
		panelInfo.add(espaceAvant);
		panelInfo.add(panelPreviousNext);
		panelInfo.add(espaceApres);
		panelInfo.add(btnGiveUp);
		panelInfo.add(btnReset);
		panelInfo.add(btnRestart);
		panelInfo.add(checkMean);
		panelInfo.add(checkNoHelp);

		//Ajout du panneauInfo au panneau principal
		add(panelInfo);
	}

	/*********************************************************************
	 * PANNEAU SCORE
	 * 
	 * Panneau contenant tous les �l�ments Swing permettant au joueur
	 * d'obtenir des indices de performances.
	 * 
	 * @Source setFont: http://stackoverflow.com/questions/20462167/increasing-font-size-in-a-jbutton
	 * 			
	 *********************************************************************/
	private void panneauScore(){

		//Permet � tous les labels du panneauScore d'afficher un contour noir
		contourNoir = BorderFactory.createLineBorder(Color.black);


		//Affiche le niveau actuel du joueur. 
		labelLevel = new JLabel("     [ Level ] 1      ");
		//Grossissement de la police de charact�re du Level
		labelLevel.setFont(new Font("Arial", Font.BOLD, 20));
		//Affiche un contour noir autour du JLabel
		labelLevel.setBorder(contourNoir);


		//Affiche le chronom�tre en secondes et en minutes
		labelTimer = new JLabel("     [ Time ] 00 : 00     ");
		//D�marrage du chronom�tre
		chronometre = new Chronometre(labelTimer);
		//Grossissement de la police de charact�re du Timer 
		labelTimer.setFont(new Font("Arial", Font.BOLD, 20));
		//Affiche un contour noir autour du JLabel
		labelTimer.setBorder(contourNoir);


		labelReset = new JLabel("     [ Resets ] 0     ");
		//Grossissement de la police de charact�re Reset
		labelReset.setFont(new Font("Arial", Font.BOLD, 20));
		//Affiche un contour noir autour du JLabel
		labelReset.setBorder(contourNoir);
		//Couleur de fond gris du Reset
		labelReset.setBackground(Color.GRAY);


		//Affiche le Score du joueur. n/a puisque le mode arcade n'est pas activ�
		labelScore = new JLabel("     [ Score ] n/a     ");
		//Grossissement de la police de charact�re du Score
		labelScore.setFont(new Font("Arial", Font.BOLD, 20));
		//Affiche un contour noir autour du JLabel
		labelScore.setBorder(contourNoir);


		//Affiche un bouton de sauvegarde apr�s une partie gagn�e
		sauvegarder = new JButton("      Save      ");
		//Grossissement de la police de charact�re du bouton
		sauvegarder.setFont(new Font("Arial", Font.BOLD, 20));
		//Couleur de fond gris du bouton
		sauvegarder.setBackground(Color.GRAY);
		//Couleur de texte vert 
		sauvegarder.setForeground(Color.GREEN);
		//Affiche un contour noir autour du bouton
		sauvegarder.setBorder(contourNoir);
		//Sera activ� seulement lorsqu'une partie sera termin�e
		sauvegarder.setEnabled(false);


		//Affiche un bouton pour �viter la sauvergarde 
		cancelSauvegarde = new JButton("    Cancel    ");
		//Grossissement de la police de charact�re du bouton
		cancelSauvegarde.setFont(new Font("Arial", Font.BOLD, 20));
		//Couleur de fond gris du bouton
		cancelSauvegarde.setBackground(Color.GRAY);
		//Couleur de texte rouge
		cancelSauvegarde.setForeground(Color.ORANGE);
		//Affiche un contour noir autour du bouton
		cancelSauvegarde.setBorder(contourNoir);
		//Sera activ� seulement lorsqu'une partie sera termin�e
		cancelSauvegarde.setEnabled(false);


		//Panneau regroupant les �l�ments de performances du joueur
		panelScore = new JPanel();
		//Couleur de fond gris du panneau Score 
		panelScore.setBackground(Color.GRAY);
		//Cr�� un contour style 3D
		contourPanel = BorderFactory.createRaisedBevelBorder();
		//Ins�re un contour style 3D au panneau Score 
		panelScore.setBorder(contourPanel);
		//Organisation des �l�ments sur l'axe des X
		panelScore.setLayout(new BoxLayout(panelScore, BoxLayout.X_AXIS));
		//Garde le panneau � gauche
		panelScore.setAlignmentX( Component.LEFT_ALIGNMENT );


		//Ajout des �l�ments Swing au panneau Score
		panelScore.add(labelLevel);
		panelScore.add(labelTimer);
		panelScore.add(labelReset);
		panelScore.add(labelScore);
		panelScore.add(sauvegarder);
		panelScore.add(cancelSauvegarde);

		//Ajout du panneau Score au panneau principal
		add(panelScore);
	}


	/****************************************************************************
	 * LISTENERS
	 * 
	 * �couteurs d'actions selon les composants swing et de leurs fonctionnalit�s
	 * 	
	 * @Source Inspiration changement de couleur: http://stackoverflow.com/questions/24541052/tile-change-color-on-mouse-hover-prevent-selecting-more-than-one-tile-at-once
	 * @Source Inspiration D�lais: http://stackoverflow.com/questions/4044726/how-to-set-a-timer-in-java
	 * @Source Inspiration Modulo tuile: http://stackoverflow.com/questions/7139382/java-rounding-up-to-an-int-using-math-ceil
	 *
	 ****************************************************************************/
	protected void setupListeners() {		

		tilePanel.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {

				//�vite le changement de couleur en cliquant ailleurs que sur les tuiles en Y
				if(e.getY() > 10 && e.getY() < 150){

					//Modulo permet d'identifier et r�cup�rer la tuile s�lectionn�e (Tuile1 = 0, Tuile2 = 1, etc).
					//En fonction du d�calage, de la dimension et de la quantit� de tuiles, on obtient des changements de couleurs ind�pendants.
					numeroTuileRelache = (int) (Math.ceil((e.getX() - 20)/ 190)% gameModel.getQuantiteValeurs());

					//Si on s�lectionne seulement une tuile pour la cr�ation d'un groupe
					if(numeroTuileClick == numeroTuileRelache && 

							//�vite la s�lection de tuiles de fa�on d�sordonn�e gr�ce au limitateur 
							numeroTuileRelache <= limitationClick &&

							//�vite que l'on puisse recliquer sur la m�me tuile
							!listePositionRelache.contains(numeroTuileClick)){

						//Garde en m�moire le num�ro de la tuile s�lectionn�e.
						//Permet d'�viter que l'on puisse recliquer sur la m�me tuile.
						listePositionRelache.add(numeroTuileRelache);
						//listePositionClick.add(numeroTuileRelache);

						//PositionTuile prend le numero de la tuile et sert au calcul de la somme temporaire
						positionTuile = numeroTuileRelache;

						//Calcule la somme temporaire de l'utilisateur
						sommeTemporaire();

						//Initialise la tuile en fonction de la position choisi
						tilePanel.setTuileActive(numeroTuileRelache);

						//Met � jour le label somme temporaire en fonction des Check Box
						checkBoxLabel(updateLabelCheck);

						//Permet de dire si le joueur � gagn� ou perdu
						joueurGagne(numeroTuileRelache);

					}
					//Si on s�lectionne deux tuiles pour la cr�ation d'un groupe
					else{

						//Impose un sens unique � s�lection de tuiles (gauche vers la droite)
						if(numeroTuileClick <= numeroTuileRelache && 

								//�vite la s�lection de tuiles de fa�on d�sordonn�e gr�ce au limitateur 
								numeroTuileRelache <= limitationClick && 

								//�vite que l'on puisse recliquer sur une tuile et cr�er un groupe plus loin
								!listePositionRelache.contains(numeroTuileClick)){

							//Garde en m�moire le num�ro des tuiles s�lectionn�es.
							//Permet d'�vite que l'on puisse recliquer sur la m�me tuile.
							listePositionRelache.add(numeroTuileRelache);
							listePositionRelache.add(numeroTuileClick);

							//PositionTuile prend le numero de la tuile et sert au calcul de la somme temporaire
							positionTuile = numeroTuileRelache;

							//Calcule la somme temporaire de l'utilisateur
							sommeTemporaire();

							//Initialise la tuile en fonction de la position choisi
							tilePanel.setTuileActive(numeroTuileRelache);

							//Met � jour le label somme temporaire en fonction des Check Box
							checkBoxLabel(updateLabelCheck);

							//Permet de dire si le joueur � gagn� ou perdu
							joueurGagne(numeroTuileRelache);

							//limitateur qui permet d'�viter la s�lection de tuiles de fa�on d�sordon�e
							limitationClick++;
						}
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {

				//�vite le changement de couleur en cliquant ailleurs que sur les tuiles en Y
				if(e.getY() > 10 && e.getY() < 150){

					//Modulo permet d'identifier et r�cup�rer la tuile s�lectionn�e (Tuile1 = 0, Tuile2 = 1, etc).
					//En fonction du d�calage, de la dimension et de la quantit� de tuiles, on obtient des changements de couleurs ind�pendants
					numeroTuileClick = (int) (Math.ceil((e.getX() - 20)/ 190)% gameModel.getQuantiteValeurs());

					//Impose un sens unique � s�lection de tuiles (gauche vers la droite)
					if(numeroTuileClick <= limitationClick && 

							//�vite que l'on puisse recliquer sur la m�me tuile
							!listePositionClick.contains(numeroTuileClick) && 

							//�vite que l'on puisse recliquer sur une tuile et cr�er un groupe plus loin
							!listePositionRelache.contains(numeroTuileClick)){

						//Garde en m�moire le num�ro de la tuile s�lectionn�e.
						//Permet d'�vite que l'on puisse recliquer sur la m�me tuile.
						listePositionClick.add(numeroTuileClick);

						//PositionTuile prend le numero de la tuile et sert au calcul de la somme temporaire
						positionTuile = numeroTuileClick;

						//R�cup�re la position de la tuile choisi
						tilePanel.setTuileActive(numeroTuileClick);

						//Obtention du nombre de groupes form�s
						setClickGroupe(++clickRegroupement);

						//Permet le changement de couleur de la tuile
						tilePanel.changeColour();

						//limitateur qui permet d'�viter la s�lection de tuiles de fa�on d�sordon�e
						limitationClick++;
					}
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseClicked(MouseEvent e) {}

		});

		/********************************************
		 * �couteur du Bouton Next
		 * 
		 * EN CONSTRUCTION
		 * 
		 * @Source get List: http://stackoverflow.com/questions/4123299/how-to-get-datas-from-listobject-java
		 * 			
		 ********************************************/
		btnPrevious.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				//Si l'utilisateur est dans le mode Replay
				if(getIndex() == 2){
					
					//On active le bouton qui est utilis� dans le mode Replay seulement.
					btnPrevious.setEnabled(true);
					
					for(int i = 0; i < replay.size(); i++){
					
						//Pour chaque tour, on envoi l'information de la liste � l'interface
						Interface modeReplay = replay.get(i);
					}
				}
				repaint();
			}
		});


		/********************************************
		 * �couteur du Bouton Next
		 * 
		 * EN CONSTRUCTION
		 * 
		 * @Source Liste Interface : http://stackoverflow.com/questions/20526718/save-different-class-instances-in-a-list			
		 ********************************************/
		btnNext.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				//Si l'utilisateur est dans le mode Replay
				if(getIndex() == 2){
					
					//On active le bouton qui est utilis� dans le mode Replay seulement.
					btnPrevious.setEnabled(true);
					
					//Cr�� la liste qui garde en m�moire l'information envoy� � l'interface.
					//Utilis� pour la mode Replay.
					 replay = new ArrayList<Interface>();
					
					//Ajout de l'information dans la liste 
					replay.add(new GameViewController(getPane()));
				}
				
				//Met la somme temporaire et le nombre de regroupements � zero
				remisParDefaut(setZero);

				//Remets les tuiles � leur �tat d'origine et g�n�re un nouveau mod�le de jeu
				remisParDefaut(resetTilesAndGame);

				//Affiche la somme temporaire � z�ro et se met � jour en fonction des Check Box 
				checkBoxLabel(setZero);

				//Remet � jour la quantit� de tuiles affich�es
				repaint();
			}
		});

		/********************************************
		 * �couteur du Bouton Give Up
		 * 
		 * EN CONSTRUCTION
		 * 			
		 ********************************************/
		btnGiveUp.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				//Si on est dans le mode Arcade
				if(getIndex() == 3){

					//Permet de d�sactiver le changement de niveau
					//si le bouton GiveUp � �t� activ� dans le mode Arcade.
					niveauFermer = true;

					//Met la somme temporaire et le nombre de regroupements � zero
					remisParDefaut(setZero);

					//Remets les tuiles � leur �tat d'origine
					remisParDefaut(resetTuile);
					
					//On d�sactive le bouton pour des raisons esth�tiques
					btnReset.setEnabled(false);
					
					//On d�sactive le bouton pour des raisons esth�tiques
					btnGiveUp.setEnabled(false);
				}
			}
		});


		/********************************************
		 * �couteur du Bouton Reset
		 * 			
		 ********************************************/
		btnReset.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e){

				//Met la somme temporaire et le nombre de regroupements � zero
				remisParDefaut(setZero);

				//Remets les tuiles � leur �tat d'origine
				remisParDefaut(resetTuile);

				//Affiche la somme temporaire � z�ro et se met � jour en fonction des Check Box 
				checkBoxLabel(setZero);

				//Garde en m�moire le nombre de fois cliqu� sur Reset
				nombreReset ++;

				//Met � jour le labelReset avec le nombre de clic.
				labelReset.setText("     [ Resets ] " + nombreReset + "     ");
			}	
		});

		/********************************************
		 * �couteur du Bouton Restart
		 * 			
		 ********************************************/
		btnRestart.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e){

				//Si le joueur a abandon� la partie (GiveUp)
				if(niveauFermer == true){
					
					//On s'assure de lui permettre de recommencer le mode
					remisParDefaut(resetTilesAndGame);
					
					//On permet au joueur de remonter de niveau
					niveauFermer = false;
					
					repaint();
				}
				
				//On remet le mode Arcade au premier niveau
				setLevel(1);

				//Affiche le niveau actuel du joueur. 
				labelLevel.setText("     [ Level ] 1      ");
			}	
		});
		/********************************************
		 * �couteur du checkBox Mean
		 * 			
		 ********************************************/
		checkMean.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e){

				//Met la somme temporaire et le nombre de regroupements � zero
				remisParDefaut(setZero);

				//Remets les tuiles � leur �tat d'origine et g�n�re un nouveau mod�le de jeu
				remisParDefaut(resetTilesAndGame);

				//Affiche la somme temporaire � z�ro et se met � jour en fonction des Check Box 
				checkBoxLabel(setZero);

				if(checkMean.isSelected()){

					//Imprime dans la console la moyenne � atteindre
					gameModel.systemOutPrint("meanSum");
				}
				//Remet � jour la quantit� de tuiles affich�es
				repaint();
			}	
		});	

		/********************************************
		 * �couteur du checkBox Mean
		 * 			
		 ********************************************/
		checkNoHelp.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e){

				//Met la somme temporaire et le nombre de regroupements � zero
				remisParDefaut(setZero);

				//Remets les tuiles � leur �tat d'origine et g�n�re un nouveau mod�le de jeu
				remisParDefaut(resetTilesAndGame);

				//Affiche la somme temporaire � z�ro et se met � jour en fonction des Check Box 
				checkBoxLabel(setZero);

				if(checkMean.isSelected()){

					//Imprime dans la console la moyenne � atteindre
					gameModel.systemOutPrint("meanSum");
				}
				//Remet � jour la quantit� de tuiles affich�es
				repaint();
			}	
		});
		
		
		/********************************************
		 * Identification de l'onglet s�lectionn� 
		 * 	
		 * @Source ChangeListener: http://stackoverflow.com/questions/6799731/jtabbedpane-changelistener
		 ********************************************/
		getPane().addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e){

				//Cr�� un nouvel �couteur de changements
				changeListener = new ChangeListener(){

					@Override
					public void stateChanged(ChangeEvent changeEvent){

						//R�cup�re la position de l'onglet s�lectionn�
						index = getPane().getSelectedIndex();

						//Initialise la position de l'onglet s�lectionn�
						setIndex(index);
						
						gameModel.setIndex(index);

						//Permet d'envoyer le nom du mode utilis� au GameModel
						gameModel.setMode(getPane().getTitleAt(index));
					}
				};
				
				//Active l'�coute du changement d'onglets
				getPane().addChangeListener(changeListener);
			}
		});


		/*************************************************
		 * �couteur du Bouton Sauvegarde
		 * 
		 * S'active seulement lorsqu'un partie se termine
		 * 			
		 *************************************************/
		sauvegarder.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				//Apr�s la s�lection de sauvegarde ou non, on les d�sactives
				cancelSauvegarde.setEnabled(false);
				sauvegarder.setEnabled(false);

				//Puisque le joueur a gagn�, on incr�mente le niveau
				level++;

				//On imprime dans la console le niveau actuel
				sauvegarde.setLevel(String.valueOf(level));
				gameModel.setLevel(String.valueOf(level));

				//D�marre la sauvegarde de la partie sur fichier .txt
				gameModel.systemOutPrint("save");

				//Met la somme temporaire et le nombre de regroupements � zero
				remisParDefaut(setZero);

				//G�n�re un nouveau mod�le de jeu
				remisParDefaut(resetTilesAndGame);

				//Affiche la somme temporaire � z�ro et se met � jour en fonction des Check Box 
				checkBoxLabel(setZero);

				//Red�marre le processus de s�lection de couleurs
				tilePanel.initializeColours();

				labelLevel.setText("     [ Level ] " + level + "      ");

				//Permet de r�activer certains �l�ments Swing apr�s
				//la s�lectionne de sauvegarde ou non
				statutBoutons(true);

				repaint();
			}
		});

		/*****************************************************
		 * �couteur du Bouton qui cancel la Sauvegarde
		 * 
		 * S'active seulement lorsqu'une partie se termine			
		 * 
		 *****************************************************/
		cancelSauvegarde.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				//Apr�s la s�lection de sauvegarde ou non, on les d�sactives
				sauvegarder.setEnabled(false);
				cancelSauvegarde.setEnabled(false);

				//Puisque le joueur a gagn�, on incr�mente le niveau
				level++;

				//On imprime dans la console le niveau actuel
				sauvegarde.setLevel(String.valueOf(level));
				gameModel.setLevel(String.valueOf(level));

				//Met la somme temporaire et le nombre de regroupements � zero
				remisParDefaut(setZero);

				//G�n�re un nouveau mod�le de jeu
				remisParDefaut(resetTilesAndGame);

				//Affiche la somme temporaire � z�ro et se met � jour en fonction des Check Box 
				checkBoxLabel(setZero);

				//Red�marre le processus de s�lection de couleurs
				tilePanel.initializeColours();

				labelLevel.setText("     [ Level ] " + level + "      ");

				//Permet de r�activer certains �l�ments Swing apr�s
				//la s�lectionne de sauvegarde ou non
				statutBoutons(true);

				repaint();
			}
		});
	}

	/*****************************************************
	 * CHANGEMENT STATUT SWING
	 * 
	 * R�sumer:
	 * 
	 * Permet de changer le statut de certains 
	 * �l�ments Swing aux besoins.
	 * 
	 * Strat�gie pour limiter la duplication de code
	 * 			
	 *****************************************************/
	private void statutBoutons(boolean statut){

		//Les �l�ments Swing sont activ�s ou non selon le statut re�u
		btnPrevious.setEnabled(statut);
		btnNext.setEnabled(statut);
		btnReset.setEnabled(statut);
		btnGiveUp.setEnabled(statut);
		checkMean.setEnabled(statut);
		checkNoHelp.setEnabled(statut);
		btnRestart.setEnabled(statut);

		//Si nous somme dans le mode Training
		if(getIndex() == 1){
			
			//On s'assure de toujour que le bouton ne soit jamais activ�
			btnPrevious.setEnabled(false);
		}
		
		//Si nous sommes autre que dans le mode Arcade
		if(getIndex() <= 2){

			//On s'assure de toujour que le bouton ne soit jamais activ�
			btnRestart.setEnabled(false);
		}

		//Si on est dans le mode Arcade
		if(getIndex() == 3){

			//On s'assure que ces �l�ments ne soient jamais modifiable
			btnPrevious.setEnabled(false);
			btnNext.setEnabled(false);
			checkMean.setEnabled(false);
			checkNoHelp.setEnabled(false);
		}
	}

	/**************************************************************************************
	 * SOMME TEMPORAIRE
	 * 
	 * R�sum�:
	 * 
	 * Permet d'obtenir la somme temporaire des valeurs s�lectionn�es de l'utilisateur.
	 * On additionne la somme en fonction des types de regroupements.
	 *
	 **************************************************************************************/
	private void sommeTemporaire(){

		if(tilePanel.getTuileActive() != positionTuile){

			//Regroupement de deux tuiles cr�� une nouvelle valeur compos� de deux chiffres
			doubleTuile = gameModel.getValeurTuile(tilePanel.getTuileActive()) + gameModel.getValeurTuile(positionTuile);

			//Additionne les valeurs compos�es de deux chiffres s�lectionn�es
			sommeTemporaire += Integer.parseInt(doubleTuile);
		} 
		else{
			//Additionne valeurs uniques s�lectionn�es 
			sommeTemporaire += Integer.parseInt(gameModel.getValeurTuile(positionTuile));	
		}	
	}

	
	/**********************************************************
	 * VARIATION SELON NIVEAU
	 * 
	 * R�sumer:
	 * 
	 * Selon le niveau atteint par le joueur, un syst�me 
	 * de probabilit� permet de faire apparaitre divers
	 * options ou de changer certains param�tres de jeu.
	 * 
	 * On s'assure d'interdire que le mode Arcade
	 * ne d�passe plus de 20 niveaux. Sinon le joueur
	 * peut recommencer au premier niveau en tout temps
	 * 
	 *@Source probabilit�: http://stackoverflow.com/questions/36612890/do-an-action-with-some-probability-in-java
	 * 
	 **********************************************************/
	private void variationNiveau(){

		//Permet d'activer plusieurs �l�ments Swing
		statutBoutons(true);

		//On active le bouton puisque nous sommes dans le mode Arcade.
		btnRestart.setEnabled(true);

		//Tant qu'on reste entre le niveau 1 et 20
		if(getLevel() <= 20){

			//Permet de varier le nombre de groupes
			numberCount = 3 + Math.round(3.0 * getLevel() / 20);
			//Initialise une nouvelle valeur pour la quantit� de groupes
			gameModel.setQuantiteGroupes((int) numberCount);
			System.out.println("\n\nProbabilit� qte. groupes: " + numberCount);

			
			//Probabilit� d'obtenir un regroupements 
			doubleDigitProba = 0.3 + (0.3 * getLevel() / 20);
			//Initialise la nouvelle probabilit� concernant kes regroupements
			gameModel.setProbabilite(doubleDigitProba);
			System.out.println("Probabilit� qte. regroupements: " + (Math.round(doubleDigitProba * 100)) + " %");

			
			//Probabilit� qu'on demande au joueur de trouver une moyenne
			meanProba = 0.1 + (0.9 * getLevel() / 20);
			System.out.println("Probabilit� d'activation Mean: " + (Math.round(meanProba * 100)) + " %");

			//Permet de g�nm�rer un chiffre entre 0 et 1 au hasard 
			if(new Random().nextFloat() < meanProba ){ 

				//Activation du CheckBox Mean
				checkMean.setSelected(true);
			}
			else{ 
				//D�sactivation du CheckBox Mean
				checkMean.setSelected(false);
			}

			
			//Probabilit� que l'option NoHelp soit activ�
			noHelpProba = 0.1 + (0.9 * level / 20);
			System.out.println("Probabilit� d'activation No Help: " + (Math.round(noHelpProba * 100)) + " %");

			if(new Random().nextFloat() < noHelpProba){ 

				//Activation du CheckBox No Help
				checkNoHelp.setSelected(true);
				System.out.println("Option NoHelp activ�");

			}
			else{ 
				//D�sactivation du CheckBox No Help
				checkNoHelp.setSelected(false);
			}
		}
		else{
			//Si on d�passe le niveau 20, on recommence au premier niveau
			setLevel(1);
		}
	}


	/*********************************************************************
	 * GAGNER OU �CHEC
	 * 
	 * R�sumer:
	 * 
	 * On change la couleur des tuiles selon si le jouer a gagn� ou perdu la partie.
	 * 
	 * Si je joueur gagne, les tuiles deviennent verte et une nouvelle partie est initialis�e.
	 * Si le joueur perd, les tuiles deviennent rouge et les tuiles redeviennent blanches
	 * 			
	 * @Source Inspiration chronom�tre: http://stackoverflow.com/questions/4044726/how-to-set-a-timer-in-java
	 *
	 *********************************************************************/
	private void joueurGagne(int positionTuile){

		//Si le numero de la tuile qui part � 0 �quivaut � la quantit� de valeurs (tuiles)
		if((positionTuile + 1) == gameModel.getQuantiteValeurs()){

			//Remets les tuiles blanche pour �tre repeintur�es
			remisParDefaut(resetTuile);

			//Si la somme temporaire �quivaut au but
			if(Integer.parseInt(gameModel.getSommeTuiles()) == sommeTemporaire) { 

				//Si le bouton GiveUp a �t� utilis� dans le mode Arcade,
				//on s'assure que le joueur ne peut passer au prochain niveau.
				//(Les boutons Sauvegarde et CancelSauvegarde s'occupent aussi du changement de niveau)
				if(niveauFermer == true){
					
					//Active le bouton de sauvegarde 
					sauvegarder.setEnabled(false);

					//Active le bouton pour canceller la sauvegarde
					cancelSauvegarde.setEnabled(false);
					
					//Donne l'option au joueur de recommencer au d�but du mode 
					btnReset.setEnabled(true);

				}
				else{

					//Active le bouton de sauvegarde 
					sauvegarder.setEnabled(true);

					//Active le bouton pour canceller la sauvegarde
					cancelSauvegarde.setEnabled(true);
				}

				//Permet de d�sactiver certains �l�ments Swing en attend que
				//le joueur s�lectionne la sauvegarde ou non
				statutBoutons(false);

				//Permet de d�sactiver les boutons et attend que
				//le joueur s�lectionne la sauvegarde ou non.
				//On les d�sactivent ici � cause du mode Arcade
				btnPrevious.setEnabled(false);
				btnNext.setEnabled(false);

				//On arr�te le chronom�tre pour que le joueur puisse 
				//prendre une d�cision ( Save / Cancel )
				chronometre.stopTimer();

				//Tuiles affichent en vert 
				tilePanel.joueurGagne(joueurGagne);
			}
			else{ 

				//Tuiles affichent en rouge 
				tilePanel.joueurGagne(joueurPerd);

				//Met la somme temporaire et le nombre de regroupements � zero
				remisParDefaut(setZero);

				//Affiche la somme temporaire � z�ro et se met � jour en fonction des Check Box 
				checkBoxLabel(setZero);
			}
		}
	}

	/*********************************************************************
	 * UPDATE LABEL FOR CHECKBOX
	 * 
	 * R�sumer:
	 * 
	 * Met � jour le label qui affiche la somme partielle et le nombre
	 * de regroupements faits par l'utilisateur en fonction des Check Box. 
	 * 
	 * 1- Si re�oit "0", on ram�ne l'affiche du label � son �tat d'origine.
	 * 2- Si re�oit "1", le label ne fait que se mettre � jour.
	 * 
	 * Strat�gie qui permet d'�vite la duplication de code.
	 * 			
	 *********************************************************************/
	private void checkBoxLabel(int resetDefault){

		//Si le checkBox Mean est s�lectionn�
		if(checkMean.isSelected()){

			//Met � jour le label qui affiche le but � atteindre du panneau Info
			labelGoal.setText(" Goal: " + gameModel.getMean());

			//Si le checkBox No Help est s�lectionn� en m�me temps que le checkBox Mean
			if(checkNoHelp.isSelected()){

				//On retire l'affichage de la somme partielle et du nombre de regroupements
				labelSum.setText(" Current mean: n/a ( n/a )"); 
			}
			else{

				if(resetDefault == 0){

					//Met � jour le label qui affiche la somme et la quantit� de groupe form�s du panneau Info
					labelSum.setText(" Current mean: 0 ( 0 )");
				}
				else{
					//Met � jour le label qui affiche la moyenne temporaire et la quantit� de groupes form�s du panneau Info
					labelSum.setText(" Current mean: " + (sommeTemporaire / getClickGroup()) + " ( " + getClickGroup() + " ) ");
				}
			}
		}
		else{
			//Met � jour le label qui affiche le but � atteindre du panneau Info
			labelGoal.setText(" Goal: " + gameModel.getSommeTuiles());

			//Si le checkBox No Help est s�lectionn�
			if(checkNoHelp.isSelected()){

				//On retire l'affichage de la somme partielle et du nombre de regroupements
				labelSum.setText(" Current sum: n/a ( n/a )"); 
			}
			else{

				if(resetDefault == 0){

					//Met � jour le label qui affiche la somme et la quantit� de groupe form�s du panneau Info
					labelSum.setText(" Current sum: 0 ( 0 )");
				}
				else{
					//Met � jour le label qui affiche la somme et la quantit� de groupes form�sdu panneau Info
					labelSum.setText(" Current sum: " + sommeTemporaire + " ( " + getClickGroup() + " ) ");
				}
			}
		}
	}

	/********************************************************************************
	 * REMISE PAR D�FAUT
	 * 
	 * R�sumer:
	 * 
	 * Selon les param�tres re�us, on remet certains �l�ments � leur �tat d'origine.
	 * Strat�gie qui permet d'�vite la duplication de code.
	 * 
	 * Si re�oit "0", la somme temporaire et le nombre de regroupements est remis � z�ro
	 * Si re�oit "1", les tuiles sont remises � leurs �tats d'origines.
	 * Si re�oit "2", les tuiles sont remises � leurs �tats d'origines, on g�n�re une 
	 * 					nouvelle partie et on d�marre un nouveau chronom�tre.
	 * 
	 ********************************************************************************/
	private void remisParDefaut(int resetDefault){

		//Le limitateur de s�lection de tuiles est remis � z�ro
		limitationClick = 0;

		//On vide les listes des positions tuiles gard�es en m�moire
		listePositionClick.clear();
		listePositionRelache.clear();

		if(resetDefault == 0){

			//Met la somme temporaire de l'utilisateur � z�ro
			sommeTemporaire = 0;

			//Met les regroupements de l'utilisateur � zero
			clickRegroupement = 0;
		}
		else{

			//Ram�ne la position des tuiles par d�faut
			tilePanel.setTuileActive(-1);

			//Met les tuiles blanches
			tilePanel.setActiveColorIndex(-1);

			//Red�marre le processus de changement de couleurs
			tilePanel.changeColour();

			if(resetDefault == 2){

				//Si nous sommes dans le mode Training
				if(getPane().getSelectedIndex() == 1){

					//Initialisation d'une nouvelle partie
					gameModel.resetListeValeurs();
				}
				//Si nous sommes dans le mode Replay
				if(getPane().getSelectedIndex() == 2){

					//Initialisation d'une nouvelle partie
					gameModel.resetListeValeurs();			
				}
				//Si nous sommes dans le mode Arcade
				if(getIndex() == 3){

					//Initialisation d'une nouvelle partie
					gameModel.resetListeValeurs();

					//On va chercher les param�tre du mode Arcade
					variationNiveau();
				}

				if(checkMean.isSelected()){

					//Imprime dans la console la moyenne � atteindre
					gameModel.systemOutPrint("meanSum");
				}

				//Arr�t du chronom�tre actuel
				chronometre.stopTimer();

				//D�marrage d'un nouveau chronom�tre
				chronometre = new Chronometre(labelTimer);
			}
		}
	}


	/********************************************
	 * �tablie la quantit� de tuiles regroup�es
	 * 			
	 ********************************************/
	public void setClickGroupe(int click){
		this.clickRegroupement = click;
	}

	/********************************************
	 * R�cup�re la quantit� de tuiles regroup�es
	 * 			
	 ********************************************/
	public int getClickGroup(){

		//Si la quantit� de groupe d�passe la limite de tuiles possibles
		if(clickRegroupement > gameModel.getQuantiteValeurs()){

			clickRegroupement--;
		}
		return clickRegroupement;
	}

	/********************************************
	 * R�cup�re la somme temporaire du joueur
	 * 			
	 ********************************************/
	public int getSommeTemporaire(){
		return sommeTemporaire;
	}

	/********************************************
	 * �tablie la somme temporaire du joueur
	 * 			
	 ********************************************/
	public void setSommeTemporaire(int sommeTemporaire){
		this.sommeTemporaire = sommeTemporaire;
	}

	/********************************************
	 * R�cup�re le niveau actuel du joueur
	 * 			
	 ********************************************/
	public int getLevel(){
		return level;
	}

	/********************************************
	 * �tablie le niveau actuel du joueur
	 * 			
	 ********************************************/
	public void setLevel(int level){
		this.level = level;
	}

	/********************************************
	 * �tablie les options onglets
	 * 			
	 ********************************************/
	public void setPane(JTabbedPane pane){
		this.pane = pane;
	}

	/********************************************
	 * Retourne les options d'onglets
	 * 			
	 ********************************************/
	public JTabbedPane getPane(){
		return pane;
	}

	/********************************************
	 * �tablie la position de l'onglet 
	 * 			
	 ********************************************/
	public void setIndex(int index){
		this.index = index;
	}

	/********************************************
	 * Retourne la position de l'onglet 
	 * 			
	 ********************************************/
	public int getIndex(){
		return index;
	}
	
	/********************************************
	 * Retourne la position de la tuile s�lectionn�e
	 * 			
	 ********************************************/
	public int getPositionTuile(){
		return positionTuile;
	}
	
	/********************************************
	 * Getter pour l'interface et le Mode Replay
	 * 			
	 ********************************************/

	//La somme � atteindre
	@Override
	public String getSommeTuiles() {
		
		String sommeTuile = gameModel.getSommeTuiles();
		return sommeTuile;
	}
	//La moyenne � atteindre
	@Override
	public int getMean() {
		
		int mean = gameModel.getMean();
		return mean;
	}
	//Valeur affich� sur une tuile
	@Override
	public String getValeurTuile(int index) {
		
		String valeurTuile = gameModel.getValeurTuile(getPositionTuile());
		return valeurTuile;
	}
	//Quantit� de groupes g�n�r�s
	@Override
	public int getQuantiteGroupes() {
		
		int quantiteGroupes = gameModel.getQuantiteGroupes();
		return quantiteGroupes;
	}
	//Quantit� de tuiles
	@Override
	public int getQUantiteValeurs() {
		
		int quantiteValeurs = gameModel.getQuantiteValeurs();
		return quantiteValeurs;
	}
	//Affiche du chronometre
	@Override
	public String getTempsEcoule() {
	
		String chrono = chronometre.getTempsEcoule();
		return chrono;
	}
}