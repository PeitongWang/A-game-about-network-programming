package NPGame;

import java.util.ArrayList;

//This class is the process of the game.
public class GameProcess {

	boolean gameStart = false;
	

	int number = 0;
	int actualSum;

	// This parameter is for more than 3 players.
	int count = 0;
	
	//Is used to judge whether the guess sum 0 has existed.
	boolean zeroGuessExists=false;
	
	// This array list is to store the player object created from each thread.
	ArrayList<Player> players = new ArrayList<Player>();

	// This method is used to calculate the guesses and return the winner for
	// the list include less than 3 players.
	public synchronized String compareSum() {

		actualSum = 0;
		actualSum += number;
		Player[] sortPlayer = new Player[2];
        StringBuffer strbuf=new StringBuffer();
		for (int i = 0; i < 2; i++) {

			actualSum += players.get(i).number;
		}

		for (int i = 0; i < 2; i++) {

			sortPlayer[i] = players.get(i);

		}

		for (int i = 0; i < sortPlayer.length - 1; i++) {

			for (int j = 0; j < sortPlayer.length - 1 - i; j++) {

				if (Math.abs(sortPlayer[j].guessSum - actualSum) > Math.abs(sortPlayer[j + 1].guessSum - actualSum)) {
					Player temp = sortPlayer[j];
					sortPlayer[j] = sortPlayer[j + 1];
					sortPlayer[j + 1] = temp;

				}

			}

		}


		for(int i=0; i<sortPlayer.length;i++){
			
        if(Math.abs(sortPlayer[i].guessSum - actualSum)==Math.abs(sortPlayer[0].guessSum - actualSum)){
				
				strbuf.append(sortPlayer[i].playerName+" ");
				
				
			}
			
			
			
		}
		
		String winner=strbuf.toString()+"Win!"+" "+"Actual Sum Is:"+actualSum;
		
		strbuf.delete(0, strbuf.length());
		
		return winner;

	}

	// This method is used to calculate the guesses and return the winner for
	// the list include more than 3 players.
	public synchronized String compareSum2(int count) {
		actualSum = 0;
		actualSum += number;
		Player[] sortPlayer = new Player[count];
        StringBuffer strbuf=new StringBuffer();

		for (int i = 0; i < count; i++) {

			actualSum += players.get(i).number;
		}

		for (int i = 0; i < count; i++) {

			sortPlayer[i] = players.get(i);

		}

		for (int i = 0; i < sortPlayer.length - 1; i++) {

			for (int j = 0; j < sortPlayer.length - 1 - i; j++) {

				if (Math.abs(sortPlayer[j].guessSum - actualSum) > Math.abs(sortPlayer[j + 1].guessSum - actualSum)) {
					Player temp = sortPlayer[j];
					sortPlayer[j] = sortPlayer[j + 1];
					sortPlayer[j + 1] = temp;

				}

			}

		}

           for(int i=0; i<sortPlayer.length;i++){
			
			if(Math.abs(sortPlayer[i].guessSum - actualSum)==Math.abs(sortPlayer[0].guessSum - actualSum)){
				
				strbuf.append(sortPlayer[i].playerName+" ");
				
			}
			
			}
		
		String winner=strbuf.toString()+"Win!"+" "+"Actual Sum Is:"+actualSum;
		
		strbuf.delete(0, strbuf.length());
		
		return winner;


	}

}
