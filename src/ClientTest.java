import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class ClientTest {
	
	
	
	public static void main(String[] args) {
		AskGui myAskGui = new AskGui("Host Adresse?", "IP des Servers?");
		
		String name = "";
		String host = "";
		
		boolean schalter = true;
		while(schalter) {
			try {
				host = myAskGui.call();
				myAskGui = new AskGui("Ihr Name", "bitte geben Sie ihren UserName ein");
				name = myAskGui.call();
				new Client(host, name);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 		//Client initialisieren mit RückgabeWert(IP) von printAskGui()
		
		
		/*AskUserYesNo endFrage = new AskUserYesNo("Reconnect?", "Frage an User");
		if(!endFrage.printGui()) {
			schalter = false;
		}*/
		schalter = false;
		}
		System.out.println("Programm Beendet");
	}
}
