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
	 * Permet de g�n�rer un chiffre al�atoire
	 */
	private Random hasard;

	/**
	 * Classe qui permet l'�criture d'information dans un fichier .txt
	 */
	private Sauvegarde sauvegarde;

	/**
	 * Liste des valeurs group�es
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
	 * Variable qui permet d'obtenir la quantit� de tuiles
	 */
	private int quantiteValeursUniques;

	/**
	 * Variable qui permet d'obtenir la quantit� de groupes
	 */
	private int quantiteGroupes;

	/**
	 * Variable qui garde en m�moire une valeur entre 0 et 1 pour 
	 * le calcul de la probabilit�
	 */
	private double probabilite;

	/**
	 * String utilis� pour afficher le niveau du joueur
	 */
	private String level = "1";

	/**
	 * String utilis� pour activer certaines impressions dans la console
	 */
	private String option;

	/**
	 * String permet d'identifier le mode atuel du joueur
	 */
	private String mode;

	/**
	 * Variable qui permet de garder en m�moire la numero de l'onglet
	 */
	private int index;


	/********************************************
	 * CONSTRUCTEUR
	 * 
	 * Constructeur du GameModel
	 * 			
	 ********************************************/
	public GameModel(){

		//Cr�ation de la liste qui garde en m�moire les valeurs group�es
		listeGroupes = new LinkedList<Integer>();

		//On cr�� un nouvel enregistrement (fichier.txt)
		sauvegarde = new Sauvegarde();

		//G�n�re des valeurs au hasard. �vite la duplication de code.
		hasard = new Random();
	
		resetListeValeurs();
	}

	/*******************************************************************
	 * VIDAGE DE LA LISTE
	 * 
	 * R�sumer:
	 * 
	 * On vide la liste si l'utilisateur g�n�re un nouvelle partie.
	 * 			
	 *******************************************************************/
	public void resetListeValeurs(){

		//Si la liste poss�de d�j� des valeurs
		if(listeGroupes.size() > 0){

			//Vide la liste de ses valeurs
			listeGroupes.clear();
		}

		quantiteRegroupements();
	}

	/**********************************************************************************
	 * QUANTIT� DE GROUPES
	 *
	 * R�sumer:
	 * 
	 * On g�n�re une quantit� al�atoire de groupes situ� entre 3 et 6.
	 * 
	 * @Source Valeur au hasard: http://stackoverflow.com/questions/5271598/java-generate-random-number-between-two-given-values	
	 * 		
	 **********************************************************************************/
	private void quantiteRegroupements(){

		//Pour g�n�rer une valeur entre 3 et 6
		int valeurMin = 3;
		int valeurMax = 6; 

		//Si on veut une quantit� de groupes provenant de l'ext�rieur (Arcade)
		quantiteGroupes = getQuantiteGroupes();
		
		//Quantit� de groupes g�n�r�s al�atoirement entre la valeur min(3) et max(6)
		quantiteGroupes = hasard.nextInt(valeurMax - valeurMin + 1) + valeurMin;

		valeursGroupes();
	}

	/*********************************************************
	 * FORMATION DES GROUPES
	 * 
	 * R�sumer:
	 * 
	 * On g�n�re des valeurs � afficher sur les tuiles.
	 * Les valeurs sont g�n�r�es de fa�on al�atoires.
	 * 
	 * Probabilit� de 70% : valeurs g�n�r�es entre 1 et 9
	 * probabilit� de 30% : valeurs g�n�r�es entre 10 et 99
	 * 
	 * @Source Valeur au hasard: http://stackoverflow.com/questions/5271598/java-generate-random-number-between-two-given-values	 
	 * 			
	 *********************************************************/
	private void valeursGroupes(){

		//Garde en m�moire les valeurs g�n�r�es au hasard
		int valeur;

		//Pour g�n�rer une valeur entre 1 et 9
		int valeurMinUnique = 1;
		int valeurMaxUnique = 9;

		//Pour g�n�r�e une valeur entre 10 et 99
		int valeurMinGroupe = 10;
		int valeurMaxGroupe = 99;

		//Boucle en fonction de la quantit� de groupes
		for (int i=0; i < quantiteGroupes; i++){

			//R�cup�re une valeur situ� entre 0 et 1
			probabilite = Math.random();
	
			//Si on veut une nouvelle probabilit� provenant de l'ext�rieur (Arcade)
			probabilite = getProbabilite();
			
			//Si la probabilit� repr�sente 70% 
			if(probabilite < 0.7){

				//Quantit� de groupes g�n�r�s al�atoirement entre la valeur min(1) et max(9)
				valeur = hasard.nextInt(valeurMaxUnique - valeurMinUnique + 1) + valeurMinUnique;

				//Ajout de la valeur dans la liste
				listeGroupes.add(valeur);
			}

			//Si la probabilit� repr�sente 30%
			else if (probabilite > 0.7){

				//Quantit� de groupes g�n�r�s al�atoirement entre la valeur min(10) et max(99)
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
	 * On r�cup�re les valeurs de la liste et on les collent 
	 * ensemble afin d'obtenir une grande valeur. 
	 * 
	 * @Source replaceAll() : //http://stackoverflow.com/questions/25852961/how-to-remove-brackets-character-in-string-java
	 * 			
	 *********************************************************/
	private void valeurNettoyer(){

		//String r�cup�re la totalit� des valeurs dans un format liste
		valeurNettoyer = Arrays.toString(listeGroupes.toArray());

		//On r�cup�re un nombre sans charact�res inutiles tels que: crochets[], espaces et virgules
		valeurNettoyer = valeurNettoyer.replaceAll("[\\[\\], ]","");

		insertionValeurs();
	}


	/*********************************************************
	 * INSERTION DES VALEURS DANS UN TABLEAU
	 * 
	 * R�sumer: 
	 * 
	 * On identifie la quantit� de valeurs pour donner une 
	 * dimension au tableau de valeurs.
	 * 
	 * On associe � chaque valeur une position pour l'ins�rer
	 * dans le tableau de valeurs.(Supporte un affichage format tuiles)
	 *  
	 * @Source Character.digit() : http://stackoverflow.com/questions/3389264/how-to-get-the-separate-digits-of-an-int-number
	 * 			
	 *********************************************************/
	private void insertionValeurs(){

		//On donne une dimension au tableau en fonction de la quantit� de valeurs
		tabValeursUniques = new int[valeurNettoyer.length()];

		//R�cup�re le nombre de valeurs pour l'imprimer dans la console
		quantiteValeursUniques = valeurNettoyer.length();

		//Boucle en fonction du nombre de valeurs 
		for(int i = 0; i < valeurNettoyer.length(); i++) {

			//On r�cup�re ind�pendemment chaque valeurs 
			int j = Character.digit(valeurNettoyer.charAt(i), 10);

			//Pour ensuite les ins�rerer dans un tableau
			tabValeursUniques[i] = j;
		}	

		//D�marre l'impression de l'information dans la console
		systemOutPrint("");
	}

	/***********************************************************************
	 * CALCUL DE LA SOMME � ATTEINDRE
	 * 
	 * R�sumer:
	 * 
	 * On additionne ensemble les valeurs de la liste. 
	 * Repr�sente le "Goal" � atteindre.
	 * 
	 ***********************************************************************/
	public String getSommeTuiles(){

		//Valeur de d�part
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
	 * CALCUL DE LA MOYENNE � ATTEINDRE
	 * 
	 * R�sumer:
	 * 
	 * On divise la somme par le nombre de groupes pour obtenir la moyenne.
	 * Repr�sente le "Mean" � atteindre.
	 * 
	 ***********************************************************************/
	public int getMean(){

		//On obtient la moyenne en divisant la somme par le nombre de regroupements
		int moyenne = (Integer.parseInt(getSommeTuiles()) / listeGroupes.size());

		//Retourne la moyenne � atteindre
		return moyenne;
	}

	/***********************************************************
	 * Retourne en String les valeurs ins�r�es dans le tableau 
	 * 
	 ***********************************************************/
	public String getValeurTuile(int index){

		return Integer.toString(tabValeursUniques[index]);
	}

	/****************************************
	 * Retourne la quantit� de regroupements
	 * 
	 ****************************************/
	public int getQuantiteGroupes(){

		return quantiteGroupes;
	}

	/**************************************
	 * �tablie la quantit� de regroupements
	 * 
	 **************************************/
	public void setQuantiteGroupes(int quantiteGroupes){

		this.quantiteGroupes = quantiteGroupes;
	}

	/*******************************************
	 * Retourne la quantit� de valeurs (tuiles)
	 *  
	 *******************************************/
	public int getQuantiteValeurs(){

		return quantiteValeursUniques;
	}

	/*******************************************
	 * �tablie la quantit� de valeurs (tuiles)
	 *  
	 *******************************************/
	public void setQuantiteValeurs(int valeur){

		this.quantiteValeursUniques = valeur;
	}

	/**************************************************
	 * �tablie une nouvelle probabilit�
	 *  
	 **************************************************/
	public void setProbabilite(double probabilite){
		this.probabilite = probabilite;
	}
	
	/**************************************************
	 * retrourne une nouvelle probabilit�
	 *  
	 **************************************************/
	public double getProbabilite(){
		return probabilite;
	}
	
	/**************************************************
	 * �tablie le num�ro de l'onglet utilis�
	 *  
	 **************************************************/
	public void setIndex(int index){
		this.index = index;
	}

	/**************************************************
	 * Retourne le num�ro de l'onglet utilis�
	 *  
	 **************************************************/
	public int getIndex(){
		return index;
	}

	/**************************************************
	 * �tablie le niveau actuel du joueur
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
	 * �tablie le mode utilis� par le joueur
	 *  
	 **************************************************/
	public void setMode(String mode){
		this.mode = mode;
	}

	/**************************************************
	 * Retourne le mode utilis� par le joueur
	 *  
	 **************************************************/
	public String getMode(){
		return mode;
	}

	
	/****************************************************************
	 * IMPRESSION CONSOLE
	 * 
	 * R�sumer:
	 * 
	 * On r�cup�re l'information du jeu � afficher et on
	 * imprime le tout dans la console. Si la m�thode
	 * re�oit un string comme par exemple "meanSum", on 
	 * imprime l'option Mean.
	 * 
	 * On envoi aussi de l'information � la classe Sauvegarde 
	 * qui �crit l'information du jeu sur un fichier .txt
	 * 
	 ****************************************************************/
	public void systemOutPrint(String option){

		this.option = option;

		//Si on re�oit un String "save"
		if(option.equals("save")){

			//On d�marre l'�criture dans un fichier .txt et affiche un titre selon le mode
			sauvegarde.ecrireFichier("Information " + getMode() + " MeanSum");
		}

		//Si on est dans un des trois mode
		if(getIndex() > 0){

			//Affiche un titre � chaque nouvelle partie 
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
			
			//Affiche la Somme � atteindre
			System.out.println("La somme � atteindre: " + getSommeTuiles());
			sauvegarde.setSomme(getSommeTuiles());

			//Affiche la Quantit� de Valeurs
			System.out.println("\nNombre de tuiles: " + quantiteValeursUniques);
			sauvegarde.setQuantiteValeurs(quantiteValeursUniques);

			//Affiche le Quantit� de Groupes
			System.out.println("Nombre de groupes: " + getQuantiteGroupes());
			sauvegarde.setQuantiteGroupes(getQuantiteGroupes());

			//Affiche les Groupes
			System.out.print("\nGroupes: " + Arrays.toString(listeGroupes.toArray()));
			sauvegarde.setListeGroupes(Arrays.toString(listeGroupes.toArray()));

			//Si on re�oit un String "meanSum"
			if(option.equals("meanSum")){

				//Affiche un avertissement du nouvel objectif
				System.out.println("\n\n****** NOUVEL OBJECTIF ******");

				//Affiche la moyennesi l'option Mean est activ�
				System.out.println("La moyenne � atteindre: " + getMean());
				sauvegarde.setMoyenne(getMean());
			}
		}
	}
}