package zad1;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


@SuppressWarnings("serial")
public class Client extends JFrame{
	private boolean running = true;
	private Socket clientSocket;
	private PrintWriter queryOut;
	
	public Client(String serverHost, int destPort) {

		try {
			clientSocket = new Socket(serverHost, destPort);
			System.out.println("Nawi¹zano po³¹czenie z " + serverHost + ", port docelowy: " + destPort);
			queryOut = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
			
			String cmd ="_idle";
			while(running){
				//gather command from GUI window
				cmd = showInputWindow();
				if (cmd.equals("userExit")) {
					running = false;
					continue;
				}

				//create client's ServerSocket listening for translation
				ServerSocket responseSocket = new ServerSocket(generateListeningPort());
				String msg = 
						String.format("%s;%d", cmd, responseSocket.getLocalPort());
				System.out.println(String.format("%s:%s", "Sending", msg));
				//send query
				queryOut.println(msg);
				Socket sTrans = responseSocket.accept();
				//receiving translation
				BufferedReader responseReader = new BufferedReader(new InputStreamReader(sTrans.getInputStream()));
				String incoming = responseReader.readLine();
				//display reply message
				//System.out.println(incoming);
				JOptionPane.showMessageDialog(this, "T³umaczenie: " + incoming);
				//close
				responseReader.close();
				responseSocket.close();
			}
			
			//exit the client
			closeClient();
				
		} catch (UnknownHostException e) {
			System.err.println("Nieznany host.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private int generateListeningPort() {
		Random r = new Random();
		int port = r.nextInt(5000) + 40000;
		return port;
	}
	
	private void closeClient() {
		try {
			queryOut.close();
			clientSocket.close();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private String showInputWindow() {
		JPanel mainPanel = new JPanel(new GridLayout());
		JPanel labelsPanel = new JPanel(new GridLayout(0, 1, 3, 3));
		JLabel languageLabel = new JLabel("Kod Jêzyka: ");
		JLabel wordLabel = new JLabel("Polskie S³owo: ");
		labelsPanel.add(languageLabel);
		labelsPanel.add(wordLabel);
		mainPanel.add(labelsPanel, BorderLayout.WEST);
		
		JPanel inputPanel = new JPanel(new GridLayout( 0, 1, 3, 3));
		JComboBox<String> langBox = new JComboBox<String>();
		//gather available languages
		for (String langCode : MainServer.dictionariesList) {
			langBox.addItem(langCode);
		}
		JTextField wordField = new JTextField();
		inputPanel.add(langBox);
		inputPanel.add(wordField);
		mainPanel.add(inputPanel, BorderLayout.CENTER);
		
		//display dialog window in order to gather query content
		JOptionPane.showMessageDialog(this, mainPanel, "Slownik TPO3 DS16537", JOptionPane.QUESTION_MESSAGE);
		String isoChosen = String.valueOf(langBox.getSelectedItem());
		String polishWord = wordField.getText();
		
		//get information gathered from dialog window
		String outcome = polishWord.equals("") ? // kill the app or provide query content
				"userExit"
					: String.format("%s;%s", polishWord, isoChosen) ;
		//System.out.println("Outcome:" +  outcome);
		return outcome;
	}
	
	public static void main(String[] args) {
       new Client("localhost", 53285);
	}

}
