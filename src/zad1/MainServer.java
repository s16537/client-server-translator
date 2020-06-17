package zad1;

import java.io.IOException;
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

public class MainServer {
	
	public static ArrayList<String> dictionariesList = new ArrayList<String>(Arrays.asList("de", "en", "fr"));
	private boolean isRunning = true;
	
	public MainServer(int listeningPort) {
		try {
			ServerSocket ss = new ServerSocket(listeningPort);
			while(isRunning) {
				//receive client connection and start new thread to serve
				//individual client
				Socket s = ss.accept();
				LangServer serverConnection = new LangServer(this, s);
				serverConnection.start();
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
	
	public static void main(String[] args) {
		//create main server and run it
		int sPort = 53285;
		new MainServer(sPort);
	}

}
