package mavenmcserver;

import org.bukkit.plugin.java.JavaPlugin;

import mavenmcserver.command.CommandTicTacToe;

public class Plugin extends JavaPlugin {

	@Override
	public void onLoad() {
		this.getLogger().info("TicTacToe loaded!");
	}
	
	@Override
	public void onEnable() {
		// Register command /tictactoe
		new CommandTicTacToe(this);
	}
	
	@Override
	public void onDisable() {
		// I am a comment
	}
	
}
