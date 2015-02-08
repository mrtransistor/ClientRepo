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
	
	public static void main(String[] args) {
		
		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
	    Future<String> ask = singleThreadExecutor.submit(new AskGui());
		String a ="";
		Client test = null;
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
		test = new Client(a);
		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		test.startClient();
	}
}
