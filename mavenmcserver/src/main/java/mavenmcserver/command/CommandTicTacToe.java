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
import mavenmcserver.game.Game;
import mavenmcserver.game.GameConfig;
import mavenmcserver.game.Game.GameEndCause;
import net.md_5.bungee.api.ChatColor;

public class CommandTicTacToe implements CommandExecutor, TabCompleter {
	
	public static String COMMAND_NAME = "tictactoe";
	public static int MIN_VALID_ARG_COUNT = 1;
	public static int MAX_VALID_ARG_COUNT = 5;
	public static int OPPONENT_ARG_INDEX = 1;
	public static int Y_SIZE_ARG_INDEX = 3;
	
	public static int SIZE_X_INDEX = 0;
	public static int SIZE_Y_INDEX = 1;
	public static int SIZE_Z_INDEX = 2;
	public static int WIN_REQUIRED_AMOUNT_INDEX = 3;
	
	private Plugin plugin;
	
	public CommandTicTacToe(Plugin plugin) {
		this.plugin = plugin;
		
		this.plugin.getCommand(CommandTicTacToe.COMMAND_NAME).setExecutor(this);
		this.plugin.getCommand(CommandTicTacToe.COMMAND_NAME).setTabCompleter(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command may only be executed by players" + ChatColor.RESET);
			return true;
		}
		
		if(args.length > 0) {
			
			boolean playerIsCurrentlyInAGame = Game.runningGames.containsKey((Player)sender);
			if(playerIsCurrentlyInAGame && args[0].equals("cancel")) {
				Game.runningGames.get((Player)sender).end(GameEndCause.CANCEL);
				return true;
			}
			
			if(args.length > CommandTicTacToe.MAX_VALID_ARG_COUNT) {
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
			} catch(InvalidArgCountException | OpponentPlayerNotFoundException | OpponentIsMainPlayerException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage() + ChatColor.RESET);
				return true;
			} catch(NumberFormatException e) {
				String nonNumberString = e.getMessage().substring(19, e.getMessage().length() - 1);
				sender.sendMessage(ChatColor.RED + "Error: expected number at '" + nonNumberString + "'." + ChatColor.RESET);
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
			sender.sendMessage("Game configuration: '" + config + "'");
			
			new Game(config, this.plugin);
		}
		
		boolean shouldShowUsage = args.length <= 0;
		return !shouldShowUsage;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if(!(sender instanceof Player)) return new ArrayList<String>();
		
		ArrayList<String> completions = new ArrayList<String>();
		
		boolean playerIsCurrentlyInAGame = Game.runningGames.containsKey((Player)sender);
		if(playerIsCurrentlyInAGame) {
			if(args.length == CommandTicTacToe.OPPONENT_ARG_INDEX) {
				completions.add("cancel");
			}
		} else {
			if(args.length == CommandTicTacToe.OPPONENT_ARG_INDEX) {
				
				for(Player player: this.plugin.getServer().getOnlinePlayers()) {
					if(player.getName().equals(sender.getName())) continue;
					completions.add(player.getName());
				}
				
				if(completions.isEmpty()) completions.add("(no available players)");
			} else if(args.length <= CommandTicTacToe.MAX_VALID_ARG_COUNT) {
				if(args.length == CommandTicTacToe.WIN_REQUIRED_AMOUNT_INDEX) {
					int integerArgs[] = CommandTicTacToe.extractIntegerArgs(args);
					int maxDimension = Math.max(integerArgs[0], Math.max(integerArgs[1], integerArgs[2]));
					completions.add("" + maxDimension);
				} else {
					completions.add(args.length == CommandTicTacToe.Y_SIZE_ARG_INDEX ? "1" : "3");
				}
			}
		}
		
		ArrayList<String> filteredCompletions = new ArrayList<String>();
		StringUtil.copyPartialMatches(args[args.length - 1], completions, filteredCompletions);
		
		return filteredCompletions;
	}
	
	public GameConfig createGameConfigFromCommand(Player mainPlayer, String args[]) throws InvalidArgCountException, OpponentPlayerNotFoundException, OpponentIsMainPlayerException, NumberFormatException {
		
		if(args.length < CommandTicTacToe.MIN_VALID_ARG_COUNT && args.length > CommandTicTacToe.MAX_VALID_ARG_COUNT) {
			throw new InvalidArgCountException("CommandTicTacToe.createGameConfigFromCommand was called with " + args.length + "arguments (min = " + CommandTicTacToe.MIN_VALID_ARG_COUNT + "; max = " + CommandTicTacToe.MAX_VALID_ARG_COUNT + ")!");
		}
		
		String opponentPlayerName = args[0];
		Player opponentPlayer = null;
		for(Player player: this.plugin.getServer().getOnlinePlayers()) {
			if(opponentPlayerName.equals(player.getName())) {
				opponentPlayer = player;
				break;
			}
		}
		
		if(opponentPlayer == null) {
			throw new OpponentPlayerNotFoundException("The requested opponent player '" + opponentPlayerName + "' was not found on this server.");
		}
		
		if(opponentPlayer == mainPlayer) {
			throw new OpponentIsMainPlayerException("You cannot play a game with yourself ('" + opponentPlayerName + "').");
		}
		
		
		int integerArguments[] = CommandTicTacToe.extractIntegerArgs(args);
		
		return new GameConfig(mainPlayer, opponentPlayer, new Vector3i(integerArguments[CommandTicTacToe.SIZE_X_INDEX], integerArguments[CommandTicTacToe.SIZE_Y_INDEX], integerArguments[CommandTicTacToe.SIZE_Z_INDEX]), integerArguments[CommandTicTacToe.WIN_REQUIRED_AMOUNT_INDEX]);
	}
	
	
	protected static int[] extractIntegerArgs(String args[]) {
		int integerArguments[] = new int[4];
		
		for(int i = 1; i < 5; i++) {
			try {
				if(args.length <= i || args[i].isEmpty()) {
					if(i == CommandTicTacToe.WIN_REQUIRED_AMOUNT_INDEX) {
						integerArguments[i - 1] = Math.max(integerArguments[0], Math.max(integerArguments[1], integerArguments[2]));
					} else {
						integerArguments[i - 1] = i == CommandTicTacToe.SIZE_Y_INDEX ? 1 : 3;
					}
				} else {
					integerArguments[i - 1] = Integer.parseInt(args[i]);
				}
			} catch(NumberFormatException e) {
				throw e;
			}
		}
		
		return integerArguments;
	}
	
	
	public class InvalidArgCountException extends Exception {
		private static final long serialVersionUID = 5946362337911270663L;
		
		public InvalidArgCountException(String message) {
			super(message);
		}
	}
	
	public class OpponentPlayerNotFoundException extends Exception {
		private static final long serialVersionUID = -3046415677251307939L;
		
		public OpponentPlayerNotFoundException(String message) {
			super(message);
		}
	}
	
	public class OpponentIsMainPlayerException extends Exception {
		private static final long serialVersionUID = 3470965440570763560L;

		public OpponentIsMainPlayerException(String message) {
			super(message);
		}
	}
	
}
