package controller;
import javax.swing.JLabel;

/*******************************************************************
 * CLASSE Chronometre
 * 
 * Résumer:
 * 
 * Class Chronometre s'occupe de la gestion tu temps qui s'affiche en 
 * secondes et en minutes. 
 * 
 * L'utilisateur peut donc évaluer sa performance de jeu entre chaque 
 * nouvelle partie générée.
 * 
 * @Source Code complet Timer: http://www.javaxp.com/2009/10/java-time-elsdsed-counter-using-swing.html?m=1
 * 
 *******************************************************************/
class Chronometre{

	/**
	 * Label qui affiche le chronomètre
	 */
	private JLabel labelTimer;

	/**
	 * On affiche des secondes et des minutes
	 */
	private String minutes;
	private String secondes;

	/**
	 * Permet la mise à jour du label
	 */
	private boolean run = true;

	/**
	 * Démarre le chronomètre
	 */
	private Thread demarrerTimer;

	/**
	 * Variables supportant le mécanisme du chronomètre
	 */
	private long tempsQuiSecoule;
	private long startTime = System.currentTimeMillis();


	/*******************************************************************
	 * CONSTRUCTEUR
	 * 
	 * résumer:
	 * 
	 * Reçoit en paramètre le Jlabel labelTimer du GameViewController
	 * 
	 *******************************************************************/
	public Chronometre(JLabel labelTimer){

		//Initialise le label reçu
		this.labelTimer = labelTimer;

		//Démarrage du chronomètre
		tempsEcoule();
	}

	/*******************************************************************
	 * DÉMARRAGE THREAD
	 * 
	 * Résumer:
	 * 
	 * Démarrage du temps qui s'écoule en minutes et secondes en 
	 * initialisant un nouveau Thread.
	 * 
	 *******************************************************************/
	private void tempsEcoule(){

		//Nouveau "Thread" qui permet l'écoulement du temps 
		demarrerTimer = new Thread(){

			public void run(){ 

				//Applique la mise à jour des minutes et secondes écoulées
				updateTemps(); 
			}
		};
		//Démarre le "Thread"
		demarrerTimer.start();	
	}

	/*******************************************************************
	 * MISE À JOUR JLABEL
	 * 
	 * Résumer:
	 * 
	 * Met à jour le temps qui s'écoule en minutes et en secondes sur 
	 * le JLabel. On initialise les paramètres pour obtenir des milisecondes.
	 * 
	 *******************************************************************/
	private void updateTemps(){

		try{
			while(run){

				//Affiche l'écoulement du temps sur le JLabel
				labelTimer.setText(getTempsEcoule());

				//Met à jour avec unité mesuré en milisecondes
				demarrerTimer.currentThread().sleep(1000);
			}
		}
		catch (Exception e){

		}
	}

	/*******************************************************************
	 *MÉCANISME CHRONOMÈTRE
	 *
	 *Résumer:
	 * 
	 * Retourne le temps qui s'écoule en minutes et secondes. 
	 * On s'assure d'obtenir un affichage standard 00:00.
	 * 
	 *******************************************************************/
	public String getTempsEcoule(){

		tempsQuiSecoule = System.currentTimeMillis() - startTime;

		//Permet de passer des milisecondes à des secondes
		tempsQuiSecoule = tempsQuiSecoule / 1000;

		//Limite l'affichage des secondes à 60
		secondes = Integer.toString((int)(tempsQuiSecoule % 60));

		//Limite l'affichage des minutes à 60
		minutes = Integer.toString((int)((tempsQuiSecoule % 3600) / 60));

		//Tant que les secondes restent sous l'unité des dizaines. 
		//Supporte un affichage 00:00 --> 00:01 --> 00:02 
		if (secondes.length() < 2)

			//Pour afficher au départ **:0*
			secondes = "0" + secondes;

		//Tant que les minutes restent sous l'unité des dizaines. 
		//Supporte un affichage 01:00 --> 02:00 --> 03:00 
		if (minutes.length() < 2)

			//Pour afficher au départ 0*:**
			minutes = "0" + minutes;

		//System.out.println("     [ Time ] " + minutes + " : " + secondes + "     ");
		return "     [ Time ] " + minutes + " : " + secondes + "     ";
	}

	/*******************************************************************
	 * Permet l'arret du chronomètre lorsqu'on génère une nouvelle partie
	 * 
	 *******************************************************************/
	public void stopTimer(){

		demarrerTimer.interrupt();
		run = false;
	}

	public void redemarrerTimer(){
		run = true;
	}

	public String getSecondes(){
		return secondes;
	}

	public void setSecondes(String secondes){
		this.secondes = secondes;
	}
}