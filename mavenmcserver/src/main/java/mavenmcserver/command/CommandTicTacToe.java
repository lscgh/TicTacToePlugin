package mavenmcserver.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.joml.Vector3i;

import mavenmcserver.Plugin;
import mavenmcserver.game.GameConfig;
import net.md_5.bungee.api.ChatColor;

public class CommandTicTacToe implements CommandExecutor, TabCompleter {
	
	public static String commandName = "tictactoe";
	public static int minValidArgCount = 1;
	public static int maxValidArgCount = 5;
	public static int opponentArgumentIndex = 1;
	public static int ySizeArgumentIndex = 3;
	
	private Plugin plugin;
	
	public CommandTicTacToe(Plugin plugin) {
		this.plugin = plugin;
		
		this.plugin.getCommand(CommandTicTacToe.commandName).setExecutor(this);
		this.plugin.getCommand(CommandTicTacToe.commandName).setTabCompleter(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage("This command may only be executed by players");
			return true;
		}
		
		if(args.length > 0) {
			
			if(args.length > CommandTicTacToe.maxValidArgCount) {
				sender.sendMessage(ChatColor.RED + "Too many arguments for command '/" + label + "'!" + ChatColor.RESET);
				return false;
			}
			
			int noAvailablePlayersMinArgCount = 3;
			if(args.length >= noAvailablePlayersMinArgCount) {
				String noAvailablePlayersPlaceholder[] = {"(no", "available", "players)"};
				if(args[0].equals(noAvailablePlayersPlaceholder[0]) && args[1].equals(noAvailablePlayersPlaceholder[1]) && args[2].equals(noAvailablePlayersPlaceholder[2])) return true;
			}
			
			// Create the game's config from the command's args
			GameConfig config;
			
			try {
				config = this.createGameConfigFromCommand((Player)sender, args);
			} catch(InvalidArgCountException | OpponentPlayerNotFoundException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage() + ChatColor.RESET);
				return true;
			} catch(NumberFormatException e) {
				String nonNumberString = e.getMessage().substring(19, e.getMessage().length() - 1);
				sender.sendMessage(ChatColor.RED + "Error: expected number at '" + nonNumberString + "'" + ChatColor.RESET);
				return true;
			}
			
			// Check for errors in the game's config
			List<String> configErrors = config.validate();
			if(!configErrors.isEmpty()) {
				for(String error: configErrors) {
					sender.sendMessage(ChatColor.RED + error + ChatColor.RESET);
				}
				
				// Don't continue on error
				return true;
			}
			
			// Show the config to the player
			sender.sendMessage("You just executed /tictactoe correctly");
			sender.sendMessage("Opponent player name: '" + config.opponentPlayer.getName() + "'");
			sender.sendMessage("Game size: " + config.size);
			sender.sendMessage("WinRequiredAmount: " + config.winRequiredAmount);
			
		}
		
		boolean shouldShowUsage = args.length <= 0;
		return !shouldShowUsage;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		ArrayList<String> completions = new ArrayList<String>();
		
		if(args.length == CommandTicTacToe.opponentArgumentIndex) {
			
			for(Player player: this.plugin.getServer().getOnlinePlayers()) {
				if(player.getName().equals(sender.getName())) continue;
				completions.add(player.getName());
			}
			
			if(completions.isEmpty()) completions.add("(no available players)");
		} else if(args.length <= CommandTicTacToe.maxValidArgCount) {
			completions.add(args.length == CommandTicTacToe.ySizeArgumentIndex ? "1" : "3");
		}
		
		ArrayList<String> filteredCompletions = new ArrayList<String>();
		StringUtil.copyPartialMatches(args[args.length - 1], completions, filteredCompletions);
		
		return filteredCompletions;
	}
	
	public GameConfig createGameConfigFromCommand(Player mainPlayer, String args[]) throws InvalidArgCountException, OpponentPlayerNotFoundException, NumberFormatException {
		if(args.length < CommandTicTacToe.minValidArgCount && args.length > CommandTicTacToe.maxValidArgCount) {
			throw new InvalidArgCountException("CommandTicTacToe.createGameConfigFromCommand was called with " + args.length + "arguments! (min = " + CommandTicTacToe.minValidArgCount + "; max = " + CommandTicTacToe.maxValidArgCount + ")");
		}
		
		String opponentPlayerName = args[0];
		Player opponentPlayer = this.plugin.getServer().getPlayer(opponentPlayerName);
		
		if(opponentPlayer == null) {
			throw new OpponentPlayerNotFoundException("The requested opponent player '" + opponentPlayerName + "' was not found on this server.");
		}
		
		int integerArguments[] = new int[4];
		int sizeXIndex = 0;
		int sizeYIndex = 1;
		int sizeZIndex = 2;
		int winRequiredAmountIndex = 3;
		
		for(int i = 1; i < 5; i++) {
			try {
				if(args.length <= i) {
					integerArguments[i - 1] = i == 2 ? 1 : 3;
				} else {
					integerArguments[i - 1] = Integer.parseInt(args[i]);
				}
			} catch(NumberFormatException e) {
				throw e;
			}
		}
		
		return new GameConfig(mainPlayer, opponentPlayer, new Vector3i(integerArguments[sizeXIndex], integerArguments[sizeYIndex], integerArguments[sizeZIndex]), integerArguments[winRequiredAmountIndex]);
	}
	
	
	class InvalidArgCountException extends Exception {
		private static final long serialVersionUID = 5946362337911270663L;

		public InvalidArgCountException(String message) {
			super(message);
		}
	}
	
	class OpponentPlayerNotFoundException extends Exception {
		private static final long serialVersionUID = -3046415677251307939L;
		
		public OpponentPlayerNotFoundException(String message) {
			super(message);
		}
	}
	
}
