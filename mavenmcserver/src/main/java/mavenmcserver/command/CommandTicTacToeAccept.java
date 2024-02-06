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
	
	public CommandTicTacToeAccept(Plugin plugin) {
		plugin.getCommand(CommandTicTacToeAccept.COMMAND_NAME).setExecutor(this);
		plugin.getCommand(CommandTicTacToeAccept.COMMAND_NAME).setTabCompleter(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "this command may only be executed by players" + ChatColor.RESET);
			return true;
		}
		
		if(args.length != CommandTicTacToeAccept.ARG_COUNT) return false;
		
		String playerName = args[CommandTicTacToeAccept.PLAYER_NAME_ARG_INDEX];
		
		Game targetGame = null;
		
		for(Game queuedGame: Game.queuedGames.values()) {
			if(queuedGame.config.opponentPlayer != (Player)sender) continue;
			if(queuedGame.config.mainPlayer.getName().equals(playerName)) {
				targetGame = queuedGame;
				break;
			}
		}
		
		boolean validUUIDIsGiven = false;
		
		if(targetGame == null) {
			try {
				UUID gameUUID = UUID.fromString(playerName);
				validUUIDIsGiven = true;
				targetGame = Game.queuedGames.get(gameUUID);
			} catch(IllegalArgumentException e) {}
		}
		
		if(targetGame == null) {
			if(validUUIDIsGiven) {
				sender.sendMessage(ChatColor.RED + "This game is not available anymore." + ChatColor.RESET);
			} else {
				sender.sendMessage(ChatColor.RED + "'" + playerName + "' hasn't sent any game request to you!" + ChatColor.RESET);
			}
			return true;
		}
		
		targetGame.start();
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if(!(sender instanceof Player)) return new ArrayList<String>();
		
		ArrayList<String> argList = new ArrayList<String>();
		for(String arg: args) argList.add(arg);
		argList.removeIf((arg) -> arg.isEmpty() && !CommandTicTacToeAccept.containsNonEmptyString(argList.subList(0, Math.max(0, argList.indexOf(arg) - 1))));
		
		if(argList.size() > CommandTicTacToeAccept.ARG_COUNT) return new ArrayList<String>();
		
		ArrayList<String> completions = new ArrayList<String>();
		for(Game queuedGame: Game.queuedGames.values()) {
			if(queuedGame.config.opponentPlayer == (Player)sender) {
				completions.add(queuedGame.config.mainPlayer.getName());
			}
		}
		
		ArrayList<String> filteredCompletions = new ArrayList<String>();
		StringUtil.copyPartialMatches(args[args.length - 1], completions, filteredCompletions);
		
		return filteredCompletions;
	}
	
	
	public static boolean containsNonEmptyString(List<String> list) {
		for(String string: list) {
			if(!string.isEmpty()) return true;
		}
		
		return false;
	}
}
