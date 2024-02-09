package mavenmcserver;

import org.bukkit.plugin.java.JavaPlugin;

import mavenmcserver.command.CommandTicTacToe;
import mavenmcserver.command.CommandTicTacToeAccept;
import mavenmcserver.game.Game;

public class Plugin extends JavaPlugin {

	@Override
	public void onLoad() {
		this.getLogger().info("TicTacToe loaded!");
	}
	
	@Override
	public void onEnable() {
		// Register command /tictactoe
		new CommandTicTacToe(this).registerToPlugin();
		// Register command /tictactoeaccept
		new CommandTicTacToeAccept(this).registerToPlugin();
	}
	
	@Override
	public void onDisable() {
		this.getLogger().info("TicTacToe disabled! Cancelling all games...");
		Game.cancelAllGames();
	}
	
}
