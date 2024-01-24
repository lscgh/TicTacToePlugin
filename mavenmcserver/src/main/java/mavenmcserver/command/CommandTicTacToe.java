package mavenmcserver.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
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
			sender.sendMessage("You just executed /tictactoe correctly");
		}
		
		return args.length > 0;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		ArrayList<String> result = new ArrayList<String>();
		
		ArrayList<String> commands = new ArrayList<String>();
		commands.add("start");
		commands.add("stop");
		commands.add("test");
		
		StringUtil.copyPartialMatches(args[args.length - 1], commands, result);
		
		return result;
	}
	
	
}
