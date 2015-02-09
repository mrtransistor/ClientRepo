import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class AskGui implements Callable<String> {
	JFrame askFrame;
	JLabel askLabel;
	JTextField answerTextField;
	String hostAddress = "";
	String tempNameOfFrame = "";
	String questionString = "";
	
	
	public AskGui(String nameOfFrame, String questionToUser) {
		tempNameOfFrame = nameOfFrame;
		questionString = questionToUser;
	}
	
	public String call() {
			System.out.println("Leer?" + hostAddress.isEmpty());
			askFrame = new JFrame(tempNameOfFrame);
			askFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			askLabel = new JLabel(questionString);
			answerTextField = new JTextField();
			answerTextField.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent event){
							hostAddress = answerTextField.getText();
							//System.out.println("IP: " + hostAddress);
							answerTextField.setText("Danke für ihre Eingabe");
							answerTextField.setEditable(false);
							askFrame.dispose();
						}
					});
			askFrame.add(askLabel,BorderLayout.NORTH);
			askFrame.add(answerTextField, BorderLayout.SOUTH);
			askFrame.setSize(350, 150);
			askFrame.pack();
			askFrame.setLocation(175, 150);
			askFrame.setVisible(true);
			System.out.println(askFrame.isActive());
			boolean isEmpty = true;
			while(isEmpty = hostAddress.isEmpty()) { //System.out.println("Leer: " + hostAddress.isEmpty());
			System.out.println(isEmpty);
			}
			return hostAddress;
	}
}
