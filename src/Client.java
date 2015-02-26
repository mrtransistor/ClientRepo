import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.management.Attribute;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;



public class Client extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String host;
	String clientName;
	KryptoClient clientCrypto;
	private final JButton buttonSend = new JButton( "Send" );
	private final JButton buttonEncryptedSend = new JButton("crypt Send");
	private final JButton buttonFire = new JButton("Spielen");
	private JTextField userText;
	private JTextPane chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String talkMessage = "";
	private String serverIP;
	private Socket connection = null; 
	private String userName = "";
	private String myNameColor = "";
	public boolean runningRequest = false;
	public boolean runningGame = false; 
	public String requestSendTo = "";
	public String playedGame = "";
	public String responseValue = ""; 
	//constructor
	public Client(String host, String clientName) throws ClassNotFoundException {
		super("AWIM - CLIENT - UserName: "+ clientName);
				this.host = host;
				this.clientName = clientName;
				//RSA wird eingerichtet
				clientCrypto = new KryptoClient();
				//ClientGui zeichnen
				printClientGui();
				//Username Farbe zufällig generieren
				setRandomNameColor();
				showMessage("<b>" + userName + "</b> deine zufällige Farbe ist: <font color=" + myNameColor + "><b>" + myNameColor + "</b></font>");
				//Client starten
				startClient();
				//ClientGui zerstören
				dispose();
	}
	
	/**
	 * 
	 */
	void setRandomNameColor() {
		String singleHexValue;
		for(int i = 0; i < 6; i++) {
			singleHexValue = String.valueOf((int) Math.round( Math.random() * 15));
			//Zufallszahlen an Hexadezimalsystem anpassen
			switch(singleHexValue) {
			case "10": singleHexValue = "A";
			break;
			case "11": singleHexValue = "B";
			break;
			case "12": singleHexValue = "C";
			break;
			case "13": singleHexValue = "D";
			break;
			case "14": singleHexValue = "E";
			break;
			case "15": singleHexValue = "F";
			break;
			}
			System.out.println(singleHexValue);
			//Farbe für Clientnamen setzen
			myNameColor += singleHexValue;
		}
	}
	
	/**
	 * 
	 */
	private void printClientGui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		userName = clientName;
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		//userText.setSize(550, 30);
		userText.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event){
						if(! userText.getText().equals("") ) {
							sendMessage(event.getActionCommand());
							userText.setText("");
						}
					}
				}
				
		);
		//Chatwindow erzeugen
		chatWindow = new JTextPane();
		chatWindow.setContentType("text/html");
		chatWindow.setEditorKit(new HTMLEditorKit());
		chatWindow.setEditable(false);
		DefaultCaret caret = (DefaultCaret)chatWindow.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		buttonSend.addActionListener( new ActionListener() {	
		public void actionPerformed( ActionEvent event ) {
			    if(! userText.getText().equals("") ) {
			    	sendMessage(userText.getText()); // Eingabe holen
			    	userText.setText(""); //reset Textfeld
			    }
				  }
				});    
				//buttonEncryptedSend.setSize(125,35);
		        buttonEncryptedSend.addActionListener( new ActionListener() {
		  		  @Override public void actionPerformed( ActionEvent event ) {
		  			    
		  			    sendMessageEncrypted(userText.getText()); // Eingabe holen
		  				userText.setText(""); //reset Texteingabefeld
		  		  }
		  		} );	        
		    	buttonFire.addActionListener( new ActionListener() {
		    		  public void actionPerformed( ActionEvent event ) {
		    			  if(!runningRequest || !runningGame) {
		    				  String s = userText.getText();
		    				  if(s.contains("$") && s.length() > 2) {
		    					  hiddenSend("@play" + s); // Eingabe holen
		    					  runningRequest = true;
		    					  requestSendTo = s.substring(0, s.indexOf('$'));
		    					  playedGame =  s.substring(s.indexOf('$')+1);
		    				  	}
		    			  }else{
		    				showMessage("Spielanfrage zur Zeit nicht möglich. Bereits Anfrage gesendet oder noch im Spiel ?");
		    			  }
		    			  userText.setText(""); //reset Texteingabefeld	
		    		  }
		    	});		    
		    	JPanel buttonPanel = new JPanel();
		    	buttonPanel.setLayout( new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		    	buttonPanel.add(buttonSend);
		    	buttonPanel.add(buttonEncryptedSend);
		    	buttonPanel.add(buttonFire);
		    	buttonPanel.setVisible(true);
		    	
		    	
		    	//EingabeFeld hinzufügen zu JFrame
		    	add(userText, BorderLayout.PAGE_START);
		    	
		    	JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,buttonPanel,new JScrollPane(chatWindow) );
		    	splitPane.setDividerLocation(115);
		    	add(splitPane, BorderLayout.CENTER);  //ChatWindow hinzufügen
		    	
		    	setLocation(250,250);
				setSize(550, 280);
				setVisible(true);
	}
	/**
	 * 
	 * @throws IndexOutOfBoundsException
	 */
	public void startClient() throws IndexOutOfBoundsException, ClassNotFoundException {
		try{
				//Verbindung zu Server aufbauen
				connectToServer();
				//Input und Outputströme aufbauen
				setupStreams();
				try{
					//Verschlüsselungsdaten übertragen
					setupKrypto();
					//Chatvorgang verwalten
					whileChatting();	
				}catch(IndexOutOfBoundsException indexOutOfBoundsException) {
					//Alle Verbindungen schließen
					closeCrap();
				}
				
		}catch(EOFException eofException){
			eofException.printStackTrace();
		}catch(IOException ioException) {
			showMessage("\n client hat verbindung getrennt");
		}finally{
			//Alle Verbindungen schließen
			closeCrap();
		}
		
	}
	
	//Verbindung zum Server aufbauen
	private void connectToServer() throws IOException {
		showMessage("\nVerbindungsaufbau...");
		connection = new Socket(InetAddress.getByName(serverIP), 3336);
		//System.out.println("IsConnected: " + connection.isConnected());
		showMessage("\nVerbindung zu:<b> " + connection.getInetAddress().getHostName() + "</b> hergestellt.");
	}

	//IOStreams starten und konfigurieren
	private void setupStreams() throws ClassNotFoundException {
		try {
			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush();
		} catch (IOException e) {
		// TODO Auto-generated catch block
			System.out.println("Fehler beim Senden");
			e.printStackTrace();
		}
		try {
			input = new ObjectInputStream(connection.getInputStream());
		} catch (IOException e) {
			System.out.println("Fehler beim empfangen");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("IOStreams eingerichtet");
		//showMessage("\n iostreams eingerichtet\n");
	}
	
	private void setupKrypto() throws IOException, ClassNotFoundException{
		String tempInput = "";
		String pubKey = "";
		//Oeffentlichen Schluessel an Server uebertragen
		output.writeObject(clientCrypto.e.toString() + "&" + clientCrypto.n.toString() +":"+ clientName);
		output.flush();
		System.out.println("RSAnachricht: " + clientCrypto.e.toString() + "&" + clientCrypto.n.toString());
		
		//von Server Passwortabfrage & PublicKey empfangen oder nur Public Key
		tempInput = (String) input.readObject();
		System.out.println("tempInput: " + tempInput);
		System.out.println(tempInput.startsWith("::pw::"));
		//Password benoetigt ?
		if(tempInput.startsWith("::pw::")) {
			//PublicKey extrahieren
			pubKey = tempInput.substring(6);
			System.out.println("pubKey e: " + pubKey.substring(0, pubKey.indexOf('&')));
			System.out.println("pubKey n: " + pubKey.substring(pubKey.indexOf('&')+1));
			//Passwortabfrage bei Client starten
			AskPassword myPassword = new AskPassword("Passwortabfrage", "Der Server verlangt ein Passwort");
			System.out.println("Passwort: " + myPassword.answerOfUser);
			String s = stringToAsciiStringAndShift(myPassword.answerOfUser, 0);
			System.out.println("String: " +"1" + s);
			BigInteger testMe = new BigInteger("1" + s).modPow( new BigInteger(pubKey.substring(0, pubKey.indexOf('&'))) , new BigInteger(pubKey.substring(pubKey.indexOf('&')+1)));
			System.out.println("BigInteger: " + testMe);
			output.writeObject(new BigInteger("1" + s).modPow( new BigInteger(pubKey.substring(0, pubKey.indexOf('&'))) , new BigInteger(pubKey.substring(pubKey.indexOf('&')+1))).toString());
			output.flush();
		}
		
		try{
		clientCrypto.setCryptoConfig(clientCrypto.privateKeyDecrypt(new BigInteger((String) input.readObject())).toString());
		showMessage("Password akzeptiert - Krypto erfolgreich eingerichtet");
		}catch(EOFException eofException) {
			System.out.println("Passwort abgelehnt");
			this.dispose();
		}
		showMessage("<b>Verbindung zu " + connection.getInetAddress().getCanonicalHostName() + " vollständig eingerichtet</b>\n---------------------------" +
				"------------------------------------------");
	}
	
	/**
	 * " 
	 * @throws IOException
	 */
	private void whileChatting() throws IOException, EOFException, ClassNotFoundException {
		//Ende des Verbindungsaufbaus und der RSA-Uebertragung. Chatfunktion startet...
		ableToType(true); 
		do{	try{
				talkMessage = (String) input.readObject();
		}catch(EOFException eofException) {
			//Server beendet Verbindung abfangen und Frage nach ReConnect
			System.out.println(eofException);
			break;
		}
			if(!talkMessage.startsWith("@rsa")) { //RSA Verbindungsdaten ignorieren
				if(talkMessage.startsWith("cr1")) {
					//System.out.println("Cipher: " + message);	
					talkMessage = clientCrypto.decryptMessage(talkMessage.substring(3), clientCrypto.rounds);
				}
			}
			//Befehl oder Nachricht
			if(talkMessage.length() > 5 && talkMessage.startsWith("@play")) {
				System.out.println("talkMessage: " + talkMessage);
				new PlayGame(talkMessage.substring(5, talkMessage.indexOf('$')), talkMessage.substring(talkMessage.indexOf('$')+1), this).run();
			}else if(talkMessage.length() > 8 && talkMessage.startsWith("@response")) {
				responseValue = talkMessage.substring(9, talkMessage.indexOf('$'));
				System.out.println("responseValue: " + responseValue);
				if(responseValue.equals("true")) {
					//Spielername und Spiel das richtige ?
					if(playedGame.equals(talkMessage.substring(talkMessage.indexOf('$')+1, talkMessage.indexOf('&'))) && requestSendTo.equals(talkMessage.substring(talkMessage.indexOf('&')+1))) {
						//Start playedGame;
						System.out.println("Game wird gestartet bei " + this.userName);
					}	
				}else{
					showMessage("Spielanfrage von " + requestSendTo  +" abgelehnt");
					runningGame = false;
					runningRequest = false;
					requestSendTo = "";
					playedGame = "";
					responseValue = "";
				}
			}
		}while(true);
	}
	/**
	 * 
	 */
	private void closeCrap() {
		showMessage("\n shutting down connection");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioexception) {
			ioexception.printStackTrace();
		}	
	}
	
	/**
	 * 
	 * @param message
	 */
	public void hiddenSend(String message) {
		
		try{System.out.println("Message to Opponent: " + message);
			output.writeObject(message);
			output.flush();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	
	}
	
	/** private void sendMessage(String message)
	 * Versendet die Nachricht im Klartext
	 * 
	 * @param message
	 */
	private void sendMessage(String message) {
		try{
			//Html OutputString erzeugen und in Outputstream legen
			output.writeObject("<html><font color=" + myNameColor + "><b>" + userName + "</b></font><font color=a3a3a3>[" + new java.util.Date().toString().substring(4,16) + "]:</font><br><b>" + message + "</b></html>");
			//versenden
			output.flush();
			//writeLogFile("client[" + new java.util.Date().toString().substring(4,16) + "]:\n"+ message + "\n", new File("/home/mrtransistor/workspace/InputOutputInterface/src/logFile.log"));
		}catch(IOException ioException) {
			System.out.println("Senden fehlgeschlagen");
		}	
	}
	
	/**public void sendMessageEncrypted(String message)
	 * Versendet die eingegebende Nachricht verschlüsselt
	 * @param message
	 */
		public void sendMessageEncrypted(String message) {
			try{
				//Nachricht in HTML formatieren und verschüsselt in den outputstream legen
				output.writeObject("cr1" + clientCrypto.encryptMessage("<html><font color=" + myNameColor + "><b>" + userName + "</b></font><font color=9f9f9f>[" + new java.util.Date().toString().substring(4,16) + "]:</font><br><b>" + message + "</b></html>", clientCrypto.rounds));
				//versenden
				output.flush();
				//writeLogFile("client[" + new java.util.Date().toString().substring(4,16) + "]:\n"+ message + "\n", new File("/home/mrtransistor/workspace/InputOutputInterface/src/logFile.log"));
			}catch(IOException ioException){
				ioException.printStackTrace();
			}
		
		}
	
	/**
	 * private void showMessage(final String text) zeigt empfangende Nachrichten
	 * im Chatfenster an.
	 * @param text
	 */
	private void showMessage(final String text) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run(){
						//Dokument aus chatWindow holen und ablegen.
						Document doc  = chatWindow.getDocument();
						try {
							//Mittels HTMLEditorkit doc (Dokument) als HTMLDocument am Ende (doc.getLength()) des Dokmuentes anhängen.
							new HTMLEditorKit().insertHTML((HTMLDocument) doc, doc.getLength(), text, 0,0, null);
						} catch (BadLocationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}				
					}
			}
		);
	}
	
	//Erlaubt User zu schreiben oder nicht
		private void ableToType(final boolean tof) {
			SwingUtilities.invokeLater(
					new Runnable() {
						public void run(){
							userText.setEditable(tof);
					}
				}
			);
		}
		/**
		 * 
		 * @param s
		 * @param file
		 */
		public void writeLogFile(String s, File file) {			
			try {
				Writer stringWriter = new FileWriter(file, true);		//Neuer FileWriter
				stringWriter.write(s);							//String in file schreiben
				stringWriter.close();							//FileWriter schliessen
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		/**
		 * public String stringToAsciiString(String message)<br>
		 * Liest einen String ein und baut daraus einen String der die ASCII 
		 * Zeichen des eingelesenen Strings repräsentiert. z.B. "abc" -> "097098099"
		 * und gibt diesen zurück.
		 * 
		 * Liefert Grundlage für RSA Verschlüsselung aus LetterString
		 * 
		 * @param message 
		 * @return dezimale ASCII-Reprästentation eines Strings
		 */
		public String stringToAsciiStringAndShift(String message, int shift) {
			//Zwischenspeicher
			String temp = "";
			//hält Ergebnis der Funktion
			String result = "";
			//Nachricht zeichenweise durchgehen
			for (int i = 0; i < message.length(); i++) {
				//dezimalen Ascii-Wert des aktuellen Zeichens als String ablegen
				temp = String.valueOf((int) message.charAt(i) + shift);
				//Ascii-Wert == 2, eine 0 davor stellen : "98" -> "098"
				if(temp.length() == 2) {
					temp = "0" + temp;
				}
				//an ErgebnisString anhängen (append)
				result += temp;
			}
			//Ergebnis zurückgeben: z.B message = "abc" -> return "097098099" 
			//!!! 1 muss noch voran gestellt werden !!! um später korrekt verarbeitet werden zu können mit BigInteger
			// new BigInteger("050") -> keine gueltige Zahl | new BigInteger("1050") -> gueltige Zahl  
			return result;
		}
	
}
