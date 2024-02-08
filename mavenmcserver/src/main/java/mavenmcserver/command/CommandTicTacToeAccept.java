package mavenmcserver.command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import mavenmcserver.Plugin;
import mavenmcserver.game.Game;
import net.md_5.bungee.api.ChatColor;

public class CommandTicTacToeAccept implements CommandExecutor, TabCompleter {
	
	public static String COMMAND_NAME = "tictactoeaccept";
	public static int ARG_COUNT = 1;
	public static int PLAYER_NAME_ARG_INDEX = 0;
	public static String ERROR_EXECUTION_IS_ONLY_ALLOWED_BY_PLAYERS = ChatColor.RED + "this command may only be executed by players" + ChatColor.RESET;
	
	public CommandTicTacToeAccept(Plugin plugin) {
		plugin.getCommand(CommandTicTacToeAccept.COMMAND_NAME).setExecutor(this);
		plugin.getCommand(CommandTicTacToeAccept.COMMAND_NAME).setTabCompleter(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(CommandTicTacToeAccept.ERROR_EXECUTION_IS_ONLY_ALLOWED_BY_PLAYERS);
			return true;
		}
		
		if(args.length != CommandTicTacToeAccept.ARG_COUNT) return false;
		
		String playerName = args[CommandTicTacToeAccept.PLAYER_NAME_ARG_INDEX];
		
		Game targetGame = this.getQueuedGameWithPlayers(playerName, (Player)sender);
		
		
		if(targetGame == null) {
			try {
				targetGame = this.getQueuedGameByUUID(args[CommandTicTacToeAccept.PLAYER_NAME_ARG_INDEX]);
				
				if(targetGame == null) {
					sender.sendMessage(ChatColor.RED + "This game is not available anymore." + ChatColor.RESET);
					return true;
				}
				
			} catch(IllegalArgumentException e) {
				sender.sendMessage(ChatColor.RED + "'" + playerName + "' hasn't sent any game request to you!" + ChatColor.RESET);
				return true;
			}
		}
		
		targetGame.start();
		
		return true;
	}
	
	private Game getQueuedGameWithPlayers(String mainPlayerName, Player opponentPlayer) {
		for(Game queuedGame: Game.queuedGames.values()) {
			if(queuedGame.config.opponentPlayer != opponentPlayer) continue;
			if(queuedGame.config.mainPlayer.getName().equals(mainPlayerName)) {
				return queuedGame;
			}
		}
		
		return null;
	}
	
	private Game getQueuedGameByUUID(String uuidString) throws IllegalArgumentException {
		UUID gameUUID = UUID.fromString(uuidString);
		return Game.queuedGames.get(gameUUID);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if(!(sender instanceof Player)) return new ArrayList<String>();
		
		ArrayList<String> argList = CommandTicTacToeAccept.removeEmptyStringsBeforeStringFromList(args);
		
		if(argList.size() >= CommandTicTacToeAccept.ARG_COUNT) return new ArrayList<String>();
		
		ArrayList<String> completions = new ArrayList<String>();
		for(Game queuedGame: Game.getRequestsTo((Player)sender)) {
			completions.add(queuedGame.config.mainPlayer.getName());
		}
		
		ArrayList<String> filteredCompletions = new ArrayList<String>();
		StringUtil.copyPartialMatches(args[args.length - 1], completions, filteredCompletions);
		
		return filteredCompletions;
	}
	
	public static ArrayList<String> removeEmptyStringsBeforeStringFromList(String[] list) {
		ArrayList<String> newList = new ArrayList<String>();
		for(String arg: list) newList.add(arg);
		
		newList.removeIf((item) -> item.isEmpty() && !CommandTicTacToeAccept.listContainsNonEmptyString(newList.subList(0, Math.max(0, newList.indexOf(item) - 1))));
		
		return newList;
	}
	
	public static boolean listContainsNonEmptyString(List<String> list) {
		for(String string: list) {
			if(!string.isEmpty()) return true;
		}
		
		return false;
	}
}
