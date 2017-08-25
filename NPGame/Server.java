package NPGame;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

//This class is the server class which will create different threads for each player.
public class Server {

	// For Server Connection logging.
	private static final Logger logger1 = Logger.getLogger(Server.class.getName());
	// For game process logging.
	private static final Logger logger2 = Logger.getLogger(ServerThread.class.getName());

	public static void main(String[] args) {

		ServerSocket servSock = null;
		Socket sock = null;
		
		//This variable is used to ensure that no more than 4 threads, no more than 4 players.
		//If the player amount is more than 4, the threads cannot be created.
		int threadCount=0;
		
		FileHandler fileHandler1 = null;
		FileHandler fileHandler2 = null;
		try {
			// Create a handler for recording Server connection and write the
			// information to the file.
			fileHandler1 = new FileHandler("ConnectionLogging.txt");
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		fileHandler1.setLevel(Level.INFO);
		fileHandler1.setFormatter(new SimpleFormatter());

		// Pass fileHandler1 as a parameter to logger1 for recording TCP
		// connection.
		logger1.addHandler(fileHandler1);
		logger1.entering("Server", "main");
		try {
			// Create a handler for recording game process and write the
			// information to the file.
			fileHandler2 = new FileHandler("GameLogging.txt");
		} catch (SecurityException e1) {

			e1.printStackTrace();
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		fileHandler2.setLevel(Level.INFO);
		fileHandler2.setFormatter(new SimpleFormatter());

		// Pass fileHandler2 as a parameter to logger2 for recording game
		// process.
		logger2.addHandler(fileHandler2);

		try {

			// Server Socket is listening to the local port for waiting for TCP
			// connection.
			servSock = new ServerSocket(10001);
			logger1.info("Server Socket is listening to the local port for waiting for TCP connection");

		} catch (IOException e) {

			logger1.info(e.getMessage());
			
			}


		try {
			servSock.setSoTimeout(10000);
		
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		

		GameProcess gp = new GameProcess();

		while (true) {
			
			if(threadCount>3){
				
				return;
				
			}

			try {

				sock = servSock.accept();
				logger1.info("One player has connected.");
			} catch (IOException e) {

				logger1.info(e.getMessage());
			}

			// If there is a computer connected with this server, create a
			// thread and start it.
			ServerThread serverThread = new ServerThread(sock, gp, logger2);
			logger1.info("Create a ServerThread Class object.");

			Thread thread = new Thread(serverThread);
			logger1.info("Create a new server thread for the player and pass the ServerThread Class object to this thread as a parameter.");

			thread.start();
			logger1.info("Start the thread:"+thread.getName());
			
			threadCount++;
		}

	}
}
