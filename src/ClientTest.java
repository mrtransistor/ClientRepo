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
	
	public ClientTest() {
		
	}
	
	private static String printAskGui(String Name, String Question) {
		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
	    Future<String> ask = singleThreadExecutor.submit(new AskGui(Name, Question));
		String a ="";
		try {
			a = ask.get();
			System.out.println(a);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return a;
	}
	
	public static void main(String[] args) {
		boolean schalter = true;
		while(schalter) {

		try {
			new Client(printAskGui("Host?", "Bitte geben Sie die Server IP ein:"), printAskGui("Clientname", "Ihr Name? "));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		//Client initialisieren mit RückgabeWert(IP) von printAskGui()
		
		
		AskUserYesNo endFrage = new AskUserYesNo("Reconnect?", "Frage an User");
		if(!endFrage.printGui()) {
			schalter = false;
		}

		}
		System.out.println("Programm Beendet");
	}
}
