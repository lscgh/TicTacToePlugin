package mavenmcserver.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.joml.Vector3i;

public class GameConfig {
	
	public static int MIN_DIMENSION_SIZE = 1;
	
	/**
	 *  The minimum X and Z size of a game
	 */
	public static int MIN_X_Z_SIZE = 2;
	
	/**
	 *  The minimum Y size of a game
	 */
	public static int MIN_HEIGHT = 1;
	
	static String ERROR_MAIN_PLAYER_NULL = "Couldn't add you to the game. Please retry!";
	static String ERROR_OPPONENT_PLAYER_NULL = "Couldn't add the opponent player to the game.";
	static String ERROR_PLAYER_ALREADY_IN_GAME = "You are currently playing a game of tic-tac-toe and, thus, cannot start another one.";
	static String ERROR_WIN_REQUIRED_AMOUNT_TOO_LARGE = "The required win amount must not be larger than the size's largest dimension.";
	
	/**
	 * The player who started the game
	 */
	public final Player mainPlayer;
	
	/**
	 * The player who was invited to the game
	 */
	public final Player opponentPlayer;
	
	/**
	 * The X, Y, Z size of the game
	 */
	public final Vector3i size;
	
	/**
	 * The number of same-player-marked fields required for that player to win
	 */
	public final int winRequiredAmount;
	
	
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
	public List<String> validateReturningErrors() {
		ArrayList<String> errors = new ArrayList<String>();
		
		if(this.mainPlayer == null) {
			errors.add(GameConfig.ERROR_MAIN_PLAYER_NULL);
			return errors;
		}
		
		if(this.opponentPlayer == null) {
			errors.add(GameConfig.ERROR_OPPONENT_PLAYER_NULL);
			return errors;
		}
		
		if(Game.runningGames.containsKey(this.mainPlayer)) {
			errors.add(GameConfig.ERROR_PLAYER_ALREADY_IN_GAME);
			return errors;
		}
		
		if(Game.runningGames.containsKey(this.opponentPlayer)) {
			errors.add("'" + this.opponentPlayer.getName() + "' is already playing tic-tac-toe with '" + Game.runningGames.get(this.opponentPlayer).config.mainPlayer.getName() + "'!");
			return errors;
		}
		
		errors.addAll(this.validateNumbersReturningErrors());
		
		return errors;
	}
	
	/**
	 * Checks the config for errors with the size and winRequiredAmount (part of validate()).
	 * @return a list with all errors found on this config regarding the number values. If no errors are found, an empty list is returned.
	 */
	List<String> validateNumbersReturningErrors() {
		ArrayList<String> errors = new ArrayList<String>();
		
		if(this.getSmallestDimension() < GameConfig.MIN_DIMENSION_SIZE) {
			errors.add("No dimension of the game can be smaller than " + GameConfig.MIN_DIMENSION_SIZE + ". The smallest possible game is (" + GameConfig.MIN_X_Z_SIZE + ", " + GameConfig.MIN_HEIGHT + ", " + GameConfig.MIN_X_Z_SIZE + ").");
		}
		
		if(Math.min(this.size.x, this.size.z) < GameConfig.MIN_X_Z_SIZE) {
			errors.add("The X and Z size of the game must not be smaller than " + GameConfig.MIN_X_Z_SIZE + ".");
		}
		
		if(this.winRequiredAmount > this.getLargestDimension()) {
			errors.add(GameConfig.ERROR_WIN_REQUIRED_AMOUNT_TOO_LARGE);
		}
		
		return errors;
	}
	
	public int getSmallestDimension() {
		return Math.min(this.size.x, Math.min(this.size.y, this.size.z));
	}
	
	public int getLargestDimension() {
		return Math.max(this.size.x, Math.max(this.size.y, this.size.z));
	}
	
	
	@Override
	public String toString() {
		return "(GameConfig: '" + this.mainPlayer.getName() + "' against '" + this.opponentPlayer.getName() + "', size " + this.size + ", winRequiredAmount " + this.winRequiredAmount + ")";
	}
}
