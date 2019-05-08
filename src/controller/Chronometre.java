package controller;
import javax.swing.JLabel;

/*******************************************************************
 * CLASSE Chronometre
 * 
 * R�sumer:
 * 
 * Class Chronometre s'occupe de la gestion tu temps qui s'affiche en 
 * secondes et en minutes. 
 * 
 * L'utilisateur peut donc �valuer sa performance de jeu entre chaque 
 * nouvelle partie g�n�r�e.
 * 
 * @Source Code complet Timer: http://www.javaxp.com/2009/10/java-time-elsdsed-counter-using-swing.html?m=1
 * 
 *******************************************************************/
class Chronometre{

	/**
	 * Label qui affiche le chronom�tre
	 */
	private JLabel labelTimer;

	/**
	 * On affiche des secondes et des minutes
	 */
	private String minutes;
	private String secondes;

	/**
	 * Permet la mise � jour du label
	 */
	private boolean run = true;

	/**
	 * D�marre le chronom�tre
	 */
	private Thread demarrerTimer;

	/**
	 * Variables supportant le m�canisme du chronom�tre
	 */
	private long tempsQuiSecoule;
	private long startTime = System.currentTimeMillis();


	/*******************************************************************
	 * CONSTRUCTEUR
	 * 
	 * r�sumer:
	 * 
	 * Re�oit en param�tre le Jlabel labelTimer du GameViewController
	 * 
	 *******************************************************************/
	public Chronometre(JLabel labelTimer){

		//Initialise le label re�u
		this.labelTimer = labelTimer;

		//D�marrage du chronom�tre
		tempsEcoule();
	}

	/*******************************************************************
	 * D�MARRAGE THREAD
	 * 
	 * R�sumer:
	 * 
	 * D�marrage du temps qui s'�coule en minutes et secondes en 
	 * initialisant un nouveau Thread.
	 * 
	 *******************************************************************/
	private void tempsEcoule(){

		//Nouveau "Thread" qui permet l'�coulement du temps 
		demarrerTimer = new Thread(){

			public void run(){ 

				//Applique la mise � jour des minutes et secondes �coul�es
				updateTemps(); 
			}
		};
		//D�marre le "Thread"
		demarrerTimer.start();	
	}

	/*******************************************************************
	 * MISE � JOUR JLABEL
	 * 
	 * R�sumer:
	 * 
	 * Met � jour le temps qui s'�coule en minutes et en secondes sur 
	 * le JLabel. On initialise les param�tres pour obtenir des milisecondes.
	 * 
	 *******************************************************************/
	private void updateTemps(){

		try{
			while(run){

				//Affiche l'�coulement du temps sur le JLabel
				labelTimer.setText(getTempsEcoule());

				//Met � jour avec unit� mesur� en milisecondes
				demarrerTimer.currentThread().sleep(1000);
			}
		}
		catch (Exception e){

		}
	}

	/*******************************************************************
	 *M�CANISME CHRONOM�TRE
	 *
	 *R�sumer:
	 * 
	 * Retourne le temps qui s'�coule en minutes et secondes. 
	 * On s'assure d'obtenir un affichage standard 00:00.
	 * 
	 *******************************************************************/
	public String getTempsEcoule(){

		tempsQuiSecoule = System.currentTimeMillis() - startTime;

		//Permet de passer des milisecondes � des secondes
		tempsQuiSecoule = tempsQuiSecoule / 1000;

		//Limite l'affichage des secondes � 60
		secondes = Integer.toString((int)(tempsQuiSecoule % 60));

		//Limite l'affichage des minutes � 60
		minutes = Integer.toString((int)((tempsQuiSecoule % 3600) / 60));

		//Tant que les secondes restent sous l'unit� des dizaines. 
		//Supporte un affichage 00:00 --> 00:01 --> 00:02 
		if (secondes.length() < 2)

			//Pour afficher au d�part **:0*
			secondes = "0" + secondes;

		//Tant que les minutes restent sous l'unit� des dizaines. 
		//Supporte un affichage 01:00 --> 02:00 --> 03:00 
		if (minutes.length() < 2)

			//Pour afficher au d�part 0*:**
			minutes = "0" + minutes;

		//System.out.println("     [ Time ] " + minutes + " : " + secondes + "     ");
		return "     [ Time ] " + minutes + " : " + secondes + "     ";
	}

	/*******************************************************************
	 * Permet l'arret du chronom�tre lorsqu'on g�n�re une nouvelle partie
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