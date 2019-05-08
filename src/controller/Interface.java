package controller;

/**
 * Interface qui supporte le mode Replay.
 * Récupère l'information du jeu.
 */
public interface Interface {

	//La somme à atteindre
	String getSommeTuiles();

	//La moyenne à atteindre
	int getMean();

	//Valeur affiché sur une tuile
	String getValeurTuile(int index);

	//Quantité de groupes générés
	int getQuantiteGroupes();

	//Quantité de tuiles
	int getQUantiteValeurs();

	//Affiche du chronometre
	String getTempsEcoule();

	//Somme temporaire de l'utilisateur
	int getSommeTemporaire();

	//Le nombre de regroupements fait par l'utilisateur
	int getClickGroup();
}
