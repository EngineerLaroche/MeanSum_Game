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
 * PARAMÈTRES DE JEU SELON LE MODE CHOISI, IL FAUT
 * CLIQUER UNE FOIS SUR LE BOUTON NEXT. 
 * 
 * UNE FOIS CLIQUÉ, IL NE SERA PLUS NÉCESSAIRE DE
 * REFAIRE CETTE ÉTAPE, TOUT SERA ACTIVÉ COMME DEMANDÉ. 
 *
 ***********************************************************************/
public class GameViewController extends JPanel implements Interface{

	/**
	 * Modèle du jeu 
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
	 * Gestion des délais de coloration des tuiles (gagner / perdu).
	 */
	private Timer timer;

	/**
	 * Les éléments Swing du panneau Menu.
	 */
	private JPanel panelInfo, panelPreviousNext;
	private Border contourPanel;
	private JLabel labelGoal, labelSum, espaceAvant, espaceApres;
	private JCheckBox checkMean, checkNoHelp;
	private JButton btnPrevious, btnNext, btnGiveUp, btnReset, btnRestart;

	/**
	 * Les éléments Swing de la barre d'affichage du panneau Score.
	 */
	private JPanel panelScore;
	private JButton sauvegarder, cancelSauvegarde;
	private Border contourNoir;
	private JLabel labelLevel, labelTimer, labelReset, labelScore;

	/**
	 * Variables permettant l'interaction etre les tuiles. 
	 * On les initialisent avec une valeur de départ.
	 */
	private int nombreReset = 0;
	private int sommeTemporaire = 0;
	private int limitationClick = 0;
	private int clickRegroupement = 0;
	
	/**
	 * Variable qui garde en mémoire le niveau du joueur. 
	 * Le niveau commence à 1.
	 */
	private int level = 1;
	
	/**
	 * Variable qui garde en mémoire le numero de l'onglet utilisé
	 */
	private int index;

	/**
	 * Variables permettant de faire la somme des regroupements de valeurs.
	 * PositionTuile récupère une position et doubleTuile permet le calcul des valeurs.
	 */
	private int positionTuile;
	private String doubleTuile;

	/**
	 * Variables qui récupèrent la position des tuiles selon la sélection.
	 * Permet le changement de couleurs des tuiles choisies.
	 */
	private int numeroTuileClick = 0;
	private int numeroTuileRelache = 0;

	/**
	 * Constantes qui supportent le mécanisme de la remise par défaut
	 */
	private static final int setZero = 0;
	private static final int resetTuile = 1;
	private static final int updateLabelCheck = 1;
	private static final int resetTilesAndGame = 2;

	/**
	 * Constantes qui supportent le mécanisme de perte ou victoire
	 */
	private static final int joueurPerd = 1;
	private static final int joueurGagne = 0;

	/**
	 * Listes qui récupèrent la position des tuiles via le click et le relâchement
	 * de la souris pour éviter de recliquer à nouveau sur les mêmes tuiles. 
	 */
	private List<Integer> listePositionClick = new ArrayList<Integer>();
	private List<Integer> listePositionRelache = new ArrayList<Integer>();

	/**
	 * Liste qui récupère l'information du jeu pour le mode Replay
	 */
	private List<Interface> replay;
	
	/**
	 * Classe qui permet l'écriture d'information dans un fichier .txt
	 */
	private Sauvegarde sauvegarde;

	/**
	 * Permet de récupérer le l'information sur l'onglet utilisé
	 */
	private JTabbedPane pane;
	
	/**
	 * Variables utilisées au support du mode Arcade
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
	 * Évite que le joueur puisse continuer si il abandonne dans le mode Arcade
	 */
	private boolean niveauFermer = false;


	/*******************************************************
	 * CONSTRUCTEUR
	 * 
	 * Résumer:
	 * 
	 * Constructeur de l'interface graphique du jeu.
	 * 
	 * Reçoit en paramètre un JTabbedPane pour identifier
	 * sur quel onglet le joueur a cliqué. 
	 * 			
	 *******************************************************/
	public GameViewController(JTabbedPane pane) {
		
		//Reçoit la sélection d'onglets
		setPane(pane);

		//Couleur de fond gris du panneau
		setBackground(Color.GRAY);

		//Organisation des panneaux sur l'axe Y
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		//Initialise le modèle du jeu
		gameModel = new GameModel();

		//Initialise les tuiles du jeu
		tilePanel = new TilePanel(gameModel);

		//On créé un nouvel enregistrement (fichier.txt)
		sauvegarde = new Sauvegarde();

		interfaceGui();
	}

	/****************************************************
	 * INTERFACE GUI
	 * 
	 * Résumer:
	 * 
	 * Regroupement des principaux éléments Swing qui
	 * permet au joueur d'avoir accès à divers options
	 * de jeu, d'obtenir des indices de performance et 
	 * assurer une bonne expérience de jeu. 
	 * 			
	 ****************************************************/
	private void interfaceGui(){

		//Barre d'affichage qui regroupe les éléments visuels sur l'avancement du joueur
		panneauScore();

		//Ajout des tuiles interactives au panneau 
		add(tilePanel);

		//Menu qui regroupe les options de l'utilisateur 
		panneauMenu();

		//Appel les écouteurs du jeu
		setupListeners();
	}


	/*********************************************************************
	 * PANNEAU MENU 
	 * 
	 * Menu du jeu formé d'un panneau contenant tous les éléments Swing
	 * permettant au joueur de faire la sélection de divers modes.
	 * 
	 * @Source setFont: http://stackoverflow.com/questions/20462167/increasing-font-size-in-a-jbutton
	 * 			
	 *********************************************************************/
	private void panneauMenu(){

		//Affiche le nombre à atteindre "Goal".
		labelGoal = new JLabel(" Goal: " + gameModel.getSommeTuiles());
		//Grossissement de la police de charactère du nombre à atteindre.
		labelGoal.setFont(new Font("Arial", Font.BOLD, 26));


		//Affiche la somme et la quantité de groupes formés
		labelSum = new JLabel(" Current sum: 0 ( 0 )"); 
		//Grossissement de la police de charactère 
		labelSum.setFont(new Font("Arial", Font.BOLD, 22));


		//Bouton qui permet de sauter à la prochaine partie
		btnPrevious = new JButton("         PREVIOUS        ");
		//Grossissement de la police de charactère du bouton
		btnPrevious.setFont(new Font("Arial", Font.BOLD, 14));
		//On le désactive puisqu'il sera seulement utilisé dans le mode Replay
		btnPrevious.setEnabled(false);
		

		//Bouton qui permet de revenir à la partie précédente
		btnNext = new JButton("             NEXT            ");
		//Grossissement de la police de charactère du bouton
		btnNext.setFont(new Font("Arial", Font.BOLD, 14));


		//Panneau regroupant les boutons "Previous" et "Next"
		panelPreviousNext = new JPanel();
		//Pour que les boutons soient orientés horizontalement
		panelPreviousNext.setLayout(new BoxLayout(panelPreviousNext, BoxLayout.X_AXIS));
		//Garde le panneau à gauche
		panelPreviousNext.setAlignmentX( Component.LEFT_ALIGNMENT );
		//Ajout des boutons "Previous" et "Next" sur le panneau
		panelPreviousNext.add(btnPrevious);
		panelPreviousNext.add(btnNext);


		//Bouton qui permet l'abandon de la partie et qui affiche la solution
		btnGiveUp = new JButton("                               GIVE UP                                ");
		//Grossissement de la police de charactère du bouton
		btnGiveUp.setFont(new Font("Arial", Font.BOLD, 14));


		//Bouton qui permet la remise des couleurs et de la somme temporaire à l'état d'origine
		btnReset = new JButton("                                 RESET                                 ");
		//Grossissement de la police de charactère du bouton
		btnReset.setFont(new Font("Arial", Font.BOLD, 14));


		//Bouton qui permet la remise au premier niveau dans le mode Arcade seulement
		btnRestart = new JButton("                              RESTART                               ");
		//Grossissement de la police de charactère du bouton
		btnRestart.setFont(new Font("Arial", Font.BOLD, 14));
		//On le désactive puisqu'il n'est disponible que dans le ode Arcade
		btnRestart.setEnabled(false);


		//Ajout d'un CheckBox pour l'option Mean
		checkMean = new JCheckBox(" Find Mean");
		//Grossissement de la police de charactère de l'option Mean 
		checkMean.setFont(new Font("Arial", Font.BOLD, 18));
		//Couleur de fond gris du CheckBox
		checkMean.setBackground(Color.GRAY);


		//Ajout d'un CheckBox pour l'option No Help
		checkNoHelp = new JCheckBox(" No Help");
		//Grossissement de la police de charactère de l'option No Help 
		checkNoHelp.setFont(new Font("Arial", Font.BOLD, 18));
		//Couleur de fond gris du CheckBox
		checkNoHelp.setBackground(Color.GRAY);


		//Labels utilisés seulement pour créer de l'espace entre les composants Swing 
		espaceAvant = new JLabel(" ");
		espaceApres = new JLabel(" ");


		//Panneau regroupant les elements swing du menu
		panelInfo = new JPanel();
		//Couleur de fond gris du panneau Menu 
		panelInfo.setBackground(Color.GRAY);
		//Organisation des éléments Swing du menu sur l'axe Y
		panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
		//Garde le panneau à gauche 
		panelInfo.setAlignmentX( Component.LEFT_ALIGNMENT );
		//Insère un contour style 3D au panneau Menu 
		panelInfo.setBorder(BorderFactory.createRaisedBevelBorder());


		//Ajout des éléments Swing au panneau Menu
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
	 * Panneau contenant tous les éléments Swing permettant au joueur
	 * d'obtenir des indices de performances.
	 * 
	 * @Source setFont: http://stackoverflow.com/questions/20462167/increasing-font-size-in-a-jbutton
	 * 			
	 *********************************************************************/
	private void panneauScore(){

		//Permet à tous les labels du panneauScore d'afficher un contour noir
		contourNoir = BorderFactory.createLineBorder(Color.black);


		//Affiche le niveau actuel du joueur. 
		labelLevel = new JLabel("     [ Level ] 1      ");
		//Grossissement de la police de charactère du Level
		labelLevel.setFont(new Font("Arial", Font.BOLD, 20));
		//Affiche un contour noir autour du JLabel
		labelLevel.setBorder(contourNoir);


		//Affiche le chronomètre en secondes et en minutes
		labelTimer = new JLabel("     [ Time ] 00 : 00     ");
		//Démarrage du chronomètre
		chronometre = new Chronometre(labelTimer);
		//Grossissement de la police de charactère du Timer 
		labelTimer.setFont(new Font("Arial", Font.BOLD, 20));
		//Affiche un contour noir autour du JLabel
		labelTimer.setBorder(contourNoir);


		labelReset = new JLabel("     [ Resets ] 0     ");
		//Grossissement de la police de charactère Reset
		labelReset.setFont(new Font("Arial", Font.BOLD, 20));
		//Affiche un contour noir autour du JLabel
		labelReset.setBorder(contourNoir);
		//Couleur de fond gris du Reset
		labelReset.setBackground(Color.GRAY);


		//Affiche le Score du joueur. n/a puisque le mode arcade n'est pas activé
		labelScore = new JLabel("     [ Score ] n/a     ");
		//Grossissement de la police de charactère du Score
		labelScore.setFont(new Font("Arial", Font.BOLD, 20));
		//Affiche un contour noir autour du JLabel
		labelScore.setBorder(contourNoir);


		//Affiche un bouton de sauvegarde après une partie gagnée
		sauvegarder = new JButton("      Save      ");
		//Grossissement de la police de charactère du bouton
		sauvegarder.setFont(new Font("Arial", Font.BOLD, 20));
		//Couleur de fond gris du bouton
		sauvegarder.setBackground(Color.GRAY);
		//Couleur de texte vert 
		sauvegarder.setForeground(Color.GREEN);
		//Affiche un contour noir autour du bouton
		sauvegarder.setBorder(contourNoir);
		//Sera activé seulement lorsqu'une partie sera terminée
		sauvegarder.setEnabled(false);


		//Affiche un bouton pour éviter la sauvergarde 
		cancelSauvegarde = new JButton("    Cancel    ");
		//Grossissement de la police de charactère du bouton
		cancelSauvegarde.setFont(new Font("Arial", Font.BOLD, 20));
		//Couleur de fond gris du bouton
		cancelSauvegarde.setBackground(Color.GRAY);
		//Couleur de texte rouge
		cancelSauvegarde.setForeground(Color.ORANGE);
		//Affiche un contour noir autour du bouton
		cancelSauvegarde.setBorder(contourNoir);
		//Sera activé seulement lorsqu'une partie sera terminée
		cancelSauvegarde.setEnabled(false);


		//Panneau regroupant les éléments de performances du joueur
		panelScore = new JPanel();
		//Couleur de fond gris du panneau Score 
		panelScore.setBackground(Color.GRAY);
		//Créé un contour style 3D
		contourPanel = BorderFactory.createRaisedBevelBorder();
		//Insère un contour style 3D au panneau Score 
		panelScore.setBorder(contourPanel);
		//Organisation des éléments sur l'axe des X
		panelScore.setLayout(new BoxLayout(panelScore, BoxLayout.X_AXIS));
		//Garde le panneau à gauche
		panelScore.setAlignmentX( Component.LEFT_ALIGNMENT );


		//Ajout des éléments Swing au panneau Score
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
	 * Écouteurs d'actions selon les composants swing et de leurs fonctionnalités
	 * 	
	 * @Source Inspiration changement de couleur: http://stackoverflow.com/questions/24541052/tile-change-color-on-mouse-hover-prevent-selecting-more-than-one-tile-at-once
	 * @Source Inspiration Délais: http://stackoverflow.com/questions/4044726/how-to-set-a-timer-in-java
	 * @Source Inspiration Modulo tuile: http://stackoverflow.com/questions/7139382/java-rounding-up-to-an-int-using-math-ceil
	 *
	 ****************************************************************************/
	protected void setupListeners() {		

		tilePanel.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {

				//Évite le changement de couleur en cliquant ailleurs que sur les tuiles en Y
				if(e.getY() > 10 && e.getY() < 150){

					//Modulo permet d'identifier et récupérer la tuile sélectionnée (Tuile1 = 0, Tuile2 = 1, etc).
					//En fonction du décalage, de la dimension et de la quantité de tuiles, on obtient des changements de couleurs indépendants.
					numeroTuileRelache = (int) (Math.ceil((e.getX() - 20)/ 190)% gameModel.getQuantiteValeurs());

					//Si on sélectionne seulement une tuile pour la création d'un groupe
					if(numeroTuileClick == numeroTuileRelache && 

							//Évite la sélection de tuiles de façon désordonnée grâce au limitateur 
							numeroTuileRelache <= limitationClick &&

							//Évite que l'on puisse recliquer sur la même tuile
							!listePositionRelache.contains(numeroTuileClick)){

						//Garde en mémoire le numéro de la tuile sélectionnée.
						//Permet d'éviter que l'on puisse recliquer sur la même tuile.
						listePositionRelache.add(numeroTuileRelache);
						//listePositionClick.add(numeroTuileRelache);

						//PositionTuile prend le numero de la tuile et sert au calcul de la somme temporaire
						positionTuile = numeroTuileRelache;

						//Calcule la somme temporaire de l'utilisateur
						sommeTemporaire();

						//Initialise la tuile en fonction de la position choisi
						tilePanel.setTuileActive(numeroTuileRelache);

						//Met à jour le label somme temporaire en fonction des Check Box
						checkBoxLabel(updateLabelCheck);

						//Permet de dire si le joueur à gagné ou perdu
						joueurGagne(numeroTuileRelache);

					}
					//Si on sélectionne deux tuiles pour la création d'un groupe
					else{

						//Impose un sens unique à sélection de tuiles (gauche vers la droite)
						if(numeroTuileClick <= numeroTuileRelache && 

								//Évite la sélection de tuiles de façon désordonnée grâce au limitateur 
								numeroTuileRelache <= limitationClick && 

								//Évite que l'on puisse recliquer sur une tuile et créer un groupe plus loin
								!listePositionRelache.contains(numeroTuileClick)){

							//Garde en mémoire le numéro des tuiles sélectionnées.
							//Permet d'évite que l'on puisse recliquer sur la même tuile.
							listePositionRelache.add(numeroTuileRelache);
							listePositionRelache.add(numeroTuileClick);

							//PositionTuile prend le numero de la tuile et sert au calcul de la somme temporaire
							positionTuile = numeroTuileRelache;

							//Calcule la somme temporaire de l'utilisateur
							sommeTemporaire();

							//Initialise la tuile en fonction de la position choisi
							tilePanel.setTuileActive(numeroTuileRelache);

							//Met à jour le label somme temporaire en fonction des Check Box
							checkBoxLabel(updateLabelCheck);

							//Permet de dire si le joueur à gagné ou perdu
							joueurGagne(numeroTuileRelache);

							//limitateur qui permet d'éviter la sélection de tuiles de façon désordonée
							limitationClick++;
						}
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {

				//Évite le changement de couleur en cliquant ailleurs que sur les tuiles en Y
				if(e.getY() > 10 && e.getY() < 150){

					//Modulo permet d'identifier et récupérer la tuile sélectionnée (Tuile1 = 0, Tuile2 = 1, etc).
					//En fonction du décalage, de la dimension et de la quantité de tuiles, on obtient des changements de couleurs indépendants
					numeroTuileClick = (int) (Math.ceil((e.getX() - 20)/ 190)% gameModel.getQuantiteValeurs());

					//Impose un sens unique à sélection de tuiles (gauche vers la droite)
					if(numeroTuileClick <= limitationClick && 

							//Évite que l'on puisse recliquer sur la même tuile
							!listePositionClick.contains(numeroTuileClick) && 

							//Évite que l'on puisse recliquer sur une tuile et créer un groupe plus loin
							!listePositionRelache.contains(numeroTuileClick)){

						//Garde en mémoire le numéro de la tuile sélectionnée.
						//Permet d'évite que l'on puisse recliquer sur la même tuile.
						listePositionClick.add(numeroTuileClick);

						//PositionTuile prend le numero de la tuile et sert au calcul de la somme temporaire
						positionTuile = numeroTuileClick;

						//Récupère la position de la tuile choisi
						tilePanel.setTuileActive(numeroTuileClick);

						//Obtention du nombre de groupes formés
						setClickGroupe(++clickRegroupement);

						//Permet le changement de couleur de la tuile
						tilePanel.changeColour();

						//limitateur qui permet d'éviter la sélection de tuiles de façon désordonée
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
		 * Écouteur du Bouton Next
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
					
					//On active le bouton qui est utilisé dans le mode Replay seulement.
					btnPrevious.setEnabled(true);
					
					for(int i = 0; i < replay.size(); i++){
					
						//Pour chaque tour, on envoi l'information de la liste à l'interface
						Interface modeReplay = replay.get(i);
					}
				}
				repaint();
			}
		});


		/********************************************
		 * Écouteur du Bouton Next
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
					
					//On active le bouton qui est utilisé dans le mode Replay seulement.
					btnPrevious.setEnabled(true);
					
					//Créé la liste qui garde en mémoire l'information envoyé à l'interface.
					//Utilisé pour la mode Replay.
					 replay = new ArrayList<Interface>();
					
					//Ajout de l'information dans la liste 
					replay.add(new GameViewController(getPane()));
				}
				
				//Met la somme temporaire et le nombre de regroupements à zero
				remisParDefaut(setZero);

				//Remets les tuiles à leur état d'origine et génère un nouveau modèle de jeu
				remisParDefaut(resetTilesAndGame);

				//Affiche la somme temporaire à zéro et se met à jour en fonction des Check Box 
				checkBoxLabel(setZero);

				//Remet à jour la quantité de tuiles affichées
				repaint();
			}
		});

		/********************************************
		 * Écouteur du Bouton Give Up
		 * 
		 * EN CONSTRUCTION
		 * 			
		 ********************************************/
		btnGiveUp.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				//Si on est dans le mode Arcade
				if(getIndex() == 3){

					//Permet de désactiver le changement de niveau
					//si le bouton GiveUp à été activé dans le mode Arcade.
					niveauFermer = true;

					//Met la somme temporaire et le nombre de regroupements à zero
					remisParDefaut(setZero);

					//Remets les tuiles à leur état d'origine
					remisParDefaut(resetTuile);
					
					//On désactive le bouton pour des raisons esthétiques
					btnReset.setEnabled(false);
					
					//On désactive le bouton pour des raisons esthétiques
					btnGiveUp.setEnabled(false);
				}
			}
		});


		/********************************************
		 * Écouteur du Bouton Reset
		 * 			
		 ********************************************/
		btnReset.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e){

				//Met la somme temporaire et le nombre de regroupements à zero
				remisParDefaut(setZero);

				//Remets les tuiles à leur état d'origine
				remisParDefaut(resetTuile);

				//Affiche la somme temporaire à zéro et se met à jour en fonction des Check Box 
				checkBoxLabel(setZero);

				//Garde en mémoire le nombre de fois cliqué sur Reset
				nombreReset ++;

				//Met à jour le labelReset avec le nombre de clic.
				labelReset.setText("     [ Resets ] " + nombreReset + "     ");
			}	
		});

		/********************************************
		 * Écouteur du Bouton Restart
		 * 			
		 ********************************************/
		btnRestart.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e){

				//Si le joueur a abandoné la partie (GiveUp)
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
		 * Écouteur du checkBox Mean
		 * 			
		 ********************************************/
		checkMean.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e){

				//Met la somme temporaire et le nombre de regroupements à zero
				remisParDefaut(setZero);

				//Remets les tuiles à leur état d'origine et génère un nouveau modèle de jeu
				remisParDefaut(resetTilesAndGame);

				//Affiche la somme temporaire à zéro et se met à jour en fonction des Check Box 
				checkBoxLabel(setZero);

				if(checkMean.isSelected()){

					//Imprime dans la console la moyenne à atteindre
					gameModel.systemOutPrint("meanSum");
				}
				//Remet à jour la quantité de tuiles affichées
				repaint();
			}	
		});	

		/********************************************
		 * Écouteur du checkBox Mean
		 * 			
		 ********************************************/
		checkNoHelp.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e){

				//Met la somme temporaire et le nombre de regroupements à zero
				remisParDefaut(setZero);

				//Remets les tuiles à leur état d'origine et génère un nouveau modèle de jeu
				remisParDefaut(resetTilesAndGame);

				//Affiche la somme temporaire à zéro et se met à jour en fonction des Check Box 
				checkBoxLabel(setZero);

				if(checkMean.isSelected()){

					//Imprime dans la console la moyenne à atteindre
					gameModel.systemOutPrint("meanSum");
				}
				//Remet à jour la quantité de tuiles affichées
				repaint();
			}	
		});
		
		
		/********************************************
		 * Identification de l'onglet sélectionné 
		 * 	
		 * @Source ChangeListener: http://stackoverflow.com/questions/6799731/jtabbedpane-changelistener
		 ********************************************/
		getPane().addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e){

				//Créé un nouvel écouteur de changements
				changeListener = new ChangeListener(){

					@Override
					public void stateChanged(ChangeEvent changeEvent){

						//Récupère la position de l'onglet sélectionné
						index = getPane().getSelectedIndex();

						//Initialise la position de l'onglet sélectionné
						setIndex(index);
						
						gameModel.setIndex(index);

						//Permet d'envoyer le nom du mode utilisé au GameModel
						gameModel.setMode(getPane().getTitleAt(index));
					}
				};
				
				//Active l'écoute du changement d'onglets
				getPane().addChangeListener(changeListener);
			}
		});


		/*************************************************
		 * Écouteur du Bouton Sauvegarde
		 * 
		 * S'active seulement lorsqu'un partie se termine
		 * 			
		 *************************************************/
		sauvegarder.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				//Après la sélection de sauvegarde ou non, on les désactives
				cancelSauvegarde.setEnabled(false);
				sauvegarder.setEnabled(false);

				//Puisque le joueur a gagné, on incrémente le niveau
				level++;

				//On imprime dans la console le niveau actuel
				sauvegarde.setLevel(String.valueOf(level));
				gameModel.setLevel(String.valueOf(level));

				//Démarre la sauvegarde de la partie sur fichier .txt
				gameModel.systemOutPrint("save");

				//Met la somme temporaire et le nombre de regroupements à zero
				remisParDefaut(setZero);

				//Génère un nouveau modèle de jeu
				remisParDefaut(resetTilesAndGame);

				//Affiche la somme temporaire à zéro et se met à jour en fonction des Check Box 
				checkBoxLabel(setZero);

				//Redémarre le processus de sélection de couleurs
				tilePanel.initializeColours();

				labelLevel.setText("     [ Level ] " + level + "      ");

				//Permet de réactiver certains éléments Swing après
				//la sélectionne de sauvegarde ou non
				statutBoutons(true);

				repaint();
			}
		});

		/*****************************************************
		 * Écouteur du Bouton qui cancel la Sauvegarde
		 * 
		 * S'active seulement lorsqu'une partie se termine			
		 * 
		 *****************************************************/
		cancelSauvegarde.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				//Après la sélection de sauvegarde ou non, on les désactives
				sauvegarder.setEnabled(false);
				cancelSauvegarde.setEnabled(false);

				//Puisque le joueur a gagné, on incrémente le niveau
				level++;

				//On imprime dans la console le niveau actuel
				sauvegarde.setLevel(String.valueOf(level));
				gameModel.setLevel(String.valueOf(level));

				//Met la somme temporaire et le nombre de regroupements à zero
				remisParDefaut(setZero);

				//Génère un nouveau modèle de jeu
				remisParDefaut(resetTilesAndGame);

				//Affiche la somme temporaire à zéro et se met à jour en fonction des Check Box 
				checkBoxLabel(setZero);

				//Redémarre le processus de sélection de couleurs
				tilePanel.initializeColours();

				labelLevel.setText("     [ Level ] " + level + "      ");

				//Permet de réactiver certains éléments Swing après
				//la sélectionne de sauvegarde ou non
				statutBoutons(true);

				repaint();
			}
		});
	}

	/*****************************************************
	 * CHANGEMENT STATUT SWING
	 * 
	 * Résumer:
	 * 
	 * Permet de changer le statut de certains 
	 * éléments Swing aux besoins.
	 * 
	 * Stratégie pour limiter la duplication de code
	 * 			
	 *****************************************************/
	private void statutBoutons(boolean statut){

		//Les éléments Swing sont activés ou non selon le statut reçu
		btnPrevious.setEnabled(statut);
		btnNext.setEnabled(statut);
		btnReset.setEnabled(statut);
		btnGiveUp.setEnabled(statut);
		checkMean.setEnabled(statut);
		checkNoHelp.setEnabled(statut);
		btnRestart.setEnabled(statut);

		//Si nous somme dans le mode Training
		if(getIndex() == 1){
			
			//On s'assure de toujour que le bouton ne soit jamais activé
			btnPrevious.setEnabled(false);
		}
		
		//Si nous sommes autre que dans le mode Arcade
		if(getIndex() <= 2){

			//On s'assure de toujour que le bouton ne soit jamais activé
			btnRestart.setEnabled(false);
		}

		//Si on est dans le mode Arcade
		if(getIndex() == 3){

			//On s'assure que ces éléments ne soient jamais modifiable
			btnPrevious.setEnabled(false);
			btnNext.setEnabled(false);
			checkMean.setEnabled(false);
			checkNoHelp.setEnabled(false);
		}
	}

	/**************************************************************************************
	 * SOMME TEMPORAIRE
	 * 
	 * Résumé:
	 * 
	 * Permet d'obtenir la somme temporaire des valeurs sélectionnées de l'utilisateur.
	 * On additionne la somme en fonction des types de regroupements.
	 *
	 **************************************************************************************/
	private void sommeTemporaire(){

		if(tilePanel.getTuileActive() != positionTuile){

			//Regroupement de deux tuiles créé une nouvelle valeur composé de deux chiffres
			doubleTuile = gameModel.getValeurTuile(tilePanel.getTuileActive()) + gameModel.getValeurTuile(positionTuile);

			//Additionne les valeurs composées de deux chiffres sélectionnées
			sommeTemporaire += Integer.parseInt(doubleTuile);
		} 
		else{
			//Additionne valeurs uniques sélectionnées 
			sommeTemporaire += Integer.parseInt(gameModel.getValeurTuile(positionTuile));	
		}	
	}

	
	/**********************************************************
	 * VARIATION SELON NIVEAU
	 * 
	 * Résumer:
	 * 
	 * Selon le niveau atteint par le joueur, un système 
	 * de probabilité permet de faire apparaitre divers
	 * options ou de changer certains paramètres de jeu.
	 * 
	 * On s'assure d'interdire que le mode Arcade
	 * ne dépasse plus de 20 niveaux. Sinon le joueur
	 * peut recommencer au premier niveau en tout temps
	 * 
	 *@Source probabilité: http://stackoverflow.com/questions/36612890/do-an-action-with-some-probability-in-java
	 * 
	 **********************************************************/
	private void variationNiveau(){

		//Permet d'activer plusieurs éléments Swing
		statutBoutons(true);

		//On active le bouton puisque nous sommes dans le mode Arcade.
		btnRestart.setEnabled(true);

		//Tant qu'on reste entre le niveau 1 et 20
		if(getLevel() <= 20){

			//Permet de varier le nombre de groupes
			numberCount = 3 + Math.round(3.0 * getLevel() / 20);
			//Initialise une nouvelle valeur pour la quantité de groupes
			gameModel.setQuantiteGroupes((int) numberCount);
			System.out.println("\n\nProbabilité qte. groupes: " + numberCount);

			
			//Probabilité d'obtenir un regroupements 
			doubleDigitProba = 0.3 + (0.3 * getLevel() / 20);
			//Initialise la nouvelle probabilité concernant kes regroupements
			gameModel.setProbabilite(doubleDigitProba);
			System.out.println("Probabilité qte. regroupements: " + (Math.round(doubleDigitProba * 100)) + " %");

			
			//Probabilité qu'on demande au joueur de trouver une moyenne
			meanProba = 0.1 + (0.9 * getLevel() / 20);
			System.out.println("Probabilité d'activation Mean: " + (Math.round(meanProba * 100)) + " %");

			//Permet de génmérer un chiffre entre 0 et 1 au hasard 
			if(new Random().nextFloat() < meanProba ){ 

				//Activation du CheckBox Mean
				checkMean.setSelected(true);
			}
			else{ 
				//Désactivation du CheckBox Mean
				checkMean.setSelected(false);
			}

			
			//Probabilité que l'option NoHelp soit activé
			noHelpProba = 0.1 + (0.9 * level / 20);
			System.out.println("Probabilité d'activation No Help: " + (Math.round(noHelpProba * 100)) + " %");

			if(new Random().nextFloat() < noHelpProba){ 

				//Activation du CheckBox No Help
				checkNoHelp.setSelected(true);
				System.out.println("Option NoHelp activé");

			}
			else{ 
				//Désactivation du CheckBox No Help
				checkNoHelp.setSelected(false);
			}
		}
		else{
			//Si on dépasse le niveau 20, on recommence au premier niveau
			setLevel(1);
		}
	}


	/*********************************************************************
	 * GAGNER OU ÉCHEC
	 * 
	 * Résumer:
	 * 
	 * On change la couleur des tuiles selon si le jouer a gagné ou perdu la partie.
	 * 
	 * Si je joueur gagne, les tuiles deviennent verte et une nouvelle partie est initialisée.
	 * Si le joueur perd, les tuiles deviennent rouge et les tuiles redeviennent blanches
	 * 			
	 * @Source Inspiration chronomètre: http://stackoverflow.com/questions/4044726/how-to-set-a-timer-in-java
	 *
	 *********************************************************************/
	private void joueurGagne(int positionTuile){

		//Si le numero de la tuile qui part à 0 équivaut à la quantité de valeurs (tuiles)
		if((positionTuile + 1) == gameModel.getQuantiteValeurs()){

			//Remets les tuiles blanche pour être repeinturées
			remisParDefaut(resetTuile);

			//Si la somme temporaire équivaut au but
			if(Integer.parseInt(gameModel.getSommeTuiles()) == sommeTemporaire) { 

				//Si le bouton GiveUp a été utilisé dans le mode Arcade,
				//on s'assure que le joueur ne peut passer au prochain niveau.
				//(Les boutons Sauvegarde et CancelSauvegarde s'occupent aussi du changement de niveau)
				if(niveauFermer == true){
					
					//Active le bouton de sauvegarde 
					sauvegarder.setEnabled(false);

					//Active le bouton pour canceller la sauvegarde
					cancelSauvegarde.setEnabled(false);
					
					//Donne l'option au joueur de recommencer au début du mode 
					btnReset.setEnabled(true);

				}
				else{

					//Active le bouton de sauvegarde 
					sauvegarder.setEnabled(true);

					//Active le bouton pour canceller la sauvegarde
					cancelSauvegarde.setEnabled(true);
				}

				//Permet de désactiver certains éléments Swing en attend que
				//le joueur sélectionne la sauvegarde ou non
				statutBoutons(false);

				//Permet de désactiver les boutons et attend que
				//le joueur sélectionne la sauvegarde ou non.
				//On les désactivent ici à cause du mode Arcade
				btnPrevious.setEnabled(false);
				btnNext.setEnabled(false);

				//On arrête le chronomètre pour que le joueur puisse 
				//prendre une décision ( Save / Cancel )
				chronometre.stopTimer();

				//Tuiles affichent en vert 
				tilePanel.joueurGagne(joueurGagne);
			}
			else{ 

				//Tuiles affichent en rouge 
				tilePanel.joueurGagne(joueurPerd);

				//Met la somme temporaire et le nombre de regroupements à zero
				remisParDefaut(setZero);

				//Affiche la somme temporaire à zéro et se met à jour en fonction des Check Box 
				checkBoxLabel(setZero);
			}
		}
	}

	/*********************************************************************
	 * UPDATE LABEL FOR CHECKBOX
	 * 
	 * Résumer:
	 * 
	 * Met à jour le label qui affiche la somme partielle et le nombre
	 * de regroupements faits par l'utilisateur en fonction des Check Box. 
	 * 
	 * 1- Si reçoit "0", on ramène l'affiche du label à son état d'origine.
	 * 2- Si reçoit "1", le label ne fait que se mettre à jour.
	 * 
	 * Stratégie qui permet d'évite la duplication de code.
	 * 			
	 *********************************************************************/
	private void checkBoxLabel(int resetDefault){

		//Si le checkBox Mean est sélectionné
		if(checkMean.isSelected()){

			//Met à jour le label qui affiche le but à atteindre du panneau Info
			labelGoal.setText(" Goal: " + gameModel.getMean());

			//Si le checkBox No Help est sélectionné en même temps que le checkBox Mean
			if(checkNoHelp.isSelected()){

				//On retire l'affichage de la somme partielle et du nombre de regroupements
				labelSum.setText(" Current mean: n/a ( n/a )"); 
			}
			else{

				if(resetDefault == 0){

					//Met à jour le label qui affiche la somme et la quantité de groupe formés du panneau Info
					labelSum.setText(" Current mean: 0 ( 0 )");
				}
				else{
					//Met à jour le label qui affiche la moyenne temporaire et la quantité de groupes formés du panneau Info
					labelSum.setText(" Current mean: " + (sommeTemporaire / getClickGroup()) + " ( " + getClickGroup() + " ) ");
				}
			}
		}
		else{
			//Met à jour le label qui affiche le but à atteindre du panneau Info
			labelGoal.setText(" Goal: " + gameModel.getSommeTuiles());

			//Si le checkBox No Help est sélectionné
			if(checkNoHelp.isSelected()){

				//On retire l'affichage de la somme partielle et du nombre de regroupements
				labelSum.setText(" Current sum: n/a ( n/a )"); 
			}
			else{

				if(resetDefault == 0){

					//Met à jour le label qui affiche la somme et la quantité de groupe formés du panneau Info
					labelSum.setText(" Current sum: 0 ( 0 )");
				}
				else{
					//Met à jour le label qui affiche la somme et la quantité de groupes formésdu panneau Info
					labelSum.setText(" Current sum: " + sommeTemporaire + " ( " + getClickGroup() + " ) ");
				}
			}
		}
	}

	/********************************************************************************
	 * REMISE PAR DÉFAUT
	 * 
	 * Résumer:
	 * 
	 * Selon les paramètres reçus, on remet certains éléments à leur état d'origine.
	 * Stratégie qui permet d'évite la duplication de code.
	 * 
	 * Si reçoit "0", la somme temporaire et le nombre de regroupements est remis à zéro
	 * Si reçoit "1", les tuiles sont remises à leurs états d'origines.
	 * Si reçoit "2", les tuiles sont remises à leurs états d'origines, on génère une 
	 * 					nouvelle partie et on démarre un nouveau chronomètre.
	 * 
	 ********************************************************************************/
	private void remisParDefaut(int resetDefault){

		//Le limitateur de sélection de tuiles est remis à zéro
		limitationClick = 0;

		//On vide les listes des positions tuiles gardées en mémoire
		listePositionClick.clear();
		listePositionRelache.clear();

		if(resetDefault == 0){

			//Met la somme temporaire de l'utilisateur à zéro
			sommeTemporaire = 0;

			//Met les regroupements de l'utilisateur à zero
			clickRegroupement = 0;
		}
		else{

			//Ramène la position des tuiles par défaut
			tilePanel.setTuileActive(-1);

			//Met les tuiles blanches
			tilePanel.setActiveColorIndex(-1);

			//Redémarre le processus de changement de couleurs
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

					//On va chercher les paramètre du mode Arcade
					variationNiveau();
				}

				if(checkMean.isSelected()){

					//Imprime dans la console la moyenne à atteindre
					gameModel.systemOutPrint("meanSum");
				}

				//Arrêt du chronomètre actuel
				chronometre.stopTimer();

				//Démarrage d'un nouveau chronomètre
				chronometre = new Chronometre(labelTimer);
			}
		}
	}


	/********************************************
	 * Établie la quantité de tuiles regroupées
	 * 			
	 ********************************************/
	public void setClickGroupe(int click){
		this.clickRegroupement = click;
	}

	/********************************************
	 * Récupère la quantité de tuiles regroupées
	 * 			
	 ********************************************/
	public int getClickGroup(){

		//Si la quantité de groupe dépasse la limite de tuiles possibles
		if(clickRegroupement > gameModel.getQuantiteValeurs()){

			clickRegroupement--;
		}
		return clickRegroupement;
	}

	/********************************************
	 * Récupère la somme temporaire du joueur
	 * 			
	 ********************************************/
	public int getSommeTemporaire(){
		return sommeTemporaire;
	}

	/********************************************
	 * Établie la somme temporaire du joueur
	 * 			
	 ********************************************/
	public void setSommeTemporaire(int sommeTemporaire){
		this.sommeTemporaire = sommeTemporaire;
	}

	/********************************************
	 * Récupère le niveau actuel du joueur
	 * 			
	 ********************************************/
	public int getLevel(){
		return level;
	}

	/********************************************
	 * Établie le niveau actuel du joueur
	 * 			
	 ********************************************/
	public void setLevel(int level){
		this.level = level;
	}

	/********************************************
	 * Établie les options onglets
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
	 * Établie la position de l'onglet 
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
	 * Retourne la position de la tuile sélectionnée
	 * 			
	 ********************************************/
	public int getPositionTuile(){
		return positionTuile;
	}
	
	/********************************************
	 * Getter pour l'interface et le Mode Replay
	 * 			
	 ********************************************/

	//La somme à atteindre
	@Override
	public String getSommeTuiles() {
		
		String sommeTuile = gameModel.getSommeTuiles();
		return sommeTuile;
	}
	//La moyenne à atteindre
	@Override
	public int getMean() {
		
		int mean = gameModel.getMean();
		return mean;
	}
	//Valeur affiché sur une tuile
	@Override
	public String getValeurTuile(int index) {
		
		String valeurTuile = gameModel.getValeurTuile(getPositionTuile());
		return valeurTuile;
	}
	//Quantité de groupes générés
	@Override
	public int getQuantiteGroupes() {
		
		int quantiteGroupes = gameModel.getQuantiteGroupes();
		return quantiteGroupes;
	}
	//Quantité de tuiles
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