package controller;

/**
 * Interface qui supporte le mode Replay.
 * R�cup�re l'information du jeu.
 */
public interface Interface {

	//La somme � atteindre
	String getSommeTuiles();

	//La moyenne � atteindre
	int getMean();

	//Valeur affich� sur une tuile
	String getValeurTuile(int index);

	//Quantit� de groupes g�n�r�s
	int getQuantiteGroupes();

	//Quantit� de tuiles
	int getQUantiteValeurs();

	//Affiche du chronometre
	String getTempsEcoule();

	//Somme temporaire de l'utilisateur
	int getSommeTemporaire();

	//Le nombre de regroupements fait par l'utilisateur
	int getClickGroup();
}
