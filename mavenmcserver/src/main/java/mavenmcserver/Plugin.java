package mavenmcserver;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import mavenmcserver.command.CommandTicTacToe;
import mavenmcserver.command.CommandTicTacToeAccept;
import mavenmcserver.game.Game;

public class Plugin extends JavaPlugin {
	
	private static String MAX_DIMENSION_SIZE_KEY_NAME = "max_dimension_size";
	private static int MAX_DIMENSION_SIZE_DEFAULT_VALUE = 15;
	
	public int getMaxDimensionSize() {
		return this.getConfig().getInt(Plugin.MAX_DIMENSION_SIZE_KEY_NAME);
	}
	
	private void addConfigDefaults() {
		FileConfiguration config = this.getConfig();
		config.addDefault(Plugin.MAX_DIMENSION_SIZE_KEY_NAME, Plugin.MAX_DIMENSION_SIZE_DEFAULT_VALUE);
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
