package NPGame;

//This class is the player object class to store the information of each player.
public class Player {

	String playerName = null;

	// Game starting mark
	boolean start = false;
	// Guess finishing mark.
	boolean guessFinish = false;
	// For receiving the number generated randomly.
	int number;
	// For receiving the input guess.
	int guessSum;
	
	//This boolean variable is used for the player whose index is more than 2 in the waiting list and join the game later.
	boolean joinLater=false;

	public Player(String playerName) {

		this.playerName = playerName;

	}

}
