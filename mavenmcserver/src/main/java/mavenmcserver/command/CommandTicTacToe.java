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

public class CommandTicTacToe implements CommandExecutor, TabCompleter {
	
	public static String commandName = "tictactoe";
	
	private Plugin plugin;
	
	public CommandTicTacToe(Plugin plugin) {
		this.plugin = plugin;
		
		this.plugin.getCommand(CommandTicTacToe.commandName).setExecutor(this);
		this.plugin.getCommand(CommandTicTacToe.commandName).setTabCompleter(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(args.length > 0) {
			
			if(args.length >= 3) {
				if(args[0].equals("(no") && args[1].equals("available") && args[2].equals("players)")) return true;
			}
			
			sender.sendMessage("You just executed /tictactoe correctly");
			
			for(String arg: args) {
				sender.sendMessage(arg);
			}
		}
		
		return args.length > 0;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		ArrayList<String> result = new ArrayList<String>();
		
		ArrayList<String> commands = new ArrayList<String>();
		
		if(args.length == 1) {
			for(Player player: this.plugin.getServer().getOnlinePlayers()) {
				if(player.getName().equals(sender.getName())) continue;
				commands.add(player.getName());
			}
			
			if(commands.isEmpty()) commands.add("(no available players)");
		} else {
			commands.add("3");
		}
		
		StringUtil.copyPartialMatches(args[args.length - 1], commands, result);
		
		return result;
	}
	
	
}
