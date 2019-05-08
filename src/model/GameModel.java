package model;
import java.io.Serializable;
import java.util.*;

import javax.swing.JTabbedPane;

import controller.Sauvegarde;

/**
 * The game model handles the logic of the game (generating the numbers, etc.).
 * The instance of the model is used by the view-controller module
 * to trigger actions (for example, generate a new game) and retrieve information
 * about the current status of the game (the digits, the goal, etc.).
 *
 */
public class GameModel implements Serializable{

	/**
	 * Permet de générer un chiffre aléatoire
	 */
	private Random hasard;

	/**
	 * Classe qui permet l'écriture d'information dans un fichier .txt
	 */
	private Sauvegarde sauvegarde;

	/**
	 * Liste des valeurs groupées
	 */
	private LinkedList<Integer> listeGroupes;

	/**
	 * String les valeurs sans espaces et virgules
	 */
	private String valeurNettoyer;

	/**
	 * Tableau regroupant les valeurs uniques
	 */
	private int[] tabValeursUniques;

	/**
	 * Variable qui permet d'obtenir la quantité de tuiles
	 */
	private int quantiteValeursUniques;

	/**
	 * Variable qui permet d'obtenir la quantité de groupes
	 */
	private int quantiteGroupes;

	/**
	 * Variable qui garde en mémoire une valeur entre 0 et 1 pour 
	 * le calcul de la probabilité
	 */
	private double probabilite;

	/**
	 * String utilisé pour afficher le niveau du joueur
	 */
	private String level = "1";

	/**
	 * String utilisé pour activer certaines impressions dans la console
	 */
	private String option;

	/**
	 * String permet d'identifier le mode atuel du joueur
	 */
	private String mode;

	/**
	 * Variable qui permet de garder en mémoire la numero de l'onglet
	 */
	private int index;


	/********************************************
	 * CONSTRUCTEUR
	 * 
	 * Constructeur du GameModel
	 * 			
	 ********************************************/
	public GameModel(){

		//Création de la liste qui garde en mémoire les valeurs groupées
		listeGroupes = new LinkedList<Integer>();

		//On créé un nouvel enregistrement (fichier.txt)
		sauvegarde = new Sauvegarde();

		//Génère des valeurs au hasard. Évite la duplication de code.
		hasard = new Random();
	
		resetListeValeurs();
	}

	/*******************************************************************
	 * VIDAGE DE LA LISTE
	 * 
	 * Résumer:
	 * 
	 * On vide la liste si l'utilisateur génère un nouvelle partie.
	 * 			
	 *******************************************************************/
	public void resetListeValeurs(){

		//Si la liste possède déjà des valeurs
		if(listeGroupes.size() > 0){

			//Vide la liste de ses valeurs
			listeGroupes.clear();
		}

		quantiteRegroupements();
	}

	/**********************************************************************************
	 * QUANTITÉ DE GROUPES
	 *
	 * Résumer:
	 * 
	 * On génère une quantité aléatoire de groupes situé entre 3 et 6.
	 * 
	 * @Source Valeur au hasard: http://stackoverflow.com/questions/5271598/java-generate-random-number-between-two-given-values	
	 * 		
	 **********************************************************************************/
	private void quantiteRegroupements(){

		//Pour générer une valeur entre 3 et 6
		int valeurMin = 3;
		int valeurMax = 6; 

		//Si on veut une quantité de groupes provenant de l'extérieur (Arcade)
		quantiteGroupes = getQuantiteGroupes();
		
		//Quantité de groupes générés aléatoirement entre la valeur min(3) et max(6)
		quantiteGroupes = hasard.nextInt(valeurMax - valeurMin + 1) + valeurMin;

		valeursGroupes();
	}

	/*********************************************************
	 * FORMATION DES GROUPES
	 * 
	 * Résumer:
	 * 
	 * On génère des valeurs à afficher sur les tuiles.
	 * Les valeurs sont générées de façon aléatoires.
	 * 
	 * Probabilité de 70% : valeurs générées entre 1 et 9
	 * probabilité de 30% : valeurs générées entre 10 et 99
	 * 
	 * @Source Valeur au hasard: http://stackoverflow.com/questions/5271598/java-generate-random-number-between-two-given-values	 
	 * 			
	 *********************************************************/
	private void valeursGroupes(){

		//Garde en mémoire les valeurs générées au hasard
		int valeur;

		//Pour générer une valeur entre 1 et 9
		int valeurMinUnique = 1;
		int valeurMaxUnique = 9;

		//Pour générée une valeur entre 10 et 99
		int valeurMinGroupe = 10;
		int valeurMaxGroupe = 99;

		//Boucle en fonction de la quantité de groupes
		for (int i=0; i < quantiteGroupes; i++){

			//Récupère une valeur situé entre 0 et 1
			probabilite = Math.random();
	
			//Si on veut une nouvelle probabilité provenant de l'extérieur (Arcade)
			probabilite = getProbabilite();
			
			//Si la probabilité représente 70% 
			if(probabilite < 0.7){

				//Quantité de groupes générés aléatoirement entre la valeur min(1) et max(9)
				valeur = hasard.nextInt(valeurMaxUnique - valeurMinUnique + 1) + valeurMinUnique;

				//Ajout de la valeur dans la liste
				listeGroupes.add(valeur);
			}

			//Si la probabilité représente 30%
			else if (probabilite > 0.7){

				//Quantité de groupes générés aléatoirement entre la valeur min(10) et max(99)
				valeur = hasard.nextInt(valeurMaxGroupe - valeurMinGroupe + 1) + valeurMinGroupe;

				//Ajout de la valeur dans la liste
				listeGroupes.add(valeur);
			}
		}	
		valeurNettoyer();
	}

	/*********************************************************
	 * REGROUPEMENTS DES VALEURS
	 * 
	 * Resumer: 
	 * 
	 * On récupère les valeurs de la liste et on les collent 
	 * ensemble afin d'obtenir une grande valeur. 
	 * 
	 * @Source replaceAll() : //http://stackoverflow.com/questions/25852961/how-to-remove-brackets-character-in-string-java
	 * 			
	 *********************************************************/
	private void valeurNettoyer(){

		//String récupère la totalité des valeurs dans un format liste
		valeurNettoyer = Arrays.toString(listeGroupes.toArray());

		//On récupère un nombre sans charactères inutiles tels que: crochets[], espaces et virgules
		valeurNettoyer = valeurNettoyer.replaceAll("[\\[\\], ]","");

		insertionValeurs();
	}


	/*********************************************************
	 * INSERTION DES VALEURS DANS UN TABLEAU
	 * 
	 * Résumer: 
	 * 
	 * On identifie la quantité de valeurs pour donner une 
	 * dimension au tableau de valeurs.
	 * 
	 * On associe à chaque valeur une position pour l'insérer
	 * dans le tableau de valeurs.(Supporte un affichage format tuiles)
	 *  
	 * @Source Character.digit() : http://stackoverflow.com/questions/3389264/how-to-get-the-separate-digits-of-an-int-number
	 * 			
	 *********************************************************/
	private void insertionValeurs(){

		//On donne une dimension au tableau en fonction de la quantité de valeurs
		tabValeursUniques = new int[valeurNettoyer.length()];

		//Récupère le nombre de valeurs pour l'imprimer dans la console
		quantiteValeursUniques = valeurNettoyer.length();

		//Boucle en fonction du nombre de valeurs 
		for(int i = 0; i < valeurNettoyer.length(); i++) {

			//On récupère indépendemment chaque valeurs 
			int j = Character.digit(valeurNettoyer.charAt(i), 10);

			//Pour ensuite les insérerer dans un tableau
			tabValeursUniques[i] = j;
		}	

		//Démarre l'impression de l'information dans la console
		systemOutPrint("");
	}

	/***********************************************************************
	 * CALCUL DE LA SOMME À ATTEINDRE
	 * 
	 * Résumer:
	 * 
	 * On additionne ensemble les valeurs de la liste. 
	 * Représente le "Goal" à atteindre.
	 * 
	 ***********************************************************************/
	public String getSommeTuiles(){

		//Valeur de départ
		int somme = 0;

		//Boucle en fonction de la grosseur de la liste
		for(int i = 0; i < listeGroupes.size(); i++){

			//Additionne les chiffres de la liste
			somme += listeGroupes.get(i);
		}

		//Retourne la somme 
		return Integer.toString(somme);
	}

	/***********************************************************************
	 * CALCUL DE LA MOYENNE À ATTEINDRE
	 * 
	 * Résumer:
	 * 
	 * On divise la somme par le nombre de groupes pour obtenir la moyenne.
	 * Représente le "Mean" à atteindre.
	 * 
	 ***********************************************************************/
	public int getMean(){

		//On obtient la moyenne en divisant la somme par le nombre de regroupements
		int moyenne = (Integer.parseInt(getSommeTuiles()) / listeGroupes.size());

		//Retourne la moyenne à atteindre
		return moyenne;
	}

	/***********************************************************
	 * Retourne en String les valeurs insérées dans le tableau 
	 * 
	 ***********************************************************/
	public String getValeurTuile(int index){

		return Integer.toString(tabValeursUniques[index]);
	}

	/****************************************
	 * Retourne la quantité de regroupements
	 * 
	 ****************************************/
	public int getQuantiteGroupes(){

		return quantiteGroupes;
	}

	/**************************************
	 * Établie la quantité de regroupements
	 * 
	 **************************************/
	public void setQuantiteGroupes(int quantiteGroupes){

		this.quantiteGroupes = quantiteGroupes;
	}

	/*******************************************
	 * Retourne la quantité de valeurs (tuiles)
	 *  
	 *******************************************/
	public int getQuantiteValeurs(){

		return quantiteValeursUniques;
	}

	/*******************************************
	 * Établie la quantité de valeurs (tuiles)
	 *  
	 *******************************************/
	public void setQuantiteValeurs(int valeur){

		this.quantiteValeursUniques = valeur;
	}

	/**************************************************
	 * Établie une nouvelle probabilité
	 *  
	 **************************************************/
	public void setProbabilite(double probabilite){
		this.probabilite = probabilite;
	}
	
	/**************************************************
	 * retrourne une nouvelle probabilité
	 *  
	 **************************************************/
	public double getProbabilite(){
		return probabilite;
	}
	
	/**************************************************
	 * Établie le numéro de l'onglet utilisé
	 *  
	 **************************************************/
	public void setIndex(int index){
		this.index = index;
	}

	/**************************************************
	 * Retourne le numéro de l'onglet utilisé
	 *  
	 **************************************************/
	public int getIndex(){
		return index;
	}

	/**************************************************
	 * Établie le niveau actuel du joueur
	 *  
	 **************************************************/
	public void setLevel(String level){
		this.level = level;
	}

	/**************************************************
	 * Retourne le niveau actuel du joueur
	 *  
	 **************************************************/
	public String getLevel(){
		return level;
	}

	/**************************************************
	 * Établie le mode utilisé par le joueur
	 *  
	 **************************************************/
	public void setMode(String mode){
		this.mode = mode;
	}

	/**************************************************
	 * Retourne le mode utilisé par le joueur
	 *  
	 **************************************************/
	public String getMode(){
		return mode;
	}

	
	/****************************************************************
	 * IMPRESSION CONSOLE
	 * 
	 * Résumer:
	 * 
	 * On récupère l'information du jeu à afficher et on
	 * imprime le tout dans la console. Si la méthode
	 * reçoit un string comme par exemple "meanSum", on 
	 * imprime l'option Mean.
	 * 
	 * On envoi aussi de l'information à la classe Sauvegarde 
	 * qui écrit l'information du jeu sur un fichier .txt
	 * 
	 ****************************************************************/
	public void systemOutPrint(String option){

		this.option = option;

		//Si on reçoit un String "save"
		if(option.equals("save")){

			//On démarre l'écriture dans un fichier .txt et affiche un titre selon le mode
			sauvegarde.ecrireFichier("Information " + getMode() + " MeanSum");
		}

		//Si on est dans un des trois mode
		if(getIndex() > 0){

			//Affiche un titre à chaque nouvelle partie 
			System.out.println("\n\n**********************************************");
			System.out.println("*              Nouvelle Partie               *");
			
			//Si on est sur l'onglet du mode Arcade
			if(getIndex() == 3){
				
				//On affiche dans la console le mode jeu et le niveau actuel
				System.out.println("*           " + getMode() + " Niveau: " + getLevel() + "             *");
			}
			
			//Si on est pas sur l'onglet du mode Arcade
			else{
				
				//On affiche dans la console le mode de jeu
				System.out.println("*                 " + getMode() + "                 *");
			}
			
			System.out.println("**********************************************");
			
			//Affiche la Somme à atteindre
			System.out.println("La somme à atteindre: " + getSommeTuiles());
			sauvegarde.setSomme(getSommeTuiles());

			//Affiche la Quantité de Valeurs
			System.out.println("\nNombre de tuiles: " + quantiteValeursUniques);
			sauvegarde.setQuantiteValeurs(quantiteValeursUniques);

			//Affiche le Quantité de Groupes
			System.out.println("Nombre de groupes: " + getQuantiteGroupes());
			sauvegarde.setQuantiteGroupes(getQuantiteGroupes());

			//Affiche les Groupes
			System.out.print("\nGroupes: " + Arrays.toString(listeGroupes.toArray()));
			sauvegarde.setListeGroupes(Arrays.toString(listeGroupes.toArray()));

			//Si on reçoit un String "meanSum"
			if(option.equals("meanSum")){

				//Affiche un avertissement du nouvel objectif
				System.out.println("\n\n****** NOUVEL OBJECTIF ******");

				//Affiche la moyennesi l'option Mean est activé
				System.out.println("La moyenne à atteindre: " + getMean());
				sauvegarde.setMoyenne(getMean());
			}
		}
	}
}