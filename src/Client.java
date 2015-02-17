import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


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
	private final JButton buttonFire = new JButton("Fire");
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String talkMessage = "";
	private String serverIP;
	private Socket connection = null; 
	private String rounds;
	private String userName = "";
	//constructor
	public Client(String host, String clientName) throws ClassNotFoundException{
		super("AWIM - CLIENT - UserName: "+ clientName);
				this.host = host;
				this.clientName = clientName;
				//RSA wird eingerichtet
				clientCrypto = new KryptoClient();
				//ClientGui zeichnen
				printClientGui();
				//Client starten
				startClient();
				//ClientGui zerstören
				dispose();
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
		chatWindow = new JTextArea();
		chatWindow.setLineWrap(true);
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
		    	//buttonFire.setSize(125,35);
		    	buttonFire.addActionListener( new ActionListener() {
		    		  @Override public void actionPerformed( ActionEvent event ) {
		    			    sendMessage(userText.getText() ); // Eingabe holen
		    				userText.setText(""); //reset Texteingabefeld
		    		  }
		    		} );		    
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
				connectToServer();
				setupStreams();
				try{
					setupKrypto();
					whileChatting();	
				}catch(IndexOutOfBoundsException indexOutOfBoundsException) {
					closeCrap();
				}
				
		}catch(EOFException eofException){
			eofException.printStackTrace();
		}catch(IOException ioException) {
			showMessage("\n client hat verbindung getrennt");
		}finally{
			closeCrap();
		}
		
	}
	
	//Verbindung zum Server aufbauen
	private void connectToServer() throws IOException {
		showMessage("\ntrying to connect...");
		connection = new Socket(InetAddress.getByName(serverIP), 3336);
		System.out.println("IsConnected: " + connection.isConnected());
		showMessage("\nconnected to server: " + connection.getInetAddress().getHostName());
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
		System.out.println("Streams eingerichtet");
		showMessage("\n iostreams eingerichtet\n");
		showMessage("---------Konversation beginnt--------");
	}
	
	private void setupKrypto() throws IOException, ClassNotFoundException{
		String tempInput = "";
		//Oeffentlichen Schluessel an Server uebertragen
		output.writeObject(clientCrypto.e.toString() + "&" + clientCrypto.n.toString() +":"+ clientName);
		output.flush();
		System.out.println("RSAnachricht: " + clientCrypto.e.toString() + "&" + clientCrypto.n.toString());
		

		tempInput = (String) input.readObject();
		System.out.println("tempInput: " + tempInput);
		//Password benoetigt ?
		if(tempInput.equals("::pw::")) {
		AskPassword myPassword = new AskPassword("Passwortabfrage", "Der Server verlangt ein Passwort");
		System.out.println("Passwort: " + myPassword.answerOfUser);
		output.writeObject(myPassword.answerOfUser);
		output.flush();
		}
		
		System.out.println("Subkey holen und setzen");
		try{
		clientCrypto.setCryptoConfig(clientCrypto.privateKeyDecrypt(new BigInteger((String) input.readObject())).toString());
		}catch(EOFException eofException) {
			System.out.println("Passwort abgelehnt");
			this.dispose();
		}
		showMessage("Verschlüsselung zu Server: " + connection.getInetAddress().getCanonicalHostName() + " eingerichtet");
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void whileChatting() throws IOException, EOFException, ClassNotFoundException {
		//Ende des Verbindungsaufbaus und der RSA-Uebertragung. Chatfunktion startet...
		ableToType(true); 
		do{	
			talkMessage = (String) input.readObject();
			if(!talkMessage.startsWith("@rsa")) { //RSA Verbindungsdaten ignorieren
				if(talkMessage.startsWith("cr1")) {
					//System.out.println("Cipher: " + message);	
					talkMessage = clientCrypto.decryptMessage(talkMessage.substring(3), clientCrypto.rounds);
					showMessage(talkMessage);
				}else{
					showMessage(talkMessage);
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
	private void hiddenSend(String message) {
		
		try{System.out.println(message);
			output.writeObject(message);
			output.flush();
			//output.writeObject(cryptoModule.n);
			//output.flush();
			//showMessage("server: " + message);
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	
	}
	
	/**
	 * 
	 * @param message
	 */
	private void sendMessage(String message) {
		try{
			output.writeObject(userName + "[" + new java.util.Date().toString().substring(4,16) + "]:\n"+ message);
			output.flush();
			//writeLogFile("client[" + new java.util.Date().toString().substring(4,16) + "]:\n"+ message + "\n", new File("/home/mrtransistor/workspace/InputOutputInterface/src/logFile.log"));
			//showMessage(userName + "[" + new java.util.Date().toString().substring(4,16) + "]:\n"  + message);
		}catch(IOException ioException) {
			chatWindow.append("\n nachrichten senden führte zu fehler");
		}	
	}
	
	/**
	 * 
	 * @param message
	 */
		public void sendMessageEncrypted(String message) {
			try{
				output.writeObject("cr1" + clientCrypto.encryptMessage(userName + "[-c- " + new java.util.Date().toString().substring(4,16) + "]:\n" + message, clientCrypto.rounds));
				output.flush();
				//writeLogFile("client[" + new java.util.Date().toString().substring(4,16) + "]:\n"+ message + "\n", new File("/home/mrtransistor/workspace/InputOutputInterface/src/logFile.log"));
				//System.out.println(cryptoModule.encryptMessage("client: " + message, cryptoModule.rounds));
				//showMessage(userName + "[-c- " + new java.util.Date().toString().substring(4,16) + "]:\n"+ message);
			}catch(IOException ioException){
				ioException.printStackTrace();
			}
		
		}
		
		/**
		 * 
		 */
		private void killConnectionOnClose() {
			new Runnable() {
					public void run() {
							while(isActive()){}
							System.out.println("TOT");
							sendMessage("killme");
					}
			};
		}
	
	/**
	 * 
	 */
	private void showMessage(final String m) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run(){
						chatWindow.append("\n" + m);
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
		

	
	
}
