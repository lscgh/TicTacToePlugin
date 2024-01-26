package mavenmcserver.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.joml.Vector3i;

public class GameConfig {
	
	/**
	 *  The minimum X and Z size of a game
	 */
	static int minFlatSize = 2;
	
	/**
	 *  The minimum Y size of a game
	 */
	static int minHeight = 1;
	
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
		}
		
		if(this.opponentPlayer == null) {
			errors.add("Couldn't add the opponent player to the game.");
		}
		
		if(Math.min(this.size.x, Math.min(this.size.y, this.size.z)) <= 0) {
			errors.add("No dimension of the game can be smaller than 1. The smallest possible game is (" + GameConfig.minFlatSize + ", " + GameConfig.minHeight + ", " + GameConfig.minFlatSize + ").");
		}
		
		if(Math.min(this.size.x, this.size.z) < GameConfig.minFlatSize) {
			errors.add("The X and Z size of the game must not be smaller than " + GameConfig.minFlatSize + ".");
		}
		
		if(this.winRequiredAmount > Math.max(this.size.x, Math.max(this.size.y, this.size.z))) {
			errors.add("The required win amount must not be larger than the size's largest dimension.");
		}
		
		return errors;
	}
}
