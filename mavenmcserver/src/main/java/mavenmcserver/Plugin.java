package mavenmcserver;

import org.bukkit.plugin.java.JavaPlugin;

import mavenmcserver.command.CommandTicTacToe;
import mavenmcserver.command.CommandTicTacToeAccept;

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
		
	}
	
}
