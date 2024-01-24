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
		this.getCommand(CommandTicTacToe.commandName).setExecutor(new CommandTicTacToe());
	}
	
	@Override
	public void onDisable() {
		
	}
	
}
