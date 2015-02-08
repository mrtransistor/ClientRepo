import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

// miniPush-NEW

public class Client extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JButton buttonSend;
	private final JButton buttonEncryptedSend;
	private final JButton buttonFire;
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection = null; 
	KryptoClient cryptoModule;
	private String rounds;
	//constructor
	public Client(String host) {
		super("AWIM - CLIENT");
		
		cryptoModule= new KryptoClient(); //Kryptomodul erzeugen
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		//userText.setSize(550, 30);
		userText.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event){
						sendMessage(event.getActionCommand());
						userText.setText("");
					}
				}
				
		);
		//Chatwindow erzeugen
		chatWindow = new JTextArea();
		//Send Button
		buttonSend = new JButton( "Send" );
		//buttonSend.setSize(125,35);
		buttonSend.addActionListener( new ActionListener() {	
		public void actionPerformed( ActionEvent event ) {
			    sendMessage(userText.getText()); // Eingabe holen
			    userText.setText(""); //reset Textfeld
				  }
				});    
		        //Encrypted SendButton
		        buttonEncryptedSend = new JButton("crypt Send");
				//buttonEncryptedSend.setSize(125,35);
		        buttonEncryptedSend.addActionListener( new ActionListener() {
		  		  @Override public void actionPerformed( ActionEvent event ) {
		  			    
		  			    sendMessageEncrypted(userText.getText() + "  +*+"); // Eingabe holen
		  				userText.setText(""); //reset Texteingabefeld
		  		  }
		  		} );	        
		        //Encrypted Fire Fire Fire Fire
		    	buttonFire = new JButton("Fire");
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
	
	//client startet arbeit
	public void startClient() {
		boolean schalter = true;
		try{
			killConnectionOnClose();
			while(schalter) {
				connectToServer();
				setupStreams();
				schalter = whileChatting();
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
		
		connection = new Socket(InetAddress.getByName(serverIP), 3333);
		showMessage("\nconnected to server: " + connection.getInetAddress().getHostName());
	}

	//IOStreams starten und konfigurieren
	private void setupStreams() throws IOException {
		
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n iostreams eingerichtet\n");
		showMessage("---------Konversation beginnt--------");
	}
	
	//während der unterhaltung
	private boolean whileChatting() throws IOException {
		
		try {
			message = (String) input.readObject();
			cryptoModule.e =  new BigInteger(message.substring(0, message.indexOf("+")) );
			cryptoModule.n = new BigInteger(message.substring(message.indexOf("+"), message.length()-1 ));
			cryptoModule.rounds = Integer.parseInt(message.substring(message.length()-1));
			System.out.println("e: " + cryptoModule.e + "  n: " + cryptoModule.n + " rounds: " + cryptoModule.rounds);
			System.out.println("Welcher subKey? (0 - 48)");
			final String key =Integer.toString( (int) (Math.random() * 24 + 1) );
			cryptoModule.subKey = Integer.parseInt(key);
			System.out.println("Key: " + key);
			message = (cryptoModule.publicKeyEncrypt(new BigInteger(key), cryptoModule.e, cryptoModule.n)).toString();
		    hiddenSend(message); //encrypted subKey
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ableToType(true);
		do{
				try {
				message = (String) input.readObject();
				}catch(ClassNotFoundException classNotFoundException) {
				showMessage("Shitty Data received...");
			}
			if(message.startsWith("cr1")) {
				//System.out.println("Cipher: " + message);	
				message = cryptoModule.decryptMessage(message.substring(3), cryptoModule.rounds);
				showMessage(message);
			}else{
			showMessage(message);
			}
			
		}while(!message.equals("server: killclient"));
		
		return false;
		
		
	}
	//Shutdown IOStream and connections
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
	
	private void sendMessage(String message) {
		try{
			output.writeObject("client[" + new java.util.Date().toString().substring(4,16) + "]:\n"+ message);
			output.flush();
			//writeLogFile("client[" + new java.util.Date().toString().substring(4,16) + "]:\n"+ message + "\n", new File("/home/mrtransistor/workspace/InputOutputInterface/src/logFile.log"));
			showMessage("client[" + new java.util.Date().toString().substring(4,16) + "]:\n"  + message);
		}catch(IOException ioException) {
			chatWindow.append("\n nachrichten senden führte zu fehler");
		}	
	}
	
	//Nachricht senden
		public void sendMessageEncrypted(String message) {
			
			try{
				output.writeObject("cr1" + cryptoModule.encryptMessage("client[" + new java.util.Date().toString().substring(4,16) + "]:\n" + message, cryptoModule.rounds));
				output.flush();
				//writeLogFile("client[" + new java.util.Date().toString().substring(4,16) + "]:\n"+ message + "\n", new File("/home/mrtransistor/workspace/InputOutputInterface/src/logFile.log"));
				System.out.println(cryptoModule.encryptMessage("client: " + message, cryptoModule.rounds));
				showMessage("client[" + new java.util.Date().toString().substring(4,16) + "]:\n"+ message);
			}catch(IOException ioException){
				ioException.printStackTrace();
			}
		
		}
		
		//Nachrichten in Textbox anzeigen
		private void killConnectionOnClose() {
			new Runnable() {
					public void run() {
							while(isActive()){}
							System.out.println("TOT");
							sendMessage("killme");
					}
			};
		}
	
	//Nachrichten in Textbox anzeigen
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
