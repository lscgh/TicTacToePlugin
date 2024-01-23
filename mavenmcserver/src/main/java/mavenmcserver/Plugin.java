package mavenmcserver;

import org.bukkit.plugin.java.JavaPlugin;

import mavenmcserver.command.CommandTicTacToe;

public class Plugin extends JavaPlugin {

	@Override
	public void onLoad() {
		this.getLogger().info("TicTacToe loaded!");
		this.getCommand(CommandTicTacToe.commandName).setExecutor(new CommandTicTacToe());
	}
	
	@Override
	public void onEnable() {
		

	}
	
	@Override
	public void onDisable() {
		
	}
	
}
