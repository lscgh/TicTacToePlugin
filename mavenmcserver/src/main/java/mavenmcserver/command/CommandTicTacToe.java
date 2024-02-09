package mavenmcserver.command;

import java.util.ArrayList;
import java.util.Arrays;
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
import mavenmcserver.game.Game.GameEndCause;
import mavenmcserver.game.GameConfig;
import net.md_5.bungee.api.ChatColor;

public class CommandTicTacToe implements CommandExecutor, TabCompleter {
	
	public static String COMMAND_NAME = "tictactoe";
	public static int MIN_VALID_ARG_COUNT = 1;
	public static int MAX_VALID_ARG_COUNT = 5;
	public static int OPPONENT_ARG_INDEX = 0;
	public static int Y_SIZE_ARG_INDEX = 2;
	public static int WIN_REQUIRED_AMOUNT_ARG_INDEX = 4;
	
	public static int SIZE_X_INDEX = 0;
	public static int SIZE_Y_INDEX = 1;
	public static int SIZE_Z_INDEX = 2;
	public static int WIN_REQUIRED_AMOUNT_INDEX = 3;
	
	public static String CANCEL_KEYWORD = "cancel";
	public static String REQUEST_RETURN_MATCH_KEYWORD = "requestReturnMatch";
	
	public static int NO_AVAILABLE_PLAYERS_MIN_ARG_COUNT = 3;
	public static String NO_AVAILABLE_PLAYERS_ARGS[] = {"(no", "available", "players)"};  
	
	private Plugin plugin;
	
	public CommandTicTacToe(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public void registerToPlugin() {
		this.plugin.getCommand(CommandTicTacToe.COMMAND_NAME).setExecutor(this);
		this.plugin.getCommand(CommandTicTacToe.COMMAND_NAME).setTabCompleter(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command may only be executed by players" + ChatColor.RESET);
			return true;
		}
		
		if(args.length < CommandTicTacToe.MIN_VALID_ARG_COUNT) {
			return false;
		}
		
		if(args.length > CommandTicTacToe.MAX_VALID_ARG_COUNT) {
			sender.sendMessage(ChatColor.RED + "Too many arguments for command '/" + label + "'!" + ChatColor.RESET);
			return false;
		}
		
		if(CommandTicTacToe.isNoAvailablePlayersPlaceholder(args)) {
			return true;
		}
			
		boolean playerIsCurrentlyInAGame = Game.runningGames.containsKey((Player)sender);
		boolean playerProvidedCancelKeyword = args[CommandTicTacToe.OPPONENT_ARG_INDEX].equals(CommandTicTacToe.CANCEL_KEYWORD); 
		if(playerIsCurrentlyInAGame && playerProvidedCancelKeyword) {
			Game gameToCancel = Game.runningGames.get((Player)sender); 
			gameToCancel.end(GameEndCause.CANCEL);
			return true;
		}
		
		if(args[CommandTicTacToe.OPPONENT_ARG_INDEX].equals(CommandTicTacToe.REQUEST_RETURN_MATCH_KEYWORD)) {
			
			GameConfig configOfReturnMatch = Game.lostGames.get((Player)sender);
			
			if(configOfReturnMatch != null) {
				
				// Show the confirmation to the player
				sender.sendMessage("You've just asked " + ChatColor.AQUA + ChatColor.BOLD + configOfReturnMatch.opponentPlayer.getName() + ChatColor.RESET + " to play a return match with you!");
				
				// Remove config from list!
				Game.lostGames.remove((Player)sender);
				Game.lostGames.remove(configOfReturnMatch.opponentPlayer);
				
				new Game(configOfReturnMatch, this.plugin, true);
				return true;
			}
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
		List<String> configErrors = config.validateReturningErrors(this.plugin.getMaxXZSize());
		if(!configErrors.isEmpty()) {
			for(String error: configErrors) {
				sender.sendMessage(ChatColor.RED + error + ChatColor.RESET);
			}
			
			// Don't continue on error
			return true;
		}
		
		this.tellAffectedPlayersThatPlayerChangedTheirRequest(config);
		
		// Show the confirmation to the player
		sender.sendMessage("You've just asked " + ChatColor.AQUA + ChatColor.BOLD + config.opponentPlayer.getName() + ChatColor.RESET + " to play a game of tic-tac-toe with you!");
		
		new Game(config, this.plugin, false);
	
		return true;
	}
	
	private static boolean isNoAvailablePlayersPlaceholder(String[] args) {
		if(args.length < CommandTicTacToe.NO_AVAILABLE_PLAYERS_MIN_ARG_COUNT) {
			return false;
		}
		
		boolean firstArgumentIsPlaceholder = args[0].equals(CommandTicTacToe.NO_AVAILABLE_PLAYERS_ARGS[0]);
		boolean secondArgumentIsPlaceholder = args[1].equals(CommandTicTacToe.NO_AVAILABLE_PLAYERS_ARGS[1]);
		boolean thirdArgumentIsPlaceholder = args[2].equals(CommandTicTacToe.NO_AVAILABLE_PLAYERS_ARGS[2]);
		boolean argumentsArePlaceholder = firstArgumentIsPlaceholder && secondArgumentIsPlaceholder && thirdArgumentIsPlaceholder;
		
		return argumentsArePlaceholder;
	}
	
	private void tellAffectedPlayersThatPlayerChangedTheirRequest(GameConfig config) {
		ArrayList<Game> queuedGames = new ArrayList<Game>();
		queuedGames.addAll(Game.queuedGames.values());
		
		for(Game queuedGame: queuedGames) {
			if(queuedGame.config.mainPlayer == config.mainPlayer) {
				Game.queuedGames.remove(queuedGame.uuid);
				
				String revokeMessage;
				if(queuedGame.config.opponentPlayer == config.opponentPlayer) {
					revokeMessage = ChatColor.AQUA + "" + ChatColor.BOLD + config.mainPlayer.getName() + ChatColor.RESET + " has updated their tic-tac-toe-game request. See below.";
				} else {
					revokeMessage = ChatColor.AQUA + "" + ChatColor.BOLD + config.mainPlayer.getName() + ChatColor.RESET + " has revoked their tic-tac-toe-game request to you.";
				}
				
				queuedGame.config.opponentPlayer.sendMessage(revokeMessage);
			}
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if(!(sender instanceof Player)) return new ArrayList<String>();
		
		ArrayList<String> argList = CommandTicTacToe.removeEmptyElementsExceptForLast(args);
		
		ArrayList<String> completions = new ArrayList<String>();
		
		boolean playerIsCurrentlyInAGame = Game.runningGames.containsKey((Player)sender);
		if(playerIsCurrentlyInAGame) {
			if(argList.size() == CommandTicTacToe.OPPONENT_ARG_INDEX + 1) {
				completions.add(CommandTicTacToe.CANCEL_KEYWORD);
			}
		} else {
			if(argList.size() == CommandTicTacToe.OPPONENT_ARG_INDEX + 1) {
				
				for(Player player: this.plugin.getServer().getOnlinePlayers()) {
					if(player.getName().equals(sender.getName())) continue;
					completions.add(player.getName());
				}
				
				if(Game.lostGames.containsKey((Player)sender)) {
					completions.add(CommandTicTacToe.REQUEST_RETURN_MATCH_KEYWORD);
				}
				
				if(completions.isEmpty()) completions.add("(no available players)");
			} else if(argList.size() == CommandTicTacToe.WIN_REQUIRED_AMOUNT_ARG_INDEX + 1) {
				ArrayList<String> listWithNoEmptyArgsAtAll = new ArrayList<String>();
				for(String arg: args) listWithNoEmptyArgsAtAll.add(arg);
				listWithNoEmptyArgsAtAll.removeIf((arg) -> arg.isEmpty());
				
				int integerArgs[] = CommandTicTacToe.extractIntegerArgs(Arrays.copyOf(listWithNoEmptyArgsAtAll.toArray(), listWithNoEmptyArgsAtAll.size(), String[].class));
				int maxDimension = Math.max(integerArgs[0], Math.max(integerArgs[1], integerArgs[2]));
				completions.add("" + maxDimension);
			} else if(argList.size() < CommandTicTacToe.MAX_VALID_ARG_COUNT + 1) {
				completions.add(argList.size() == (CommandTicTacToe.Y_SIZE_ARG_INDEX + 1) ? "1" : "3");
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
		
		String opponentPlayerName = args[CommandTicTacToe.OPPONENT_ARG_INDEX];
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
					if(i == CommandTicTacToe.WIN_REQUIRED_AMOUNT_INDEX + 1) {
						integerArguments[i - 1] = Math.max(integerArguments[0], Math.max(integerArguments[1], integerArguments[2]));
					} else {
						integerArguments[i - 1] = i == (CommandTicTacToe.SIZE_Y_INDEX + 1) ? 1 : 3;
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
	
	private static ArrayList<String> removeEmptyElementsExceptForLast(String[] list) {
		ArrayList<String> newList = new ArrayList<String>();
		int i = 0;
		for(String element: list) {
			if(!element.trim().isEmpty() || (i == list.length - 1)) {
				newList.add(element);
			}
			
			i++;
		}
		
		return newList;
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
