package mavenmcserver;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import mavenmcserver.command.CommandTicTacToe;
import mavenmcserver.command.CommandTicTacToeAccept;
import mavenmcserver.game.Game;

public class Plugin extends JavaPlugin {
	
	private static String MAX_X_Z_SIZE_KEY_NAME = "max_xz_size";
	
	public int getMaxXZSize() {
		return this.getConfig().getInt(Plugin.MAX_X_Z_SIZE_KEY_NAME);
	}
	
	private void addConfigDefaults() {
		FileConfiguration config = this.getConfig();
		config.addDefault(Plugin.MAX_X_Z_SIZE_KEY_NAME, 15);
		config.options().copyDefaults(true);
		this.saveConfig();
	}

	@Override
	public void onLoad() {
		this.getLogger().info("TicTacToe loaded!");
	}
	
	@Override
	public void onEnable() {
		
		this.addConfigDefaults();
		
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
