package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LangServer extends Thread{
	private static final Pattern IN_QUERY_MSG_PATTERN = Pattern.compile("(.+);([\\w]{2});([\\d]+)");
	private boolean shouldBeRunning = true;
	private Socket socket;
	private int destinationPort;
	private MainServer mainServer;
	private BufferedReader in;
	private String polishWord;
	private String destLang = null;
	
	public LangServer(MainServer mainServer, Socket socket) {
		super("Language Server Thread");
		this.socket = socket;
		this.mainServer = mainServer;
	}
	
	private void sendMsgToClient(Socket goSocket, String msg) throws IOException {
		PrintWriter translator = new PrintWriter(goSocket.getOutputStream(), true);
		translator.println(msg);
		translator.close();
		goSocket.close();
	}
	
	private String getTranslation(String text, String languageCode) {
		String textTranslated = "";
		try {
			List<String> dictLines = Files.readAllLines(
					Paths.get("src", "res", languageCode + ".txt"));
			//search for translation
			textTranslated =  dictLines.stream()
	                .filter(line -> line.startsWith(text.toLowerCase()))
	                .findFirst()
	                .get()
	                	.split(":")[1].trim();
		} catch (IOException e) { 
			textTranslated = "S³ownik nie istnieje!";
			//e.printStackTrace();
		} catch(NoSuchElementException e) {
			textTranslated = "Nie znaleziono t³umaczenia";
			//e.printStackTrace();
		}
		
		return textTranslated;
	}
	

	public void run() {
		//correct input message example: 
		//"polish word to translate;destination language code;port"
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			while(shouldBeRunning) {
				String responseMsg = null;
				String clientQuery = in.readLine();
				//System.out.println("Clientquery:" + clientQuery);
				
				if (clientQuery == null) clientQuery = "none"; // check to avoid Pattern.matcher()'s null pointer exc
				Matcher matcher = IN_QUERY_MSG_PATTERN.matcher(clientQuery);
				if(matcher.matches()) { // query has the right format -> unpack the incoming message
					//init variables needed to fulfill request
					polishWord = matcher.group(1);
					destLang = matcher.group(2);
					destinationPort = Integer.parseInt(matcher.group(3));
					
					//gather translated text
					responseMsg = getTranslation(polishWord, destLang);

					//establish connection with awaiting client and send the result
					Socket replySocket;
					replySocket = new Socket(socket.getLocalAddress(), destinationPort);
					sendMsgToClient(replySocket, responseMsg);
					
				}else { // stop the loop
					shouldBeRunning = false;
				}
			}
			
			stopLangServer();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stopLangServer() throws IOException {
		in.close();
		socket.close();
	}
}
