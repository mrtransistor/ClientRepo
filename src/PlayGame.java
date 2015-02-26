import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlayGame implements Runnable {

	String opponentsName;
	String game;
	boolean getKillGui = true;
	boolean getUserDecision = true;
	Client client;
	
	public PlayGame(String opponentName, String game, Client clientObject) {
		this.opponentsName = opponentName;
		this.game = game;
		this.client = clientObject;
	}
	
	@Override
	public void run() {
		askClient();
		if(getUserDecision) {
			client.hiddenSend("@responsetrue$"+game + "&" + opponentsName);
		}else {
			client.hiddenSend("@responsefalse"+game + "&" + opponentsName);			
		}
		
	}
	
	private void askClient() {
		
		JFrame askFrame = new JFrame("Spielaufforderung zu: " + this.game);
		JLabel askLabel = new JLabel(this.opponentsName + " hat dich zu einer Partie " + game + " eingeladen?");
		JLabel askLabel2 = new JLabel("Wollen Sie annehmen?");
		JButton yesButton = new JButton("Annehmen");
		yesButton.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent event ) {
				getUserDecision = true;
				getKillGui = false;
			}
		});
		JButton noButton = new JButton("Ablehnen");
		noButton.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent event ) {
				getUserDecision = false;
				getKillGui = false;
			}
		});
    	JPanel buttonPanel = new JPanel();
    	buttonPanel.setLayout( new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
    	buttonPanel.add(yesButton);
    	buttonPanel.add(noButton);
    	buttonPanel.setVisible(true);
    	askFrame.add(askLabel, BorderLayout.PAGE_START);
    	askFrame.add(askLabel2,BorderLayout.CENTER);
    	askFrame.add(buttonPanel, BorderLayout.PAGE_END);
    	askFrame.setSize(325, 125);
    	askFrame.setResizable(false);
    	askFrame.setLocation(250, 250);
    	askFrame.setVisible(true);
		while(getKillGui) {
			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		askFrame.dispose();
		System.out.println("decision: " + getUserDecision);
	}

}
