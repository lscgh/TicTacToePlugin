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
import net.md_5.bungee.api.ChatColor;

public class CommandTicTacToe implements CommandExecutor, TabCompleter {
	
	public static String commandName = "tictactoe";
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
			sender.sendMessage("This command mayo only be executed by players");
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
			
			sender.sendMessage("You just executed /tictactoe correctly");
			
			for(String arg: args) {
				sender.sendMessage(arg);
			}
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
	
	
}
