package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import zad1.LangServer;


public class MainServer {
	
	public static ArrayList<String> dictionariesList = new ArrayList<String>(Arrays.asList("de", "en", "fr"));
	
	private boolean isRunning = true;
	private ServerSocket ss;
	public static List<LangServer> connections = new ArrayList<>();
	
	public MainServer(int listeningPort) {
		System.out.println("G³ówny serwer uruchomiony, czekam na po³¹czenia...");
		//new Thread(new CommandsReader()).start();
		
		try {
			ss = new ServerSocket(listeningPort);
			while(isRunning) {
				//receive client connection and start new thread to serve
				//individual client
				Socket s = ss.accept();
				LangServer serverConnection = new LangServer(this, s);
				serverConnection.start();
				connections.add(serverConnection);
				System.out.println("Nawi¹zano po³¹czenie.");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//add new dictionary for translations
	public static void addNewDictionary(Map<String, String> fromPolishTo, String langCode) throws IOException {
		//check if lang currently not available
		for(String code : dictionariesList) {
			if (code.equalsIgnoreCase(langCode)) 
				System.err.println("Jêzyk o podanym kodzie jest ju¿ obs³ugiwany!");
				return;
		}
		
		//if not in dictionariesList proceed with language service addition
		List<String> dictLines = new ArrayList<>();
		for(Map.Entry<String, String> entry : fromPolishTo.entrySet()) {
			String line = entry.getKey() + ":" + entry.getValue();
			dictLines.add(line);
		}
		
		//create new resource file
		Path resDir = Paths.get("src", "res", langCode + ".txt");
		Files.write(resDir, dictLines, Charset.defaultCharset());
	}
	
	/*
	private void stopMainServer() throws IOException {
		for(LangServer ls : MainServer.connections) {
			ls.stopLangServer();
		}
		System.out.println("Zatrzymujê serwer.");
		System.exit(0);
	}
	*/
	
	public static void main(String[] args) {
		//create main server and run it
		int sPort = 53285;
		new MainServer(sPort);
	}
	
	/*
	private class CommandsReader implements Runnable{
		private 
		
		private List<String> mainCommands = Arrays.asList("stop server", "dictlist");
		private BufferedReader adminInput = new BufferedReader(new InputStreamReader(System.in));
		private boolean reading = true;
		
		
		@Override
		public void run(){
			String enteredCommand = "_nothing";
			
			try {
				while((enteredCommand = adminInput.readLine()) != null) {
					switch(enteredCommand) {
						case "dictlist":
							System.out.println("Obecnie obs³ugiwane jêzyki:");
							MainServer.dictionariesList.stream().forEach(System.out::println);
							break;
						case "stop server":
							System.out.println("Czy na pewno chcesz zatrzymaæ g³ówny serwer? (t/n)");
							break;
						case "t":
							//zatrzymaj server
							stopMainServer();
						case "n":
							break;
						default:
							System.out.println("Nie rozpoznano komendy (wpisz \"server help\" by wyœwietliæ  opcje)");
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
	}
	*/

}
