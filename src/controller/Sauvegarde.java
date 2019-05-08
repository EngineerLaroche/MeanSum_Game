package controller;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Sauvegarde {

	/**
	 * Permet d'écrire en continue l'information du jeu sur un fichier .txt
	 */
	private PrintWriter impression;	

	//	private ObjectInputStream stream;
	//	private ObjectOutputStream output;

	private Chronometre chronometre;

	private int quantiteValeursUniques;
	private int quantiteGroupes;
	private int moyenne;

	private String listeGroupes;
	private String temps;
	private String level = "1";
	private String somme;
	private String nomFichier;

	/***************************************************
	 *Constructeur de la classe Sauvegarde par défaut
	 *
	 ***************************************************/
	public Sauvegarde(){
		
		chronometre = new Chronometre(null);
	}
	
	/***************************************************
	 * ECRITURE FICHIER .TXT
	 * 
	 * Résumer:
	 * 
	 * Permet d'écrire dans un fichier .txt de façon continu toute
	 * l'information du jeu et des performances du joueur. On reçoit
	 * en paramètre le texte à écrire et le nom associé au fichier .txt.
	 * 
	 * @Source .write() : http://stackoverflow.com/questions/31657112/saving-to-txt-file-java						
	 * @Source %n : http://www.homeandlearn.co.uk/java/write_to_textfile.html
	 * @Source FileWriter : http://www.homeandlearn.co.uk/java/write_to_textfile.html
	 * 
	 ***************************************************/
	public void ecrireFichier(String nomFichier){

		//Donne un titre au fichier .txt selon le mode
		this.nomFichier = nomFichier;

			//Permet d'écrire en continue l'information du jeu sur un fichier .txt et donne un titre au fichier.
			try {
				impression = new PrintWriter(new BufferedWriter(new FileWriter(".\\" + nomFichier + ".txt", true)));
			} 
			catch (IOException e) {
			
				e.printStackTrace();
			}
	
		//Affiche un titre et le niveau actuel du joueur
		impression.printf("%n");
		impression.write("**********************************************");
		impression.printf("%n");
		impression.write("*              Nouvelle Partie               *");
		impression.printf("%n");
		impression.write("*                 Niveau: " + getLevel() + "                  *");      
		impression.printf("%n");
		impression.write("**********************************************");

		impression.printf("%n");
		impression.write("La somme à atteindre: " + getSomme());

		impression.printf("%n" + "%n");
		impression.write("Nombre de tuiles: " + getQuantiteValeurs());

		impression.printf("%n");
		impression.write("Nombre de groupes: " + getQuantiteGroupes());

		impression.printf("%n" + "%n");
		impression.write("Groupes: " + getListeGroupes());

		impression.printf("%n" + "%n");
		impression.write(chronometre.getTempsEcoule());

		impression.printf("%n");
		impression.close();
		

		//		try {
		//
		//			output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(".\\" + nomFichier + ".txt"))));
		//			output.writeObject(new GameModel());
		//
		//			try {
		//
		//				stream.readObject();
		//
		//			} catch (ClassNotFoundException e) {
		//
		//				e.printStackTrace();
		//			}
		//
		//		} catch (IOException e) {
		//
		//			e.printStackTrace();
		//		}	
	} 


	public void setLevel(String level){
		this.level = level;
	}

	public String getLevel(){
		return level;
	}

	public void setSomme(String somme){
		this.somme = somme;
	}

	public String getSomme(){
		return somme;
	}

	public void setQuantiteValeurs(int quantiteValeursUniques){
		this.quantiteValeursUniques = quantiteValeursUniques;
	}

	public int getQuantiteValeurs(){
		return quantiteValeursUniques;
	}

	public void setQuantiteGroupes(int quantiteGroupes){
		this.quantiteGroupes = quantiteGroupes;
	}

	public int getQuantiteGroupes(){
		return quantiteGroupes;
	}

	public void setListeGroupes(String listeGroupes){
		this.listeGroupes = listeGroupes;
	}

	public String getListeGroupes(){
		return listeGroupes;
	}

	public void setTemps(String temps){
		this.temps = temps;
	}

	public String getTemps(){
		return temps;
	}

	public void setMoyenne(int moyenne){
		this.moyenne = moyenne;
	}

	public int getMoyenne(){
		return moyenne;
	}
}