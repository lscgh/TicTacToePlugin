package mavenmcserver.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.joml.Vector3i;

public class GameConfig {
	
	/**
	 *  The minimum X and Z size of a game
	 */
	public static int MIN_X_Z_SIZE = 2;
	
	/**
	 *  The minimum Y size of a game
	 */
	public static int MIN_HEIGHT = 1;
	
	/**
	 * The player who started the game
	 */
	public Player mainPlayer;
	
	/**
	 * The player who was invited to the game
	 */
	public Player opponentPlayer;
	
	/**
	 * The X, Y, Z size of the game
	 */
	public Vector3i size;
	
	/**
	 * The number of same-player-marked fields required for that player to win
	 */
	public int winRequiredAmount;
	
	
	public GameConfig(Player mainPlayer, Player opponentPlayer, Vector3i size, int winRequiredAmount) {
		this.mainPlayer = mainPlayer;
		this.opponentPlayer = opponentPlayer;
		this.size = size;
		this.winRequiredAmount = winRequiredAmount;
	}
	
	/**
	 * Checks the config for errors.
	 * @return a list with all errors found on this config. If no errors are found, an empty list is returned.
	 */
	public List<String> validate() {
		ArrayList<String> errors = new ArrayList<String>();
		
		if(this.mainPlayer == null) {
			errors.add("Couldn't add you to the game. Please retry!");
			return errors;
		}
		
		if(this.opponentPlayer == null) {
			errors.add("Couldn't add the opponent player to the game.");
			return errors;
		}
		
		if(Game.runningGames.containsKey(this.mainPlayer)) {
			errors.add("You are currently playing a game of tic-tac-toe and, thus, cannot start another one.");
			return errors;
		}
		
		if(Game.runningGames.containsKey(this.opponentPlayer)) {
			errors.add("'" + this.opponentPlayer.getName() + "' is already playing tic-tac-toe with '" + Game.runningGames.get(this.opponentPlayer).config.mainPlayer.getName() + "'!");
			return errors;
		}
		
		errors.addAll(this.validateNumbers());
		
		return errors;
	}
	
	/**
	 * Checks the config for errors with the size and winRequiredAmount (part of validate()).
	 * @return a list with all errors found on this config regarding the number values. If no errors are found, an empty list is returned.
	 */
	List<String> validateNumbers() {
		ArrayList<String> errors = new ArrayList<String>();
		
		if(Math.min(this.size.x, Math.min(this.size.y, this.size.z)) <= 0) {
			errors.add("No dimension of the game can be smaller than 1. The smallest possible game is (" + GameConfig.MIN_X_Z_SIZE + ", " + GameConfig.MIN_HEIGHT + ", " + GameConfig.MIN_X_Z_SIZE + ").");
		}
		
		if(Math.min(this.size.x, this.size.z) < GameConfig.MIN_X_Z_SIZE) {
			errors.add("The X and Z size of the game must not be smaller than " + GameConfig.MIN_X_Z_SIZE + ".");
		}
		
		if(this.winRequiredAmount > Math.max(this.size.x, Math.max(this.size.y, this.size.z))) {
			errors.add("The required win amount must not be larger than the size's largest dimension.");
		}
		
		return errors;
	}
	
	
	@Override
	public String toString() {
		return "(GameConfig: '" + this.mainPlayer.getName() + "' against '" + this.opponentPlayer.getName() + "', size " + this.size + ", winRequiredAmount " + this.winRequiredAmount + ")";
	}
}
