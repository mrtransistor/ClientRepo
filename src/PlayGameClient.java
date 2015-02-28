import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlayGameClient extends JPanel implements Runnable, ActionListener, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6982796210746404223L;
	String opponentsName;
	String game;
	boolean getKillGui = true;
	boolean getUserDecision = true;
	Client OLDresponseConnection;
	Socket connectionToServer;
	boolean isRequest;
	String serverIP;
	int serverPort;
	ObjectOutputStream output;
	ObjectInputStream input;
	int[] startConfig;
	private JFrame frame;
	private PongPanel pongPanel;
	private JFrame askFrame;
	private JLabel askLabel;
	private JLabel askLabel2;
	private JButton yesButton;
	private JButton noButton;
	private JPanel buttonPanel;
	private int paddleDirection;
	
	/**
	 * 
	 * @param clientName
	 * @param opponentName
	 * @param game
	 * @param clientObject
	 * @param isRequest
	 * @param serverIP
	 * @param serverPort
	 */
	public PlayGameClient(String clientName, String opponentName, String game, Client clientObject, boolean isRequest, String serverIP, int serverPort) {
		this.opponentsName = opponentName;
		this.game = game;
		this.OLDresponseConnection = clientObject;
		this.isRequest = isRequest;
		this.serverIP = serverIP;
		this.serverPort = serverPort;
	}
	
	/**
	 * 
	 */
    public void drawPong() {
        frame = new JFrame("TalkerPong - Ver.0.1 alpha @ sticklobot & mrt");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        pongPanel = new PongPanel(new Point(startConfig[0], startConfig[1]), new Point(startConfig[2], startConfig[3]), new Point(startConfig[4], startConfig[5]),
				 startConfig[6], startConfig[7], new Point(startConfig[8], startConfig[9]), new Point(startConfig[10], startConfig[11]), new Point(startConfig[12], 
				 startConfig[13]), startConfig[14], startConfig[15], startConfig[16]);
        frame.add(pongPanel, BorderLayout.CENTER);
        frame.setSize(450, 300);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
	
	/**
	 * IOStreams starten und konfigurieren
	 * @throws ClassNotFoundException
	 */
		private void setupStreams() {
			try {
				output = new ObjectOutputStream(connectionToServer.getOutputStream());
				output.flush();
			} catch (IOException e) {
			// TODO Auto-generated catch block
				System.out.println("Fehler beim Senden");
				e.printStackTrace();
			}
			try {
				input = new ObjectInputStream(connectionToServer.getInputStream());
			} catch (IOException e) {
				System.out.println("Fehler beim empfangen");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("IOStreams eingerichtet");
			//showMessage("\n iostreams eingerichtet\n");
		}

	/**
	 * Client nach Spielannahme fragen und Ergebnis zurueckgeben
	 */
	private void askClient() {
		
		 askFrame = new JFrame("Spielaufforderung zu: " + this.game);
		 askLabel = new JLabel(this.opponentsName + " hat dich zu einer Partie " + game + " eingeladen?");
		 askLabel2 = new JLabel("Wollen Sie annehmen?");
		 yesButton = new JButton("Annehmen");
		 yesButton.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent event ) {
				getUserDecision = true;
				getKillGui = false;
			}
		});
		noButton = new JButton("Ablehnen");
		noButton.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent event ) {
				getUserDecision = false;
				getKillGui = false;
			}
		});
		buttonPanel = new JPanel();
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
		System.out.println("AskClient() : Warten");
		while(getKillGui) {
			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("AskClient() : Tot");
		askFrame.dispose();
		System.out.println("AskClient() : decision: " + getUserDecision);
	}

	/**
	 * 
	 */
	public void run() {
		boolean runGame = false;
		//Anfrage gesendet?
		if(this.isRequest) {
			askClient();
			System.out.println("getUserDecision: " + getUserDecision);
			if(getUserDecision) {
				OLDresponseConnection.hiddenSend("@responsetrue$"+game + "&" + opponentsName);
				runGame = true;
			}else {
				runGame = false;
				OLDresponseConnection.hiddenSend("@responsefalse"+game + "&" + opponentsName);
				//SPIEL NICHT STARTEN WENN CLIENT ABLEHNT
			}
		}else {
			//Spielstart erlauben ohne Requestbeantwortung
			runGame = true;
		}
		System.out.println("runGame: " + runGame);
		if(runGame) {
			try{
				System.out.println("jetzt wirds ernst....");
				Thread.sleep(1000);
				//SPIELSTARTEN
				//Mit Server verbinden
				System.out.println("serverIP: " + serverIP);
				System.out.println("serverPort: " + serverPort);
				connectionToServer = new Socket(InetAddress.getByName(this.serverIP), serverPort);
				System.out.println("Verbunden mit Server");
				//Ein & Ausgabestroeme anlegen
				setupStreams();
				System.out.println("Ströme eingerichtet");
				//receiveStartConfig
				startConfig  = (int[]) input.readObject();
				for (int i = 0; i < startConfig.length; i++) {
					System.out.println(startConfig[i]  + ", ");
				}
				//DrawPong mit StartKonfiguration des Servers
				drawPong();
				
				//While Gaming
		        //Timer timer = new Timer(1000/120, this);
		        //timer.start();
				
				Point[] serverInput;
				System.out.println("while schleife läuft");
				while(true) { 
					
					//PaddleDirection holen und in OutputStream schreiben
					output.writeObject(pongPanel.getValue());
					output.flush();
					//ServerDaten entgegenehmen
					serverInput = (Point[]) input.readObject();
					//PongPanel Werte aktualisieren
					System.out.println("VALUES:" + serverInput[0] + " | " + serverInput[1] + " | " +serverInput[2] + " | " + serverInput[3]);
					pongPanel.setValues(serverInput[0], serverInput[1], serverInput[2], serverInput[3]);
					pongPanel.repaint();
				}
				
			}catch(IOException | ClassNotFoundException | InterruptedException ioException) {
				ioException.printStackTrace();
				System.out.println("Fehler beim Starten");	
			}
		}else {
			//
			System.out.println("Spiel wurde nicht gestartet - runGame: " + runGame + " isRequest: " + isRequest);
		}
		
		
		
		//SPIEL STARTEN
		
	}

	@Override
	public void keyPressed(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.VK_UP) {
			paddleDirection = -1;
		}else if(event.getKeyCode() == KeyEvent.VK_DOWN) {
			paddleDirection = 1;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.VK_UP) {
			paddleDirection = 0;
		}else if(event.getKeyCode() == KeyEvent.VK_DOWN) {
			paddleDirection = 0;
		}
		
	}

	@Override
	public void keyTyped(KeyEvent event) {
		// TODO Auto-generated method stub
		
	}
		

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
