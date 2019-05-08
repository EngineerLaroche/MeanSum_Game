package view;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class Introduction extends JPanel{

	private JLabel titre, messageIntro, messageIntro2, messageIntro3, messageIntro4, espace, espace2;
	
	private String intro = " **** BIENVENUE AU JEU MEANSUM ****";
	
	private String message = "POUR INITIALISER UNE PARTIE ET OBTENIR LES BON PARAMÈTRES DE JEU SELON LE MODE CHOISI, ";
	private String message2 = "IL FAUT CLIQUER UNE FOIS SUR LE BOUTON NEXT. UNE FOIS CLIQUÉ, IL NE SERA PLUS NÉCESSAIRE ";
	private String message3 = "DE REFAIRE CETTE ÉTAPE, TOUT SERA ACTIVÉ COMME DEMANDÉ. ";
	private String message4 = "AVANT DE SÉLECTIONNER UN DES MODES AU BAS DE LA PAGE, VEUILLEZ LIRE CECI: ";

	
	public Introduction(JTabbedPane pane){

		titre = new JLabel();
		titre.setText(intro);
		titre.setFont(new Font("Arial", Font.BOLD, 26));
		
		espace = new JLabel();
		espace.setText(" ");
		
		espace2 = new JLabel();
		espace2.setText(" ");
		
		messageIntro4 = new JLabel();
		messageIntro4.setText(message4);
		messageIntro4.setFont(new Font("Arial", Font.BOLD, 26));
		
		messageIntro = new JLabel();
		messageIntro.setText(message);
		messageIntro.setFont(new Font("Arial", Font.BOLD, 20));
		
		messageIntro2 = new JLabel();
		messageIntro2.setText(message2);
		messageIntro2.setFont(new Font("Arial", Font.BOLD, 20));
		
		messageIntro3 = new JLabel();
		messageIntro3.setText(message3);
		messageIntro3.setFont(new Font("Arial", Font.BOLD, 20));
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(titre, CENTER_ALIGNMENT);
		add(espace, CENTER_ALIGNMENT);
		add(messageIntro4, CENTER_ALIGNMENT);
		add(espace2, CENTER_ALIGNMENT);
		add(messageIntro, CENTER_ALIGNMENT);
		add(messageIntro2, CENTER_ALIGNMENT);
		add(messageIntro3, CENTER_ALIGNMENT);
	}
}
