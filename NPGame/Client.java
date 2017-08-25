package NPGame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

//This class is the client side for the player.
public class Client {

	public static void main(String[] args) throws UnknownHostException, IOException {
		System.setProperty("java.net.preferIPv6Addresses", "false");
		System.setProperty("java.net.preferIPv4Stack", "true");
		
		//Multicast address
		final String MULTICAST_HOST = "224.0.0.1";
		//Multicast port
		final int MULTICAST_PORT = 10002;
		byte[] buffer = null;
        DatagramPacket datagrPack = null;
	
        // Socket will connect to the server.
		Socket sock = new Socket(InetAddress.getLocalHost().getHostAddress(), 10001);
		
		MulticastSocket multicaSock = new MulticastSocket(MULTICAST_PORT);
		 
		//This variable is used for recording how many players will attend multicast.
		int  playerAmount=0;
		
	    //This variable is used for computing how many players has been sent for multicast.
		int receivedAmount=0;
		 
		InetAddress group = InetAddress.getByName(MULTICAST_HOST);

		// Get the input and out put streams from the socket.
		OutputStream out = sock.getOutputStream();
		InputStream in = sock.getInputStream();
		InputStream typeIn = System.in;
		OutputStream printOut = System.out;

		int ch = 0;
		// This string buffer is used to get every information, then will be
		// clear for next use.
		StringBuffer strbuf = new StringBuffer();
		int guessSum = 0;
		// Get Player IP and Port from server and print out.
		while ((ch = in.read()) != (int) '\n') {

			strbuf.append((char) ch);

		}
		printOut.write(strbuf.toString().getBytes());
		printOut.flush();
		strbuf.delete(0, strbuf.length());
		System.out.println("\n");

		// Get request from the server for inputting player Name.
		while ((ch = in.read()) != (int) '\n') {

			strbuf.append((char) ch);

		}
		printOut.write(strbuf.toString().getBytes());
		printOut.flush();
		strbuf.delete(0, strbuf.length());
		System.out.println("\n");

		// Type Player Name from the keyboard.
Outer2:	while(true){	
		
		while ((ch = typeIn.read()) != (int) '\n') {

			strbuf.append((char) ch);
		}

		out.write((strbuf.toString() + "\n").getBytes());
		out.flush();
		strbuf.delete(0, strbuf.length());
		
		while ((ch = in.read()) != (int) '\n') {

			strbuf.append((char) ch);

		}
		
		if(strbuf.toString().equalsIgnoreCase("Player Name Has Already existed. Please Input Again:")){
			
			printOut.write((strbuf.toString()+"\n\n").getBytes());
			printOut.flush();
		
			strbuf.delete(0, strbuf.length());
			continue Outer2;
		}
		
		break;
	}
		strbuf.delete(0, strbuf.length());
		System.out.println("\n");

		// Get waiting request from the server to wait other players.
		while ((ch = in.read()) != (int) '\n') {

			strbuf.append((char) ch);

		}
		printOut.write(strbuf.toString().getBytes());
		printOut.flush();
		strbuf.delete(0, strbuf.length());
		System.out.println("\n");

		while (true) {
		
			// Get the inquiry from the server about whether being ready.
			while ((ch = in.read()) != (int) '\n') {

				strbuf.append((char) ch);

			}
			printOut.write(strbuf.toString().getBytes());
			printOut.flush();
			strbuf.delete(0, strbuf.length());
			System.out.println("\n");

			// If do not type "y", the loop Outer1 can not be broken.
     
Outer1:	      while (true) {
				// Answer if the player has been ready.
				while ((ch = typeIn.read()) != (int) '\n') {

					strbuf.append((char) ch);
				}

				out.write((strbuf.toString()).getBytes());
				out.flush();

				if (strbuf.toString().equalsIgnoreCase("y")) {
					break Outer1;
				}
				strbuf.delete(0, strbuf.length());
			}
			strbuf.delete(0, strbuf.length());

			// Get game start information and the player amount information from the server.
			while ((ch = in.read()) != (int) '\n') {

				strbuf.append((char) ch);

			}
			printOut.write(strbuf.toString().getBytes());
			printOut.flush();
			strbuf.delete(0, strbuf.length());
			System.out.println("\n");
			
			//Join the multicast group.
			multicaSock.joinGroup(group);

			// Get player name registered from the server.
			while ((ch = in.read()) != (int) '\n') {

				strbuf.append((char) ch);

			}
			printOut.write(strbuf.toString().getBytes());
			printOut.flush();
			strbuf.delete(0, strbuf.length());
			System.out.println("\n");

			// Player side generates a random number.
			Random random = new Random();
			int number = random.nextInt(3);

			// Send the number to the server.
			out.write(number);
			out.flush();

			// Print the number at client side.
			System.out.println("Your Number Is :" + number);
			System.out.println();
			System.out.println("Server has already generated a number: * ");
            System.out.println();
			System.out.println("Please Input Your Guess Sum :");

			// Player Types the guess sum.
			while ((ch = typeIn.read()) != '\n') {

				guessSum = ch;
			}

			// Send the guess sum to the server.
			out.write(Integer.parseInt(((char) guessSum) + ""));

			out.flush();

			// Judge whether the guess sum has existed, if has, the loop cannot
			// be broken.
			Outer2: while (true) {

				// Get the information about whether the guess sum exists.
				while ((ch = in.read()) != (int) '\n') {

					strbuf.append((char) ch);

				}
				printOut.write(strbuf.toString().getBytes());

				printOut.flush();

				System.out.println("\n");

				// If the guess sum has existed, get this information from the
				// server.
				if (strbuf.toString().equalsIgnoreCase("Your Guess Sum has already existed ! Please Input Again:")) {

					// Input guess sum again.
					while ((ch = typeIn.read()) != '\n') {

						guessSum = ch;
					}

					// Send the second guess sum to the server.
					out.write(Integer.parseInt(((char) guessSum) + ""));

					out.flush();

					strbuf.delete(0, strbuf.length());

					continue Outer2;

				}

				strbuf.delete(0, strbuf.length());
				break;

			}
			
			
			 //Receive the player amount for multicast.
		      playerAmount=in.read();
				
		      //This variable is used for computing how many players has been sent for multicast.
			  receivedAmount=0;
		 
		    
			
		     while(receivedAmount<playerAmount){
		    	 receivedAmount++;
			
		    	 buffer=new byte[1024];
			datagrPack = new DatagramPacket(buffer,0, buffer.length);
			
			
			//Receive multicast guess.
			multicaSock.receive(datagrPack);
			
			
			String receivedGuess= new String(datagrPack.getData(),0,datagrPack.getLength());
			
			//Print multicast information.
			printOut.write(receivedGuess.getBytes());
			printOut.flush();
			
			}
			
			
			
			// Get the winner information from the server.
			while ((ch = in.read()) != (int) '\n') {

				strbuf.append((char) ch);

			}
			printOut.write(strbuf.toString().getBytes());
			printOut.flush();
			strbuf.delete(0, strbuf.length());
			System.out.println("\n");
			
			//Leave the multicast group.
			multicaSock.leaveGroup(group);

		}
	}

}
