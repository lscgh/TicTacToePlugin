package mavenmcserver.command;

import java.util.ArrayList;
import java.util.List;

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
		
		if(args.length != 1) return false;
		
		String playerName = args[0];
		
		Game targetGame = null;
		
		for(Game queuedGame: Game.queuedGames.values()) {
			if(queuedGame.config.mainPlayer.getName() == playerName) {
				targetGame = queuedGame;
				break;
			}
		}
		
		if(targetGame == null) {
			sender.sendMessage(ChatColor.RED + "'" + playerName + "' hasn't sent any game request to you!" + ChatColor.RESET);
			return true;
		}
		
		targetGame.start();
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if(!(sender instanceof Player)) return new ArrayList<String>();
		
		if(args.length > 1) return new ArrayList<String>();
		
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
	
	
}
