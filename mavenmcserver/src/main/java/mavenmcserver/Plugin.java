package mavenmcserver;

import java.util.ArrayList;

import org.bukkit.plugin.java.JavaPlugin;

import mavenmcserver.command.CommandTicTacToe;
import mavenmcserver.command.CommandTicTacToeAccept;
import mavenmcserver.game.Game;
import mavenmcserver.game.Game.GameEndCause;

public class Plugin extends JavaPlugin {

	@Override
	public void onLoad() {
		this.getLogger().info("TicTacToe loaded!");
	}
	
	@Override
	public void onEnable() {
		// Register command /tictactoe
		new CommandTicTacToe(this);
		new CommandTicTacToeAccept(this);
	}
	
	@Override
	public void onDisable() {
		
		// Cancel all running games
		ArrayList<Game> runningGames = new ArrayList<Game>();
		runningGames.addAll(Game.runningGames.values());
		
		for(Game runningGame: runningGames) {
			runningGame.end(GameEndCause.CANCEL);
		}
		
	}
	
}
