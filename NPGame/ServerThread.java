package NPGame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

//This class is the individual thread of the server serving for each player.
public class ServerThread implements Runnable {
	// For logging

	Logger logger2 = null;
	FileHandler fileHandler2 = null;
	Socket sock = null;
	InputStream in = null;
	OutputStream out = null;
	Player player = null;
	String playerName = null;
	GameProcess gp = null;
	DatagramSocket datagrSock = null;
	DatagramPacket datagrPack=null;
	 int serverNumber=0;
	//Multicast address
	final String MULTICAST_HOST = "224.0.0.1";
	
	//Multicast Port
	final int MULTICAST_PORT = 10002;
	
	//Multicast group
	InetAddress group=null;
	
	byte[] buffer=null;
	// This variable is used when the player amount equals 3 or more than 3.
	int count = 3;
	
	Random random = new Random();

	public ServerThread(Socket sock, GameProcess gp, Logger logger2) {
		this.logger2 = logger2;
		this.sock = sock;
		this.gp = gp;

	}

	@Override
	public void run() {
		System.setProperty("java.net.preferIPv6Addresses", "false");
		System.setProperty("java.net.preferIPv4Stack", "true");

		try {
			
		group = InetAddress.getByName(MULTICAST_HOST);
		
		} catch (UnknownHostException e) {
			
		logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
		}
		
		
		
		try {
		
		datagrSock = new DatagramSocket();
		
		} catch (SocketException e) {
			
		logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
		
		}


		// For logging.
		logger2.entering("ServerThread", "run");

		// Get InputStream from socket.
		try {
			
			in = sock.getInputStream();
			
			logger2.info(Thread.currentThread().getName() + ":"+"Get InputSream from socket.");
		
		} catch (IOException e) {

			logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
		}

		// Get OutputStream from socket.
		try {
			
			out = sock.getOutputStream();
			logger2.info(Thread.currentThread().getName() + ":"+"Get InputSream from socket.");
		
		} catch (IOException e) {
			
			logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
		}

		// For reading from the socket Input Stream.
		int ch = 0;
		// For storing message.
		StringBuffer strbuf = new StringBuffer();

		// Send player IP Address and player Port to the player.
		try {
			out.write(("Player IP Address :" + sock.getInetAddress().getHostAddress() + "  " + "Player Port :"
					+ sock.getPort() + "\n").getBytes());
			out.flush();
			
			logger2.info(Thread.currentThread().getName() + ":"+"Send player IP Address and player Port to the player.");
		
		} catch (IOException e) {
			
			logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
		}

		// Send request for inputting player name.
		try {
			out.write(("Please Input Your Player Name :" + "\n").getBytes());

			out.flush();
			logger2.info(Thread.currentThread().getName() + ":"+"Send request for inputting player name.");
		
		    } catch (IOException e) {

			logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
		    }

		// Read the Player Name from client.
		logger2.info(Thread.currentThread().getName() + ":"+"Read the Player Name from client.");
		try {
			

	        //Get name
Outer1:		while(true){	
			
			
			while ((ch = in.read()) != (int) '\n') {

				strbuf.append((char) ch);

			}
			
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		for(int i=0; i<gp.players.size();i++){
			
			if(gp.players.get(i).playerName.equals(strbuf.toString())){
				
			out.write("Player Name Has Already existed. Please Input Again:\n".getBytes());	
			out.flush();
			strbuf.delete(0, strbuf.length());
			continue Outer1;
		}
		}
		out.write("Player Name Has been Received.\n".getBytes());	
		out.flush();
		    break;
		
		}
		
		
		} catch (IOException e) {
			logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
		}

		playerName = strbuf.toString();

		// Create player object.
		player = new Player(playerName);
		logger2.info(Thread.currentThread().getName() +":"+"Create a player object with the player name:" + playerName);
		strbuf.delete(0, strbuf.length());
		
		// Add the new player into the waiting list.
		gp.players.add(player);
		
		// Name the thread with player name.
		logger2.info(Thread.currentThread().getName() + ":Name current thread with current player name:" + playerName);
		Thread.currentThread().setName(player.playerName);

		// Send request for waiting to the client.
		try {
			out.write(" Please Wait...\n".getBytes());

			out.flush();
			logger2.info(Thread.currentThread().getName() + ":"+"Send request for waiting to the client.");
		
		} catch (IOException e) {
			
			logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
		
		}
		

		
		// Judge whether the game is already started. If the game is already started, the new player cannot join.
		logger2.info(Thread.currentThread().getName() +":"+"Judge whether the game has already started.");
		while (gp.gameStart == true) {

			if(player.joinLater==false){
				
				player.joinLater=true;
				
				
			}
			
			
			try {
			
			Thread.sleep(3000);
			
			} catch (InterruptedException e) {
				
			logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
			}

			System.out.println(Thread.currentThread().getName() + " Is Waiting to Join...");
			continue;
		}


		logger2.info(Thread.currentThread().getName() +":"+"Add the new player into the waiting list.");

		// Judge whether there are 2 or more than 2 players.
		logger2.info(Thread.currentThread().getName() +":"+"Judge whether there are 2 or more than 2 players");
		while (gp.players.size() < 2) {
      
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {

			logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
		}
		
		continue;
        }
		
		
		//If there are more than 3 players in the waiting list, at the beginning of the game, get first 3 players.
	   logger2.info(Thread.currentThread().getName() +":"+"Judge whether the player is in the first 3 players at the beginning of the game.");
Outer2:		while(count==3 && gp.players.indexOf(player)>2){
			
	          if(player.joinLater==true){
	        	  
	        	  count=gp.players.size();
	        	  
	        	  break;
	        	  
	          }
	
	
	
			try {
				
				Thread.sleep(3000);
				
				} catch (InterruptedException e) {
					
				logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
				}

			
			System.out.println(player.playerName+" is waiting to join...");
	
			
               if(gp.gameStart==false){
    	   
    	       
            	  
    	           continue Outer2;
    	         
    
    	          
                }else if(gp.gameStart==true){
    	        	  
    	        	  while(gp.gameStart==true){
    	        		  
    	        		  try {
    	        				
    	        				Thread.sleep(3000);
    	        				
    	        				} catch (InterruptedException e) {
    	        					
    	        				logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
    	        				}
    	        			System.out.println(player.playerName+" "+"is waiting to join...");
    	        		    continue;
    	        		  
    	        	       }
    	        	  
    	        	  count=gp.players.size();
    	        	  
    	        	  continue Outer2;
    	        	  
    	          }
			
			}
		
		
while (true) {

			// The last thread generate a server random number which
			// override previous ones.
	        serverNumber = random.nextInt(3);
            gp.number=serverNumber;
	        logger2.info(Thread.currentThread().getName()+":Generate a server random number:"+serverNumber+" (The last thread's random number will override previous ones.)");
			try {
				
			Thread.sleep(5000);
			
			} catch (InterruptedException e) {

			logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
			}

			// Send the inquiry about whether the player is ready.
			try {
				out.write("Are You Ready?(y/n)\n".getBytes());
				out.flush();
			logger2.info(Thread.currentThread().getName() + ":Send the inquiry about whether the player is ready.");
			
			} catch (IOException e) {
				
			logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
			}

			// Receive the message and Judge whether the player is ready.
			logger2.info(Thread.currentThread().getName() + ":Receive the message and Judge whether the player is ready.");
			try {
				while (in.read() != 'y') {

					continue;

				}
			} catch (IOException e) {
				
			logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
			
			}

			// Player game starting mark.
			player.start = true;
			logger2.info(Thread.currentThread().getName() + ":Modify player game starting mark.");

			// If all the players game starting mark is true, game will continue.
			logger2.info(Thread.currentThread().getName() + ":Judge wether all the players' game starting marks are true.");
			
			for (int i = 0; i < gp.players.size(); i++) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
				}
				
				if(count==3 && i>2){
					break;
				}
				
				if (gp.players.get(i).start != true) {
					i = -1;
					continue;

				}

			}

			// If all the players game starting mark are true, the game process
			// starting mark will be true.

			gp.gameStart = true;
			logger2.info(Thread.currentThread().getName() + ":"+"Modify game process starting mark.");

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Player amount is less than 3.
			logger2.info(Thread.currentThread().getName() + ":"+"Judge whether player amount is less than 3.");
			if (gp.players.size() < 3) {

				// Send Game Start information and tells all the players how
				// many players there are.
				try {
					out.write(("Game Start ! Player Amount :"+2 + "\n").getBytes());
					out.flush();
					logger2.info(Thread.currentThread().getName()+":Send Game Start information and tells all the players how many players there are:"+2);
				} catch (IOException e) {
					logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
				}

				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
				}

				// Tells each player their player name.
				try {
					out.write(("Your Player Name :" + player.playerName + "\n").getBytes());
					out.flush();
					logger2.info(Thread.currentThread().getName() +":"+"Tell player the registered player name.");
				} catch (IOException e) {
					logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
				}

				// Get the player number generated.
				try {
					player.number = in.read();
					logger2.info(Thread.currentThread().getName() +":"+"Get the player number generated.");
				} catch (IOException e) {
					logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
				}

				// For receiving the Guess from the player.
				int tempGuess = 0;
				try {
					tempGuess = in.read();
					logger2.info(Thread.currentThread().getName() +":"+"Receive the guess sum from the player.");
				} catch (IOException e) {
					logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
				}

			
				// Judge whether guess exists or not.
				logger2.info(Thread.currentThread().getName() + ":"+"Judge whether guess exists or not.");
Outer3:   while(true){	
	           //If the guess sum is not 0.
				if(tempGuess!=0){	
				
					for (int i = 0; i<2; i++) {

					if (gp.players.get(i).guessSum == tempGuess) {

						try {
							out.write(("Your Guess Sum has already existed ! Please Input Again:" + "\n").getBytes());

							out.flush();
							logger2.info(Thread.currentThread().getName() +":"+"Tell the player guess has existed.");
						} catch (IOException e) {
							logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
						}

						try {
							// If guess has already existed, input again.
							tempGuess = in.read();
							logger2.info(Thread.currentThread().getName() +":"+"Receive guess again");
						} catch (IOException e) {
							logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
						}

						continue Outer3;
						}
                     }
				 //If the guess sum is 0.
			      }else if(tempGuess==0){
				
				if(gp.zeroGuessExists==true){
					try {
						out.write(("Your Guess Sum has already existed ! Please Input Again:" + "\n").getBytes());

						out.flush();
						logger2.info(Thread.currentThread().getName() +":"+"Tell the player guess has existed.");
					} catch (IOException e) {
						logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
					}

					try {
						// If guess has already existed, input again.
						tempGuess = in.read();
						logger2.info(Thread.currentThread().getName() +":"+"Receive guess again");
					} catch (IOException e) {
						logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
					}

					continue Outer3;
					
				}
				
				   gp.zeroGuessExists=true;
			}
				
				       break;
         }
				
				// Receive the guess.
				player.guessSum = tempGuess;
				logger2.info(Thread.currentThread().getName() +":"+"Receive guess sum successfully.Guess sum is:"+player.guessSum);
			
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
				}
				
				// Sends message to the player to tell the player that guess is already received.
				try {
						out.write(("Your Guess Has Been Received Successfully!" + "\n").getBytes());
					    out.flush();
					    logger2.info(Thread.currentThread().getName() +":"+"Sends message to the player to tell the player that guess is already received.");
					} catch (IOException e) {
						logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
					}
					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
					}
					
					try {
						//Tell the player how many players joined for multicast. 
						out.write(2);
					    out.flush();
					    logger2.info(Thread.currentThread().getName() +":"+"Tell the player how many players joined for multicast");
					} catch (IOException e) {
						logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
					}

					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
					}
					
					buffer=("Player:"+player.playerName+"  Guess Sum :"+player.guessSum+"\n").getBytes();
					datagrPack=new DatagramPacket(buffer,buffer.length,group,MULTICAST_PORT);
					try {
						//Send multicast packet to the group.
						datagrSock.send(datagrPack);
						logger2.info(Thread.currentThread().getName() +":"+"Send the multicast packet to the group.");
					} catch (IOException e) {
						logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
					}
				    
				 // Player finish guessing, modify the guess finishing mark.
				  player.guessFinish = true;
				  logger2.info(Thread.currentThread().getName() +":"+"Player finish guessing, modify the guess finishing mark.");

			    // If all the players' guess finishing marks are true, game will continue.
				 logger2.info(Thread.currentThread().getName() +":"+"Judge whether all the players have finished guessing");
				 for (int i = 0; i < 2; i++) {

					if (gp.players.get(i).guessFinish == false) {

						i = -1;
						continue;
					}
				}

				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
				}
				
				
				try {
				
				//Send the winner information and actual sum to the player.
				out.write((gp.compareSum()+"\n").getBytes());
			    out.flush();
			    logger2.info(Thread.currentThread().getName() +":"+"Tell the player the result:"+gp.compareSum());
		       } catch (IOException e) {
		    	logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
			}
		     
		        player.start = false;
				logger2.info(Thread.currentThread().getName()+":"+"Modify player's game starting mark.");
				
				// One round finished
				gp.gameStart = false;
				logger2.info(Thread.currentThread().getName() +":"+"Modify game process starting mark.");

				// whether all the players' game starting marks are false.
				logger2.info(Thread.currentThread().getName()+":"+"Judge whether all the players' game starting marks are false.");
				for (int i = 0; i <2; i++) {

					if (gp.players.get(i).start != false) {

						i = -1;
						continue;

					}
				}
				player.guessFinish = false;
				logger2.info(Thread.currentThread().getName() + ":Modify player's guess finishing mark for next time use.");
				player.guessSum = 0;
				logger2.info(Thread.currentThread().getName() + ":Modify player's guess sum for next time use.");
				player.number = 0;
				logger2.info(Thread.currentThread().getName() + ":Modify player's number generated for next time use.");
                gp.zeroGuessExists=false;
                
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// There are 3 or more than 3 players
			} else if (gp.players.size() >= 3) {

				// Send Game Start information and tells all the players how
				// many players there are.
				try {
					out.write(("Game Start ! Player Amount :" +count+ "\n").getBytes());
					out.flush();
					logger2.info(Thread.currentThread().getName()+":Send Game Start information and tells all the players how many players there are:"+count);
				} catch (IOException e) {
					logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
				}

				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
				}

				// Tells each player their player name.
				try {
					out.write(("Your Player Name :" + player.playerName + "\n").getBytes());
					out.flush();
					logger2.info(Thread.currentThread().getName() +":"+"Tell player the registered player name.");
				} catch (IOException e) {
					logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
				}

				// Get the player number generated.
				try {
					player.number = in.read();
					logger2.info(Thread.currentThread().getName() + ":Get the player number generated.");
				} catch (IOException e) {
					logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
				}

				// For receiving the Guess from the player.
				int tempGuess = 0;
				try {
					tempGuess = in.read();
					logger2.info(Thread.currentThread().getName() + ":Receive the guess sum from the player.");
				} catch (IOException e) {
					logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
				}

				// Judge whether guess exists or not.
				logger2.info(Thread.currentThread().getName() +":"+"Judge whether guess exists or not.");
Outer3:   while(true){	
					//If the guess sum is not 0.
					if(tempGuess!=0){	
					
						for (int i = 0; i < gp.players.size(); i++) {

						if (gp.players.get(i).guessSum == tempGuess) {

							try {
								out.write(("Your Guess Sum has already existed ! Please Input Again:" + "\n").getBytes());

								out.flush();
								logger2.info(Thread.currentThread().getName() +":"+"Tell the player guess has existed.");
							} catch (IOException e) {
								logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
							}

							try {
								// If guess has already existed, input again.
								tempGuess = in.read();
								logger2.info(Thread.currentThread().getName() +":"+"Receive guess again");
							} catch (IOException e) {
								logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
							}

							continue Outer3;
						}

					}
				 //If the guess sum is 0.
				  }else if(tempGuess==0){
					
					if(gp.zeroGuessExists==true){
						try {
							out.write(("Your Guess Sum has already existed ! Please Input Again:" + "\n").getBytes());

							out.flush();
							logger2.info(Thread.currentThread().getName() + ":Tell the player guess has existed.");
						} catch (IOException e) {
							logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
						}

						try {
							// If guess has already existed, input again.
							tempGuess = in.read();
							logger2.info(Thread.currentThread().getName() +":"+"Receive guess again");
						} catch (IOException e) {
							logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
						}

						continue Outer3;
						
					}
					
					   gp.zeroGuessExists=true;
				}
					
					       break;
	         }

				// Receive the guess.
				player.guessSum = tempGuess;
				logger2.info(Thread.currentThread().getName() + ":"+"Receive guess sum successfully. Guess sum is:"+player.guessSum);
			
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
				}
				
				// Sends message to the player to tell the player that guess is already received.
				try {
						out.write(("Your Guess Has Been Received Successfully!" + "\n").getBytes());
					    out.flush();
					    logger2.info(Thread.currentThread().getName() + ":"+"Sends message to the player to tell the player that guess is already received.");
					} catch (IOException e) {
						logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
					}
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
					}
					
					try {
						//Tell the player how many players joined for multicast. 
						out.write(count);
					    out.flush();
					    logger2.info(Thread.currentThread().getName() +":"+"Tell the player how many players joined for multicast");
					} catch (IOException e) {
						logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
					}

					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
					}
					
					buffer=("Player:"+player.playerName+"  Guess Sum :"+player.guessSum+"\n").getBytes();
					datagrPack=new DatagramPacket(buffer,buffer.length,group,MULTICAST_PORT);
					try {
						//Send multicast packet to the group.
						datagrSock.send(datagrPack);
						logger2.info(Thread.currentThread().getName() + ":"+"Send the multicast packet to the group.");
					} catch (IOException e) {
						logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
					}
				    
				 // Player finish guessing, modify the guess finishing mark.
				  player.guessFinish = true;
				  logger2.info(Thread.currentThread().getName() + ":"+"Player finish guessing, modify the guess finishing mark.");

			    // If all the players' guess finishing marks are true, game will continue.
				 logger2.info(Thread.currentThread().getName() + ":"+"Judge whether all the players have finished guessing");
				 for (int i = 0; i <count; i++) {

					if (gp.players.get(i).guessFinish == false) {

						i = -1;
						continue;
					}
				}

				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
				}
				
				
				try {
				
				//Send the winner information and actual sum to the player.
				out.write((gp.compareSum2(count)+"\n").getBytes());
			    out.flush();
			    logger2.info(Thread.currentThread().getName() +":"+"Tell the player the result:"+gp.compareSum2(count));
		       } catch (IOException e) {
		    	logger2.info(Thread.currentThread().getName()+":"+e.getMessage());
			}
				
			
		     
		        player.start = false;
				logger2.info(Thread.currentThread().getName() + ":Modify player's game starting mark.");
				
				
				// whether all the players' game starting marks are false.
				logger2.info(Thread.currentThread().getName()+ ":Judge whether all the players' game starting marks are false.");
				for (int i = 0; i <count; i++) {

					if (gp.players.get(i).start != false) {

						i = -1;
						continue;

					}
				}
				
				// One round finished
				gp.gameStart = false;
				logger2.info(Thread.currentThread().getName() + ":Modify game process starting mark.");

				player.guessFinish = false;
				logger2.info(Thread.currentThread().getName() + ":Modify player's guess finishing mark for next time use.");
				player.guessSum = 0;
				logger2.info(Thread.currentThread().getName() + ":Modify player's guess sum for next time use.");
				player.number = 0;
				logger2.info(Thread.currentThread().getName() + ":Modify player's number generated for next time use.");
			    gp.zeroGuessExists=false;
			    count=gp.players.size();
		     
				}

		}

	}

	}

